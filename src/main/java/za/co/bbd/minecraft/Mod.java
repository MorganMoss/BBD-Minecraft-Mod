package za.co.bbd.minecraft;

import net.fabricmc.api.ModInitializer;
import za.co.bbd.minecraft.registry.ModBlocks;
import za.co.bbd.minecraft.registry.ModItemGroups;
import za.co.bbd.minecraft.registry.ModItems;

import java.util.logging.Logger;

public class Mod implements ModInitializer {
    //TODO: Improve this logger
    public static final Logger LOGGER = Logger.getLogger(Mod.class.getName());
    public static final String MOD_ID = "bbd";

    @Override
    public void onInitialize() {
        LOGGER.info( "Initializing Mod for" + MOD_ID);
        ModItemGroups.registerModItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
    }
}
