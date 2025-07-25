package de.tomalbrc.tameable_ravagers.impl;

import de.tomalbrc.tameable_ravagers.mixin.AreaEffectCoudAccessor;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.Map;

public class SmokeBombCloud extends AreaEffectCloud implements PolymerEntity {
    int age = 0;

    public SmokeBombCloud(EntityType<? extends SmokeBombCloud> entityType, Level level) {
        super(entityType, level);
    }


    public SmokeBombCloud(Level level, double x, double y, double z) {
        super(level, x, y, z);

    }

    @Override
    public void tick() {
        super.tick();

        for (Map.Entry<Entity, Integer> entry : ((AreaEffectCoudAccessor) this).getVictims().entrySet()) {
            if (entry.getKey() instanceof RavagerConfusion ravager) {
                ravager.setConfusedTicks(10*20);
            }
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            if (age < 2) {
                serverLevel.sendParticles (ParticleTypes.FLASH, position().x, position().y + getBbHeight()/2, position().z, 5, 1, 1, 1, 0);
            }

            // lots of particles, slow down ravager
            this.getBoundingBox().getXsize();
            var scale = 1.f;
            serverLevel.sendParticles (ParticleTypes.CLOUD, position().x, position().y + getBbHeight()*2, position().z, 10, getBbWidth()/scale, getBbHeight()*2, getBbWidth()/scale, 0);
            serverLevel.sendParticles (ParticleTypes.LARGE_SMOKE, position().x, position().y + getBbHeight()*2, position().z, 5, getBbWidth()/scale, getBbHeight()*2, getBbWidth()/scale, 0);
            serverLevel.sendParticles (ParticleTypes.SMOKE, position().x, position().y + getBbHeight()*2, position().z, 30, getBbWidth()/scale, getBbHeight()*2, getBbWidth()/scale, 0);
            serverLevel.sendParticles (ParticleTypes.WHITE_SMOKE, position().x, position().y + getBbHeight()*2, position().z, 30, getBbWidth()/scale, getBbHeight()*2, getBbWidth()/scale, 0);
            serverLevel.sendParticles (new DustParticleOptions(new Vector3f(1), 8f), position().x, position().y + getBbHeight()*2, position().z, 50, getBbWidth()/scale, getBbHeight()*2, getBbWidth()/scale, 0);
        }

        age++;
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayer serverPlayer) {
        return EntityType.AREA_EFFECT_CLOUD;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("CustomAge")) this.age = compoundTag.getInt("CustomAge");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("CustomAge", this.age);
    }
}
