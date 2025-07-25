package de.tomalbrc.tameable_ravagers.mixin;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AreaEffectCloud.class)
public interface AreaEffectCoudAccessor {
    @Accessor
    Map<Entity, Integer> getVictims();
}
