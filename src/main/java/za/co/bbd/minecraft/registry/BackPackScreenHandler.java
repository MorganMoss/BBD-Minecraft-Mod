package za.co.bbd.minecraft.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import za.co.bbd.minecraft.misc.Dimension;
import za.co.bbd.minecraft.misc.Point;

public class BackPackScreenHandler extends ScreenHandler{
    private final ItemStack backpackStack;
    private final int padding = 8;
    private final int titleSpace = 10;

    private static final Identifier ID = new Identifier("bbd", "backpack");
    private static final ScreenHandlerType<BackPackScreenHandler> C_TYPE = ScreenHandlerRegistry.registerExtended(ID, BackPackScreenHandler::new);

    public BackPackScreenHandler(int syncID, PlayerInventory playerInventory, PacketByteBuf packetByteBuf) {
        this(syncID, playerInventory, packetByteBuf.readItemStack());
    }

    public BackPackScreenHandler(int syncID, PlayerInventory playerInventory, ItemStack backpackStack) {
        super(C_TYPE, syncID);
        this.backpackStack = backpackStack;

        if (backpackStack.getItem() instanceof BackPackItem) {
            setupContainer(playerInventory, backpackStack);
        } else {
            PlayerEntity player = playerInventory.player;
            this.close(player);
        }
    }

    private void setupContainer(PlayerInventory playerInventory, ItemStack backpStack) {
        Dimension dimension = getDimension();
        int rowWidth = 10;
        int numberOfRows = 4;

        NbtList tag = backpackStack.getOrCreateNbt().getList("Inventory", NbtType.COMPOUND);
        BackpackInventory inventory = new BackpackInventory(rowWidth * numberOfRows) {
            @Override
            public void markDirty() {
                backpackStack.getOrCreateNbt().put("Inventory", InventoryUtils.toTag(this));
                super.markDirty();
            }
        };

        InventoryUtils.fromTag(tag, inventory);

        for (int y = 0; y < numberOfRows; y++) {
            for (int x = 0; x < rowWidth; x++) {
                Point backpackSlotPosition = getBackpackSlotPosition(dimension, x, y);
                addSlot(new BackpackLockedSlot(inventory, y * rowWidth + x, backpackSlotPosition.x + 1, backpackSlotPosition.y + 1));
            }
        }
        
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                Point playerInvSlotPosition = getPlayerInvSlotPosition(dimension, x, y);
                this.addSlot(new BackpackLockedSlot(playerInventory, x + y * 9 + 9, playerInvSlotPosition.x + 1, playerInvSlotPosition.y + 1));
            }
        }
        
        for (int x = 0; x < 9; ++x) {
            Point playerInvSlotPosition = getPlayerInvSlotPosition(dimension, x, 3);
            this.addSlot(new BackpackLockedSlot(playerInventory, x, playerInvSlotPosition.x + 1, playerInvSlotPosition.y + 1));
        }
    }

    public Dimension getDimension(int rowWidth, int numberOfRows) {
        return new Dimension(padding * 2 + Math.max(rowWidth, 9) * 18, padding * 2 + titleSpace * 2 + 8 + (numberOfRows + 4) * 18);
    }

    public Point getBackpackSlotPosition(Dimension dimension, int x, int y) {
        return new Point(dimension.getWidth() / 2 - 10 * 9 + x * 18, padding + titleSpace + y * 18);
    }
    
    public Point getPlayerInvSlotPosition(Dimension dimension, int x, int y) {
        return new Point(dimension.getWidth() / 2 - 9 * 9 + x * 18, dimension.getHeight() - padding - 4 * 18 - 3 + y * 18 + (y == 3 ? 4 : 0));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return backpackStack.getItem() instanceof BackPackItem;
    }

    public ItemStack getBackpackStack() {
        return backpackStack;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack toInsert = slot.getStack();
            itemStack = toInsert.copy();
            BackPackInfo tier = getItem().getTier();
            if (index < tier.getNumberOfRows() * tier.getRowWidth()) {
                if (!this.insertItem(toInsert, tier.getNumberOfRows() * tier.getRowWidth(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(toInsert, 0, tier.getNumberOfRows() * tier.getRowWidth(), false)) {
                return ItemStack.EMPTY;
            }
            
            if (toInsert.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        
        return itemStack;
    }

    private class BackpackLockedSlot extends Slot {

        public BackpackLockedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        
        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return stackMovementIsAllowed(getStack());
        }
        
        @Override
        public boolean canInsert(ItemStack stack) {
            return stackMovementIsAllowed(stack);
        }

        private boolean stackMovementIsAllowed(ItemStack stack) {
            return !(stack.getItem() instanceof BackPackItem) && stack != backpackStack;
        }
    }

    public static class BackpackInventory extends SimpleInventory {

        public BackpackInventory(int slots) {
            super(slots);
        }
    }

}
