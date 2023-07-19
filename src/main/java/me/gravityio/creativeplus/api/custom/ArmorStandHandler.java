package me.gravityio.creativeplus.api.custom;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.helper.EntityNbtHelper;
import me.gravityio.creativeplus.lib.helper.Helper;
import me.gravityio.creativeplus.lib.helper.VecHelper;
import me.gravityio.creativeplus.lib.idk.MutableEulerAngle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;


public class ArmorStandHandler extends CustomHandler {

    private final ArmorStandEntity armorStand;
    private final MinecraftClient client;
    private MutableEulerAngle headRot;
    private MutableEulerAngle bodyRot;
    private MutableEulerAngle leftArmRot;
    private MutableEulerAngle rightArmRot;
    private MutableEulerAngle leftLegRot;
    private MutableEulerAngle rightLegRot;
    private MutableEulerAngle current;
    private MutableEulerAngle beforeAxis;
    private Box headBox;
    private Box bodyBox;
    private Box rightArmBox;
    private Box leftArmBox;
    private Box rightLegBox;
    private Box leftLegBox;
    private Vector3f axis;
    private boolean isRotating = false;
    private boolean hasAxisSelected = false;
    private boolean hasLimbSelected = false;

    private Box getBox(Vec3d vec) {
       return getBox(vec, 0.25, 0.25, 0.25);
    }

    private Box getBox(Vec3d vec, double x) {
        return getBox(vec, x, x, x);
    }

    private Box getBox(Vec3d vec, double x, double y, double z) {
        return new Box(vec.x - x, vec.y - y, vec.z - z, vec.x + x, vec.y + y, vec.z + z);
    }

    public ArmorStandHandler(MinecraftClient client, Entity entity) {
        this.client = client;
        this.armorStand = (ArmorStandEntity) entity;
    }

    @Override
    public void onActivated() {
        CreativePlus.LOGGER.debug("[ArmorStandHandler] onActivated");

        Vec3d pos = this.armorStand.getPos();
        Vec2f temp = this.armorStand.getRotationClient();
        Vec2f rot = new Vec2f(0, temp.y);

        this.headRot = MutableEulerAngle.of(this.armorStand.getHeadRotation());
        this.bodyRot = MutableEulerAngle.of(this.armorStand.getBodyRotation());
        this.leftArmRot = MutableEulerAngle.of(this.armorStand.getLeftArmRotation());
        this.rightArmRot = MutableEulerAngle.of(this.armorStand.getRightArmRotation());
        this.leftLegRot = MutableEulerAngle.of(this.armorStand.getLeftLegRotation());
        this.rightLegRot = MutableEulerAngle.of(this.armorStand.getRightLegRotation());

        var height = this.armorStand.getHeight();
        var width = this.armorStand.getWidth();
        var boxSize = height * 0.125;

        Vec3d headPos = VecHelper.toAbsolutePos(pos, rot, new Vec3d(0, height, 0.1));
        Vec3d bodyPos = VecHelper.toAbsolutePos(pos, rot, new Vec3d(0, height * 0.6, 0.1));
        Vec3d leftArmPos = VecHelper.toAbsolutePos(pos, rot, new Vec3d(width * 0.8, height * 0.75, 0.1));
        Vec3d rightArmPos = VecHelper.toAbsolutePos(pos, rot, new Vec3d(-(width * 0.8), height * 0.75, 0.1));
        Vec3d leftLegPos = VecHelper.toAbsolutePos(pos, rot, new Vec3d((width * 0.35), height * 0.25, 0.1));
        Vec3d rightLegPos = VecHelper.toAbsolutePos(pos, rot, new Vec3d(-(width * 0.35),height* 0.25, 0.1));

        this.headBox = getBox(headPos, boxSize);
        this.bodyBox = getBox(bodyPos, boxSize);
        this.leftArmBox = getBox(leftArmPos, boxSize);
        this.rightArmBox = getBox(rightArmPos, boxSize);
        this.leftLegBox = getBox(leftLegPos, boxSize);
        this.rightLegBox = getBox(rightLegPos, boxSize);
    }

    @Override
    public void tick() {
        Helper.addParticle(this.client.world, ParticleTypes.COMPOSTER, this.headBox.getCenter());
        Helper.addParticle(this.client.world, ParticleTypes.COMPOSTER, this.bodyBox.getCenter());
        Helper.addParticle(this.client.world, ParticleTypes.COMPOSTER, this.leftArmBox.getCenter());
        Helper.addParticle(this.client.world, ParticleTypes.COMPOSTER, this.rightArmBox.getCenter());
        Helper.addParticle(this.client.world, ParticleTypes.COMPOSTER, this.leftLegBox.getCenter());
        Helper.addParticle(this.client.world, ParticleTypes.COMPOSTER, this.rightLegBox.getCenter());

        this.armorStand.setHeadRotation(this.headRot.toEulerAngle());
        this.armorStand.setBodyRotation(this.bodyRot.toEulerAngle());
        this.armorStand.setLeftArmRotation(this.leftArmRot.toEulerAngle());
        this.armorStand.setRightArmRotation(this.rightArmRot.toEulerAngle());
        this.armorStand.setLeftLegRotation(this.leftLegRot.toEulerAngle());
        this.armorStand.setRightLegRotation(this.rightLegRot.toEulerAngle());
    }

    @Override
    public ActionResult onMouseDelta(long window, double dx, double dy) {
        var d = dx - dy;
        if (this.isRotating) {
            if (d > 0)
                this.current.add(this.axis.x, this.axis.y, this.axis.z);
            if (d < 0)
                this.current.add(-this.axis.x, -this.axis.y, -this.axis.z);

        } else {
            return ActionResult.PASS;
        }

        return ActionResult.FAIL;
    }

    @Override
    public ActionResult onMouseButton(long window, int button, int action, int mods) {
        if (window != this.client.getWindow().getHandle()) return ActionResult.PASS;
        if (action != GLFW.GLFW_PRESS) return ActionResult.PASS;

        if (this.hasAxisSelected) {
            this.onFinishAxis();
            return ActionResult.FAIL;
        }

        if (Helper.isLookingAt(this.headBox, this.client.player)) {
            this.client.player.sendMessage(Text.literal("Selected Head"), true);
            this.setCurrent(this.headRot);

        } else if (Helper.isLookingAt(this.bodyBox, this.client.player)) {
            this.client.player.sendMessage(Text.literal("Selected Body"), true);
            this.setCurrent(this.bodyRot);

        } else if (Helper.isLookingAt(this.leftArmBox, this.client.player)) {
            this.client.player.sendMessage(Text.literal("Selected Left Hand"), true);
            this.setCurrent(this.leftArmRot);

        } else if (Helper.isLookingAt(this.rightArmBox, this.client.player)) {
            this.client.player.sendMessage(Text.literal("Selected Right Hand"), true);
            this.setCurrent(this.rightArmRot);

        } else if (Helper.isLookingAt(this.leftLegBox, this.client.player)) {
            this.client.player.sendMessage(Text.literal("Selected Left Leg"), true);
            this.setCurrent(this.leftLegRot);

        } else if (Helper.isLookingAt(this.rightLegBox, this.client.player)) {
            this.client.player.sendMessage(Text.literal("Selected Right Leg"), true);
            this.setCurrent(this.rightLegRot);

        } else {
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;

    }

    @Override
    public ActionResult onKey(long window, int key, int scancode, int action, int mods) {
        if (window != this.client.getWindow().getHandle()) return ActionResult.PASS;
        if (action != GLFW.GLFW_PRESS) return ActionResult.PASS;

        if (key == GLFW.GLFW_KEY_B) {
            this.complete();
        }

        if (!this.hasLimbSelected) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                this.cancel();
            } else {
                return ActionResult.PASS;
            }

        } else {
            if (key == GLFW.GLFW_KEY_X) {
                this.setAxis(new Vector3f(1, 0, 0));
                this.client.player.sendMessage(Text.literal("Select X Axis"), true);

            } else if (key == GLFW.GLFW_KEY_Y) {
                this.setAxis(new Vector3f(0, 1, 0));
                this.client.player.sendMessage(Text.literal("Select Y Axis"), true);

            } else if (key == GLFW.GLFW_KEY_Z) {
                this.setAxis(new Vector3f(0, 0, 1));
                this.client.player.sendMessage(Text.literal("Select Z Axis"), true);

            } else if (key == GLFW.GLFW_KEY_ESCAPE) {
                if (this.hasAxisSelected) {
                    this.onCancelAxis();
                } else {
                    this.setCurrent(null);
                    this.client.player.sendMessage(Text.literal("Deselected Limb"), true);
                }
                return ActionResult.FAIL;

            } else {
                return ActionResult.PASS;
            }
            this.beforeAxis = this.current.copy();

        }

        return ActionResult.FAIL;
    }

    @Override
    public void transform(NbtCompound nbt) {
        var a = headRot.toEulerAngle();
        var b = bodyRot.toEulerAngle();
        var c = leftArmRot.toEulerAngle();
        var d = rightArmRot.toEulerAngle();
        var e = leftLegRot.toEulerAngle();
        var f = rightLegRot.toEulerAngle();
        NbtCompound pose = EntityNbtHelper.ArmorStand.mergePose(new NbtCompound(), a, b, c, d, e, f);
        nbt.put("Pose", pose);
    }

    @Override
    public boolean isAdditive() {
        return false;
    }

    public void onDeselectAxis() {
        this.setAxis(null);
        this.client.player.sendMessage(Text.literal("Deselected Axis"), true);
    }

    public void onFinishAxis() {
        this.onDeselectAxis();
    }

    public void onCancelAxis() {
        this.current.set(this.beforeAxis);
        this.onDeselectAxis();
    }

    private void setAxis(Vector3f axis) {
        this.axis = axis;
        this.hasAxisSelected = axis != null;
        this.isRotating = this.hasAxisSelected && this.hasLimbSelected;
    }

    private void setCurrent(MutableEulerAngle current) {
        this.current = current;
        this.hasLimbSelected = this.current != null;
        if (!this.hasLimbSelected)
            this.isRotating = false;
    }

}
