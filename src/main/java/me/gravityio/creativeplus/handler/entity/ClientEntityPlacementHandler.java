package me.gravityio.creativeplus.handler.entity;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.MyNbtElementVisitor;
import me.gravityio.creativeplus.lib.ClientServerCommunication;
import me.gravityio.creativeplus.lib.EntityNbtHelper;
import me.gravityio.creativeplus.lib.NbtHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

/**
 * The placement handler spawns a completely new entity on the client and when user places will send a summon command to the server with the specified NBT
 */
public class ClientEntityPlacementHandler extends ClientEntityMovementHandler {

    private static final String commandFormat = "summon %s %.2f %.2f %.2f %s";
    private static final String clearCommand = "tag @e[tag=TEMPORARY_TAG] remove TEMPORARY_TAG";
    private static final String mergeFormat = "data merge entity @e[tag=TEMPORARY_TAG, limit=1] %s";

    public NbtCompound originalNbt;
    protected final MinecraftClient client;
    protected EntityType<? extends Entity> entityType;
    protected long until;
    protected Mode mode = Mode.EXECUTE;
    protected OnCancel onCancelCallback;
    protected OnPlace onPlaceCallback;
    protected OnPlaceError onPlaceErrorCallback;

    public ClientEntityPlacementHandler(MinecraftClient client, @Nullable EntityType<? extends Entity> entityType) {
        super(client, null);
        this.client = client;
        this.entityType = entityType;
    }

    /**
     * On any mouse button will try to place the entity
     */
    @Override
    public ActionResult onMouseButton(long window, int button, int action, int mods) {
        if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
        if (super.entity == null || this.client.currentScreen != null || action == GLFW.GLFW_RELEASE) return ActionResult.PASS;
        this.place();
        return ActionResult.FAIL;
    }

    /**
     * Enter Places the Entity
     * Escape Cancels the Placement
     */
    @Override
    public ActionResult onKey(long window, int key, int scancode, int action, int mods) {
        if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
        if (super.entity == null || this.client.currentScreen != null) return ActionResult.PASS;

        if (key == GLFW.GLFW_KEY_ENTER) {
            this.place();
        } else if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.cancel();
        } else {
            return super.onKey(window, key, scancode, action, mods);
        }

        return ActionResult.FAIL;
    }

    /**
     * Sets a delay until cancelling or placing can be executed <br><br>
     * So you can prevent clicks or key presses from menus feeding into this.
     */
    public void setDelay(long delay) {
        this.until = System.currentTimeMillis() + delay;
    }

    /**
     * Sets the Entity Type to spawn when startPlacing is run
     * @param type
     */
    public void setEntityType(EntityType<? extends Entity> type) {
        this.entityType = type;
    }

    /**
     * Spawns the client sided entity for placement
     */
    public void startPlacing(@Nullable NbtCompound nbt) {
        if (super.entity != null) return;
        this.originalNbt = nbt;
        CreativePlus.LOGGER.debug("[ClientEntityPlacementHandler] Started Placing!");
        Entity ent = entityType.create(this.client.world);
        if (ent == null) return;
        super.setEntity(ent);
        ent.noClip = true;
        super.alignEntityToPlayer();
        if (nbt != null) {
            ent.readNbt(ent.writeNbt(new NbtCompound()).copyFrom(nbt));
            ent.setUuid(UUID.randomUUID());
        }
        this.client.world.addEntity(-1, ent);
    }

    /**
     * Spawns an entity using /summon <br>
     * OR <br>
     * Copies the final command to clipboard
     */
    public void place() {
        if (super.entity == null) {
            if (this.onPlaceErrorCallback != null)
                this.onPlaceErrorCallback.onPlaceError(PlaceStatus.NO_ENTITY);
            return;
        }

        if (System.currentTimeMillis() < this.until) {
            if (this.onPlaceErrorCallback != null)
                this.onPlaceErrorCallback.onPlaceError((PlaceStatus.TOO_EARLY));
            return;
        }

        Identifier entityIdentifier = Registries.ENTITY_TYPE.getId(this.entityType);

        CreativePlus.LOGGER.debug("[ClientEntityPlacementHandler] Placed Entity!");

        Vec3d pos = super.entity.getPos();

        NbtCompound nbt = new NbtCompound();
        if (originalNbt != null)
            nbt.copyFrom(originalNbt);

        Vec2f rot = entity.getRotationClient();
        nbt.put("Rotation", EntityNbtHelper.toNbtList(rot.y, rot.x));

        if (this.mode == Mode.EXECUTE) {
            String mergedNbt = MyNbtElementVisitor.toString(nbt);
            String mergedCommand = commandFormat.formatted(entityIdentifier, pos.x, pos.y, pos.z, mergedNbt);
            if (mergedCommand.length() > 256) {
                CreativePlus.LOGGER.debug("[ClientEntityPlacementHandler] Sending Split NBT!");

                String command1 = commandFormat.formatted(entityIdentifier, pos.x, pos.y, pos.z, "{Tags:[\"TEMPORARY_TAG\"]}");
                ClientServerCommunication.sendCommand(command1, false);
                for (String split : NbtHelper.splitCompoundIntoStrings(nbt, 256 - mergeFormat.length() - 2)) {
                    ClientServerCommunication.sendCommand(mergeFormat.formatted(split), false);
                }
                ClientServerCommunication.sendCommand(clearCommand, false);
            } else {
                CreativePlus.LOGGER.debug("[ClientEntityPlacementHandler] Sending Merged NBT!");
                ClientServerCommunication.sendCommand(mergedCommand, false);
            }

        } else if (this.mode == Mode.CLIPBOARD) {
            String command = "/" + commandFormat.formatted(Registries.ENTITY_TYPE.getId(this.entityType), pos.x, pos.y, pos.z, MyNbtElementVisitor.toString(nbt));
            GLFW.glfwSetClipboardString(0, command);
        }
        if (this.onPlaceCallback != null)
            this.onPlaceCallback.onPlace();
    }

    public void cancel() {
        if (System.currentTimeMillis() < this.until) return;
        if (this.onCancelCallback != null) {
            this.onCancelCallback.onCancel();
        } else {
            discardNull();
        }
    }

    /**
     * Discards the client side entity
     */
    public void discard() {
        if (super.entity != null)
            super.entity.discard();
    }

    /**
     * Discards the client side entity and nullifies it
     */
    public void discardNull() {
        discard();
        super.setEntity(null);
    }


    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * Sets a callback for when the handler actually spawns the final entity
     * @param onPlaceCallback A callback for when the entity is actually placed
     */
    public void onPlace(OnPlace onPlaceCallback) {
        this.onPlaceCallback = onPlaceCallback;
    }

    public void onCancel(OnCancel onCancelCallback) {
        this.onCancelCallback = onCancelCallback;
    }

    public void onPlaceError(OnPlaceError onPlaceErrorCallback) {
        this.onPlaceErrorCallback = onPlaceErrorCallback;
    }

    public enum Mode {
        EXECUTE, CLIPBOARD
    }

    public interface OnPlace {
        void onPlace();
    }

    public interface OnCancel {
        void onCancel();
    }

    public interface OnPlaceError {
        void onPlaceError(PlaceStatus status);
    }

    public enum PlaceStatus {
        COMMAND_TOO_LONG,
        NO_ENTITY,
        TOO_EARLY,
        SUCCESS,
    }

}
