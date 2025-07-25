package de.tomalbrc.tameable_ravagers.mixin;

import de.tomalbrc.tameable_ravagers.impl.TameRavager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    // Something about polymer seems to be buggy...
    @Inject(method = "interact", at = @At(value = "HEAD"), cancellable = true)
    private void tr$onInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (interactionHand == InteractionHand.OFF_HAND && (Object)this instanceof TameRavager tameableRavager && tameableRavager.isLeashed()) {
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }
}
