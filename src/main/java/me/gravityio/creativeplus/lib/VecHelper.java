package me.gravityio.creativeplus.lib;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class VecHelper {

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

}
