package me.gravityio.creativeplus.entity.client;

import me.gravityio.creativeplus.api.nbt.frame.AbstractMinecartFrame;
import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import me.gravityio.creativeplus.lib.helper.EntityNbtHelper.AbstractMinecart;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class ClientAbstractMinecart extends ClientEntity implements AbstractMinecartFrame {

    // TODO: CAPABILITY OF BLOCKSTATE?

    private final AbstractMinecartEntity minecart;
    private final AbstractMinecartEntity transform;
    private boolean customDisplayTile;
    private BlockState displayState;
    private int displayOffset;

    private String key(String v) {
        return "abstract_minecart.nbt." + v;
    }

    public final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::hasCustomDisplayState, this::setHasCustomDisplayState, NbtPiece.Type.BOOLEAN, key("custom_display_tile")),
//            NbtPiece.of(this::getCustomDisplayState, this::setCustomDisplayState, NbtPiece.Type.BLOCK_STATE, key("display_state")),
            NbtPiece.of(this::getDisplayOffset, this::setDisplayOffset, NbtPiece.Type.INT, key("display_offset"))
    );

    public ClientAbstractMinecart(AbstractMinecartEntity minecart, AbstractMinecartEntity transform, NbtCompound realNbt) {
        super(minecart, transform, realNbt);

        this.transform = transform;
        this.minecart = minecart;
        this.init();
    }

    private void init() {
        this.customDisplayTile = this.minecart.hasCustomBlock();
        this.displayState = this.minecart.getContainedBlock();
        this.displayOffset = this.minecart.getBlockOffset();
    }

    @Override
    protected void updateRealNbt() {
        super.updateRealNbt();

        this.customDisplayTile = super.realNbt.getBoolean(AbstractMinecart.CUSTOM_DISPLAY_TILE_PRESENT);
        if (this.customDisplayTile) {
//            this.displayState = NbtHelper.toBlockState(this.minecart.world.createCommandRegistryWrapper(RegistryKeys.BLOCK), super.realNbt.getCompound(AbstractMinecart.DISPLAY_STATE));
            this.displayOffset = super.realNbt.getInt(AbstractMinecart.DISPLAY_OFFSET);
        }
    }

    @Override
    public NbtCompound getOutput() {
        super.outputNull(AbstractMinecart.CUSTOM_DISPLAY_TILE_PRESENT, this.customDisplayTile, NbtCompound::putBoolean, NbtCompound::getBoolean);
//        if (this.displayState != null) {
//            super.outputNull(AbstractMinecart.DISPLAY_STATE, NbtHelper.fromBlockState(this.displayState), NbtCompound::put, NbtCompound::get);
//        } else {
//            super.outputNull(AbstractMinecart.DISPLAY_STATE, null, NbtCompound::put, NbtCompound::get);
//        }
        super.outputNull(AbstractMinecart.DISPLAY_OFFSET, this.displayOffset, NbtCompound::putInt, NbtCompound::getInt);


        return super.getOutput();
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }

    @Override
    public boolean hasCustomDisplayState() {
        return this.customDisplayTile;
    }

    @Override
    public BlockState getCustomDisplayState() {
        return this.displayState;
    }

    @Override
    public int getDisplayOffset() {
        return this.displayOffset;
    }

    @Override
    public void setHasCustomDisplayState(boolean hasCustomDisplayTile) {
        transform.setCustomBlockPresent(hasCustomDisplayTile);
        this.customDisplayTile = hasCustomDisplayTile;
    }

    @Override
    public void setCustomDisplayState(BlockState displayState) {
        transform.setCustomBlock(displayState);
        this.displayState = displayState;
    }

    @Override
    public void setDisplayOffset(int displayOffset) {
        this.transform.setCustomBlockOffset(displayOffset);
        this.displayOffset = displayOffset;
    }
}
