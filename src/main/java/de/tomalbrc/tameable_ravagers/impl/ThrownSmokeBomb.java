package de.tomalbrc.tameable_ravagers.impl;

import de.tomalbrc.tameable_ravagers.TameableRavagers;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class ThrownSmokeBomb extends ThrowableItemProjectile implements PolymerEntity {

    public ThrownSmokeBomb(Level level) {
        super(TameableRavagers.THROWN_POTION, level);
    }

    public ThrownSmokeBomb(EntityType<? extends ThrownSmokeBomb> type, Level level) {
        super(type, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.PAPER;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            ItemStack itemStack = this.getItem();
            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

            this.makeAreaOfEffectCloud(potionContents);

            int i = potionContents.potion().isPresent() && ((Potion)((Holder)potionContents.potion().get()).value()).hasInstantEffects() ? 2007 : 2002;
            this.level().levelEvent(i, this.blockPosition(), potionContents.getColor());
            this.discard();
        }
    }

    private void makeAreaOfEffectCloud(PotionContents potionContents) {
        AreaEffectCloud areaEffectCloud = new SmokeBombCloud(this.level(), this.getX(), this.getY(), this.getZ());
        Entity var4 = this.getOwner();
        if (var4 instanceof LivingEntity livingEntity) {
            areaEffectCloud.setOwner(livingEntity);
        }

        areaEffectCloud.setRadius(3.0F);
        areaEffectCloud.setRadiusOnUse(-0.5F);
        areaEffectCloud.setWaitTime(10);
        areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / (float)areaEffectCloud.getDuration());
        areaEffectCloud.setPotionContents(potionContents);
        this.level().addFreshEntity(areaEffectCloud);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext serverPlayer) {
        return EntityTypes.SMALL_FIREBALL;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles (ParticleTypes.LARGE_SMOKE, position().x, position().y + getBbHeight()/2, position().z, 1, 0, 0, 0, 0);
        }
    }
}
