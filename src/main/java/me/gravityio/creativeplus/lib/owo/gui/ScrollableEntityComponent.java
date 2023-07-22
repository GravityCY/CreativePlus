package me.gravityio.creativeplus.lib.owo.gui;

import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

/**
 * An Entity Component Button that allows for scrolling in and out
 */
public class ScrollableEntityComponent<E extends Entity> extends EntityComponent<E> {
    public ScrollableEntityComponent(Sizing sizing, E entity) {
        super(sizing, entity);
        this.init();
    }

    public ScrollableEntityComponent(Sizing sizing, EntityType<E> type, @Nullable NbtCompound nbt) {
        super(sizing, type, nbt);
        this.init();
    }

    private float getEntitySize() {
        float width = 0.5f / super.entity.getWidth();
        float height = 0.5f / super.entity.getHeight();
        return Math.min(width, height);
    }

    private void init() {
        super.mouseScroll().subscribe((mouseX, mouseY, amount) -> {
            float size = this.getEntitySize();
            float mod = Screen.hasControlDown() ? 8 * size : 4 * size;
            super.scale = (float) MathHelper.clamp(super.scale + mouseY * amount * 0.0001f * mod, size * 0.5f, size * 2f);
            return true;
        });
    }
}
