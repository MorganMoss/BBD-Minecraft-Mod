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
import za.co.bbd.minecraft.blockEntities.RedstoneTransmitterEntity;
import za.co.bbd.minecraft.blocks.RedstoneReceiverBlock;
import za.co.bbd.minecraft.blocks.RedstoneTransmitterBlock;

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

    if (world.getBlockState(blockPos).getBlock() instanceof RedstoneReceiverBlock) {
      world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0f,
          1.0f);
      if (world.isClient) {
        playerEntity.sendMessage(Text.literal("Receiver Location " + blockPos.toShortString() + " copied!"));
      }
      this.storedPosition = blockPos;
    } else if (world.getBlockState(blockPos).getBlock() instanceof RedstoneTransmitterBlock) {
      world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0f,
          1.0f);
      if (world.isClient) {
        playerEntity
            .sendMessage(
                Text.literal("Receiver Location " + storedPosition.toShortString() + " loaded into Transmitter!"));
      }
      ((RedstoneTransmitterEntity) world.getBlockEntity(blockPos)).setTarget(storedPosition);
    } else {
      if (world.isClient) {
        playerEntity.sendMessage(Text.literal("Current Copied Location: " + storedPosition.toShortString()));
      }
    }

    return ActionResult.success(world.isClient);
  }
}
