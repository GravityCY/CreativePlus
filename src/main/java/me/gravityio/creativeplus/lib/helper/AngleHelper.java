package me.gravityio.creativeplus.lib.helper;

import net.minecraft.util.math.Vec2f;

/**
 * Helps with Angles
 */
public class AngleHelper {

  public static boolean isFacingUpwards(float value) {
    return value < -45 || value > 45;
  }

  public static float snapHorizontalCardinals(float value) {
    if (value >= -45 && value < 45)
      return 0;
    if (value >= 45 && value < 135)
      return 90;
    if (value >= 135 && value < 180 || value >= -180 && value < -135)
      return 180;
    if (value >= -135 && value < -45)
      return -90;
    return value;
  }

  public static float snapVerticalCardinals(float value) {
    if (value < -45)
      return -90;
    if (value > 45)
      return 90;
    return value;
  }

  public static float rotateAngle(float angle, float amount) {
    angle -= amount;
    if (angle < -180)
      angle += 360;
    if (angle > 180)
      angle -= 360;
    return angle;
  }

  public static Vec2f toNormalFuckingRotation(Vec2f rotation) {
    return new Vec2f(toNormalFuckingAngle(rotation.x), toNormalFuckingAngle(rotation.y));
  }

  public static float toNormalFuckingAngle(float angle) {
    float n = angle % 360;

    if (n > 0 && angle < 0) {
      n -= 360;
    }
    if (n < 180)
      n += 360;
    if (n > 180)
      n -= 360;
    return n;
  }

}
