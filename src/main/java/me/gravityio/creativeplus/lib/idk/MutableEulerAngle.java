package me.gravityio.creativeplus.lib.idk;

import net.minecraft.util.math.EulerAngle;

public class MutableEulerAngle {
    protected float pitch;
    protected float yaw;
    protected float roll;

    public MutableEulerAngle(float pitch, float yaw, float roll) {
        this.pitch = Float.isInfinite(pitch) || Float.isNaN(pitch) ? 0.0f : pitch % 360.0f;
        this.yaw = Float.isInfinite(yaw) || Float.isNaN(yaw) ? 0.0f : yaw % 360.0f;
        this.roll = Float.isInfinite(roll) || Float.isNaN(roll) ? 0.0f : roll % 360.0f;
    }

    public EulerAngle toEulerAngle() {
        return MutableEulerAngle.toEulerAngle(this);
    }

    public void add(float pitch, float yaw, float roll) {
        this.set(
                this.pitch + pitch,
                this.yaw + yaw,
                this.roll + roll
        );

    }

    public void set(float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public void set(MutableEulerAngle other) {
        this.set(
                other.pitch,
                other.yaw,
                other.roll
        );

    }

    public MutableEulerAngle copy() {
        return new MutableEulerAngle(pitch, yaw, roll);
    }

    public static EulerAngle toEulerAngle(MutableEulerAngle mutable) {
        return new EulerAngle(mutable.pitch, mutable.yaw, mutable.roll);
    }

    public static MutableEulerAngle of(EulerAngle angle) {
        return new MutableEulerAngle(angle.getPitch(), angle.getYaw(), angle.getRoll());
    }
}
