package me.gravityio.creativeplus.entity.living;

import me.gravityio.creativeplus.entity.nbt.NbtPiece;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.function.BiPredicate;

import static me.gravityio.creativeplus.entity.nbt.NbtPiece.Type.BOOLEAN;
import static me.gravityio.creativeplus.lib.EntityNbtHelper.ArmorStand;

/**
 * {@link ArmorStandEntity#writeCustomDataToNbt}
 */
public class ClientArmorStandEntity extends ClientLivingEntity {

    ArmorStandEntity stand;
    boolean invisible;
    boolean small;
    boolean showArms;
    boolean hideBaseplate;
    boolean marker;

    private static String key(String v) {
        return "armor_stand.nbt." + v;
    }

    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::isInvisible, this::setInvisible, BOOLEAN, key("invisible")),
            NbtPiece.of(this::isSmall, this::setSmall, BOOLEAN, key("small")),
            NbtPiece.of(this::isShowArms, this::setShowArms, BOOLEAN, key("show_arms")),
            NbtPiece.of(this::isHideBaseplate, this::setHideBaseplate, BOOLEAN, key("hide_baseplate")),
            NbtPiece.of(this::isMarker, this::setMarker, BOOLEAN, key("marker"))/*,
                NbtPiece.of(this::getDisabledSlots, this::setDisabledSlots, INT)*/
    );

    public ClientArmorStandEntity(ArmorStandEntity stand, NbtCompound realNbt) {
        super(stand, realNbt);
        this.stand = stand;

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
    public void update() {
        super.update();

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

    public boolean isShowArms() {
        return this.showArms;
    }

    public boolean isHideBaseplate() {
        return this.hideBaseplate;
    }

    public boolean isMarker() {
        return this.marker;
    }

    public int getDisabledSlots() {
        return 0;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public void setSmall(boolean small) {
        this.small = small;
    }

    public void setShowArms(boolean showArms) {
        this.showArms = showArms;
    }

    public void setHideBaseplate(boolean hideBaseplate) {
        this.hideBaseplate = hideBaseplate;
    }

    public void setMarker(boolean marker) {
        this.marker = marker;
    }

    public void setDisabledSlots(int v) {

    }

    //    public SOMETHING getHandItems() {
//
//    }

//    public SOMETHING getArmorItems() {
//
//    }

//    public void setHandItems(SOMETHING something) {
//
//    }

//    public void setArmorItems(SOMETHING something) {
//
//    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }
}
