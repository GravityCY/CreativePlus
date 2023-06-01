package me.gravityio.creativeplus.items;

import me.gravityio.creativeplus.CreativePlus;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CallbackItem extends Item {

    public CallbackItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return ActionResult.PASS;
        BlockState block = world.getBlockState(context.getBlockPos());
        PlayerEntity player = context.getPlayer();
        ServerCommandSource source = new ServerCommandSource(player, Vec3d.ofCenter(context.getBlockPos()).add(0, -0.5f, 0), Vec2f.ZERO, (ServerWorld) world, 10, player.getName().toString(), player.getName(), world.getServer(), player);
        for (String cb : CreativePlus.getCurrentPreset()) {
            String formatted = cb.replaceAll("&id", String.valueOf(Registries.BLOCK.getId(block.getBlock())));
            world.getServer().getCommandManager().executeWithPrefix(source, formatted);
        }
        return ActionResult.SUCCESS;
    }
}
