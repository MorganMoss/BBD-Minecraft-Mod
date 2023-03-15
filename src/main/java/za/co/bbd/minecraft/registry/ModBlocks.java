package za.co.bbd.minecraft.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static za.co.bbd.minecraft.Mod.LOGGER;
import static za.co.bbd.minecraft.Mod.MOD_ID;
import static za.co.bbd.minecraft.registry.ModItemGroups.addToItemGroup;

public class ModBlocks {

    //Blocks
    public static final Block REDSTONE_RECEIVER_BLOCK = registerBlock(
            "redstone_receiver_block",
            new Block(FabricBlockSettings.of(Material.PISTON).strength(4.0f)),
            ItemGroups.REDSTONE, ModItemGroups.BBD
    );


    //Initializer
    public static void registerModBlocks() {
        LOGGER.info("Registering Mod Blocks for " + MOD_ID);
    }

    //Helper Methods
    private static Item registerBlockItem(String name, Block block, ItemGroup...groups){
        final Item registeredBlockItem = Registry.register(
                Registries.ITEM,
                new Identifier(MOD_ID, name),
                new BlockItem(block, new FabricItemSettings())
        );
        addToItemGroup(registeredBlockItem, groups);
        return registeredBlockItem;
    }

    private static Block registerBlock(String name, Block block, ItemGroup...groups){
        Item registeredBlockItem = registerBlockItem(name, block, groups);
        return Registry.register(
                Registries.BLOCK,
                new Identifier(MOD_ID, name),
                block
        );
    }

}
