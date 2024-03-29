package me.gravityio.creativeplus.entity.client.living;

import me.gravityio.creativeplus.api.nbt.frame.living.ArmorStandFrame;
import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.function.BiPredicate;

import static me.gravityio.creativeplus.api.nbt.pieces.NbtPiece.Type.BOOLEAN;
import static me.gravityio.creativeplus.lib.helper.EntityNbtHelper.ArmorStand;

/**
 * {@link ArmorStandEntity#writeCustomDataToNbt}
 */
public class ClientArmorStandEntity extends ClientLivingEntity implements ArmorStandFrame {

    private final ArmorStandEntity transform;
    private final ArmorStandEntity stand;
    private boolean invisible;
    private boolean small;
    private boolean showArms;
    private boolean hideBaseplate;
    private boolean marker;

    private static String key(String v) {
        return "armor_stand.nbt." + v;
    }

    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::isInvisible, this::setInvisible, BOOLEAN, key("invisible")),
            NbtPiece.of(this::isSmall, this::setSmall, BOOLEAN, key("small")),
            NbtPiece.of(this::canShowArms, this::setCanShowArms, BOOLEAN, key("show_arms")),
            NbtPiece.of(this::isBaseplateHidden, this::setBaseplateHidden, BOOLEAN, key("hide_baseplate")),
            NbtPiece.of(this::isMarker, this::setMarker, BOOLEAN, key("marker"))/*,
                NbtPiece.of(this::getDisabledSlots, this::setDisabledSlots, INT)*/
    );

    public ClientArmorStandEntity(ArmorStandEntity stand, ArmorStandEntity transform, NbtCompound realNbt) {
        super(stand, transform, realNbt);
        this.stand = stand;
        this.transform = transform;

        this.init();
    }

    private void init() {
        this.invisible = this.stand.isInvisible();
        this.small = this.stand.isSmall();
        this.showArms = this.stand.shouldShowArms();
        this.hideBaseplate = this.stand.shouldHideBasePlate();
        this.marker = this.stand.isMarker();
    }

    @Override
    public void updateRealNbt() {
        super.updateRealNbt();

        this.invisible = super.realNbt.getBoolean(ArmorStand.INVISIBLE);
        this.small = super.realNbt.getBoolean(ArmorStand.SMALL);
        this.showArms = super.realNbt.getBoolean(ArmorStand.SHOW_ARMS);
        this.hideBaseplate = super.realNbt.getBoolean(ArmorStand.NO_BASEPLATE);
        this.marker = super.realNbt.getBoolean(ArmorStand.MARKER);
    }

    @Override
    public NbtCompound getOutput() {
        BiPredicate<Boolean, Boolean> p = (v, rv) -> (rv == null && v) || !v.equals(rv);

        output(ArmorStand.INVISIBLE, this.invisible, NbtCompound::putBoolean, NbtCompound::getBoolean, p);
        output(ArmorStand.SMALL, this.small, NbtCompound::putBoolean, NbtCompound::getBoolean, p);
        output(ArmorStand.SHOW_ARMS, this.showArms, NbtCompound::putBoolean, NbtCompound::getBoolean, p);
        output(ArmorStand.NO_BASEPLATE, this.hideBaseplate, NbtCompound::putBoolean, NbtCompound::getBoolean, p);
        output(ArmorStand.MARKER, this.marker, NbtCompound::putBoolean, NbtCompound::getBoolean, p);

        return super.getOutput();
    }

    public boolean isInvisible() {
        return this.invisible;
    }
    public boolean isSmall() {
        return this.small;
    }

    public boolean canShowArms() {
        return this.showArms;
    }

    public boolean isBaseplateHidden() {
        return this.hideBaseplate;
    }

    public boolean isMarker() {
        return this.marker;
    }

    public int getDisabledSlots() {
        return 0;
    }

    @Override
    public List<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public List<ItemStack> getHandItems() {
        return null;
    }

    public void setInvisible(boolean v) {
        this.transform.setInvisible(v);
        this.invisible = v;
    }

    public void setSmall(boolean v) {
        this.transform.setSmall(v);
        this.small = v;
    }

    public void setCanShowArms(boolean v) {
        this.transform.setShowArms(v);
        this.showArms = v;
    }

    public void setBaseplateHidden(boolean v) {
        this.transform.setHideBasePlate(v);
        this.hideBaseplate = v;
    }

    public void setMarker(boolean v) {
        this.transform.setMarker(v);
        this.marker = v;
    }

    public void setDisabledSlots(int v) {

    }

    @Override
    public void setArmorItems(List<ItemStack> v) {
        // TODO: ?
    }

    @Override
    public void setHandItems(List<ItemStack> v) {
        // TODO: ?
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }
}
