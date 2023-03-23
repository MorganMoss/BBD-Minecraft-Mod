package za.co.bbd.minecraft.items;

import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
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

  private HashSet<BlockPos> storedPositions;

  public RedstoneLinkerItem(Item.Settings settings) {
    super(settings);
    this.storedPositions = new HashSet<BlockPos>();
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    BlockPos blockPos = context.getBlockPos();
    World world = context.getWorld();
    PlayerEntity playerEntity = context.getPlayer();
    BlockEntity blockEntity = world.getBlockEntity(blockPos);
    Block block = world.getBlockState(blockPos).getBlock();

    // Add Receiver to list
    if (block instanceof RedstoneReceiverBlock) {
      world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0f,
          1.0f);
      if (world.isClient) {
        playerEntity.sendMessage(Text.literal("Receiver Location added!"));
      }
      this.storedPositions.add(blockPos);

      // Store List in Transmitter
    } else if (block instanceof RedstoneTransmitterBlock) {
      world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0f,
          1.0f);

      ((RedstoneTransmitterEntity) blockEntity).setTargets(List.copyOf(this.storedPositions));
      if (!world.isClient)
        this.storedPositions.clear();

      if (world.isClient) {
        playerEntity
            .sendMessage(
                Text.literal("Receiver Locations loaded into Transmitter!"));
      }

      // Check List
    } else {
      if (world.isClient) {
        playerEntity.sendMessage(Text.literal("Current Receiver list:" + storedPositions));
      }
    }

    return ActionResult.success(world.isClient);
  }
}
