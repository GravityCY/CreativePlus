package me.gravityio.creativeplus.api.placement;

import me.gravityio.creativeplus.MyNbtElementVisitor;
import me.gravityio.creativeplus.lib.helper.EntityNbtHelper;
import me.gravityio.creativeplus.lib.helper.TextHelper;
import me.gravityio.creativeplus.lib.idk.ClientServerCommunication;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

import static me.gravityio.creativeplus.screen.EditEntityScreen.Mode;

public class ClientEntityTeleportHandler extends ClientEntityMovementHandler {
    private static final String commandFormat = "data merge entity %s %s";
    private UUID uuid;
    private Text name;
    private Mode mode = Mode.EXECUTE;
    private Runnable onFinish;

    public ClientEntityTeleportHandler(MinecraftClient client) {
        super(client);
    }

    public void onFinish(Runnable onFinish) {
        this.onFinish = onFinish;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void start() {
        if (super.locked) {
            client.player.sendMessage(Text.translatable("message.creativeplus.teleport.start_move_locked", this.name), true);
        } else {
            client.player.sendMessage(Text.translatable("message.creativeplus.teleport.start_move", this.name), true);
        }
        super.start();
    }

    @Override
    public void setupEntity(@NotNull Entity entity) {
        this.uuid = entity.getUuid();
        this.name = TextHelper.getLimit(entity.getName(), 20);
        super.setupEntity(entity);
    }

    @Override
    protected void onPlace(OutputData data) {
        Vec3d pos = data.pos();
        Vec2f rot = data.rot();
        NbtCompound nbt = data.output();
        nbt.put("Pos", EntityNbtHelper.toNbtList(pos.x, pos.y, pos.z));
        nbt.put("Rotation", EntityNbtHelper.toNbtList(rot.y, rot.x));
        String command = commandFormat.formatted(uuid.toString(), MyNbtElementVisitor.toString(nbt));
        if (mode == Mode.EXECUTE) {
            ClientServerCommunication.sendCommand(command, false);
            this.client.player.sendMessage(Text.translatable("message.creativeplus.teleport.end_move", this.name), true);
        } else {
            GLFW.glfwSetClipboardString(0, "/" + command);
            this.client.player.sendMessage(Text.translatable("message.creativeplus.teleport.copy_command"), true);
        }
        this.runFinish();
    }

    @Override
    protected void onCancel(OutputData data) {
        client.player.sendMessage(Text.translatable("message.creativeplus.teleport.cancel_move", this.name), true);
        this.runFinish();
    }

    private void runFinish() {
        if (onFinish == null) return;
        this.onFinish.run();
    }
}
