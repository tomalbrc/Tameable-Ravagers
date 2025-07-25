package de.tomalbrc.tameable_ravagers.impl;

//
//public class SmokeBombItem extends LingeringPotionItem implements PolymerItem {
//    public SmokeBombItem(Properties properties) {
//        super(properties.component(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SLOWNESS)));
//    }
//
//    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
//        ItemStack itemStack = player.getItemInHand(interactionHand);
//        if (!level.isClientSide) {
//            ThrownSmokeBomb thrownPotion = new ThrownSmokeBomb(level, player);
//            thrownPotion.setItem(itemStack);
//            thrownPotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
//            level.addFreshEntity(thrownPotion);
//        }
//
//        player.awardStat(Stats.ITEM_USED.get(this));
//        itemStack.consume(1, player);
//        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
//    }
//
//    @Override
//    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayer serverPlayer) {
//        return Items.SNOWBALL;
//    }
//}
