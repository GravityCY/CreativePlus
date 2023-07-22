package me.gravityio.creativeplus.api.placement;

import me.gravityio.creativeplus.lib.helper.AngleHelper;
import me.gravityio.creativeplus.lib.helper.EntityHelper;
import me.gravityio.creativeplus.lib.helper.Helper;
import me.gravityio.creativeplus.lib.helper.VecHelper;
import me.gravityio.creativeplus.lib.idk.ClientInputListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class EntityMovementHandler implements ClientInputListener {


    // The initial offset entities will spawn at relative to the player
    protected static final Vec3d OFFSET = new Vec3d(0, 0, 2);

    // Multiplies any of the base values by this amount
    protected static final double FAST_MOD = 3;
    // A Modifier for the GLFW scroll values for when scrolling the position
    protected static final double SCROLL_MOD_POS = 0.05d;
    // A Modifier for the GLFW scroll values for when scrolling the rotation
    protected static final double SCROLL_MOD_ROT = 2d;
    // The amount to nudge for every key event
    protected static final double NUDGE = 0.01d;
    // Minimum distance to offset entity relative to player

    protected Entity entity;
    protected final MinecraftClient client;
    protected Vec3d localPosOffset = new Vec3d(OFFSET.x, OFFSET.y, OFFSET.z);
    protected Vec3d globalPosOffset = new Vec3d(0, 0, 0);
    protected float minDistance = 1.5f;
    protected float defaultYaw = 0;
    protected float yawOffset = 0;
    protected boolean locked = false;
    protected boolean active = false;

    public EntityMovementHandler(MinecraftClient client) {
        this(client, null);
    }

    public EntityMovementHandler(MinecraftClient client, @Nullable Entity entity) {
        this.client = client;
        this.setEntity(entity);
    }

    /**
     * <ul>
     *     <li>Ticks Entity Transforms</li>
     *     <li>Ticks Keys</li>
     * </ul>
     */
    @Override
    public void tick() {
        if (!this.active) return;

        updateEntityTransforms();
        onTickKeys();
    }

    @Override
    public ActionResult onMouseDelta(long window, double nx, double ny) {
        return ActionResult.PASS;
    }

    @Override
    public ActionResult onMouseScroll(long window, double horizontal, double vertical) {
        if (!this.active) return ActionResult.PASS;

        if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
        if (this.entity == null || this.client.currentScreen != null) return ActionResult.PASS;

        if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL))
            vertical *= FAST_MOD;

        if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT)) {
            yawOffset += vertical * SCROLL_MOD_ROT;
        } else {
            if (locked) {
                Vec2f rotClient = this.client.player.getRotationClient();
                Vec2f rot = AngleHelper.toNormalFuckingRotation(rotClient);
                float pitch = AngleHelper.snapVerticalCardinals(rot.x);
                float yaw = AngleHelper.snapHorizontalCardinals(rot.y);
                boolean isFacingUp = AngleHelper.isFacingUpwards(rot.x);
                if (isFacingUp) {
                    yaw = 0;
                } else {
                    pitch = 0;
                }
                this.entity.setPosition(VecHelper.toAbsolutePos(this.entity.getPos(), new Vec2f(pitch, yaw), new Vec3d(0, 0, vertical * SCROLL_MOD_POS)));
            } else {
                Vec3d newOffset = this.localPosOffset.add(0, 0, vertical * SCROLL_MOD_POS);
                if (newOffset.z >= this.minDistance) {
                    this.localPosOffset = newOffset;
                } else {
                    this.localPosOffset = new Vec3d(newOffset.x, newOffset.y, this.minDistance);
                }
            }
        }

        return ActionResult.FAIL;
    }

    @Override
    public ActionResult onMouseButton(long window, int button, int action, int mods) {
        return ActionResult.PASS;
    }

    /**
     * Listens to key events and does some actions<br>
     * <ul>
     *     <li>F -> Faces Player</li>
     *     <li>T -> Positions Entity on the Ground</li>
     * </ul>
     *
     */
    @Override
    public ActionResult onKey(long window, int key, int scancode, int action, int mods) {
        if (!this.active) return ActionResult.PASS;

        if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
        if (this.entity == null || this.client.currentScreen != null) return ActionResult.PASS;
        if (action != GLFW.GLFW_PRESS) return ActionResult.PASS;

        if (key == GLFW.GLFW_KEY_LEFT_ALT) {
            setLocked(!locked);
        } else if (key == GLFW.GLFW_KEY_F) {
            doFacePlayer();
        } else if (key == GLFW.GLFW_KEY_T) {
            doPlaceGround();
        } else {
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Locks the entity from being positioned in front of the player
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
        if (this.locked) {
            this.client.player.sendMessage(Text.translatable("message.creativeplus.movement.locked"), true);
        }
    }

    /**
     * Sets the current entity to try to position
     */
    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
        if (this.entity == null) return;
        var size = EntityHelper.getSize(this.entity);
        var entityHeight = this.entity.getHeight();
        this.defaultYaw = this.entity.getYaw();
        this.globalPosOffset = new Vec3d(0, -(entityHeight / 2), 0);
        this.localPosOffset = new Vec3d(0, 0, Math.max(size / 1.5f, 2));
        if (!this.locked) {
            this.alignEntityToPlayer();
        }
    }

    public void reset() {
        this.entity = null;
        this.yawOffset = 0;
        this.locked = false;
        this.active = false;
        this.localPosOffset = OFFSET.add(Vec3d.ZERO);
    }

    /**
     * Nudges the current entity using the arrow keys, in a direction relative to the player's current facing
     */
    private void onTickKeys() {
        if (this.entity == null || this.client.currentScreen != null) return;
        long handle = this.client.getWindow().getHandle();

        double nudge = NUDGE;
        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_CONTROL))
            nudge *= FAST_MOD;

        Vec2f rot = AngleHelper.toNormalFuckingRotation(this.client.player.getRotationClient());

        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT)) {
            if (locked) {
                float yaw = AngleHelper.rotateAngle(AngleHelper.snapHorizontalCardinals(rot.y), 90);
                this.entity.setPosition(VecHelper.toAbsolutePos(this.entity.getPos(), new Vec2f(0, yaw), new Vec3d(0, 0, nudge)));
            } else {
                localPosOffset = localPosOffset.add(nudge, 0, 0);
            }
        }

        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT)) {
            if (locked) {
                float yaw = AngleHelper.rotateAngle(AngleHelper.snapHorizontalCardinals(rot.y), -90);
                this.entity.setPosition(VecHelper.toAbsolutePos(this.entity.getPos(), new Vec2f(0, yaw), new Vec3d(0, 0, nudge)));
            } else {
                localPosOffset = localPosOffset.add(-nudge, 0, 0);
            }
        }

        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_UP)) {
            if (locked) {
                if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    entity.setPosition(entity.getPos().add(0, nudge, 0));
                } else {
                    float yaw = AngleHelper.snapHorizontalCardinals(rot.y);
                    this.entity.setPosition(VecHelper.toAbsolutePos(this.entity.getPos(), new Vec2f(0, yaw), new Vec3d(0, 0, nudge)));
                }
            } else {
                localPosOffset = localPosOffset.add(0, nudge, 0);
            }
        }

        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_DOWN)) {
            if (locked) {
                if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    entity.setPosition(entity.getPos().add(0, -nudge, 0));
                } else {
                    float yaw = AngleHelper.rotateAngle(AngleHelper.snapHorizontalCardinals(rot.y), 180);
                    this.entity.setPosition(VecHelper.toAbsolutePos(this.entity.getPos(), new Vec2f(0, yaw), new Vec3d(0, 0, nudge)));
                }
            } else {
                localPosOffset = localPosOffset.add(0, -nudge, 0);
            }
        }
    }

    /**
     * Positions entity at the block the player is looking at
     */
    public void doPlaceGround() {
        var hit = Helper.raycast(client, 20, false);
        if (hit == null) return;
        this.entity.setPosition(hit.getPos());
        this.client.player.sendMessage(Text.translatable("message.creativeplus.movement.place_ground"), true);
        this.locked = true;
    }

    /**
     * Rotates entity to face the player
     */
    public void doFacePlayer() {
        this.client.player.sendMessage(Text.translatable("message.creativeplus.movement.face_player"), true);
        Vec2f newRot = VecHelper.toLookAt(this.client.player.getEyePos(), this.entity.getEyePos());
        this.entity.setPitch(newRot.x);
        this.yawOffset = newRot.y - this.defaultYaw;

//        this.entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, this.client.player.getEyePos());
    }

    /**
     * Update's the entities position and rotation
     */
    protected void updateEntityTransforms() {
        alignEntityToPlayer();
        updateRotation();
    }

    /**
     * Sets the current entity's position to in front of the player plus the offset
     */
    protected void alignEntityToPlayer() {
        if (this.entity == null || this.locked) return;
        Vec3d p = this.client.player.getEyePos();
        Vec2f r = this.client.player.getRotationClient();
        Vec3d np = VecHelper.toAbsolutePos(p, r, this.localPosOffset).add(this.globalPosOffset);
        this.entity.setPosition(np);
    }

    /**
     * Sets the current entity's yaw from the field
     */
    protected void updateRotation() {
        if (entity == null) return;
        float yaw = defaultYaw + yawOffset;
        entity.setBodyYaw(yaw);
        entity.setHeadYaw(yaw);
        entity.setYaw(yaw);
    }
}
