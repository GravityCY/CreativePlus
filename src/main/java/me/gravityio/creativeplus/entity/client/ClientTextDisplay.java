package me.gravityio.creativeplus.entity.client;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.api.nbt.frame.TextDisplayFrame;
import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import me.gravityio.creativeplus.lib.helper.EntityNbtHelper.TextDisplay;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

public class ClientTextDisplay extends ClientDisplay implements TextDisplayFrame {
    private final TextDisplayEntity display;
    private final TextDisplayEntity transform;
    private Text realText;
    private Text text;
    private Integer realLineWidth;
    private int lineWidth;
    private Integer realBackgroundColor;
    private int backgroundColor;
    private Byte realTextOpacity;
    private byte textOpacity;
    private Boolean realCastShadow;
    private boolean castShadow;
    private Boolean realSeeThrough;
    private boolean seeThrough;
    private Boolean realDefaultBackground;
    private boolean defaultBackground;
    private TextDisplayEntity.TextAlignment alignment;

    private static String key(String v) {
        return "text_display.nbt." + v;
    }

    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.ofBounded(this::getText, this::setText, NbtPiece.Type.TEXT, key("text"), 0, 128),
            NbtPiece.of(this::getLineWidth, this::setLineWidth, NbtPiece.Type.INT, key("line_width")),
            NbtPiece.of(this::getBackgroundColor, this::setBackgroundColor, NbtPiece.Type.INT, key("background_color")),
            NbtPiece.of(this::getTextOpacity, this::setTextOpacity, NbtPiece.Type.BYTE, key("text_opacity")),
            NbtPiece.of(this::shouldCastShadow, this::setShouldCastShadow, NbtPiece.Type.BOOLEAN, key("cast_shadow")),
            NbtPiece.of(this::isSeeThrough, this::setSeeThrough, NbtPiece.Type.BOOLEAN, key("see_through")),
            NbtPiece.of(this::shouldUseDefaultBackground, this::setShouldUseDefaultBackground, NbtPiece.Type.BOOLEAN, key("default_background")),
            NbtPiece.ofEnum(this::getAlignment, (e) -> this.setAlignment((TextDisplayEntity.TextAlignment) e), TextDisplayEntity.TextAlignment.class, key("alignment"))
    );

    public ClientTextDisplay(TextDisplayEntity display, TextDisplayEntity transform, NbtCompound realNbt) {
        super(display, transform, realNbt);

        this.display = display;
        this.transform = transform;

        this.init();
    }

    private static boolean shouldCastShadow(TextDisplayEntity textDisplay) {
        return (textDisplay.getDisplayFlags() & TextDisplayEntity.SHADOW_FLAG) != 0;
    }

    private static boolean isSeeThrough(TextDisplayEntity textDisplay) {
        return (textDisplay.getDisplayFlags() & TextDisplayEntity.SEE_THROUGH_FLAG) != 0;
    }

    private static boolean shouldUseDefaultBackground(TextDisplayEntity textDisplay) {
        return (textDisplay.getDisplayFlags() & TextDisplayEntity.DEFAULT_BACKGROUND_FLAG) != 0;
    }

    private static void setFlag(TextDisplayEntity textDisplay, byte bit, boolean flag) {
        byte flags = textDisplay.getDisplayFlags();
        if (flag) {
            textDisplay.setDisplayFlags((byte) (flags | bit));
        } else {
            textDisplay.setDisplayFlags((byte) (flags & ~(bit)));
        }
    }

    private static void setAlignment(TextDisplayEntity textDisplay, TextDisplayEntity.TextAlignment alignment) {
        // TODO:
    }

    private void init() {
        this.text = this.display.getText();
        this.lineWidth = this.display.getLineWidth();
        this.backgroundColor = this.display.getBackground();
        this.textOpacity = this.display.getTextOpacity();
        this.castShadow = ClientTextDisplay.shouldCastShadow(this.display);
        this.seeThrough = ClientTextDisplay.isSeeThrough(this.display);
        this.defaultBackground = ClientTextDisplay.shouldUseDefaultBackground(this.display);
        this.alignment = TextDisplayEntity.getAlignment(this.display.getDisplayFlags());
    }

    private static <T> T getOrDefault(T got, T def) {
        return got == null ? def : got;
    }

    @Override
    protected void updateRealNbt() {
        super.updateRealNbt();

        this.realText = TextDisplay.getText(super.realNbt);
        this.text = this.realText;

        this.realLineWidth = TextDisplay.getLineWidth(super.realNbt);
        this.lineWidth = getOrDefault(this.realLineWidth, this.lineWidth);

        this.realBackgroundColor = TextDisplay.getBackground(super.realNbt);
        this.backgroundColor = getOrDefault(this.realBackgroundColor, this.backgroundColor);

        this.realTextOpacity = TextDisplay.getTextOpacity(super.realNbt);
        this.textOpacity = getOrDefault(this.realTextOpacity, this.textOpacity);

        this.realCastShadow = TextDisplay.getShouldCastShadow(super.realNbt);
        this.castShadow = getOrDefault(this.realCastShadow, this.castShadow);

        this.realSeeThrough = TextDisplay.getIsSeeThrough(super.realNbt);
        this.seeThrough = getOrDefault(this.realSeeThrough, this.seeThrough);

        this.realDefaultBackground = TextDisplay.getShouldUseDefaultBackground(super.realNbt);
        this.defaultBackground = getOrDefault(this.realDefaultBackground, this.defaultBackground);
    }

    @Override
    public NbtCompound getOutput() {
        if (!this.text.equals(this.realText)) {
            TextDisplay.putText(super.output, this.text);
        }

        return super.getOutput();
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(this.pieces);
        return a;
    }

    @Override
    public Text getText() {
        return this.text;
    }

    @Override
    public int getLineWidth() {
        return this.lineWidth;
    }

    @Override
    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    @Override
    public byte getTextOpacity() {
        return this.textOpacity;
    }

    @Override
    public boolean shouldCastShadow() {
        return this.castShadow;
    }

    @Override
    public boolean isSeeThrough() {
        return this.seeThrough;
    }

    @Override
    public boolean shouldUseDefaultBackground() {
        return this.defaultBackground;
    }

    @Override
    public TextDisplayEntity.TextAlignment getAlignment() {
        return this.alignment;
    }

    @Override
    public void setText(Text text) {
        this.transform.setText(text == null ? Text.empty() : text);
        this.text = text;
    }

    @Override
    public void setLineWidth(int lineWidth) {
        this.transform.setLineWidth(lineWidth);
        this.lineWidth = lineWidth;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.transform.setBackground(backgroundColor);
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void setTextOpacity(byte textOpacity) {
        this.transform.setTextOpacity(textOpacity);
        this.textOpacity = textOpacity;
    }

    @Override
    public void setShouldCastShadow(boolean shouldCastShadow) {
        setFlag(this.display, TextDisplayEntity.SHADOW_FLAG, shouldCastShadow);
        this.castShadow = shouldCastShadow;
    }

    @Override
    public void setSeeThrough(boolean seeThrough) {
        setFlag(this.display, TextDisplayEntity.SEE_THROUGH_FLAG, seeThrough);
        this.seeThrough = seeThrough;
    }

    @Override
    public void setShouldUseDefaultBackground(boolean shouldUseDefaultBackground) {
        setFlag(this.display, TextDisplayEntity.DEFAULT_BACKGROUND_FLAG, shouldUseDefaultBackground);
        this.defaultBackground = shouldUseDefaultBackground;
    }

    @Override
    public void setAlignment(TextDisplayEntity.TextAlignment alignment) {
//        this.transform.setAlignment(alignment);
        this.alignment = alignment;
    }
}
