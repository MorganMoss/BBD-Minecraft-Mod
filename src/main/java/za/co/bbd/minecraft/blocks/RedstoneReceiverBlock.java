package za.co.bbd.minecraft.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RedstoneReceiverBlock extends Block {

    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public RedstoneReceiverBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ACTIVE, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (world.getBlockState(pos).get(ACTIVE)) {
            player.playSound(SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 1);
            world.setBlockState(pos, state.with(ACTIVE, false));
        } else {
            player.playSound(SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, 1, 1);
            world.setBlockState(pos, state.with(ACTIVE, true));
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (world.getBlockState(pos).get(ACTIVE)) {
            return 15;
        } else {
            return 0;
        }
    }
}
