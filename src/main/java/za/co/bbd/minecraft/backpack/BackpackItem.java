package za.co.bbd.minecraft.backpack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import za.co.bbd.minecraft.database.Database;

public class BackpackItem extends Item{
    Database db = new Database();
    public BackpackItem(Settings settings) {
        super(settings);
        db.setupConnection();
    }

    @Override
    public boolean canBeNested() {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(player.world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));
        // This is called when backpack opens, so need to get contents

        player.setCurrentHand(hand);
        ItemStack stack = player.getStackInHand(hand);
        player.openHandledScreen(createScreenHandlerFactory(stack));

        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    private NamedScreenHandlerFactory createScreenHandlerFactory(ItemStack stack)
    {
        return new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                NewBackpackScreenHandler.createGeneric9x6(i, playerInventory, new BackpackInventory(stack, db)), stack.getName());
    }
}
