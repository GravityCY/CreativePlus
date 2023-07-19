package me.gravityio.creativeplus.lib.helper;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class VecHelper {

    /**
     * Gets a position (offset param) relative to another position and it's rotation<br><br>
     * 
     * Yoinked this from {@link net.minecraft.command.argument.LookingPosArgument#toAbsolutePos LookingPosArgument#toAbsolutePos}
     */
    public static Vec3d toAbsolutePos(Vec3d position, Vec2f rotation, Vec3d offset) {
        float f = MathHelper.cos((rotation.y + 90.0f) * ((float)Math.PI / 180));
        float g = MathHelper.sin((rotation.y + 90.0f) * ((float)Math.PI / 180));
        float h = MathHelper.cos(-rotation.x * ((float)Math.PI / 180));
        float i = MathHelper.sin(-rotation.x * ((float)Math.PI / 180));
        float j = MathHelper.cos((-rotation.x + 90.0f) * ((float)Math.PI / 180));
        float k = MathHelper.sin((-rotation.x + 90.0f) * ((float)Math.PI / 180));
        Vec3d vec3d2 = new Vec3d(f * h, i, g * h);
        Vec3d vec3d3 = new Vec3d(f * j, k, g * j);
        Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0);
        double d = vec3d2.x * offset.z + vec3d3.x * offset.y + vec3d4.x * offset.x;
        double e = vec3d2.y * offset.z + vec3d3.y * offset.y + vec3d4.y * offset.x;
        double l = vec3d2.z * offset.z + vec3d3.z * offset.y + vec3d4.z * offset.x;
        return new Vec3d(position.x + d, position.y + e, position.z + l);
    }


    /**
     * Gets a rotation to essentially look at the other position
     * Yoinked this from {@link net.minecraft.command.argument.LookingPosArgument#toAbsolutePos LookingPosArgument#toAbsolutePos}
     */
    public static Vec2f toLookAt(Vec3d position, Vec3d other) {
        double d = position.x - other.x;
        double e = position.y - other.y;
        double f = position.z - other.z;
        double g = Math.sqrt(d * d + f * f);
        float h = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
        float i = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);
        return new Vec2f(h, i);
    }

}
