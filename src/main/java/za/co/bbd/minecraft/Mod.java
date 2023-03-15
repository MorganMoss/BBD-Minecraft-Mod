package za.co.bbd.minecraft;

import net.fabricmc.api.ModInitializer;
import za.co.bbd.minecraft.registry.ModItems;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Mod implements ModInitializer {

    public static final Logger LOGGER = Logger.getLogger(Mod.class.getName());
    public static final String MOD_ID = "bbd";

    @Override
    public void onInitialize() {
        LOGGER.log(Level.INFO, "Initializing Mod");
        ModItems.registerModItems();
    }
}
