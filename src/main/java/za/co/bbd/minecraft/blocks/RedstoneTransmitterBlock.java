package za.co.bbd.minecraft.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import za.co.bbd.minecraft.blockEntities.RedstoneTransmitterEntity;

import static za.co.bbd.minecraft.registry.ModBlocks.REDSTONE_RECEIVER_BLOCK;

import jakarta.annotation.Nullable;

public class RedstoneTransmitterBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public RedstoneTransmitterBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState) this.getDefaultState().with(ACTIVE, false));
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState().with(ACTIVE,
                ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
            boolean notify) {
        BlockPos target = ((RedstoneTransmitterEntity) world.getBlockEntity(pos)).getTarget();
        if (world.getBlockState(target).getBlock() instanceof RedstoneReceiverBlock) {
            if (world.isReceivingRedstonePower(pos)) {
                world.setBlockState(target, REDSTONE_RECEIVER_BLOCK.getDefaultState().with(ACTIVE, true));
                world.setBlockState(pos, (BlockState) state.with(ACTIVE, true));
            } else {
                world.setBlockState(target, REDSTONE_RECEIVER_BLOCK.getDefaultState().with(ACTIVE, false));
                world.setBlockState(pos, (BlockState) state.with(ACTIVE, false));
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneTransmitterEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
}
