package me.gravityio.creativeplus.api.nbt.frame;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.potion.Potion;

import java.util.List;

/**
 * {@link net.minecraft.entity.AreaEffectCloudEntity#writeCustomDataToNbt}
 */
public interface AreaEffectCloudFrame {
    int getAge();
    int getDuration();
    int getWaitTime();
    int getReapplicationDelay();
    int getDurationOnUse();
    int getColor();
    float getRadiusOnUse();
    float getRadiusPerTick();
    float getRadius();
    ParticleEffect getParticle();
    Potion getPotion();
    List<StatusEffectInstance> getEffects();

    void setAge(int v);
    void setDuration(int v);
    void setWaitTime(int v);
    void setReapplicationDelay(int v);
    void setDurationOnUse(int v);
    void setColor(int v);
    void setRadiusOnUse(float v);
    void setRadiusPerTick(float v);
    void setRadius(float v);
    void setParticle(ParticleEffect v);
    void setPotion(Potion v);
    void setEffects(List<StatusEffectInstance> v);
}
