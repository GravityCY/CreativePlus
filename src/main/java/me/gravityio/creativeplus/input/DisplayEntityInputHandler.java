package me.gravityio.creativeplus.input;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.helper.Helper;
import me.gravityio.creativeplus.lib.idk.ClientServerCommunication;
import me.gravityio.creativeplus.lib.idk.DefaultInputListener;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class DisplayEntityInputHandler implements DefaultInputListener {

    private static final String blockCommand = "data modify entity %s block_state set value {Name:\"%s\"}";
    private static final String itemCommand = "data modify entity %s item set value {id:\"%s\",Count:1b}";
    private static final String itemCommandWithTag = "data modify entity %s item set value {id:\"%s\",Count:1b,tag:%s}";
    private final MinecraftClient client;

    public DisplayEntityInputHandler(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public ActionResult onMouseButton(long window, int button, int action, int mods) {
        if (client.player == null || client.currentScreen != null || CreativePlus.hasCurrentHandler()) return ActionResult.PASS;

        boolean controlDown = (mods & GLFW.GLFW_MOD_CONTROL) != 0;

        Entity entity = Helper.getTargetedEntity(InputTypeHelper.TARGETED_ENTITY, 20);
        if (entity == null || entity.getId() < 0) return ActionResult.PASS;

        boolean isBlockDisplay = entity.getType() == EntityType.BLOCK_DISPLAY;
        boolean isItemDisplay = entity.getType() == EntityType.ITEM_DISPLAY;

        if (!isBlockDisplay && !isItemDisplay) return ActionResult.PASS;

        ItemStack main = client.player.getMainHandStack();
        String command;
        if (isBlockDisplay) {
            command = getBlockCommand((DisplayEntity.BlockDisplayEntity) entity, main, controlDown);
        } else {
            command = getItemCommand((DisplayEntity.ItemDisplayEntity) entity, main, controlDown);
        }

        if (command == null) return ActionResult.PASS;
        ClientServerCommunication.sendCommand(command, false);
        return ActionResult.FAIL;
    }

    private String getItemCommand(DisplayEntity.ItemDisplayEntity itemDisplay, ItemStack main, boolean controlDown) {
        boolean isEmpty = main.isEmpty();
        ItemStack current = itemDisplay.getItemStack();
        if (areItemsEqual(current, main)) return null;
        Identifier itemId = Registries.ITEM.getId(main.getItem());

        String itemName = "null";
        if (!isEmpty) {
            if (itemId.getNamespace().equals("minecraft")) {
                itemName = itemId.getPath();
            } else {
                itemName = itemId.toString();
            }

            this.client.player.sendMessage(Text.translatable("message.creativeplus.display_helper.set_item", main.getName()), true);
        } else {
            if (!controlDown) return null;
            this.client.player.sendMessage(Text.translatable("message.creativeplus.display_helper.clear_item", main.getName()), true);

        }

        if (main.hasNbt()) {
            return itemCommandWithTag.formatted(itemDisplay.getUuidAsString(), itemName, main.getNbt().asString());
        }
        return itemCommand.formatted(itemDisplay.getUuidAsString(), itemName);
    }

    private String getBlockCommand(DisplayEntity.BlockDisplayEntity blockDisplay, ItemStack main, boolean controlDown) {
        boolean isEmpty = main.isEmpty();
        Identifier current = Registries.BLOCK.getId(blockDisplay.getBlockState().getBlock());
        String blockName = "null";
        if (!isEmpty) {
            if (!(main.getItem() instanceof BlockItem blockItem)) return null;
            Block block = blockItem.getBlock();
            Identifier blockId = Registries.BLOCK.getId(block);
            if (current.equals(blockId)) return null;
            blockName = blockId.toString();
            if (blockId.getNamespace().equals("minecraft"))
                blockName = blockId.getPath();
            this.client.player.sendMessage(Text.translatable("message.creativeplus.display_helper.set_block", block.getName()), true);
        } else {
            if (!controlDown) return null;
            this.client.player.sendMessage(Text.translatable("message.creativeplus.display_helper.clear_block", main.getName()), true);
        }

        return blockCommand.formatted(blockDisplay.getUuidAsString(), blockName);
    }

    private boolean areItemsEqual(ItemStack a, ItemStack b) {
        if (a == b) return true;
        if (a.getItem() != b.getItem()) return false;
        boolean aHasNbt = a.hasNbt();
        boolean bHasNbt = b.hasNbt();
        if (aHasNbt != bHasNbt) return false;
        return !aHasNbt || a.getNbt().equals(b.getNbt());
    }
}
