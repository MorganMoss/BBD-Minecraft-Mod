package za.co.bbd.minecraft.registry;

import net.fabricmc.api.ModInitializer;
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
import za.co.bbd.minecraft.blocks.RedstoneReceiverBlock;

import static za.co.bbd.minecraft.Mod.LOGGER;
import static za.co.bbd.minecraft.Mod.MOD_ID;
import static za.co.bbd.minecraft.registry.ModItemGroups.addToItemGroup;

public class ModBlocks {

    // Blocks
    // TODO: need to make a custom Block Class with an onUse of some kind
    // maybe it opens up a ui that accepts a code that you can match to a
    // transmitters code
    // Alternatively, have using the item on this handle that for you.
    // then will listen for a use of a transmitter.
    // https://fabricmc.net/wiki/tutorial:blocks
    // https://fabricmc.net/wiki/tutorial:callbacks

    // TODO: Should have an active state, where when true - acts as a redstone
    // source and uses another texture
    // https://fabricmc.net/wiki/tutorial:blockstate

    // TODO: Could make this a directional block like a repeater
    // Would need to change how the textures work then
    // https://fabricmc.net/wiki/tutorial:directionalblock

    // TODO: (Optional) See if theres a better way to setup tool requirements than a
    // config file.

    public static final RedstoneReceiverBlock REDSTONE_RECEIVER_BLOCK = registerRedstoneReceiverBlock(
            "redstone_receiver_block",
            new RedstoneReceiverBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(4.0f)),
            ItemGroups.REDSTONE, ModItemGroups.BBD);

    // Initializer
    public static void registerModBlocks() {
        LOGGER.info("Registering Mod Blocks for " + MOD_ID);
    }

    // Helper Methods
    private static Item registerRedstoneReceiverBlockItem(String name, RedstoneReceiverBlock block,
            ItemGroup... groups) {
        final Item registeredBlockItem = Registry.register(
                Registries.ITEM,
                new Identifier(MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
        addToItemGroup(registeredBlockItem, groups);
        return registeredBlockItem;
    }

    private static RedstoneReceiverBlock registerRedstoneReceiverBlock(String name, RedstoneReceiverBlock block,
            ItemGroup... groups) {
        Item registeredBlockItem = registerRedstoneReceiverBlockItem(name, block, groups);
        return Registry.register(
                Registries.BLOCK,
                new Identifier(MOD_ID, name),
                block);
    }

}
