package za.co.bbd.minecraft.tileEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import static za.co.bbd.minecraft.registry.ModBlocks.REDSTONE_TRANSMITTER_BLOCK_ENTITY;

public class RedstoneTransmitterEntity extends BlockEntity {

  private boolean isActive = false;

  public RedstoneTransmitterEntity(BlockPos pos, BlockState state) {
    super(REDSTONE_TRANSMITTER_BLOCK_ENTITY, pos, state);
  }

  @Override
  public void writeNbt(NbtCompound nbt) {
    nbt.putBoolean("isActive", isActive);

    super.writeNbt(nbt);
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);

    isActive = nbt.getBoolean("isActive");
  }

  public void toggle() {
    isActive = !isActive;
    markDirty();
  }

  public boolean getState() {
    return isActive;
  }
}
