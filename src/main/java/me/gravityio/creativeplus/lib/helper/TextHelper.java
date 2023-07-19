package me.gravityio.creativeplus.lib.helper;


import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Text Helper class
 */
public class TextHelper {

    public static final char COLOR_CHAR = '&';

    /**
     * Returns the Text with up until a certain limit then adds some decoration
     * @param text
     * @param limit
     * @return
     */
    public static Text getLimit(Text text, int limit) {
        String string = text.getString();
        if (string.length() >= limit) {
            string = string.substring(0, limit - 3) + "§7...";
            return Text.literal(string);
        } else {
            return text;
        }
    }

    /**
     * Gets a String from some Stylized Text, this doesn't work dependably right now<br><br>
     *
     * "&1This is Going to be the Result in Blue &fAnd this is going to be the result in white"
     */
    public static String getFromStyledText(Text text) {
        StringBuilder sb = new StringBuilder();
        for (Formatting styleFormat : getStyleFormats(text.getStyle())) {
            sb.append(COLOR_CHAR);
            sb.append(styleFormat.getCode());
        }
        sb.append(getString(text));

        for (Text sibling : text.getSiblings()) {
            for (Formatting styleFormat : getStyleFormats(sibling.getStyle())) {
                sb.append(COLOR_CHAR);
                sb.append(styleFormat.getCode());
            }
            sb.append(getString(sibling));
        }
        return sb.toString();
    }

    /**
     * Gets a Text Object from a Styled String, this doesn't work dependably right now<br><br>
     *
     * "&1This is Going to be the Result in Blue &fAnd this is going to be the result in white" <br><br>
     * <span style="color: blue">This is Going to be the Result in Blue</span> <span style="color: white"'>And this is going to be the result in white</span>
     */
    public static Text getStyledText(String string) {
        MutableText parent = null;
        for (MutableText text : getStyledTextAsList(string)) {
            if (parent == null) {
                parent = text;
            } else {
                parent.append(text);
            }
        }
        return parent;
    }

    private static List<Formatting> getStyleFormats(Style style) {
        List<Formatting> formatting = new ArrayList<>();
        if (style.isBold()) {
            formatting.add(Formatting.BOLD);
        } else if (style.isItalic()) {
            formatting.add(Formatting.ITALIC);
        } else if (style.isStrikethrough()) {
            formatting.add(Formatting.STRIKETHROUGH);
        } else if (style.isUnderlined()) {
            formatting.add(Formatting.UNDERLINE);
        } else if (style.isObfuscated()) {
            formatting.add(Formatting.OBFUSCATED);
        } else if (style.isEmpty()) {
            formatting.add(Formatting.RESET);
        }

        if (style.getColor() == null) return formatting;

        int rgb = style.getColor().getRgb();

        for (Formatting value : Formatting.values()) {
            if (!value.isColor()) continue;
            if (value.getColorValue() != rgb) continue;
            formatting.add(value);
            break;
        }
        return formatting;
    }

    // &k&aa&4test&f&ka
    // BUG: Doesn't work, I'm too tired
    private static List<MutableText> getStyledTextAsList(String string) {
        List<MutableText> text = new ArrayList<>();
        Style style = Style.EMPTY;
        StringBuilder builder = new StringBuilder();
        boolean started = false;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            if (c != COLOR_CHAR) {
                builder.append(c);
                started = true;
            } else {
                if (i == string.length() - 1) break;

                char d = string.charAt(i + 1);
                if (d == COLOR_CHAR) {
                    builder.append(d);
                    i++;
                    continue;
                }

                Formatting f = Formatting.byCode(d);
                if (f == null) {
                    builder.append(COLOR_CHAR);
                    builder.append(d);
                    i++;
                    continue;
                }

                if (started) {
                    started = false;
                    text.add(Text.literal(builder.toString()).setStyle(style));
                    builder = new StringBuilder();

                    style = Style.EMPTY.withFormatting(f);
                } else {
                    style = Style.EMPTY.withFormatting(f);
                }

                i++;
            }
        }
        text.add(Text.literal(builder.toString()).setStyle(style));

        return text;
    }

    private static String getString(Text text) {
        return text.getContent().visit(Optional::of).get();
    }

    private static Text getLastSibling(Text text) {
        var siblings = text.getSiblings();
        if (siblings.isEmpty()) return null;
        return siblings.get(siblings.size() - 1);
    }

}
