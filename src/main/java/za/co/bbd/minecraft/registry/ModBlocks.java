package za.co.bbd.minecraft.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import za.co.bbd.minecraft.blockEntities.RedstoneTransmitterEntity;
import za.co.bbd.minecraft.blocks.RedstoneReceiverBlock;
import za.co.bbd.minecraft.blocks.RedstoneTransmitterBlock;

import static za.co.bbd.minecraft.Mod.LOGGER;
import static za.co.bbd.minecraft.Mod.MOD_ID;
import static za.co.bbd.minecraft.registry.ModItemGroups.addToItemGroup;

public class ModBlocks {

        // Alternatively, have using the item on this handle that for you.
        // then will listen for a use of a transmitter.
        // https://fabricmc.net/wiki/tutorial:blocks
        // https://fabricmc.net/wiki/tutorial:callbacks

        // TODO: Could make this a directional block like a repeater
        // Would need to change how the textures work then
        // https://fabricmc.net/wiki/tutorial:directionalblock

        // TODO: (Optional) See if theres a better way to setup tool requirements than a
        // config file.

        public static final Block REDSTONE_RECEIVER_BLOCK = registerBlock(
                        "redstone_receiver_block",
                        new RedstoneReceiverBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(4.0f)),
                        ItemGroups.REDSTONE, ModItemGroups.BBD);

        public static final Block REDSTONE_TRANSMITTER_BLOCK = registerBlock(
                        "redstone_transmitter_block",
                        new RedstoneTransmitterBlock(
                                        FabricBlockSettings.of(Material.METAL).requiresTool().strength(4.0f)),
                        ItemGroups.REDSTONE, ModItemGroups.BBD);

        public static final BlockEntityType<RedstoneTransmitterEntity> REDSTONE_TRANSMITTER_BLOCK_ENTITY = Registry
                        .register(Registries.BLOCK_ENTITY_TYPE,
                                        new Identifier(MOD_ID, "redstone_transmitter_entity"),
                                        FabricBlockEntityTypeBuilder
                                                        .create(RedstoneTransmitterEntity::new,
                                                                        REDSTONE_TRANSMITTER_BLOCK)
                                                        .build());

        // Initializer
        public static void registerModBlocks() {
                LOGGER.info("Registering Mod Blocks for " + MOD_ID);
        }

        // Helper Methods
        private static Block registerBlock(String name, Block block,
                        ItemGroup... groups) {
                registerBlockItem(name, block, groups);
                return Registry.register(
                                Registries.BLOCK,
                                new Identifier(MOD_ID, name),
                                block);
        }

        private static Item registerBlockItem(String name, Block block,
                        ItemGroup... groups) {
                final Item registeredBlockItem = Registry.register(
                                Registries.ITEM,
                                new Identifier(MOD_ID, name),
                                new BlockItem(block, new FabricItemSettings()));
                addToItemGroup(registeredBlockItem, groups);
                return registeredBlockItem;
        }
}
