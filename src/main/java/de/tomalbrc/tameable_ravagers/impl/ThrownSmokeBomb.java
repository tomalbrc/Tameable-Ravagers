package de.tomalbrc.tameable_ravagers.impl;

//public class ThrownSmokeBomb extends ThrowableItemProjectile implements PolymerEntity {
//
//    public ThrownSmokeBomb(Level level, LivingEntity livingEntity) {
//        super(TameableRavagers.THROWN_POTION, livingEntity, level);
//    }
//
//    public ThrownSmokeBomb(EntityType<? extends ThrownSmokeBomb> type, Level level) {
//        super(type, level);
//    }
//
//    @Override
//    protected @NotNull Item getDefaultItem() {
//        return Items.PAPER;
//    }
//
//    @Override
//    protected void onHit(HitResult hitResult) {
//        super.onHit(hitResult);
//        if (!this.level().isClientSide) {
//            ItemStack itemStack = this.getItem();
//            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
//
//            this.makeAreaOfEffectCloud(potionContents);
//
//            int i = potionContents.potion().isPresent() && ((Potion)((Holder)potionContents.potion().get()).value()).hasInstantEffects() ? 2007 : 2002;
//            this.level().levelEvent(i, this.blockPosition(), potionContents.getColor());
//            this.discard();
//        }
//    }
//
//    private void makeAreaOfEffectCloud(PotionContents potionContents) {
//        AreaEffectCloud areaEffectCloud = new SmokeBombCloud(this.level(), this.getX(), this.getY(), this.getZ());
//        Entity var4 = this.getOwner();
//        if (var4 instanceof LivingEntity livingEntity) {
//            areaEffectCloud.setOwner(livingEntity);
//        }
//
//        areaEffectCloud.setRadius(3.0F);
//        areaEffectCloud.setRadiusOnUse(-0.5F);
//        areaEffectCloud.setWaitTime(10);
//        areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / (float)areaEffectCloud.getDuration());
//        areaEffectCloud.setPotionContents(potionContents);
//        this.level().addFreshEntity(areaEffectCloud);
//    }
//
//    @Override
//    public EntityType<?> getPolymerEntityType(ServerPlayer serverPlayer) {
//        return EntityType.SMALL_FIREBALL;
//    }
//
//    @Override
//    public void tick() {
//        super.tick();
//
//        if (this.level() instanceof ServerLevel serverLevel) {
//            serverLevel.sendParticles (ParticleTypes.LARGE_SMOKE, position().x, position().y + getBbHeight()/2, position().z, 1, 0, 0, 0, 0);
//        }
//    }
//}
