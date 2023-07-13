package me.gravityio.creativeplus.mixin.lib;

import me.gravityio.creativeplus.lib.CreativeEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

  @Inject(method = "use", at = @At("HEAD"), cancellable = true)
  private void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
    TypedActionResult<ItemStack> result = CreativeEvents.ON_ITEM_USE.invoker().onUse(world, user, hand);
    if (result != null)
      cir.setReturnValue(result);
  }
}
