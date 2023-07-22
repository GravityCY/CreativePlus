package me.gravityio.creativeplus.api.nbt.frame;

import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.AffineTransformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * {@link net.minecraft.entity.decoration.DisplayEntity#writeCustomDataToNbt}
 */
public interface DisplayFrame {
    AffineTransformation getTransformations();
    Vector3f getTranslation();
    Vector3f getScale();
    Quaternionf getLeftRotation();
    Quaternionf getRightRotation();
    DisplayEntity.BillboardMode getBillboardMode();
    int getInterpolationDuration();
    float getViewRange();
    float getShadowRadius();
    float getShadowStrength();
    float getWidth();
    float getHeight();
    int getGlowColor();
    Brightness getBrightness();

    void setTransformations(AffineTransformation transformations);
    void setTranslation(Vector3f translation);
    void setScale(Vector3f scale);
    void setLeftRotation(Quaternionf leftRotation);
    void setRightRotation(Quaternionf rightRotation);
    void setBillboardMode(DisplayEntity.BillboardMode billboardMode);
    void setInterpolationDuration(int interpolationDuration);
    void setViewRange(float viewRange);
    void setShadowRadius(float shadowRadius);
    void setShadowStrength(float shadowStrength);
    void setWidth(float width);
    void setHeight(float height);
    void setGlowColor(int glowColor);
    void setBrightness(Brightness brightness);
}
