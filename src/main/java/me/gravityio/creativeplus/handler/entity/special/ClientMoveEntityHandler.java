package me.gravityio.creativeplus.handler.entity.special;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.handler.entity.ClientEntityMovementHandler;
import me.gravityio.creativeplus.lib.ClientServerCommunication;
import me.gravityio.creativeplus.lib.Helper;
import me.gravityio.creativeplus.lib.TextHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

/**
 * Tries to get an existing entity and position it using {@link ClientEntityMovementHandler} <br><br>
 *
 * Holding alt and right-clicking will clone an entity on the client and set it to positioning mode<br><br>
 * When finished, it will discard the cloned entity and send a command to the server to teleport the aforementioned entity using its UUID
 */
public class ClientMoveEntityHandler extends ClientEntityMovementHandler {

    private static final String commandFormat = "teleport %s %f %f %f %f %f";
    private final MinecraftClient client;
    private Text name;
    private Entity original;
    public ClientMoveEntityHandler(MinecraftClient client) {
        super(client, null);
        this.client = client;
    }

    /**
     * Handles cancellation if there is a currently cloned entity
     */
    @Override
    public ActionResult onKey(long window, int key, int scancode, int action, int mods) {
        if (this.original == null) return ActionResult.PASS;

        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            this.discard();
            client.player.sendMessage(Text.translatable("message.creativeplus.move.cancel"), true);
            return ActionResult.FAIL;
        }

        return super.onKey(window, key, scancode, action, mods);
    }

    /**
     * Handles Entity Selection and Setup
     */
    @Override
    public ActionResult onMouseButton(long window, int button, int action, int mods) {
        if (window != this.client.getWindow().getHandle() || this.client.currentScreen != null || action != GLFW.GLFW_PRESS) return ActionResult.PASS;

        boolean isAltDown = (mods & GLFW.GLFW_MOD_ALT) != 0;

        if (super.entity != null) {
            this.onMove();
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && isAltDown) {
            Entity targetedEntity = Helper.getTargetedEntity(EntityPredicates.EXCEPT_SPECTATOR.and(e -> e.getVehicle() == null && !e.isPlayer()), 20);
            if (targetedEntity == null) return ActionResult.PASS;

            boolean isControlDown = (mods & GLFW.GLFW_MOD_CONTROL) != 0;
            this.original = targetedEntity;
            this.name = TextHelper.getLimit(this.original.getName(), 20);
            Entity clone = targetedEntity.getType().create(this.client.world);
            clone.copyFrom(targetedEntity);
            clone.setUuid(UUID.randomUUID());
            super.entityYaw = targetedEntity.getYaw();
            clone.setId(-1);
            super.setEntity(clone);

            this.client.world.addEntity(-1, clone);

            if (isControlDown) {
                super.isLocked = true;
                client.player.sendMessage(Text.translatable("message.creativeplus.move.on_move_locked", this.name), true);
            } else {
                super.alignEntityToPlayer();
                client.player.sendMessage(Text.translatable("message.creativeplus.move.on_move", this.name), true);
            }
        } else {
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    /**
     * When the user has positioned the entity and has finished, send the tp command to the server
     */
    private void onMove() {
        Vec3d pos = super.entity.getPos();
        float yaw = super.entity.getYaw();
        float pitch = super.entity.getPitch();
        String command = commandFormat.formatted(this.original.getUuidAsString(), pos.x, pos.y, pos.z, yaw, pitch);
        ClientServerCommunication.sendCommand(command, false);
        client.player.sendMessage(Text.translatable("message.creativeplus.move.success", this.name), true);
        this.discard();
    }

    /**
     * Discards the currently cloned entity and resets some bits and bots of the super Handler
     */
    private void discard() {
        super.entity.discard();
        super.setEntity(null);
        super.isLocked = false;
        super.posOffset = ClientEntityMovementHandler.OFFSET.add(Vec3d.ZERO);
        super.entityYaw = 0;
        this.original = null;
    }

}
