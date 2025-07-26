package de.tomalbrc.tameable_ravagers.impl;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class TameRavager extends Horse implements PolymerEntity, Leashable {
    private int attackCooldown = -1;

    public TameRavager(EntityType<? extends Horse> entityType, Level level) {
        super(entityType, level);
        this.setTamed(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.JUMP_STRENGTH, 0.7)
                .add(Attributes.MAX_HEALTH, 50.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.STEP_HEIGHT, 1.0F)
                .add(Attributes.SAFE_FALL_DISTANCE, 6.0F)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0.5F)
                .add(Attributes.CAMERA_DISTANCE, 5F);
    }

    @Override
    public void modifyRawEntityAttributeData(List<ClientboundUpdateAttributesPacket.AttributeSnapshot> data, ServerPlayer player, boolean initial) {
        data.add(snapshot(Attributes.CAMERA_DISTANCE));
        data.add(snapshot(Attributes.MAX_HEALTH));
        data.add(snapshot(Attributes.MOVEMENT_SPEED));
    }

    private ClientboundUpdateAttributesPacket.AttributeSnapshot snapshot(Holder<Attribute> attribute) {
        return new ClientboundUpdateAttributesPacket.AttributeSnapshot(attribute, this.getAttribute(attribute).getBaseValue(), this.getAttribute(attribute).getModifiers());
    }

    @Override
    protected void randomizeAttributes(RandomSource randomSource) {

    }

    @Override
    protected @NotNull Component getTypeName() {
        return Component.literal("Tame Ravager");
    }

    @Override
    protected void playGallopSound(SoundType soundType) {
        super.playGallopSound(soundType);
    }

    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected @Nullable SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT.value();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected @NotNull SoundEvent getAngrySound() {
        return SoundEvents.RAVAGER_ROAR;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        if (!blockState.liquid()) {
            BlockState blockState2 = this.level().getBlockState(blockPos.above());
            SoundType soundType = blockState.getSoundType();
            if (blockState2.is(Blocks.SNOW)) {
                soundType = blockState2.getSoundType();
            }

            if (this.isVehicle() && this.canGallop) {
                ++this.gallopSoundCounter;
                if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                    this.playGallopSound(soundType);
                } else if (this.gallopSoundCounter <= 5) {
                    this.playSound(SoundEvents.RAVAGER_STEP, soundType.getVolume() * 0.15F, soundType.getPitch());
                }
            } else if (this.isWoodSoundType(soundType)) {
                this.playSound(SoundEvents.RAVAGER_STEP, soundType.getVolume() * 0.15F, soundType.getPitch());
            } else {
                this.playSound(SoundEvents.RAVAGER_STEP, soundType.getVolume() * 0.15F, soundType.getPitch());
            }

        }
    }

    private boolean isWoodSoundType(SoundType soundType) {
        return soundType == SoundType.WOOD || soundType == SoundType.NETHER_WOOD || soundType == SoundType.STEM || soundType == SoundType.CHERRY_WOOD || soundType == SoundType.BAMBOO_WOOD;
    }

    @Override
    protected void playJumpSound() {
        this.playSound(SoundEvents.RAVAGER_STUNNED, 0.4F, 1.0F);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.RAVAGER;
    }

    @Override
    @NotNull
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
        if (attackCooldown != -1)
            return Vec3.ZERO;

        ServerPlayer p = (ServerPlayer) player;
        float x = p.getLastClientInput().left() ? 1 : p.getLastClientInput().right() ? -1 : 0;
        float z = p.getLastClientInput().forward() ? 1 : p.getLastClientInput().backward() ? -1 : 0;
        if (z <= 0.0F) {
            z *= 0.5F;
        }

        return new Vec3(x, 0.0, z).normalize().scale(this.getRiddenSpeed(player)*2.f);
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof ServerPlayer player) {
            return player;
        } else if (!this.isNoAi() && passenger instanceof Mob mob) {
            return mob;
        }
        return null;
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        if (getOwner() == null)
            tameWithName(player);

        this.setRot(player.getYRot(), player.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yBodyRotO = this.yHeadRot = this.yHeadRotO = this.getYRot();

        if (attackCooldown == -1 && player instanceof ServerPlayer serverPlayer && (serverPlayer.getLastClientInput().jump())) {
            attackCooldown += 30;
        } else if (attackCooldown > 15) {
            if (this.getPose() != Pose.ROARING)
                roar();
            this.setPose(Pose.ROARING);
        } else if (attackCooldown < 5) {
            this.setPose(Pose.STANDING);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (attackCooldown >= 0) attackCooldown--;
    }

    private void roar() {
        if (this.isAlive()) {
            for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0F), (entity) -> !(entity instanceof TameRavager) && getControllingPassenger() != entity)) {
                if (!(livingEntity instanceof AbstractIllager)) {
                    livingEntity.hurt(this.damageSources().mobAttack(this), 6.0F);
                }

                this.strongKnockback(livingEntity);
            }

            Vec3 vec3 = this.getBoundingBox().getCenter();

            for(int i = 0; i < 40; ++i) {
                double d = this.random.nextGaussian() * 0.2;
                double e = this.random.nextGaussian() * 0.2;
                double f = this.random.nextGaussian() * 0.2;
                ((ServerLevel)this.level()).sendParticles(ParticleTypes.POOF, vec3.x, vec3.y, vec3.z, 0, d, e, f, 1);
            }

            this.gameEvent(GameEvent.ENTITY_ACTION);
        }

    }

    private void strongKnockback(Entity entity) {
        double d = entity.getX() - this.getX();
        double e = entity.getZ() - this.getZ();
        double f = Math.max(d * d + e * e, 0.001);
        entity.push(d / f * (double)4.0F, 0.2, e / f * (double)4.0F);
    }

    @Override
    public void setInLove(@Nullable Player player) {
        if (this.level() instanceof ServerLevel level) {
            for (int i = 0; i < 7; ++i) {
                double xOffset = this.random.nextGaussian() * 0.02;
                double yOffset = this.random.nextGaussian() * 0.02;
                double zOffset = this.random.nextGaussian() * 0.02;
                level.sendParticles(ParticleTypes.HEART, this.getRandomX(1), this.getRandomY() + 0.5, this.getRandomZ(1), 0, xOffset, yOffset, zOffset, 0);
            }
        }

        super.setInLove(player);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (player.isSecondaryUseActive() && !isVehicle()) {
            var item = player.getItemInHand(interactionHand);
            if (item.isEmpty()) {
                openCustomInventoryScreen(player);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, interactionHand);
    }



    @Override
    public void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);

        if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1), this.getRandomY() + 0.5, this.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
            }

            --this.forcedAgeTimer;
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason mobSpawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setTamed(true);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
    }

    @Override
    public boolean canUseSlot(EquipmentSlot equipmentSlot) {
        return false;
    }

    @Override
    public void setBaby(boolean bl) {
        super.setBaby(false);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }
}
