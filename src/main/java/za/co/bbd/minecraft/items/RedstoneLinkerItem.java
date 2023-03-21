package za.co.bbd.minecraft.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import za.co.bbd.minecraft.blocks.RedstoneReceiverBlock;

public class RedstoneLinkerItem extends Item {

  private BlockPos storedPosition;

  public RedstoneLinkerItem(Item.Settings settings) {
    super(settings);
    this.storedPosition = new BlockPos(0, 0, 0);
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    BlockPos blockPos = context.getBlockPos();
    World world = context.getWorld();
    PlayerEntity playerEntity = context.getPlayer();

    if (world.isClient) {
      if (world.getBlockState(blockPos).getBlock() instanceof RedstoneReceiverBlock) {
        world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0f,
            1.0f);
        playerEntity.sendMessage(Text.literal("Position " + blockPos.toShortString() + " stored!"));
        this.storedPosition = blockPos;
      } else {
        playerEntity.sendMessage(Text.literal("Saved position: " + storedPosition.toShortString()));
      }
    }

    return ActionResult.success(world.isClient);
  }
}
