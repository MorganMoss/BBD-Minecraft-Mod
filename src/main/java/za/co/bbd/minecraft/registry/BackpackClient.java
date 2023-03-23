package za.co.bbd.minecraft.registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import za.co.bbd.minecraft.backpack.BackpackScreenHandler;

@Environment(EnvType.CLIENT)
public class BackpackClient implements ClientModInitializer{

    public static ScreenHandlerType<BackpackScreenHandler> BACKPACK_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, createID("backpack"), new ScreenHandlerType<>(BackpackScreenHandler::new));

    @Override
    public void onInitializeClient() {
        HandledScreens.register(BACKPACK_SCREEN_HANDLER, GenericContainerScreen::new);
    }

    private static Identifier createID(String path) {
        return new Identifier("BBD", path);
    }
}
