package com.raphydaphy.betterbeacons.beacon;

import com.raphydaphy.betterbeacons.BetterBeaconsMod;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TileEntityBetterBeacon extends TileEntityBeacon
{
    private static final String IRON_KEY = "Iron";
    private static final String GOLD_KEY = "Gold";
    private static final String EMERALD_KEY = "Emerald";
    private static final String DIAMOND_KEY = "Diamond";
    private static final String ACTIVATED_TIERS_KEY = "ActivatedTier";

    // How many of each block is in the beacon structure
    private int iron = 0;
    private int gold = 0;
    private int emerald = 0;
    private int diamond = 0;

    private ItemStack star;
    private boolean[] activatedTiers = new boolean[4];

    public TileEntityBetterBeacon()
    {
        super();
        this.star = ItemStack.EMPTY;
    }

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
    public int getField(int field)
    {
        switch (field)
        {
            case 0:
                return this.levels;
            case 1:
                return countToStage(iron);
            case 2:
                return countToStage(gold);
            case 3:
                return countToStage(emerald);
            case 4:
                return countToStage(diamond);
            default:
                if (field > 4 && field < 9)
                {
                    return activatedTiers[field - 5] ? 1 : 0;
                }
                return 0;
        }
    }

    @Override
    public void setField(int field, int value)
    {
        if (field == 0)
        {
            this.levels = value;
        } else if (field > 4 && field < 9)
        {
            if (value == 1 && !activatedTiers[field - 5])
            {
                this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT);
            }
            activatedTiers[field - 5] = value == 1;
        }
        markDirty();
    }

    @Override
    public int getFieldCount()
    {
        return 9;
    }

    private int countToStage(int count) // 164 in a completed pyramid
    {
        if (count >= 164)
        {
            return 5;
        } else if (count >= 96)
        {
            return 4;
        } else if (count >= 48)
        {
            return 3;
        } else if (count >= 16)
        {
            return 2;
        } else if (count >= 4)
        {
            return 1;
        }
        return 0;
    }

    @Override
    public String getGuiID()
    {
        return BetterBeaconsMod.MOD_ID + ":better_beacon";
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.iron = tag.getInteger(IRON_KEY);
        this.gold = tag.getInteger(GOLD_KEY);
        this.emerald = tag.getInteger(EMERALD_KEY);
        this.diamond = tag.getInteger(DIAMOND_KEY);
        for (int i = 0; i < 4; i++)
        {
            activatedTiers[i] = tag.getBoolean(ACTIVATED_TIERS_KEY + i);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger(IRON_KEY, iron);
        tag.setInteger(GOLD_KEY, gold);
        tag.setInteger(EMERALD_KEY, emerald);
        tag.setInteger(DIAMOND_KEY, diamond);
        for (int i = 0; i < 4; i++)
        {
            tag.setBoolean(ACTIVATED_TIERS_KEY + i, activatedTiers[i]);
        }
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
        BeamSegment lvt_5_1_ = new BeamSegment(EnumDyeColor.WHITE.getColorComponentValues());
        this.beamSegments.add(lvt_5_1_);
        boolean lvt_6_1_ = true;
        BlockPos.MutableBlockPos lvt_7_1_ = new BlockPos.MutableBlockPos();

        int counter;
        for(counter = posY + 1; counter < 256; ++counter) {
            IBlockState lvt_9_1_ = this.world.getBlockState(lvt_7_1_.setPos(posX, counter, posZ));
            Block lvt_11_1_ = lvt_9_1_.getBlock();
            float[] lvt_10_3_;
            if (lvt_11_1_ instanceof BlockStainedGlass) {
                lvt_10_3_ = ((BlockStainedGlass)lvt_11_1_).getColor().getColorComponentValues();
            } else {
                if (!(lvt_11_1_ instanceof BlockStainedGlassPane)) {
                    if (lvt_9_1_.getOpacity(this.world, lvt_7_1_) >= 15 && lvt_11_1_ != Blocks.BEDROCK) {
                        this.isComplete = false;
                        this.beamSegments.clear();
                        break;
                    }

                    lvt_5_1_.incrementHeight();
                    continue;
                }

                lvt_10_3_ = ((BlockStainedGlassPane)lvt_11_1_).getColor().getColorComponentValues();
            }

            if (!lvt_6_1_) {
                lvt_10_3_ = new float[]{(lvt_5_1_.getColors()[0] + lvt_10_3_[0]) / 2.0F, (lvt_5_1_.getColors()[1] + lvt_10_3_[1]) / 2.0F, (lvt_5_1_.getColors()[2] + lvt_10_3_[2]) / 2.0F};
            }

            if (Arrays.equals(lvt_10_3_, lvt_5_1_.getColors())) {
                lvt_5_1_.incrementHeight();
            } else {
                lvt_5_1_ = new BeamSegment(lvt_10_3_);
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
                } else
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
        if (this.isComplete && this.levels > 0 && !this.world.isRemote)
        {
            double range = (double) (this.levels * 10 + 10);
            int potionDurations = (9 + this.levels * 2) * 20;
            int posX = this.pos.getX();
            int posY = this.pos.getY();
            int posZ = this.pos.getZ();

            AxisAlignedBB beaconAOE = (new AxisAlignedBB((double) posX, (double) posY, (double) posZ, (double) (posX + 1), (double) (posY + 1), (double) (posZ + 1))).grow(range).expand(0.0D, (double) this.world.getHeight(), 0.0D);
            List<EntityPlayer> nearbyPlayers = this.world.getEntitiesWithinAABB(EntityPlayer.class, beaconAOE);
            Iterator nearbyPlayersIterator = nearbyPlayers.iterator();

            EntityPlayer nextPlayer;
            while (nearbyPlayersIterator.hasNext())
            {
                nextPlayer = (EntityPlayer) nearbyPlayersIterator.next();
                // Offensive (iron)
                if (activatedTiers[0])
                {
                    int tier = countToStage(iron);
                    if (tier >= 1)
                    {
                        nextPlayer.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, potionDurations, tier >= 2 ? 1 : 0, true, true));
                    }
                    if (tier >= 2)
                    {
                        nextPlayer.addPotionEffect(new PotionEffect(BetterBeaconsMod.FAST_ATTACK_POTION, potionDurations, tier >= 3 ? 1 : 0, true, true));
                    }
                }
                // Quality of Life (gold)
                if (activatedTiers[1])
                {
                    int tier = countToStage(gold);
                    if (tier >= 1)
                    {
                        nextPlayer.addPotionEffect(new PotionEffect(MobEffects.SPEED, potionDurations, tier >= 2 ? 1 : 0, true, true));
                    }
                    if (tier >= 2)
                    {
                        nextPlayer.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, potionDurations, tier >= 3 ? 1 : 0, true, true));
                    }
                    if (tier >= 3)
                    {
                        nextPlayer.addPotionEffect(new PotionEffect(MobEffects.HASTE, potionDurations, tier >= 4 ? 1 : 0, true, true));
                    }
                    if (tier >= 5)
                    {
                        nextPlayer.addPotionEffect(new PotionEffect(BetterBeaconsMod.LONG_REACH_POTION, potionDurations, 0, true, true));
                    }
                }
                // Defensive (diamond)
                if (activatedTiers[3])
                {
                    int tier = countToStage(diamond);
                    if (tier >= 1)
                    {
                        nextPlayer.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, potionDurations, tier >= 2 ? 1 : 0, true, true));
                    }
                }
            }

            // Resistance for Iron/Snow golems and villagers ( Defense Tier 3 )
            if (activatedTiers[0])
            {
                int tier = countToStage(iron);
                if (tier >= 3)
                {
                    List<EntityGolem> nearbyGolems = this.world.getEntitiesWithinAABB(EntityGolem.class, beaconAOE);

                    for (EntityGolem golem : nearbyGolems)
                    {
                        golem.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, potionDurations, tier >= 4 ? 1 : 0, false, true));
                    }
                }
            }
        }
    }

    @Override
    public int getSizeInventory()
    {
        return 2;
    }

    public boolean isEmpty()
    {
        return this.payment.isEmpty() && this.star.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int id)
    {
        return id == 0 ? this.payment : (id == 1 ? this.star : ItemStack.EMPTY);
    }

    @Override
    public ItemStack decrStackSize(int id, int amount)
    {
        if (id == 0 && !this.payment.isEmpty())
        {
            if (amount >= this.payment.getCount())
            {
                ItemStack lvt_3_1_ = this.payment;
                this.payment = ItemStack.EMPTY;
                return lvt_3_1_;
            } else
            {
                return this.payment.splitStack(amount);
            }
        } else if (id == 1 && !this.star.isEmpty())
        {
            if (amount >= this.star.getCount())
            {
                ItemStack lvt_3_1_ = this.star;
                this.star = ItemStack.EMPTY;
                return lvt_3_1_;
            } else
            {
                return this.star.splitStack(amount);
            }
        } else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int id)
    {
        if (id == 0)
        {
            ItemStack copy = this.payment;
            this.payment = ItemStack.EMPTY;
            return copy;
        } else if (id == 1)
        {
            ItemStack copy = this.star;
            this.star = ItemStack.EMPTY;
            return copy;
        } else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int id, ItemStack stack)
    {
        if (id == 0)
        {
            this.payment = stack;
        } else if (id == 1)
        {
            this.star = stack;
        }
    }

    @Override
    public Container createContainer(InventoryPlayer playerInv, EntityPlayer player)
    {
        return new ContainerBetterBeacon(playerInv, this);
    }

    @Override
    public void clear()
    {
        super.clear();
        this.star = ItemStack.EMPTY;
    }
}