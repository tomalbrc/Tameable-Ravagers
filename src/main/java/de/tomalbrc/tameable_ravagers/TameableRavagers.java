package de.tomalbrc.tameable_ravagers;

import de.tomalbrc.tameable_ravagers.impl.TameRavager;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class TameableRavagers implements ModInitializer {
    public static String MODID = "tameable_ravagers";

    public static EntityType<?> RAVAGER = register(ResourceLocation.fromNamespaceAndPath(MODID, "ravager"), FabricEntityType.Builder.createLiving(TameRavager::new, MobCategory.CREATURE, x -> x.defaultAttributes(TameRavager::createAttributes)).sized(1.95F, 2.2F).passengerAttachments(new Vec3(0, 2.2625,-0.0625F)).clientTrackingRange(10));

    //public static EntityType<ThrownSmokeBomb> THROWN_POTION = register(ResourceLocation.fromNamespaceAndPath(MODID, "thrown_smoke_bomb"), EntityType.Builder.of(ThrownSmokeBomb::new, MobCategory.MISC));
    //public static EntityType<SmokeBombCloud> SMOKE_BOMB_CLOUD = register(ResourceLocation.fromNamespaceAndPath(MODID, "smoke_bomb_cloud"), EntityType.Builder.of(SmokeBombCloud::new, MobCategory.MISC));
    //public static Item SMOKE_BOMB = registerItem(ResourceLocation.fromNamespaceAndPath(MODID, "smoke_bomb"), SmokeBombItem::new, new Item.Properties());

    private static <T extends Entity> EntityType<T> register(ResourceLocation string, EntityType.Builder<T> builder) {
        var entityType = Registry.register(BuiltInRegistries.ENTITY_TYPE, string.toString(), builder.build(string.toString()));
        PolymerEntityUtils.registerType(entityType);
        return entityType;
    }


    public static ResourceKey<Item> itemKey(ResourceLocation id) {
        return ResourceKey.create(Registries.ITEM, id);
    }

    public static <T extends Item> T registerItem(ResourceLocation identifier, Function<Item.Properties, T> function, Item.Properties properties) {
        //T item = function.apply(properties.setId(identifier));
        T item = function.apply(properties);
        Registry.register(BuiltInRegistries.ITEM, itemKey(identifier), item);

        return item;
    }

    @Override
    public void onInitialize() {

    }


}
