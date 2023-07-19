package me.gravityio.creativeplus.input;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.api.placement.ClientEntityTeleportHandler;
import me.gravityio.creativeplus.lib.helper.Helper;
import me.gravityio.creativeplus.lib.idk.DefaultInputListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

/**
 * Tries to get an existing entity and position it using {@link PREV} <br><br>
 *
 * Holding alt and right-clicking will clone an entity on the client and set it to positioning mode<br><br>
 * When finished, it will discard the cloned entity and send a command to the server to teleport the aforementioned entity using its UUID
 */
public class ClientMoveEntityInputHandler implements DefaultInputListener {

    private final ClientEntityTeleportHandler teleportHandler;
    private final MinecraftClient client;
    public ClientMoveEntityInputHandler(MinecraftClient client) {
        this.client = client;
        this.teleportHandler = new ClientEntityTeleportHandler(this.client);
        this.teleportHandler.onFinish(CreativePlus::unsetCurrentHandler);
    }

    public void moveEntity(Entity entity, boolean controlDown) {
        CreativePlus.setCurrentHandler(this.teleportHandler);
        this.teleportHandler.setLocked(controlDown);
        this.teleportHandler.setupEntity(entity);
        this.teleportHandler.start();
    }

    /**
     * Handles Entity Selection and Setup
     */
    @Override
    public ActionResult onMouseButton(long window, int button, int action, int mods) {
        if (window != this.client.getWindow().getHandle() || this.client.currentScreen != null || action != GLFW.GLFW_PRESS) return ActionResult.PASS;
        boolean isAltDown = (mods & GLFW.GLFW_MOD_ALT) != 0;
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && isAltDown) {
            Entity targetedEntity = Helper.getTargetedEntity(EntityPredicates.EXCEPT_SPECTATOR.and(e -> e.getVehicle() == null && !e.isPlayer()), 20);
            if (targetedEntity == null) return ActionResult.PASS;
            moveEntity(targetedEntity, (mods & GLFW.GLFW_MOD_CONTROL) != 0);
        } else {
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }
}
