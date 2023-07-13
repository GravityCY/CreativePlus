package me.gravityio.creativeplus;

import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A List of commands, to be executed with the callback item, or any other interface I add in the future (like keybinds)
 */
public class ModularCommands {

    private final HashMap<String, List<String>> presets = new HashMap<>();
    private List<String> current = new ArrayList<>();

    public List<String> getAllPresets() {
        return presets.keySet().stream().toList();
    }

    public boolean setCurrentPreset(String newPreset) {
        List<String> preset = presets.get(newPreset);
        if (preset == null) return false;
        current = new ArrayList<>(preset);
        return true;
    }

    public void addPreset(String presetName) {
        presets.put(presetName, new ArrayList<>(current));
    }

    public List<String> getCurrent() {
        return current;
    }

    public boolean add(String string) {
        if (current == null) return false;

        current.add(string);
        return true;
    }

    public void run(ClientPlayerEntity player) {
        for (String s : current) {
            player.networkHandler.sendChatCommand(s);
        }
    }

}
