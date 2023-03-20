package za.co.bbd.minecraft.registry;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BackPackItem extends Item implements FabricItem{
    
    private final BackPackInfo bpinfo;

    public BackPackItem(BackPackInfo bpinfo, Item.Settings settings) {
        super(settings);
        this.bpinfo = bpinfo;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        openScreen(user, user.getStackInHand(hand));
        return TypedActionResult.success(user.getStackInHand(hand));
    }
    

    public static void openScreen(PlayerEntity player, ItemStack backpackItemStack) {
        if(player.world != null && !player.world.isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
               @Override
               public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                packetByteBuf.writeItemStack(backpackItemStack);
               }
               
               @Override
               public Text getDisplayName() {
                return (Text) new TranslatableTextContent(backpackItemStack.getItem().getTranslationKey());
               }
               
               @Override
                public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return null;
                }
            });
        }   
    }
}
