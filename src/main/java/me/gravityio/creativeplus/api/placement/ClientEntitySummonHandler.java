package me.gravityio.creativeplus.api.placement;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.MyNbtElementVisitor;
import me.gravityio.creativeplus.lib.helper.EntityNbtHelper;
import me.gravityio.creativeplus.lib.helper.NbtHelper;
import me.gravityio.creativeplus.lib.helper.TextHelper;
import me.gravityio.creativeplus.lib.idk.ClientServerCommunication;
import me.gravityio.creativeplus.screen.EditEntityScreen.Mode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class ClientEntitySummonHandler extends ClientEntityMovementHandler {
    private static final String commandFormat = "summon %s %.2f %.2f %.2f %s";
    private static final String clearCommand = "tag @e[tag=TEMPORARY_TAG_CREATIVEPLUS] remove TEMPORARY_TAG_CREATIVEPLUS";
    private static final String mergeFormat = "data merge entity @e[tag=TEMPORARY_TAG_CREATIVEPLUS, limit=1] %s";
    private EntityType<?> entityType;
    private Text name;
    private Mode mode = Mode.EXECUTE;
    private Runnable onFinish;

    public ClientEntitySummonHandler(@NotNull MinecraftClient client) {
        super(client);
    }

    public void onFinish(Runnable onFinish) {
        this.onFinish = onFinish;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void setupEntity(EntityType<?> type, NbtCompound nbt) {
        this.entityType = type;
        this.name = TextHelper.getLimit(type.getName(), 20);
        super.setupEntity(type, nbt);
    }

    @Override
    public void start() {
        this.client.player.sendMessage(Text.translatable("message.creativeplus.summon.start_summon", this.name), true);
        super.start();
    }

    /**
     * Spawns an entity using /summon <br>
     * OR <br>
     * Copies the final command to clipboard
     */
    @Override
    protected void onPlace(OutputData data) {
        Identifier entityIdentifier = Registries.ENTITY_TYPE.getId(this.entityType);
        Vec3d pos = data.pos();
        NbtCompound nbt = data.output();
        Vec2f rot = data.rot();
        nbt.put("Rotation", EntityNbtHelper.toNbtList(rot.y, rot.x));
        String mergedNbt = MyNbtElementVisitor.toString(nbt);
        String mergedCommand = commandFormat.formatted(entityIdentifier, pos.x, pos.y, pos.z, mergedNbt);

        if (this.mode == Mode.EXECUTE) {
            if (mergedCommand.length() > 256) {
                CreativePlus.LOGGER.debug("[ClientEntityPlacementHandler] Sending Split NBT!");
                String command1 = commandFormat.formatted(entityIdentifier, pos.x, pos.y, pos.z, "{Tags:[\"TEMPORARY_TAG_CREATIVE_PLUS\"]}");
                ClientServerCommunication.sendCommand(command1, false);
                for (String split : NbtHelper.splitCompoundIntoStrings(nbt, 256 - mergeFormat.length() - 2)) {
                    ClientServerCommunication.sendCommand(mergeFormat.formatted(split), false);
                }
                ClientServerCommunication.sendCommand(clearCommand, false);

            } else {
                CreativePlus.LOGGER.debug("[ClientEntityPlacementHandler] Sending Merged NBT!");
                ClientServerCommunication.sendCommand(mergedCommand, false);
            }
            this.client.player.sendMessage(Text.translatable("message.creativeplus.summon.end_summon", this.name), true);
        } else if (this.mode == Mode.COPY) {
            this.client.player.sendMessage(Text.translatable("message.creativeplus.summon.copy_command"), true);
            GLFW.glfwSetClipboardString(0, "/" + mergedCommand);
        }
        CreativePlus.LOGGER.debug("[ClientEntityPlacementHandler] Placed Entity!");
        this.runFinish();
    }

    @Override
    protected void onCancel(OutputData data) {
        this.client.player.sendMessage(Text.translatable("message.creativeplus.summon.cancel_summon"), true);
        this.runFinish();
    }

    private void runFinish() {
        if (this.onFinish == null) return;
        this.onFinish.run();
    }
}
