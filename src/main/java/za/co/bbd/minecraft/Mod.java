package za.co.bbd.minecraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider.Entries;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

import org.apache.commons.lang3.RegExUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.bbd.minecraft.backpack.BackpackItem;
import za.co.bbd.minecraft.chat.ChatGPTEndpoint;
import za.co.bbd.minecraft.registry.ModBlocks;
import za.co.bbd.minecraft.registry.ModItemGroups;
import za.co.bbd.minecraft.registry.ModItems;


public class Mod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Mod.class.getName());
    public static final String MOD_ID = "bbd";
    public static final ItemGroup BACKPACK_GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "backpack"))
        .icon(() -> new ItemStack(Items.DIAMOND))
        .build();
    public static final Item BACKPACK = new BackpackItem(new Item.Settings().maxCount(1));


    //This should move but lets see how it goes
    

    @Override
    public void onInitialize() {
        LOGGER.info( "Initializing Mod for " + MOD_ID);
        ModItemGroups.registerModItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ChatGPTEndpoint.initialize();

        ItemGroupEvents.modifyEntriesEvent(BACKPACK_GROUP).register(content -> {
            content.add(BACKPACK);
        });

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "backpack"), BACKPACK);
    }
}
