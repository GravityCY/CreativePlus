package me.gravityio.creativeplus.lib.idk;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.gravityio.creativeplus.CreativePlus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.function.Function;

/**
 * A Library to try and get the response of a command after a client sends a command, in order to use that information, or to simply cancel it from being viewed by the client<br><br>
 *
 * Example:<br>
 * <pre>{@code (client sends) /data get entity SOME-UUID-OF-AN-ENTITY}</pre>
 * <pre>{@code (client receives) Entity has the following entity data: { Invisible:1b, blablabla.... }}</pre><br>
 *
 * This has some edge cases and is super scuffed to depend on the string response of a command, but this is what I get for making a purely client mod
 */
public class ClientServerCommunication {
    private static MinecraftClient client;
    private static final Deque<CommandResponse> queue = new ArrayDeque<>();

    public static void setMinecraftClient(MinecraftClient client) {
        ClientServerCommunication.client = client;
    }

    public static boolean onCommand(Text text) {
        var command = queue.peekFirst();
        if (command == null) return true;
        CreativePlus.LOGGER.debug("[ClientServerCommunication] Received Response: %s".formatted(text.getString()));
        if (command.callback != null) {
            command.callback.onResponse(text.getString());
        }
        queue.removeFirst();
        return command.doPrint;
    }

    public static void sendCommand(String cmd, boolean doPrint) {
        queue.add(new CommandResponse(doPrint));
        client.getNetworkHandler().sendCommand(cmd);
        CreativePlus.LOGGER.debug("[ClientServerCommunication] Sending Command: %s".formatted(cmd));
    }

    public static <T> void sendCommand(String cmd, boolean doPrint, Function<String, T> converter, OnCommandResponse<T> onCommandResponse) {
        OnCommandResponse<String> res = s -> onCommandResponse.onResponse(converter.apply(s));
        queue.add(new CommandResponse(doPrint, res));
        client.getNetworkHandler().sendCommand(cmd);
    }

    public static void getEntityNbt(UUID entity, boolean doPrint, OnCommandResponse<NbtCompound> onCommandResponse) {
        sendCommand("data get entity " + entity.toString(), doPrint, s -> {
            int i = s.lastIndexOf("data: {");
            if (i == -1) return null;
            String compound = s.substring(i + 6);
            try {
                return NbtHelper.fromNbtProviderString(compound);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }, onCommandResponse);
    }

    public interface OnCommandResponse<T> {
        void onResponse(T t);
    }

    record CommandResponse(boolean doPrint, @Nullable OnCommandResponse<String> callback) {
        CommandResponse(boolean doPrint) {
            this(doPrint, null);
        }
    }
}
