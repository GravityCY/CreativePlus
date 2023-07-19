package me.gravityio.creativeplus.api.placement;

import me.gravityio.creativeplus.api.custom.ArmorStandHandler;
import me.gravityio.creativeplus.api.custom.CustomHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class ClientEntityMovementHandler extends EntityMovementHandler {
    protected NbtCompound output;
    private NbtCompound beforeHandler;
    private @Nullable Entity original;
    private @Nullable CustomHandler handler;
    private @Nullable OnPlace onPlace;
    private @Nullable OnCancel onCancel;
    private PreviousEntityData data;
    protected boolean resetOnPlace = true;
    protected boolean resetOnCancel = true;
    protected boolean handlerActive = false;
    protected boolean active = false;

    public ClientEntityMovementHandler(MinecraftClient client) {
        super(client);
    }

    @Override
    public void tick() {
        if (!this.active) return;

        if (this.handlerActive) {
            this.handler.tick();
            if (!this.handler.isAdditive()) {
                return;
            }
        }

        super.tick();
    }

    @Override
    public ActionResult onMouseButton(long window, int button, int action, int mods) {
        if (!this.active) return ActionResult.PASS;

        if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
        if (this.entity == null || this.client.currentScreen != null) return ActionResult.PASS;

        if (this.handlerActive) {
            var result = this.handler.onMouseButton(window, button, action, mods);
            if (!this.handler.isAdditive())
                return result;
            if (result == ActionResult.FAIL) return ActionResult.FAIL;
        }

        if (action != GLFW.GLFW_PRESS) return ActionResult.PASS;

        this.place();
        return ActionResult.FAIL;
    }

    @Override
    public ActionResult onMouseDelta(long window, double nx, double ny) {
        if (!this.active) return ActionResult.PASS;

        if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
        if (this.entity == null || this.client.currentScreen != null) return ActionResult.PASS;

        if (this.handlerActive) {
            var result = this.handler.onMouseDelta(window, nx, ny);
            if (!this.handler.isAdditive())
                return result;
            if (result == ActionResult.FAIL) return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult onMouseScroll(long window, double horizontal, double vertical) {
        if (!this.active) return ActionResult.PASS;

        if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
        if (this.entity == null || this.client.currentScreen != null) return ActionResult.PASS;

        ActionResult handlerResult = null;
        if (this.handlerActive) {
            var result = this.handler.onMouseScroll(window, horizontal, vertical);
            if (!this.handler.isAdditive())
                return result;
            handlerResult = result;
        }

        var superResult = super.onMouseScroll(window, horizontal, vertical);
        if ((handlerResult == ActionResult.FAIL) || superResult == ActionResult.FAIL) return ActionResult.FAIL;
        return ActionResult.PASS;
    }

    @Override
    public ActionResult onKey(long window, int key, int scancode, int action, int mods) {
        if (!this.active) return ActionResult.PASS;

        if (this.client.getWindow().getHandle() != window) return ActionResult.PASS;
        if (this.entity == null || this.client.currentScreen != null) return ActionResult.PASS;

        ActionResult handlerResult = null;
        if (this.handlerActive) {
            var result = this.handler.onKey(window, key, scancode, action, mods);
            if (!this.handler.isAdditive())
                return result;
            handlerResult = result;
        }

        var superResult = super.onKey(window, key, scancode, action, mods);
        if (superResult == ActionResult.FAIL || handlerResult == ActionResult.FAIL) return ActionResult.FAIL;

        if (action != GLFW.GLFW_PRESS) return ActionResult.PASS;

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.cancel();
        } else if (key == GLFW.GLFW_KEY_B) {
            if (this.handler != null) {
                this.setHandlerActive(true);
                this.beforeHandler = super.entity.writeNbt(new NbtCompound());
                this.handler.onActivated();
            } else {
                this.client.player.sendMessage(Text.translatable("message.creativeplus.movement.no_custom_handler"), true);
            }
        } else if (key == GLFW.GLFW_KEY_ENTER) {
            this.place();
        } else {
            return ActionResult.PASS;
        }

        return ActionResult.FAIL;
    }

    /**
     * Sets the handler as active (allows events) and spawns the client side entity
     */
    public void start() {
        this.setActive(true);
        if (this.original != null) {
            this.data = new PreviousEntityData(this.original.isInvisible(), this.original.isGlowing());

            this.original.setInvisible(true);
            this.original.setGlowing(true);
        }

        super.client.world.addEntity(-1, super.entity);
    }

    /**
     * Sets the handler as inactive (disallows events) and discards the client side entity
     */
    public void stop(StopReason reason) {
        if (this.original != null) {
            this.original.setInvisible(this.data.wasInvisible());
            this.original.setGlowing(this.data.wasGlowing());
        }
        this.resetLocal(reason);
    }

    /**
     * Discards the entity and runs the on place callback
     */
    public void place() {
        var output = this.getOutputData();
        this.stop(StopReason.PLACE);
        this.onPlace(output);
        if (this.onPlace == null) return;
        this.onPlace.onPlace(output);
    }

    /**
     * Discards the entity and runs the on cancel callback
     */
    public void cancel() {
        var output = this.getOutputData();
        this.stop(StopReason.CANCEL);
        this.onCancel(output);
        if (this.onCancel == null) return;
        this.onCancel.onCancel(output);
    }

    /**
     * Discards the entity and resets the handler
     */
    public void discard() {
        if (super.entity == null) return;
        super.entity.discard();
    }

    /**
     * Stops current placement of an entity
     */
    @Override
    public void reset() {
        this.setActive(false);
        this.discard();
        this.data = null;
        this.handler = null;
        this.handlerActive = false;
        this.output = null;
        super.reset();
    }

    /**
     * Sets whether this should even run its events
     */
    @Override
    public void setActive(boolean v) {
        super.setActive(v);
        this.active = v;
    }

    public void setResetOnPlace(boolean v) {
        this.resetOnPlace = v;
    }

    public boolean willResetOnPlace() {
        return this.resetOnPlace;
    }

    public void setResetOnCancel(boolean v) {
        this.resetOnPlace = v;
    }

    public boolean willResetOnCancel() {
        return this.resetOnPlace;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setupEntity(Entity entity) {
        this.original = entity;
        Entity clone = entity.getType().create(entity.world);
        if (clone == null) return;
        this.setupClientEntity(clone, entity.writeNbt(new NbtCompound()));
        this.output = new NbtCompound();
    }

    /**
     * Creates a clone of the given entity, given nbt and sets the clone to be operated on <br><br>
     * The NBT must be formatted for sending to the server, attempt to not exceed 256 characters!
     */
    public void setupEntity(EntityType<?> type, NbtCompound nbt) {
        var clone = type.create(client.world);
        if (clone == null) return;
        this.setupClientEntity(clone, nbt);
        this.output = nbt;
    }

    public void setupClientEntity(Entity clone, NbtCompound nbt) {
        clone.readNbt(nbt);
        clone.setInvisible(false);
        clone.setUuid(UUID.randomUUID());
        clone.setId(-1);
        this.handler = getUpdatedCustomHandler(this.client, clone);
        super.setEntity(clone);
    }

    public void onPlaceCallback(@Nullable OnPlace onPlace) {
        this.onPlace = onPlace;
    }

    public void onCancelCallback(@Nullable OnCancel onCancel) {
        this.onCancel = onCancel;
    }

    protected void onPlace(OutputData data) {}

    protected void onCancel(OutputData data) {}

    private void setHandlerActive(boolean v) {
        this.handlerActive = v;
        super.active = !this.handlerActive;
    }

    private void resetLocal(StopReason reason) {
        if (reason == StopReason.CANCEL && !resetOnCancel) {
            return;
        } else if (reason == StopReason.PLACE && !resetOnPlace) {
            return;
        }

        this.reset();
    }

    private CustomHandler getUpdatedCustomHandler(MinecraftClient client, Entity entity) {
        var handler = getCustomHandler(client, entity);
        if (handler != null) {
            handler.onComplete(() -> {
                this.setHandlerActive(false);
                handler.transform(this.output);

            });

            handler.onCancel(() -> {
                this.setHandlerActive(false);
                super.entity.readNbt(this.beforeHandler);
            });

        }
        return handler;
    }

    private OutputData getOutputData() {
        return new OutputData(super.entity.getPos(), super.entity.getRotationClient(), this.output);
    }

    private static CustomHandler getCustomHandler(MinecraftClient client, Entity entity) {
        if (entity instanceof ArmorStandEntity) {
            return new ArmorStandHandler(client, entity);
        }
        return null;
    }

    private enum StopReason {
        CANCEL, PLACE;
    }

    public interface OnPlace {
        void onPlace(OutputData output);
    }

    public interface OnCancel {
        void onCancel(OutputData output);
    }

    private record PreviousEntityData(boolean wasInvisible, boolean wasGlowing) {}

    public record OutputData(Vec3d pos, Vec2f rot, NbtCompound output) {}
}
