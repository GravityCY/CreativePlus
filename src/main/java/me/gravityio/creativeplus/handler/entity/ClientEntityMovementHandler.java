package me.gravityio.creativeplus.handler.entity;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.AngleHelper;
import me.gravityio.creativeplus.lib.Helper;
import me.gravityio.creativeplus.lib.VecHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

/**
 * Moves an entity on the client<br><br>
 * Tries to position an entity in the direction of the player<br><br>
 * Listens to key events for locking, placing on the floor, looking at the player, rotating etc. <br><br>
 * Key's are hardcoded, eventually maybe I'll make a system for them or use minecraft's keybinds somehow<br><br>
 *
 * Doesn't have any cancelling logic, that is supposed to be implemented by whoever is using this
 */
public class ClientEntityMovementHandler implements ClientInputListener {
    public static boolean inUse = false;
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
    protected static final double MIN_DISTANCE = 1.5;

    protected final MinecraftClient client;
    public Entity entity;
    protected float entityHeight;
    protected Vec3d posOffset = OFFSET.add(Vec3d.ZERO);
    protected float entityYaw = 0;
    protected boolean isLocked = false;

    public ClientEntityMovementHandler(MinecraftClient client, @Nullable Entity entity) {
        this.client = client;
        if (entity != null)
            this.setEntity(entity);
    }

    /**
     * <ul>
     *     <li>Ticks Entity Transforms</li>
     *     <li>Ticks Keys</li>
     * </ul>
     */
    public void tick() {
        updateEntityTransforms();
        onTickKeys();
    }

    public ActionResult onMouseScroll(long window, double horizontal, double vertical) {
        if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
        if (this.entity == null || this.client.currentScreen != null) return ActionResult.PASS;

        if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL))
            vertical *= FAST_MOD;

        if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT)) {
            entityYaw += vertical * SCROLL_MOD_ROT;
        } else {
            if (isLocked) {
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
                Vec3d newOffset = posOffset.add(0, 0, vertical * SCROLL_MOD_POS);
                if (newOffset.z >= MIN_DISTANCE)
                    posOffset = newOffset;
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
    public ActionResult onKey(long window, int key, int scancode, int action, int mods) {
        if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
        if (this.entity == null || this.client.currentScreen != null) return ActionResult.PASS;

        if (key == GLFW.GLFW_KEY_LEFT_ALT && action == GLFW.GLFW_PRESS) {
            setLocked(!isLocked);
        } else if (key == GLFW.GLFW_KEY_F) {
            doFacePlayer();
        } else if (key == GLFW.GLFW_KEY_T) {
            doPlaceGround();
        } else {
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    /**
     * Positions entity at the block the player is looking at
     */
    public void doPlaceGround() {
        var hit = Helper.raycast(client, 20, false);
        if (hit == null) return;
        this.entity.setPosition(hit.getPos());
        this.client.player.sendMessage(Text.translatable("message.creativeplus.movement.place_ground"), true);
        this.isLocked = true;
    }

    /**
     * Rotates entity to face the player
     */
    public void doFacePlayer() {
        this.client.player.sendMessage(Text.translatable("message.creativeplus.movement.face_player"), true);
        Vec2f newRot = VecHelper.toLookAt(this.client.player.getEyePos(), this.entity.getEyePos());
        this.entity.setPitch(newRot.x);
        this.entityYaw = newRot.y;

//        this.entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, this.client.player.getEyePos());
    }

    /**
     * Locks the entity from being positioned in front of the player
     */
    public void setLocked(boolean isLocked) {
        if (isLocked) {
            this.client.player.sendMessage(Text.translatable("message.creativeplus.movement.locked"), true);
        }
        this.isLocked = isLocked;
    }

    /**
     * Sets the current entity to try to position
     */
    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
        if (entity != null) {
            this.entityHeight = entity.getDimensions(entity.getPose()).height;
            ClientEntityPlacementHandler.inUse = true;
        } else {
            ClientEntityPlacementHandler.inUse = false;
        }
    }

    public void reset() {
        this.entity = null;
        this.entityHeight = 0;
        this.entityYaw = 0;
        this.isLocked = false;
        this.posOffset = OFFSET.add(Vec3d.ZERO);
        ClientEntityPlacementHandler.inUse = false;
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
            if (isLocked) {
                float yaw = AngleHelper.rotateAngle(AngleHelper.snapHorizontalCardinals(rot.y), 90);
                this.entity.setPosition(VecHelper.toAbsolutePos(this.entity.getPos(), new Vec2f(0, yaw), new Vec3d(0, 0, nudge)));
            } else {
                posOffset = posOffset.add(nudge, 0, 0);
            }
        }

        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT)) {
            if (isLocked) {
                float yaw = AngleHelper.rotateAngle(AngleHelper.snapHorizontalCardinals(rot.y), -90);
                this.entity.setPosition(VecHelper.toAbsolutePos(this.entity.getPos(), new Vec2f(0, yaw), new Vec3d(0, 0, nudge)));
            } else {
                posOffset = posOffset.add(-nudge, 0, 0);
            }
        }

        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_UP)) {
            if (isLocked) {
                if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    entity.setPosition(entity.getPos().add(0, nudge, 0));
                } else {
                    float yaw = AngleHelper.snapHorizontalCardinals(rot.y);
                    this.entity.setPosition(VecHelper.toAbsolutePos(this.entity.getPos(), new Vec2f(0, yaw), new Vec3d(0, 0, nudge)));
                }
            } else {
                posOffset = posOffset.add(0, nudge, 0);
            }
        }

        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_DOWN)) {
            if (isLocked) {
                if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    entity.setPosition(entity.getPos().add(0, -nudge, 0));
                } else {
                    float yaw = AngleHelper.rotateAngle(AngleHelper.snapHorizontalCardinals(rot.y), 180);
                    this.entity.setPosition(VecHelper.toAbsolutePos(this.entity.getPos(), new Vec2f(0, yaw), new Vec3d(0, 0, nudge)));
                }
            } else {
                posOffset = posOffset.add(0, -nudge, 0);
            }
        }
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
        if (entity == null || isLocked) return;
        Vec3d p = this.client.player.getEyePos();
        Vec2f r = this.client.player.getRotationClient();
        entity.setPosition(VecHelper.toAbsolutePos(p, r, posOffset).add(0, -(entityHeight / 2), 0));
    }

    /**
     * Sets the current entity's yaw from the field
     */
    protected void updateRotation() {
        if (entity == null) return;
        entity.setBodyYaw(entityYaw);
        entity.setHeadYaw(entityYaw);
        entity.setYaw(entityYaw);
    }

}
