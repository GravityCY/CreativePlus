package me.gravityio.creativeplus;

import io.wispforest.owo.Owo;
import me.gravityio.creativeplus.items.CallbackItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreativePlus implements ModInitializer {

    public static final String MOD_ID = "creativeplus";
    public static final Logger LOGGER = LoggerFactory.getLogger(CreativePlus.MOD_ID);

    private static final Item CALLBACK_ITEM = new CallbackItem();
    private static final HashMap<String, List<String>> presets = new HashMap<>();

    private static List<String> defaultList = new ArrayList<>();

    @Override
    public void onInitialize() {
        LOGGER.info("[CreativePlus] Initializing...");
        LOGGER.debug("[CreativePlus] Debug mode.");

        Commands.onInitialize();
        Registry.register(Registries.ITEM, new Identifier(CreativePlus.MOD_ID, "callback_item"), CALLBACK_ITEM);

        LOGGER.info("[CreativePlus] Initialized...");
    }

    public static List<String> getAllPresets() {
        return presets.keySet().stream().toList();
    }

    public static boolean loadPreset(String newPreset) {
        List<String> preset = presets.get(newPreset);
        if (preset == null) return false;
        defaultList = new ArrayList<>(preset);
        return true;
    }

    public static void savePreset(String presetName) {
        presets.put(presetName, new ArrayList<>(defaultList));
    }

    public static List<String> getCurrentPreset() {
        return defaultList;
    }
}
