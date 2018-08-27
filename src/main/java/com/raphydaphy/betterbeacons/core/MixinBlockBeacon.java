package com.raphydaphy.betterbeacons.core;

import com.raphydaphy.betterbeacons.beacon.TileEntityBetterBeacon;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockBeacon.class)
public class MixinBlockBeacon extends BlockContainer implements IBucketPickupHandler, ILiquidContainer
{
	private static final BooleanProperty WATERLOGGED;

	static
	{
		WATERLOGGED = BlockStateProperties.WATERLOGGED;
	}

	/**
	 * @author raphydaphy
	 * @reason Overwrite is necessary to initialize the waterlogged property
	 */
	@Overwrite
	public MixinBlockBeacon(Builder builder)
	{
		super(builder);
		this.setDefaultState((this.stateContainer.getBaseState()).withProperty(WATERLOGGED, false));
	}

	protected void fillStateContainer(net.minecraft.state.StateContainer.Builder<Block, IBlockState> stateMap) {
		stateMap.add(new IProperty[]{WATERLOGGED});
	}

	/**
	 * @author raphydaphy
	 * @reason In order to prevent replacing the entire TileEntityBeacon class, I need to overwrite this to use a custom TileEntity
	 */
	@Overwrite
	public TileEntity createNewTileEntity(IBlockReader reader)
	{
		return new TileEntityBetterBeacon();
	}

	/**
	 * @author raphydaphy
	 * @reason This is required to open my Better Beacon GUI instead of the vanilla one
	 */
	@Overwrite
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float i, float dont, float care)
	{
		if (!world.isRemote)
		{
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityBetterBeacon)
			{
				player.displayGUIChest((IInventory) te);
				player.addStat(StatList.BEACON_INTERACTION);
			}
		}
		return true;
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState state, EnumFacing facing, IBlockState otherState, IWorld world, BlockPos pos, BlockPos otherPos)
	{
		if ((Boolean) state.getValue(WATERLOGGED))
		{
			world.getPendingFluidTicks().scheduleUpdate(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return super.updatePostPlacement(state, facing, otherState, world, pos, otherPos);
	}

	@Override
	public Fluid pickupFluid(IWorld world, BlockPos pos, IBlockState state) {
		if ((Boolean)state.getValue(WATERLOGGED)) {
			world.setBlockState(pos, (IBlockState)state.withProperty(WATERLOGGED, false), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public IFluidState getFluidState(IBlockState state) {
		return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader reader, BlockPos pos, IBlockState state, Fluid fluid) {
		return !(Boolean)state.getValue(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld world, BlockPos pos, IBlockState state, IFluidState fluidState) {
		if (!(Boolean)state.getValue(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
			if (!world.isRemote()) {
				world.setBlockState(pos, (IBlockState)state.withProperty(WATERLOGGED, true), 3);
				world.getPendingFluidTicks().scheduleUpdate(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		IFluidState fluidState = ctx.getWorld().getFluidState(ctx.getPos());

		return this.getDefaultState().withProperty(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}
}
