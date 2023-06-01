package me.gravityio.creativeplus.client;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.Events;
import me.gravityio.creativeplus.lib.VecHelper;
import me.gravityio.creativeplus.client.screen.ArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class ArmorStandScreenHandler {

    private static final Vec3d OFFSET = new Vec3d(0, 0, 2);
    private static final long SPAWN_COOLDOWN = 100;
    private static final double FAST_MOD = 2;
    private static final double SCROLL_MOD_POS = 0.05d;
    private static final double SCROLL_MOD_ROT = 2d;

    private static final double NUDGE = 0.01d;

    private final MinecraftClient client;
    private ArmorStandScreen screen;
    private long clickSpawnTime = System.currentTimeMillis();
    private boolean locked = false;
    private Mode mode = Mode.EXECUTE;
    public ArmorStandEntity entity;
    public Vec3d posOffset = OFFSET.add(Vec3d.ZERO);
    public float yaw = 0;

    public ArmorStandScreenHandler(MinecraftClient client) {
        this.client = client;
        // If in placement mode then intercept mouse scrolling to only the entity's placement
        Events.ON_MOUSE_SCROLLED.register((window, horizontal, vertical) -> {
            if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
            if (this.entity == null || this.client.currentScreen != null) return ActionResult.PASS;

            if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL))
                vertical *= FAST_MOD;

            if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT)) {
                yaw += vertical * SCROLL_MOD_ROT;
            } else {
                posOffset = posOffset.add(0, 0, vertical * SCROLL_MOD_POS);
            }

            return ActionResult.FAIL;
        });
        // If in placement mode then intercept mouse clicks to place the entity
        Events.ON_MOUSE_PRESSED.register((window, button, action, mods) -> {
            if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
            if (this.entity == null || this.client.currentScreen != null || System.currentTimeMillis() - clickSpawnTime < SPAWN_COOLDOWN) return ActionResult.PASS;
            this.place();
            return ActionResult.FAIL;
        });
        // If in placement mode then intercept any keys pressed and cancel any modifier keys being pressed
        Events.ON_KEY_PRESSED.register((window, key, scancode, action, mods) -> {
            if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
            if (this.entity == null || this.client.currentScreen != null) return ActionResult.PASS;
            double nudge = NUDGE;
            if ((mods & GLFW.GLFW_MOD_CONTROL) != 0)
                nudge *= FAST_MOD;

            if (key == GLFW.GLFW_KEY_LEFT_ALT && action == GLFW.GLFW_PRESS) {
                locked = !locked;
            } else if (key == GLFW.GLFW_KEY_LEFT) {
                posOffset = posOffset.add(nudge, 0, 0);
            } else if (key == GLFW.GLFW_KEY_RIGHT) {
                posOffset = posOffset.add(-nudge, 0, 0);
            } else if (key == GLFW.GLFW_KEY_UP) {
                if (locked)
                    entity.setPosition(entity.getPos().add(0, nudge, 0));
                else
                    posOffset = posOffset.add(0, nudge, 0);
            } else if (key == GLFW.GLFW_KEY_DOWN) {
                if (locked)
                    entity.setPosition(entity.getPos().add(0, -nudge, 0));
                else
                    posOffset = posOffset.add(0, -nudge, 0);
            } else {
                return key == GLFW.GLFW_KEY_LEFT_SHIFT ? ActionResult.FAIL : ActionResult.PASS;
            }
            return ActionResult.FAIL;
        });
    }

    /**
     * Will open the screen and shit
     */
    public void create() {
        if (this.client.world == null) return;

        if (screen == null)
            screen = new ArmorStandScreen(this);

        this.client.setScreen(screen);
    }
    /**
     * Will spawn client sided armor stand
     */
    public void spawn(ArmorStandEntity screenEntity) {
        clickSpawnTime = System.currentTimeMillis();
        if (entity != null) return;
        Vec3d playerPos = this.client.player.getPos();
        entity = new ArmorStandEntity(this.client.world, playerPos.x, playerPos.y, playerPos.z);
        entity.copyFrom(screenEntity);
        this.client.world.addEntity(UUID.randomUUID().hashCode(), entity);
        updateEntity();
    }
    /**
     * Spawns an armor stand with /summon <br>
     * OR <br>
     * Copies the final command to clipboard
     */
    public void place() {
        clickSpawnTime = System.currentTimeMillis();
        if (this.entity == null) return;
        Vec3d p = entity.getPos();
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        String command = String.format("summon armor_stand %f %f %f %s", p.x, p.y, p.z, nbt);
        if (this.mode == Mode.EXECUTE) {
            this.client.getNetworkHandler().sendCommand(command);
        } else if (this.mode == Mode.CLIPBOARD) {
            GLFW.glfwSetClipboardString(0, "/"+command);
        }
        this.close();

    }
    /**
     * Called every tick to update the entity with new positions
     */
    public void tick() {
        updateEntity();
    }
    public void setMode(Mode mode) {
        CreativePlus.LOGGER.debug("SETTING MODE TO "+mode);
        this.mode = mode;
    }

    private void close() {
        if (this.entity == null) return;
        this.entity.discard();;
        this.entity = null;
        this.screen = null;
        this.posOffset = OFFSET.add(Vec3d.ZERO);
        this.locked = false;
        this.mode = Mode.EXECUTE;
    }
    private void updateEntity() {
        if (entity == null || locked) return;
        Vec3d p = this.client.player.getPos();
        Vec2f r = this.client.player.getRotationClient();
        entity.setYaw(yaw);
        entity.setPosition(VecHelper.toAbsolutePos(p, r, posOffset));
    }

    public enum Mode {
        EXECUTE, CLIPBOARD
    }

}
