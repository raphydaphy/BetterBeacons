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

	protected void fillStateContainer(net.minecraft.state.StateContainer.Builder<Block, IBlockState> p_fillStateContainer_1_) {
		p_fillStateContainer_1_.add(new IProperty[]{WATERLOGGED});
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
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand p_onBlockActivated_5_, EnumFacing p_onBlockActivated_6_, float p_onBlockActivated_7_, float p_onBlockActivated_8_, float p_onBlockActivated_9_)
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
	public IBlockState updatePostPlacement(IBlockState p_updatePostPlacement_1_, EnumFacing p_updatePostPlacement_2_, IBlockState p_updatePostPlacement_3_, IWorld p_updatePostPlacement_4_, BlockPos p_updatePostPlacement_5_, BlockPos p_updatePostPlacement_6_)
	{
		if ((Boolean) p_updatePostPlacement_1_.getValue(WATERLOGGED))
		{
			p_updatePostPlacement_4_.getPendingFluidTicks().scheduleUpdate(p_updatePostPlacement_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_updatePostPlacement_4_));
		}
		return super.updatePostPlacement(p_updatePostPlacement_1_, p_updatePostPlacement_2_, p_updatePostPlacement_3_, p_updatePostPlacement_4_, p_updatePostPlacement_5_, p_updatePostPlacement_6_);
	}

	@Override
	public Fluid pickupFluid(IWorld p_pickupFluid_1_, BlockPos p_pickupFluid_2_, IBlockState p_pickupFluid_3_) {
		if ((Boolean)p_pickupFluid_3_.getValue(WATERLOGGED)) {
			p_pickupFluid_1_.setBlockState(p_pickupFluid_2_, (IBlockState)p_pickupFluid_3_.withProperty(WATERLOGGED, false), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public IFluidState getFluidState(IBlockState p_getFluidState_1_) {
		return (Boolean)p_getFluidState_1_.getValue(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_getFluidState_1_);
	}

	@Override
	public boolean canContainFluid(IBlockReader p_canContainFluid_1_, BlockPos p_canContainFluid_2_, IBlockState p_canContainFluid_3_, Fluid p_canContainFluid_4_) {
		return !(Boolean)p_canContainFluid_3_.getValue(WATERLOGGED) && p_canContainFluid_4_ == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld p_receiveFluid_1_, BlockPos p_receiveFluid_2_, IBlockState p_receiveFluid_3_, IFluidState p_receiveFluid_4_) {
		if (!(Boolean)p_receiveFluid_3_.getValue(WATERLOGGED) && p_receiveFluid_4_.getFluid() == Fluids.WATER) {
			if (!p_receiveFluid_1_.isRemote()) {
				p_receiveFluid_1_.setBlockState(p_receiveFluid_2_, (IBlockState)p_receiveFluid_3_.withProperty(WATERLOGGED, true), 3);
				p_receiveFluid_1_.getPendingFluidTicks().scheduleUpdate(p_receiveFluid_2_, Fluids.WATER, Fluids.WATER.getTickRate(p_receiveFluid_1_));
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
