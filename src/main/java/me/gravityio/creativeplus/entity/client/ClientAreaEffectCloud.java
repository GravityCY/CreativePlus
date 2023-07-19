package me.gravityio.creativeplus.entity.client;

import me.gravityio.creativeplus.api.nbt.frame.AreaEffectCloudFrame;
import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import me.gravityio.creativeplus.lib.helper.EntityNbtHelper.AreaEffectCloud;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.potion.Potion;

import java.util.List;

import static me.gravityio.creativeplus.api.nbt.pieces.NbtPiece.Type.FLOAT;
import static me.gravityio.creativeplus.api.nbt.pieces.NbtPiece.Type.INT;

public class ClientAreaEffectCloud extends ClientEntity implements AreaEffectCloudFrame {

    // TODO: CAPABILITY OF EFFECTS POTIONS ETC?

    private final AreaEffectCloudEntity cloud;
    private final AreaEffectCloudEntity transform;

    private int age;
    private int duration;
    private int waitTime;
    private int reapplicationDelay;
    private int durationOnUse;
    private int color;
    private float radiusOnUse;
    private float radiusPerTick;
    private float radius;
    private ParticleEffect particle;
    private Potion potion;
    private List<StatusEffectInstance> effects;

    private String key(String v) {
        return "area_effect_cloud.nbt." + v;
    }

    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.ofBounded(this::getAge, this::setAge, INT, key("age"), -24000, 24000),
            NbtPiece.of(this::getDuration, this::setDuration, INT, key("duration")),
            NbtPiece.of(this::getWaitTime, this::setWaitTime, INT, key("wait_time")),
            NbtPiece.of(this::getReapplicationDelay, this::setReapplicationDelay, INT, key("reapplication_delay")),
            NbtPiece.of(this::getDurationOnUse, this::setDurationOnUse, INT, key("duration_on_use")),
            NbtPiece.of(this::getColor, this::setColor, INT, key("color")),
            NbtPiece.of(this::getRadiusOnUse, this::setRadiusOnUse, FLOAT, key("radius_on_use")),
            NbtPiece.of(this::getRadiusPerTick, this::setRadiusPerTick, FLOAT, key("radius_per_tick")),
            NbtPiece.of(this::getRadius, this::setRadius, FLOAT, key("radius"))/*,
            NbtPiece.of(this::getParticle, this::setParticle, STRING, key("particle")),
            NbtPiece.of(this::getPotion, this::setPotion, STRING, key("potion")),
            NbtPiece.of(this::getEffects, this::setEffects, STRING, key("effects"))*/
    );

    public ClientAreaEffectCloud(AreaEffectCloudEntity cloud, AreaEffectCloudEntity transform, NbtCompound realNbt) {
        super(cloud, transform, realNbt);

        this.cloud = cloud;
        this.transform = transform;

        this.init();
    }

    private void init() {
        this.age = this.cloud.age;
        this.duration = this.cloud.getDuration();
        this.waitTime = this.cloud.getWaitTime();
        this.reapplicationDelay = this.cloud.reapplicationDelay;
        this.durationOnUse = this.cloud.getDurationOnUse();
        this.color = this.cloud.getColor();
        this.radiusOnUse = this.cloud.getRadiusOnUse();
        this.radiusPerTick = this.cloud.getRadiusGrowth();
        this.radius = this.cloud.getRadius();

//        this.particle = this.cloud.getParticleType();
//        this.potion = this.cloud.getPotion();
//        this.effects = this.cloud.getPotion().getEffects();
    }

    @Override
    protected void updateRealNbt() {
        super.updateRealNbt();

        this.age = super.realNbt.getInt(AreaEffectCloud.AGE);
        this.duration = super.realNbt.getInt(AreaEffectCloud.DURATION);
        this.waitTime = super.realNbt.getInt(AreaEffectCloud.WAIT_TIME);
        this.reapplicationDelay = super.realNbt.getInt(AreaEffectCloud.REAPPLICATION_DELAY);
        this.durationOnUse = super.realNbt.getInt(AreaEffectCloud.DURATION_ON_USE);
        this.color = super.realNbt.getInt(AreaEffectCloud.COLOR);
        this.radiusOnUse = super.realNbt.getFloat(AreaEffectCloud.RADIUS_ON_USE);
        this.radiusPerTick = super.realNbt.getFloat(AreaEffectCloud.RADIUS_PER_TICK);
        this.radius = super.realNbt.getFloat(AreaEffectCloud.RADIUS);
//        try {
//            this.particle = ParticleEffectArgumentType.readParameters(new StringReader(super.realNbt.getString("Particle")), Registries.PARTICLE_TYPE.getReadOnlyWrapper());
//        } catch (CommandSyntaxException e) {
//            CreativePlus.LOGGER.error("Couldn't Load Custom Particle.");
//        }
//        this.potion = PotionUtil.getPotion(super.realNbt);
//        this.effects = super.realNbt.getList("Effects", NbtCompound.class);
    }

    @Override
    public NbtCompound getOutput() {
        output(AreaEffectCloud.AGE, this.age, NbtCompound::putInt, NbtCompound::getInt);
        output(AreaEffectCloud.DURATION, this.duration, NbtCompound::putInt, NbtCompound::getInt);
        output(AreaEffectCloud.WAIT_TIME, this.waitTime, NbtCompound::putInt, NbtCompound::getInt);
        output(AreaEffectCloud.REAPPLICATION_DELAY, this.reapplicationDelay, NbtCompound::putInt, NbtCompound::getInt);
        output(AreaEffectCloud.DURATION_ON_USE, this.durationOnUse, NbtCompound::putInt, NbtCompound::getInt);
        output(AreaEffectCloud.COLOR, this.color, NbtCompound::putInt, NbtCompound::getInt);
        output(AreaEffectCloud.RADIUS_ON_USE, this.radiusOnUse, NbtCompound::putFloat, NbtCompound::getFloat);
        output(AreaEffectCloud.RADIUS_PER_TICK, this.radiusPerTick, NbtCompound::putFloat, NbtCompound::getFloat);
        output(AreaEffectCloud.RADIUS, this.radius, NbtCompound::putFloat, NbtCompound::getFloat);

//        output(AreaEffectCloud.PARTICLE, this.particle, NbtCompound::putString, NbtCompound::getString);
//        output(AreaEffectCloud.POTION, this.potion, NbtCompound::putString, NbtCompound::getString);
//        output(AreaEffectCloud.EFFECTS, this.effects, NbtCompound::putString, NbtCompound::getString);

        return super.getOutput();
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    @Override
    public int getWaitTime() {
        return this.waitTime;
    }

    @Override
    public int getReapplicationDelay() {
        return this.reapplicationDelay;
    }

    @Override
    public int getDurationOnUse() {
        return this.durationOnUse;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public float getRadiusOnUse() {
        return this.radiusOnUse;
    }

    @Override
    public float getRadiusPerTick() {
        return this.radiusPerTick;
    }

    @Override
    public float getRadius() {
        return this.radius;
    }

    @Override
    public ParticleEffect getParticle() {
        return this.particle;
    }

    @Override
    public Potion getPotion() {
        return this.potion;
    }

    @Override
    public List<StatusEffectInstance> getEffects() {
        return this.effects;
    }

    @Override
    public void setAge(int v) {
        this.transform.age = v;
        this.age = v;
    }

    @Override
    public void setDuration(int v) {
        this.transform.setDuration(v);
        this.duration = v;
    }

    @Override
    public void setWaitTime(int v) {
        this.transform.setWaitTime(v);
        this.waitTime = v;
    }

    @Override
    public void setReapplicationDelay(int v) {
        this.transform.reapplicationDelay = v;
        this.reapplicationDelay = v;
    }

    @Override
    public void setDurationOnUse(int v) {
        this.transform.setDurationOnUse(v);
        this.durationOnUse = v;
    }

    @Override
    public void setColor(int v) {
        this.transform.setColor(v);
        this.color = v;
    }

    @Override
    public void setRadiusOnUse(float v) {
        this.transform.setRadiusOnUse(v);
        this.radiusOnUse = v;
    }

    @Override
    public void setRadiusPerTick(float v) {
        this.transform.setRadiusGrowth(v);
        this.radiusPerTick = v;
    }

    @Override
    public void setRadius(float v) {
        this.transform.setRadius(v);
        this.radius = v;
    }

    @Override
    public void setParticle(ParticleEffect v) {
        this.transform.setParticleType(v);
        this.particle = v;
    }

    @Override
    public void setPotion(Potion v) {
        this.transform.setPotion(v);
        this.potion = v;
    }

    @Override
    public void setEffects(List<StatusEffectInstance> v) {
        this.effects = v;
    }
}
