package me.gravityio.creativeplus.api.custom;

import me.gravityio.creativeplus.CreativePlus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class DisplayEntityHandler extends CustomHandler {

    private final MinecraftClient client;
    private final DisplayEntity display;
    private Vector3f scaleBefore;
    private Vector3f scale;
    private Vector3f rotationBefore;
    private Vector3f rotation;
    private Vector3f rotAxis;

    private Mode mode = Mode.NONE;
    private boolean isScaling = false;
    private boolean isRotating = false;

    public DisplayEntityHandler(MinecraftClient client, DisplayEntity display) {
        this.client = client;
        this.display = display;
        this.scale = this.getScale();
        this.rotation = this.getRotation();
        this.display.startInterpolationOnNextTick = true;
    }

    private Vector3f getScale() {
        return this.display.getDataTracker().get(DisplayEntity.SCALE);
    }

    private Vector3f getRotation() {
        return this.getRotationQ().getEulerAnglesXYZ(new Vector3f());
    }

    private Quaternionf getRotationQ() {
        return this.display.getDataTracker().get(DisplayEntity.LEFT_ROTATION);
    }

    private void addScale(float a) {
        this.addScale(a, a, a);
    }

    private void addScale(float x, float y, float z) {
        this.setScale(this.scale.add(x, y, z));
    }

    private void addRotation(float x, float y, float z) {
        this.setRotation(this.rotation.add(x, y, z, new Vector3f()));
    }

    private void setRotation(Vector3f vec) {
        Vector3f minMax = new Vector3f((float) Math.PI, (float) Math.PI, (float) Math.PI);
        vec.min(minMax);
        vec.max(minMax.negate());
        var q = new Quaternionf().rotateXYZ(vec.x, vec.y, vec.z);
        CreativePlus.LOGGER.debug("Setting Rotation to {}", vec);
        CreativePlus.LOGGER.debug("Setting Quaternion to {}", q);
        this.rotation = vec;
        this.display.getDataTracker().set(DisplayEntity.LEFT_ROTATION, q);
        this.display.startInterpolationOnNextTick = true;
    }

    private void setScale(Vector3f vec) {
        this.scale = vec.max(new Vector3f(0.1f, 0.1f, 0.1f));
        this.display.getDataTracker().set(DisplayEntity.SCALE, this.scale);
        this.display.startInterpolationOnNextTick = true;
    }

    private void setMode(Mode mode) {
        this.mode = mode;
    }

    private void onExitCurrent() {
        if (this.mode == Mode.ROTATE)
            this.onExitRotateMode();
        if (this.mode == Mode.SCALE)
            this.onExitScale();
    }

    private void onExitScale() {
        if (this.scaleBefore != null)
            this.setScale(this.scaleBefore);
        this.setMode(Mode.NONE);
        this.isScaling = false;
        this.scaleBefore = null;
    }

    private void onExitRotateAxis() {
        if (this.rotationBefore != null) {
            this.setRotation(new Vector3f(this.rotationBefore));
        }
        this.setRotAxis(null);
    }

    private void onExitRotateMode() {
        this.setMode(Mode.NONE);
        if (this.rotAxis != null)
            this.onExitRotateAxis();
    }

    private void onScaleComplete() {
        this.setMode(Mode.NONE);
        this.isScaling = false;
        this.scaleBefore = null;
        this.complete();
    }

    private void setRotationBefore(Vector3f vec) {
        CreativePlus.LOGGER.debug("Setting Previous Rotation to: {}", vec);
        this.rotationBefore = vec;
    }

    private void onRotateComplete() {
        this.setRotAxis(null);
        this.setRotationBefore(new Vector3f(this.rotation));
        this.complete();
    }

    private void setRotAxis(@Nullable Vector3f axis) {
        this.rotAxis = axis;
        this.isRotating = this.rotAxis != null;
    }

    private boolean onRotateKey(int key, int action, int mods) {
        if (key == GLFW.GLFW_KEY_X) {
            client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.axis_x"), true);
            this.setRotAxis(new Vector3f(1, 0, 0));
        } else if (key == GLFW.GLFW_KEY_Y) {
            client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.axis_y"), true);
            this.setRotAxis(new Vector3f(0, 1, 0));
        } else if (key == GLFW.GLFW_KEY_Z) {
            client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.axis_z"), true);
            this.setRotAxis(new Vector3f(0, 0, 1));
        } else {
            return false;
        }
        return true;
    }

    @Override
    public ActionResult onKey(long window, int key, int scancode, int action, int mods) {
        if (action != GLFW.GLFW_PRESS) return ActionResult.PASS;

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (this.isScaling || this.isRotating || this.mode == Mode.ROTATE) {
                if (this.isScaling) {
                    client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.exit_scale"), true);
                    this.onExitScale();
                } else if (this.isRotating) {
                    client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.deselect_axis"), true);
                    this.onExitRotateAxis();
                } else {
                    client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.exit_rotate"), true);
                    this.onExitRotateMode();
                }
                return ActionResult.FAIL;
            }
        }

        if (this.mode == Mode.ROTATE) {
            var b = this.onRotateKey(key, action, mods);
            if (b) return ActionResult.FAIL;
        }

        boolean shiftDown = (mods & GLFW.GLFW_MOD_SHIFT) != 0;
        if (key == GLFW.GLFW_KEY_S && shiftDown) {
            if (this.mode != Mode.NONE) this.onExitCurrent();

            client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.start_scale", this.display.getName()), true);
            this.setMode(Mode.SCALE);
            this.scaleBefore = new Vector3f(this.scale);
            this.isScaling = true;
        } else if (key == GLFW.GLFW_KEY_R && shiftDown) {
            if (this.mode == Mode.ROTATE) {
                client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.reset_rotate", this.display.getName()), true);
                this.setRotation(new Vector3f(0.0f, 0.0f, 0.0f));
                this.onRotateComplete();
            } else if (this.mode != Mode.NONE) {
                this.onExitCurrent();
            } else {
                this.setRotationBefore(new Vector3f(this.rotation));
                client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.start_rotate", this.display.getName()), true);
                this.setMode(Mode.ROTATE);
            }
        } else {
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public ActionResult onMouseButton(long window, int button, int action, int mods) {
        if (this.isScaling) {
            client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.complete_scale"), true);
            this.onScaleComplete();
        } else if (this.isRotating) {
            client.player.sendMessage(Text.translatable("message.creativeplus.display_handler.complete_rotate"), true);
            this.onRotateComplete();
        } else {
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public ActionResult onMouseDelta(long window, double nx, double ny) {
        float d = (float) -nx * 0.01f;
        if (this.isScaling) {
            this.addScale(d);
        } else if (this.isRotating) {
            this.addRotation(this.rotAxis.x * d, this.rotAxis.y * d, this.rotAxis.z * d);
        } else {
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public void transform(NbtCompound nbt) {
        Codecs.MATRIX4F.xmap(AffineTransformation::new, AffineTransformation::getMatrix).encodeStart(NbtOps.INSTANCE, DisplayEntity.getTransformation(this.display.getDataTracker())).result().ifPresent(transformations -> nbt.put(DisplayEntity.TRANSFORMATION_NBT_KEY, transformations));
    }

    @Override
    public boolean isAdditive() {
        return !this.isScaling;
    }

    @Override
    public boolean isMerged() {
        return true;
    }

    @Override
    public void onActivated() {

    }

    enum Mode {
        NONE,
        SCALE,
        ROTATE
    }
}
