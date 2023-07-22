package me.gravityio.creativeplus.input;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.MyNbtElementVisitor;
import me.gravityio.creativeplus.entity.client.ClientEntity;
import me.gravityio.creativeplus.lib.helper.Helper;
import me.gravityio.creativeplus.lib.helper.NbtHelper;
import me.gravityio.creativeplus.lib.idk.ClientServerCommunication;
import me.gravityio.creativeplus.screen.EditEntityScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class BindEditHelper {

    private static final String commandFormat = "data merge entity %s %s";

    public static void onEditKeyPressed(MinecraftClient client) {
        if (!client.player.hasPermissionLevel(2)) {
            client.player.sendMessage(Text.translatable("message.creativeplus.no_permissions_error"), true);
            return;
        }

        if (CreativePlus.hasCurrentHandler()) {
            // TODO: MAKE IT OPEN THE CLIENT SIDED ENTITY IN
            //  THE EDIT SCREEN AND GET THE OUTPUT OF THE EDIT SCREEN AND PUT IT INTO
            //  CURRENT HANDLERS OUTPUT
            client.player.sendMessage(Text.translatable("message.creativeplus.edit_placing_error"), true);
            return;
        }

        Entity readEntity = Helper.getTargetedEntity(EntityPredicates.EXCEPT_SPECTATOR, 20);
        if (readEntity == null) {
            client.player.sendMessage(Text.translatable("message.creativeplus.edit.no_entity"), true);
            return;
        }

        Entity writeEntity = readEntity.getType().create(readEntity.world);

        if (writeEntity == null) {
            client.player.sendMessage(Text.translatable("message.creativeplus.edit.spawn_clone_error"));
        }

        writeEntity.copyFrom(readEntity);
        writeEntity.setYaw(0);
        writeEntity.setBodyYaw(0);
        writeEntity.setHeadYaw(0);
        writeEntity.setPitch(0);

        ClientEntity clientEntity = ClientEntity.create(readEntity, writeEntity, new NbtCompound());
        var editScreen = new EditEntityScreen(clientEntity);
        editScreen.onFinish((data) -> {
            NbtCompound out = clientEntity.getOutput();
            if (out.isEmpty()) {
                client.player.sendMessage(Text.translatable("message.creativeplus.edit.nothing_changed"), true);
                editScreen.close();
                return;
            }

            String nbt = MyNbtElementVisitor.toString(clientEntity.getOutput());
            String command = commandFormat.formatted(readEntity.getUuidAsString(), nbt);
            if (data.mode() == EditEntityScreen.Mode.EXECUTE) {
                if (command.length() < 256) {
                    ClientServerCommunication.sendCommand(command, false);
                } else {
                    List<String> split = NbtHelper.splitCompoundIntoStrings(out, 256);
                    for (String s : split) {
                        ClientServerCommunication.sendCommand(commandFormat.formatted(readEntity.getUuidAsString(), s), false);
                    }
                }
                client.player.sendMessage(Text.translatable("message.creativeplus.edit.success"), true);
            } else {
                client.player.sendMessage(Text.translatable("message.creativeplus.generic.copy_command"), true);
                GLFW.glfwSetClipboardString(0, "/" + command);
            }

            editScreen.close();
        });
        ClientServerCommunication.getEntityNbt(readEntity.getUuid(), false, v -> onRespondNBT(client, editScreen, clientEntity, v));
        client.setScreen(editScreen);
    }

    private static void onRespondNBT(MinecraftClient client, EditEntityScreen editScreen, ClientEntity clientEntity, NbtCompound nbt) {
        if (nbt == null) {
            client.player.sendMessage(Text.translatable("message.creativeplus.screen.edit.failed_to_get_nbt"), true);
            editScreen.close();
            return;
        }

        clientEntity.setRealNbt(nbt);
        editScreen.updateNBT();
    }

}
