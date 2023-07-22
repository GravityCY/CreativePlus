package me.gravityio.creativeplus.mixin.mod;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.gravityio.creativeplus.CreativeConfig;
import me.gravityio.creativeplus.CreativePlus;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class DisplayEntityMixins {

    @Mixin(Entity.class)
    public static abstract class BoundingBoxMixin {
        @Shadow public World world;
        @Shadow public abstract Vec3d getPos();
        @Shadow public abstract BlockPos getBlockPos();

        @ModifyReturnValue(method = "calculateBoundingBox", at = @At("RETURN"))
        private Box getDisplayBoundingBox(Box original) {
            Entity self = (Entity) (Object) this;
            if (!this.world.isClient || !(self instanceof DisplayEntity display)) return original;
            Vec3d pos = this.getPos();
            Vector3f scale = display.getDataTracker().get(DisplayEntity.SCALE).absolute();

            if (display instanceof DisplayEntity.BlockDisplayEntity blockDisplay) {
                BlockState state = blockDisplay.getBlockState();
                if (state == null || state.isAir()) return Box.of(pos, 0.5f * scale.x, 0.5f * scale.y, 0.5f * scale.z);
                VoxelShape shape = state.getOutlineShape(this.world, this.getBlockPos());
                double xDiff = Math.abs(shape.getMax(Direction.Axis.X) - shape.getMin(Direction.Axis.X));
                double yDiff = Math.abs(shape.getMax(Direction.Axis.Y) - shape.getMin(Direction.Axis.Y));
                double zDiff = Math.abs(shape.getMax(Direction.Axis.Z) - shape.getMin(Direction.Axis.Z));
                double xSize = Math.max(xDiff * scale.x, 0.1f);
                double ySize = Math.max(yDiff * scale.y, 0.1f);
                double zSize = Math.max(zDiff * scale.z, 0.1f);
                Vec3d boxPos = pos.add(xSize * 0.5f, ySize *0.5f, zSize * 0.5f);
                return Box.of(boxPos, xSize, ySize, zSize);
            } else if (display instanceof DisplayEntity.ItemDisplayEntity) {;
                return Box.of(pos, 0.5f * scale.x, 0.5f * scale.y, 0.5f * scale.z);
            } else if (display instanceof DisplayEntity.TextDisplayEntity textDisplay) {
                int width = CreativePlus.client.textRenderer.getWidth(textDisplay.getText().getString());
                return Box.of(pos, 0.025f * width * scale.x, 0.5f * scale.y, 0.25f * scale.z);
            }
            return original;
        }
    }

    /**
     * Makes it so that the width and height of
     * the display entity is proportionate to the display entity's content
     */
    @Mixin(Entity.class)
    public static abstract class WidthHeightMixin {
        @Shadow public abstract Vec3d getPos();

        @Shadow public World world;

        @Shadow public abstract BlockPos getBlockPos();

        @ModifyReturnValue(method = {"getWidth"}, at = @At("RETURN"))
        private float getDisplayWidth(float original) {
            Entity self = (Entity) (Object) this;
            if (!(self instanceof DisplayEntity display)) return original;
            Vector3f scale = display.getDataTracker().get(DisplayEntity.SCALE).absolute();

            if (display instanceof DisplayEntity.BlockDisplayEntity blockDisplay) {
                BlockState state = blockDisplay.getBlockState();
                if (state == null || state.isAir()) return 0.5f;
                VoxelShape shape = state.getOutlineShape(this.world, this.getBlockPos());
                float xDiff = (float) Math.abs(shape.getMax(Direction.Axis.X) - shape.getMin(Direction.Axis.X));
                float zDiff = (float) Math.abs(shape.getMax(Direction.Axis.Z) - shape.getMin(Direction.Axis.Z));
                float xSize = Math.max(xDiff * scale.x * 2f, 0.1f);
                float zSize = Math.max(zDiff * scale.z * 2f, 0.1f);
                return Math.max(xSize, zSize);
            } else if (display instanceof DisplayEntity.ItemDisplayEntity) {;
                return scale.x * 1.5f;
            } else if (display instanceof DisplayEntity.TextDisplayEntity textDisplay) {
                int width = CreativePlus.client.textRenderer.getWidth(textDisplay.getText().getString());
                return 0.025f * width * scale.x;
            }
            return original;
        }

        @ModifyReturnValue(method = "getHeight", at = @At("RETURN"))
        private float getDisplayHeight(float original) {
            Entity self = (Entity) (Object) this;
            if (!(self instanceof DisplayEntity display)) return original;
            Vector3f scale = display.getDataTracker().get(DisplayEntity.SCALE).absolute();

            if (display instanceof DisplayEntity.BlockDisplayEntity blockDisplay) {
                BlockState state = blockDisplay.getBlockState();
                if (state == null || state.isAir()) return 0.5f;
                VoxelShape shape = state.getOutlineShape(this.world, this.getBlockPos());
                float yDiff = (float) Math.abs(shape.getMax(Direction.Axis.Y) - shape.getMin(Direction.Axis.Y));
                return Math.max(yDiff * scale.y, 0.1f);
            } else if (display instanceof DisplayEntity.ItemDisplayEntity) {;
                return scale.y * 0.5f;
            } else if (display instanceof DisplayEntity.TextDisplayEntity) {
                return 0.025f * 9 * scale.x;
            }
            return original;
        }
    }

    /**
     * Renders the display entity's hitbox with lower alpha and no rotation line if the config is toggled on
     */
    @Mixin(EntityRenderDispatcher.class)
    public static class RenderHitboxMixin {
        @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderHitboxes:Z"))
        private <T extends Entity> boolean alwaysRenderDisplayEntityHitbox(boolean original, T entity) {
            if (entity instanceof DisplayEntity && CreativeConfig.SHOW_DISPLAY_ENTITY_HITBOX) {
                return true;
            }
            return original;
        }

//        @ModifyConstant(method = "renderHitbox", constant = @Constant(intValue = 255))
//        private static int dontRenderRotationLineIfConfig(int alpha, MatrixStack matrices, VertexConsumer vertices, Entity entity) {
//            if (!(entity instanceof DisplayEntity) || !CreativeConfig.SHOW_DISPLAY_ENTITY_HITBOX) return alpha;
//            return 0;
//        }

        @Inject(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;vertex(Lorg/joml/Matrix4f;FFF)Lnet/minecraft/client/render/VertexConsumer;", ordinal = 0), cancellable = true)
        private static void dontRenderRotationLine(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
            if (!(entity instanceof DisplayEntity) || !CreativeConfig.SHOW_DISPLAY_ENTITY_HITBOX) return;
            ci.cancel();
        }

        @ModifyConstant(
                method = "renderHitbox",
                constant = @Constant(floatValue = 1.0f, ordinal = 3)
        )
        private static float renderHitboxWithLowerAlpha(float alpha, MatrixStack matrices, VertexConsumer vertices, Entity entity) {
            if (!(entity instanceof DisplayEntity) || !CreativeConfig.SHOW_DISPLAY_ENTITY_HITBOX) return alpha;
            return 0.25f;
        }


    }
}
