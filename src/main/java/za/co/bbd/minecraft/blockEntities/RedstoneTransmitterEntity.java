package za.co.bbd.minecraft.blockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

import static za.co.bbd.minecraft.registry.ModBlocks.REDSTONE_TRANSMITTER_BLOCK_ENTITY;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;

public class RedstoneTransmitterEntity extends BlockEntity {

  private List<BlockPos> targets = new ArrayList<BlockPos>();

  public RedstoneTransmitterEntity(BlockPos pos, BlockState state) {
    super(REDSTONE_TRANSMITTER_BLOCK_ENTITY, pos, state);
  }

  @Override
  public void writeNbt(NbtCompound nbt) {
    int[] positions = new int[this.targets.size() * 3];
    for (int i = 0; i < this.targets.size(); i++) {
      positions[(i * 3)] = this.targets.get(i).getX();
      positions[(i * 3) + 1] = this.targets.get(i).getY();
      positions[(i * 3) + 2] = this.targets.get(i).getZ();
    }
    nbt.putIntArray("targets", positions);
    super.writeNbt(nbt);
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    int[] positions = nbt.getIntArray("targets");
    this.targets = new ArrayList<BlockPos>();
    for (int i = 0; i < positions.length / 3; i++) {
      this.targets.add(new BlockPos(positions[(i * 3)], positions[(i * 3) + 1], positions[(i * 3) + 2]));
    }
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

  public void setTargets(List<BlockPos> positions) {
    this.targets = positions;
    markDirty();
  }

  public List<BlockPos> getTargets() {
    return this.targets;
  }
}
