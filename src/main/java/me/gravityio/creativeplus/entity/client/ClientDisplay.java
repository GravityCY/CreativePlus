package me.gravityio.creativeplus.entity.client;

import me.gravityio.creativeplus.api.nbt.frame.DisplayFrame;
import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import me.gravityio.creativeplus.lib.helper.EntityNbtHelper.Display;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.math.AffineTransformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ClientDisplay extends ClientEntity implements DisplayFrame {
    private final DisplayEntity transform;
    private final DisplayEntity display;
    private DisplayEntity.BillboardMode realMode;
    private DisplayEntity.BillboardMode mode;

    private static String key(String name) {
        return "display.nbt." + name;
    }
    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.ofEnum(this::getBillboardMode, (unknown) -> this.setBillboardMode((DisplayEntity.BillboardMode) unknown), DisplayEntity.BillboardMode.class, key("billboard"))
    );

    public ClientDisplay(DisplayEntity display, DisplayEntity transform, NbtCompound realNbt) {
        super(display, transform, realNbt);

        this.display = display;
        this.transform = transform;

        this.init();
    }

    private static NbtString writeBillboard(DisplayEntity.BillboardMode mode) {
        AtomicReference<NbtString> temp = new AtomicReference<>();
        DisplayEntity.BillboardMode.CODEC
                .encodeStart(NbtOps.INSTANCE, mode)
                .result()
                .ifPresent(billboard -> temp.set((NbtString) billboard));
        return temp.get();
    }


    private static DisplayEntity.BillboardMode readBillboard(NbtCompound nbt) {
        AtomicReference<DisplayEntity.BillboardMode> temp = new AtomicReference<>();

        DisplayEntity.BillboardMode.CODEC
                .decode(NbtOps.INSTANCE, nbt.get(DisplayEntity.BILLBOARD_NBT_KEY))
                .result()
                .ifPresent(pair -> temp.set(pair.getFirst()));

        return temp.get();
    }


    private void init() {
        this.mode = this.display.getBillboardMode();
    }

    @Override
    protected void updateRealNbt() {
        super.updateRealNbt();

        if (super.realNbt.contains(Display.BILLBOARD_NBT_KEY, NbtElement.STRING_TYPE)) {
            this.mode = ClientDisplay.readBillboard(super.realNbt);
            this.realMode = this.mode;
        }
    }

    @Override
    public NbtCompound getOutput() {
        if (this.mode != this.realMode) {
            super.output.put(Display.BILLBOARD_NBT_KEY, ClientDisplay.writeBillboard(this.mode));
        }

        return super.getOutput();
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(this.pieces);
        return a;
    }

    @Override
    public AffineTransformation getTransformations() {
        return null;
    }

    @Override
    public Vector3f getTranslation() {
        return null;
    }

    @Override
    public Vector3f getScale() {
        return null;
    }

    @Override
    public Quaternionf getLeftRotation() {
        return null;
    }

    @Override
    public Quaternionf getRightRotation() {
        return null;
    }

    @Override
    public DisplayEntity.BillboardMode getBillboardMode() {
        return this.mode;
    }

    @Override
    public int getInterpolationDuration() {
        return 0;
    }

    @Override
    public float getViewRange() {
        return 0;
    }

    @Override
    public float getShadowRadius() {
        return 0;
    }

    @Override
    public float getShadowStrength() {
        return 0;
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public int getGlowColor() {
        return 0;
    }

    @Override
    public Brightness getBrightness() {
        return null;
    }

    @Override
    public void setTransformations(AffineTransformation transformations) {

    }

    @Override
    public void setTranslation(Vector3f translation) {

    }

    @Override
    public void setScale(Vector3f scale) {

    }

    @Override
    public void setLeftRotation(Quaternionf leftRotation) {

    }

    @Override
    public void setRightRotation(Quaternionf rightRotation) {

    }

    @Override
    public void setBillboardMode(DisplayEntity.BillboardMode billboardMode) {
        this.transform.setBillboardMode(billboardMode);
        this.mode = billboardMode;
    }

    @Override
    public void setInterpolationDuration(int interpolationDuration) {

    }

    @Override
    public void setViewRange(float viewRange) {

    }

    @Override
    public void setShadowRadius(float shadowRadius) {

    }

    @Override
    public void setShadowStrength(float shadowStrength) {

    }

    @Override
    public void setWidth(float width) {

    }

    @Override
    public void setHeight(float height) {

    }

    @Override
    public void setGlowColor(int glowColor) {

    }

    @Override
    public void setBrightness(Brightness brightness) {

    }
}
