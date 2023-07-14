package me.gravityio.creativeplus.entity.frame;

import me.gravityio.creativeplus.entity.nbt.NbtPiece;
import net.minecraft.text.Text;

import java.util.List;

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

    List<NbtPiece<?>> getNbt();

}
