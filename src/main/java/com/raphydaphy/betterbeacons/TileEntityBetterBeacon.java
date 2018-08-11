package com.raphydaphy.betterbeacons;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Iterator;

public class TileEntityBetterBeacon extends TileEntityBeacon
{
    private static final String IRON_KEY = "Iron";
    private static final String GOLD_KEY = "Gold";
    private static final String EMERALD_KEY = "Emerald";
    private static final String DIAMOND_KEY = "Diamond";

    // How many of each block is in the beacon structure
    private int iron = 0;
    private int gold = 0;
    private int emerald = 0;
    private int diamond = 0;

    @Override
    public void updateBeacon()
    {
        if (this.world != null)
        {
            this.updateSegmentColorsBetter();
            this.addBetterEffects();
        }

    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.iron = tag.getInteger(IRON_KEY);
        this.gold = tag.getInteger(GOLD_KEY);
        this.emerald = tag.getInteger(EMERALD_KEY);
        this.diamond = tag.getInteger(DIAMOND_KEY);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger(IRON_KEY, iron);
        tag.setInteger(GOLD_KEY, gold);
        tag.setInteger(EMERALD_KEY, emerald);
        tag.setInteger(DIAMOND_KEY, diamond);
        return tag;
    }

    private void updateSegmentColorsBetter()
    {
        int posX = this.pos.getX();
        int posY = this.pos.getY();
        int posZ = this.pos.getZ();
        int oldLevels = this.levels;
        this.levels = 0;
        this.beamSegments.clear();
        this.isComplete = true;
        TileEntityBeacon.BeamSegment lvt_5_1_ = new TileEntityBeacon.BeamSegment(EnumDyeColor.WHITE.getColorComponentValues());
        this.beamSegments.add(lvt_5_1_);
        boolean lvt_6_1_ = true;
        BlockPos.MutableBlockPos lvt_7_1_ = new BlockPos.MutableBlockPos();

        int counter;
        for (counter = posY + 1; counter < 256; ++counter)
        {
            IBlockState lvt_9_1_ = this.world.getBlockState(lvt_7_1_.setPos(posX, counter, posZ));
            Block lvt_11_1_ = lvt_9_1_.getBlock();
            float[] lvt_10_3_;
            if (lvt_11_1_ instanceof BlockStainedGlass)
            {
                lvt_10_3_ = ((BlockStainedGlass) lvt_11_1_).func_196457_d().getColorComponentValues();
            } else
            {
                if (!(lvt_11_1_ instanceof BlockStainedGlassPane))
                {
                    if (lvt_9_1_.getOpacity(this.world, lvt_7_1_) >= 15 && lvt_11_1_ != Blocks.BEDROCK)
                    {
                        this.isComplete = false;
                        this.beamSegments.clear();
                        break;
                    }

                    lvt_5_1_.incrementHeight();
                    continue;
                }

                lvt_10_3_ = ((BlockStainedGlassPane) lvt_11_1_).func_196419_d().getColorComponentValues();
            }

            if (!lvt_6_1_)
            {
                lvt_10_3_ = new float[]{(lvt_5_1_.getColors()[0] + lvt_10_3_[0]) / 2.0F, (lvt_5_1_.getColors()[1] + lvt_10_3_[1]) / 2.0F, (lvt_5_1_.getColors()[2] + lvt_10_3_[2]) / 2.0F};
            }

            if (Arrays.equals(lvt_10_3_, lvt_5_1_.getColors()))
            {
                lvt_5_1_.incrementHeight();
            } else
            {
                lvt_5_1_ = new TileEntityBeacon.BeamSegment(lvt_10_3_);
                this.beamSegments.add(lvt_5_1_);
            }

            lvt_6_1_ = false;
        }

        if (this.isComplete)
        {
            this.iron = 0;
            this.gold = 0;
            this.emerald = 0;
            this.diamond = 0;
            for (counter = 1; counter <= 4; this.levels = counter++)
            {
                int lvt_9_2_ = posY - counter;
                if (lvt_9_2_ < 0)
                {
                    break;
                }

                boolean levelComplete = true;

                int levelIron = 0;
                int levelGold = 0;
                int levelEmerald = 0;
                int levelDiamond = 0;

                for (int lvt_11_2_ = posX - counter; lvt_11_2_ <= posX + counter && levelComplete; ++lvt_11_2_)
                {
                    for (int lvt_12_1_ = posZ - counter; lvt_12_1_ <= posZ + counter; ++lvt_12_1_)
                    {
                        Block block = this.world.getBlockState(new BlockPos(lvt_11_2_, lvt_9_2_, lvt_12_1_)).getBlock();
                        if (block == Blocks.IRON_BLOCK)
                        {
                            levelIron++;
                        } else if (block == Blocks.GOLD_BLOCK)
                        {
                            levelGold++;
                        } else if (block == Blocks.EMERALD_BLOCK)
                        {
                            levelEmerald++;
                        } else if (block == Blocks.DIAMOND_BLOCK)
                        {
                            levelDiamond++;
                        } else
                        {
                            levelComplete = false;
                            break;
                        }
                    }
                }

                if (levelComplete)
                {
                    this.iron += levelIron;
                    this.gold += levelGold;
                    this.emerald += levelEmerald;
                    this.diamond += levelDiamond;
                }
                else
                {
                    break;
                }
            }

            if (this.levels == 0)
            {
                this.isComplete = false;
            }
        }

        if (!this.world.isRemote && oldLevels < this.levels)
        {
            Iterator var14 = this.world.getEntitiesWithinAABB(EntityPlayerMP.class, (new AxisAlignedBB((double) posX, (double) posY, (double) posZ, (double) posX, (double) (posY - 4), (double) posZ)).grow(10.0D, 5.0D, 10.0D)).iterator();

            while (var14.hasNext())
            {
                EntityPlayerMP lvt_9_3_ = (EntityPlayerMP) var14.next();
                CriteriaTriggers.CONSTRUCT_BEACON.trigger(lvt_9_3_, this);
            }
        }

    }

    private void addBetterEffects()
    {
        if (diamond > 15)
        {
            System.out.println(diamond + " A LOT OF DIAMOND");
        }
        if (gold > 30)
        {
            System.out.println("Many golds " + gold);
        }
        if (emerald > 5)
        {
            System.out.println("EMERAL " + emerald);
        }
        if (iron > 50)
        {
            System.out.println(iron + " iron for day");
        }
    }
}