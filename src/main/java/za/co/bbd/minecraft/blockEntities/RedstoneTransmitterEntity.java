package za.co.bbd.minecraft.blockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

import static za.co.bbd.minecraft.registry.ModBlocks.REDSTONE_TRANSMITTER_BLOCK_ENTITY;

import jakarta.annotation.Nullable;

public class RedstoneTransmitterEntity extends BlockEntity {

  private BlockPos target = new BlockPos(0, 0, 0);

  public RedstoneTransmitterEntity(BlockPos pos, BlockState state) {
    super(REDSTONE_TRANSMITTER_BLOCK_ENTITY, pos, state);
  }

  @Override
  public void writeNbt(NbtCompound nbt) {
    int[] pos = { target.getX(), target.getY(), target.getZ() };
    nbt.putIntArray("target", pos);
    super.writeNbt(nbt);
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    int[] pos = nbt.getIntArray("target");
    target = new BlockPos(pos[0], pos[1], pos[2]);
  }

  @Nullable
  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  @Override
  public NbtCompound toInitialChunkDataNbt() {
    return createNbt();
  }

  public void setTarget(BlockPos pos) {
    this.target = pos;
    markDirty();
  }

  public BlockPos getTarget() {
    return this.target;
  }
}
