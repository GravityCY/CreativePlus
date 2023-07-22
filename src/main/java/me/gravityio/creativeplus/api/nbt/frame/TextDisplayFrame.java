package me.gravityio.creativeplus.api.nbt.frame;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

/**
 * {@link net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity#writeCustomDataToNbt}
 */
public interface TextDisplayFrame {
    Text getText();
    int getLineWidth();
    int getBackgroundColor();
    byte getTextOpacity();
    boolean shouldCastShadow();
    boolean isSeeThrough();
    boolean shouldUseDefaultBackground();
    DisplayEntity.TextDisplayEntity.TextAlignment getAlignment();

    void setText(Text text);
    void setLineWidth(int lineWidth);
    void setBackgroundColor(int backgroundColor);
    void setTextOpacity(byte textOpacity);
    void setShouldCastShadow(boolean shouldCastShadow);
    void setSeeThrough(boolean seeThrough);
    void setShouldUseDefaultBackground(boolean shouldUseDefaultBackground);
    void setAlignment(DisplayEntity.TextDisplayEntity.TextAlignment alignment);

    default Text getDefaultText() {
        return null;
    }

    default Integer getDefaultLineWidth() {
        return 200;
    }

    default Integer getDefaultBackgroundColor() {
        return 0x40000000;
    }

    default Byte getDefaultTextOpacity() {
        return -1;
    }
}
