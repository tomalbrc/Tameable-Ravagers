package de.tomalbrc.tameable_ravagers.mixin;

import de.tomalbrc.tameable_ravagers.TameableRavagers;
import de.tomalbrc.tameable_ravagers.impl.RavagerConfusion;
import de.tomalbrc.tameable_ravagers.impl.TameRavager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Ravager.class)
public abstract class RavagerMixin extends Raider implements RavagerConfusion {
    @Unique int tr$confusionTicks = 0;

    protected RavagerMixin(EntityType<? extends Raider> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void setConfusedTicks(int t) {
        tr$confusionTicks = t;
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    void tr$aiStep(CallbackInfo ci) {
        if (tr$confusionTicks > 0) {
            setTarget(null);
            tr$confusionTicks--;
            ci.cancel();
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        var item = player.getItemInHand(interactionHand);
        if (item.is(Items.ENCHANTED_GOLDEN_APPLE) && tr$isWeak()) {
            TameRavager tameRavager = (TameRavager) TameableRavagers.RAVAGER.create((ServerLevel) level(), x -> {}, blockPosition(), EntitySpawnReason.CONVERSION, true, true);
            tameRavager.setPos(this.position());
            tameRavager.setLeashData(this.getLeashData());
            tameRavager.setXRot(this.getXRot());
            tameRavager.setYRot(this.getYRot());
            tameRavager.setYHeadRot(this.getYHeadRot());
            tameRavager.tameWithName(player);
            if (this.hasCustomName()) tameRavager.setCustomName(this.getCustomName());
            level().addFreshEntity(tameRavager);

            this.discard();

            item.consume(1, player);

            level().playSound(null, tameRavager, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.NEUTRAL, 1.f, 1.f);

            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, interactionHand);
    }

    @Unique
    private boolean tr$isWeak() {
        return this.hasEffect(MobEffects.SLOWNESS) && this.hasEffect(MobEffects.WEAKNESS);
    }
}
