package de.tomalbrc.tameable_ravagers.impl;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmokeBombItem extends LingeringPotionItem implements PolymerItem {
    public SmokeBombItem(Properties properties) {
        super(properties.component(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SLOWNESS)));
    }

    public @NotNull InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (!level.isClientSide()) {
            ThrownSmokeBomb thrownPotion = new ThrownSmokeBomb(level);
            thrownPotion.setItem(itemStack);
            thrownPotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(thrownPotion);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, player);
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable PacketContext serverPlayer) {
        return Items.SNOWBALL;
    }
}
