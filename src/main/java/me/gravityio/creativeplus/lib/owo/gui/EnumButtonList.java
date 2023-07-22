package me.gravityio.creativeplus.lib.owo.gui;

import net.minecraft.text.Text;

import java.util.function.Consumer;

/**
 * A Button List that iterates through different enums
 */
public class EnumButtonList extends ButtonList {
    private final String enumName;
    private final Enum<?>[] enums;
    private Consumer<Enum<?>> onEnumChanged;

    public EnumButtonList(Class<? extends Enum<?>> enumClass) {
        this.enumName = enumClass.getSimpleName().toLowerCase();
        this.enums = enumClass.getEnumConstants();
        for (Enum<?> anEnum : this.enums) {
            super.list.add(getAsText(anEnum));
        }
        super.onChanged(integer -> {
            if (this.onEnumChanged == null) return;
            this.onEnumChanged.accept(this.enums[integer]);
        });
        super.setMessage(super.list.get(0));
    }

    public Enum<?> currentEnum() {
        return this.enums[super.index];
    }

    public void setEnum(Enum<?> enumValue) {
        for (int i = 0; i < this.enums.length; i++) {
            if (this.enums[i].equals(enumValue)) {
                super.set(i);
                return;
            }
        }
    }
    
    public void onEnumChanged(Consumer<Enum<?>> onEnumChanged) {
        this.onEnumChanged = onEnumChanged;
    }

    private Text getAsText(Enum<?> enumValue) {
        var valueName = enumValue.toString().toLowerCase();
        return Text.translatable("buttonlist.enum.%s.%s".formatted(this.enumName, valueName));
    }
}
