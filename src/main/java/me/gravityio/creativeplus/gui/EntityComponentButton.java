package me.gravityio.creativeplus.gui;

import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * An Entity Component Button that detects clicks.
 */
public class EntityComponentButton<E extends Entity> extends EntityComponent<E> {
  private Consumer<EntityComponentButton<E>> onPressConsumer;
  public EntityComponentButton(Sizing sizing, EntityType<E> type, @Nullable NbtCompound nbt) {
    super(sizing, type, nbt);
  }

  public void onPress(Consumer<EntityComponentButton<E>> onPressConsumer) {
    this.onPressConsumer = onPressConsumer;
  }

  @Override
  public boolean onMouseDown(double mouseX, double mouseY, int button) {
    super.onMouseDown(mouseX, mouseY, button);
    if (onPressConsumer != null)
      onPressConsumer.accept(this);
    return true;
  }
}
