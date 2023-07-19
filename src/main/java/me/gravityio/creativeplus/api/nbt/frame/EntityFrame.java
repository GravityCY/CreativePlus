package me.gravityio.creativeplus.api.nbt.frame;

import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import net.minecraft.text.Text;

import java.util.List;

/**
 * {@link net.minecraft.entity.Entity#writeCustomDataToNbt}
 */
public interface EntityFrame {

    boolean isGlowing();
    boolean isInvulnerable();
    boolean hasNoGravity();
    boolean isSilent();
    boolean isCustomNameVisible();
    Text getCustomName();

    void setGlowing(boolean v);
    void setInvulnerable(boolean v);
    void setNoGravity(boolean v);
    void setSilent(boolean v);
    void setCustomNameVisible(boolean v);
    void setCustomName(Text text);

    /**
     * Any subclass of the class that implements this interface should also have this
     * function overriden to merge all of it's nbt pieces into the super one!
     */
    List<NbtPiece<?>> getNbt();
}
