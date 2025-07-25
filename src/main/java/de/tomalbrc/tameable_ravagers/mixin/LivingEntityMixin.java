package de.tomalbrc.tameable_ravagers.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.tomalbrc.tameable_ravagers.impl.TameRavager;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isControlledByLocalInstance()Z"))
    public boolean tr$travel(LivingEntity instance, Operation<Boolean> original) {
        return instance instanceof TameRavager || original.call(instance);
    }

    @WrapOperation(method = "travelRidden", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isControlledByLocalInstance()Z"))
    public boolean tr$travelRidden(LivingEntity instance, Operation<Boolean> original) {
        return instance instanceof TameRavager tameRavager ? tameRavager.isTamed() && tameRavager.isVehicle() : original.call(instance);
    }
}
