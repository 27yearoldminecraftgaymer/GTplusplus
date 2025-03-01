package gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.generators;

import static gregtech.api.enums.GT_Values.V;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GT_UIInfos;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicTank;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import gregtech.api.util.GT_Utility;
import gtPlusPlus.core.lib.CORE;
import gtPlusPlus.core.util.math.MathUtils;
import gtPlusPlus.core.util.minecraft.gregtech.PollutionUtils;

public abstract class GregtechRocketFuelGeneratorBase extends GT_MetaTileEntity_BasicTank {

    private boolean useFuel = false;
    protected int pollMin, pollMax;

    public GregtechRocketFuelGeneratorBase(final int aID, final String aName, final String aNameRegional,
            final int aTier, final String aDescription, final ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, 3, aDescription, aTextures);
        pollMin = (int) (CORE.ConfigSwitches.baseMinPollutionPerSecondRocketFuelGenerator
                * CORE.ConfigSwitches.pollutionReleasedByTierRocketFuelGenerator[mTier]);
        pollMax = (int) (CORE.ConfigSwitches.baseMaxPollutionPerSecondRocketFuelGenerator
                * CORE.ConfigSwitches.pollutionReleasedByTierRocketFuelGenerator[mTier]);
    }

    public GregtechRocketFuelGeneratorBase(final String aName, final int aTier, final String aDescription,
            final ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
        pollMin = (int) (CORE.ConfigSwitches.baseMinPollutionPerSecondRocketFuelGenerator
                * CORE.ConfigSwitches.pollutionReleasedByTierRocketFuelGenerator[mTier]);
        pollMax = (int) (CORE.ConfigSwitches.baseMaxPollutionPerSecondRocketFuelGenerator
                * CORE.ConfigSwitches.pollutionReleasedByTierRocketFuelGenerator[mTier]);
    }

    @Override
    public ITexture[][][] getTextureSet(final ITexture[] aTextures) {
        final ITexture[][][] rTextures = new ITexture[10][17][];
        for (byte i = -1; i < 16; i++) {
            rTextures[0][i + 1] = this.getFront(i);
            rTextures[1][i + 1] = this.getBack(i);
            rTextures[2][i + 1] = this.getBottom(i);
            rTextures[3][i + 1] = this.getTop(i);
            rTextures[4][i + 1] = this.getSides(i);
            rTextures[5][i + 1] = this.getFrontActive(i);
            rTextures[6][i + 1] = this.getBackActive(i);
            rTextures[7][i + 1] = this.getBottomActive(i);
            rTextures[8][i + 1] = this.getTopActive(i);
            rTextures[9][i + 1] = this.getSidesActive(i);
        }
        return rTextures;
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity aBaseMetaTileEntity, final byte aSide, final byte aFacing,
            final byte aColorIndex, final boolean aActive, final boolean aRedstone) {
        return this.mTextures[(aActive ? 5 : 0) + (aSide == aFacing ? 0
                : aSide == GT_Utility.getOppositeSide(aFacing) ? 1 : aSide == 0 ? 2 : aSide == 1 ? 3 : 4)][aColorIndex
                        + 1];
    }

    @Override
    public String[] getDescription() {
        String aPollution = "Causes between " + pollMin + " and " + pollMax + " Pollution per second";
        return new String[] { this.mDescription, "Fuel Efficiency: " + this.getEfficiency() + "%", aPollution,
                CORE.GT_Tooltip.get() };
    }

    @Override
    public boolean onRightclick(final IGregTechTileEntity aBaseMetaTileEntity, final EntityPlayer aPlayer) {
        GT_UIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer);
        return true;
    }

    public ITexture[] getFront(final byte aColor) {
        return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[this.mTier][aColor + 1] };
    }

    public ITexture[] getBack(final byte aColor) {
        return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[this.mTier][aColor + 1] };
    }

    public ITexture[] getBottom(final byte aColor) {
        return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[this.mTier][aColor + 1] };
    }

    public ITexture[] getTop(final byte aColor) {
        return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[this.mTier][aColor + 1] };
    }

    public ITexture[] getSides(final byte aColor) {
        return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[this.mTier][aColor + 1] };
    }

    public ITexture[] getFrontActive(final byte aColor) {
        return this.getFront(aColor);
    }

    public ITexture[] getBackActive(final byte aColor) {
        return this.getBack(aColor);
    }

    public ITexture[] getBottomActive(final byte aColor) {
        return this.getBottom(aColor);
    }

    public ITexture[] getTopActive(final byte aColor) {
        return this.getTop(aColor);
    }

    public ITexture[] getSidesActive(final byte aColor) {
        return this.getSides(aColor);
    }

    @Override
    public boolean isFacingValid(final byte aSide) {
        return aSide > 1;
    }

    @Override
    public boolean isSimpleMachine() {
        return false;
    }

    @Override
    public boolean isValidSlot(final int aIndex) {
        return aIndex < 2;
    }

    @Override
    public boolean isEnetOutput() {
        return true;
    }

    @Override
    public boolean isOutputFacing(final byte aSide) {
        return this.getBaseMetaTileEntity().getFrontFacing() == aSide;
    }

    @Override
    public boolean isAccessAllowed(final EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public long maxEUOutput() {
        return V[this.mTier];
    }

    @Override
    public long maxEUStore() {
        return Math.max(this.getEUVar(), (V[this.mTier] * 500) + this.getMinimumStoredEU());
    }

    @Override
    public boolean doesFillContainers() {
        return false;
    }

    @Override
    public boolean doesEmptyContainers() {
        return true;
    }

    @Override
    public boolean canTankBeFilled() {
        return true;
    }

    @Override
    public boolean canTankBeEmptied() {
        return false;
    }

    @Override
    public boolean displaysItemStack() {
        return true;
    }

    @Override
    public boolean displaysStackSize() {
        return false;
    }

    @Override
    public boolean isFluidInputAllowed(final FluidStack aFluid) {
        return this.getFuelValue(aFluid) > 0;
    }

    @Override
    public void onPostTick(final IGregTechTileEntity aBaseMetaTileEntity, final long aTick) {

        // super.onPostTick(aBaseMetaTileEntity, aTick);

        /*
         * if (aBaseMetaTileEntity.isServerSide() && aBaseMetaTileEntity.isAllowedToWork() && aTick % 10L == 0L) { int
         * tFuelValue; if (this.mFluid == null) { if (aBaseMetaTileEntity.getUniversalEnergyStored() <
         * this.maxEUOutput() + this.getMinimumStoredEU()) { this.mInventory[this.getStackDisplaySlot()] = null; } else
         * { if (this.mInventory[this.getStackDisplaySlot()] == null) { this.mInventory[this.getStackDisplaySlot()] =
         * new ItemStack(Blocks.fire, 1); }
         * this.mInventory[this.getStackDisplaySlot()].setStackDisplayName("Generating: " +
         * (aBaseMetaTileEntity.getUniversalEnergyStored() - this.getMinimumStoredEU()) + " EU"); } } else { tFuelValue
         * = this.getFuelValue(this.mFluid); int tConsumed = this.consumedFluidPerOperation(this.mFluid); if (tFuelValue
         * > 0 && tConsumed > 0 && this.mFluid.amount > tConsumed) { long tFluidAmountToUse = Math.min((long)
         * (this.mFluid.amount / tConsumed), (this.maxEUStore() - aBaseMetaTileEntity.getUniversalEnergyStored()) /
         * (long) tFuelValue); if (tFluidAmountToUse > 0L && aBaseMetaTileEntity
         * .increaseStoredEnergyUnits(tFluidAmountToUse * (long) tFuelValue, true)) {
         * PollutionUtils.addPollution(this.getBaseMetaTileEntity(), 10 * this.getPollution()); this.mFluid.amount =
         * (int) ((long) this.mFluid.amount - tFluidAmountToUse * (long) tConsumed); } } } if
         * (this.mInventory[this.getInputSlot()] != null && aBaseMetaTileEntity.getUniversalEnergyStored() <
         * this.maxEUOutput() * 20L + this.getMinimumStoredEU() &&
         * GT_Utility.getFluidForFilledItem(this.mInventory[this.getInputSlot()], true) == null) { tFuelValue =
         * this.getFuelValue(this.mInventory[this.getInputSlot()]); if (tFuelValue > 0) { ItemStack tEmptyContainer =
         * this.getEmptyContainer(this.mInventory[this.getInputSlot()]); if
         * (aBaseMetaTileEntity.addStackToSlot(this.getOutputSlot(), tEmptyContainer)) {
         * aBaseMetaTileEntity.increaseStoredEnergyUnits((long) tFuelValue, true);
         * aBaseMetaTileEntity.decrStackSize(this.getInputSlot(), 1);
         * PollutionUtils.addPollution(this.getBaseMetaTileEntity(), 10 * this.getPollution()); } } } } if
         * (aBaseMetaTileEntity.isServerSide()) { aBaseMetaTileEntity.setActive(aBaseMetaTileEntity.isAllowedToWork() &&
         * aBaseMetaTileEntity .getUniversalEnergyStored() >= this.maxEUOutput() + this.getMinimumStoredEU()); }
         */

        if (aBaseMetaTileEntity.isServerSide() && aBaseMetaTileEntity.isAllowedToWork() && ((aTick % 10) == 0)) {
            if (this.mFluid == null) {
                if (aBaseMetaTileEntity.getUniversalEnergyStored() < (this.maxEUOutput() + this.getMinimumStoredEU())) {
                    this.mInventory[this.getStackDisplaySlot()] = null;
                } else {
                    if (this.mInventory[this.getStackDisplaySlot()] == null) {
                        this.mInventory[this.getStackDisplaySlot()] = new ItemStack(Blocks.fire, 1);
                    }
                    this.mInventory[this.getStackDisplaySlot()].setStackDisplayName(
                            "Generating: "
                                    + (aBaseMetaTileEntity.getUniversalEnergyStored() - this.getMinimumStoredEU())
                                    + " EU");
                }
            } else {
                final int tFuelValue = this.getFuelValue(this.mFluid),
                        tConsumed = this.consumedFluidPerOperation(this.mFluid);
                if ((tFuelValue > 0) && (tConsumed > 0) && (this.mFluid.amount >= tConsumed)) {
                    final long tFluidAmountToUse = Math.min(
                            this.mFluid.amount / tConsumed,
                            (((this.maxEUOutput() * 20) + this.getMinimumStoredEU())
                                    - aBaseMetaTileEntity.getUniversalEnergyStored()) / tFuelValue);
                    if ((tFluidAmountToUse > 0)
                            && aBaseMetaTileEntity.increaseStoredEnergyUnits(tFluidAmountToUse * tFuelValue, true)) {
                        int aSafeFloor = (int) Math.max(((tFluidAmountToUse * tConsumed) / 3), 1);
                        this.mFluid.amount -= (int) aSafeFloor;
                        PollutionUtils.addPollution(getBaseMetaTileEntity(), 10 * getPollution());
                    }
                }
            }
            if ((this.mInventory[this.getInputSlot()] != null)
                    && (aBaseMetaTileEntity.getUniversalEnergyStored()
                            < ((this.maxEUOutput() * 20) + this.getMinimumStoredEU()))
                    && (GT_Utility.getFluidForFilledItem(this.mInventory[this.getInputSlot()], true) == null)) {
                final int tFuelValue = this.getFuelValue(this.mInventory[this.getInputSlot()]);
                if (tFuelValue > 0) {
                    final ItemStack tEmptyContainer = this.getEmptyContainer(this.mInventory[this.getInputSlot()]);
                    if (aBaseMetaTileEntity.addStackToSlot(this.getOutputSlot(), tEmptyContainer)) {
                        aBaseMetaTileEntity.increaseStoredEnergyUnits(tFuelValue, true);
                        aBaseMetaTileEntity.decrStackSize(this.getInputSlot(), 1);
                        PollutionUtils.addPollution(getBaseMetaTileEntity(), 10 * getPollution());
                    }
                }
            }
        }

        if (aBaseMetaTileEntity.isServerSide()) {
            aBaseMetaTileEntity.setActive(
                    aBaseMetaTileEntity.isAllowedToWork() && (aBaseMetaTileEntity.getUniversalEnergyStored()
                            >= (this.maxEUOutput() + this.getMinimumStoredEU())));
        }
    }

    public int getPollution() {
        return MathUtils.randInt(pollMin, pollMax);
    }

    public abstract GT_Recipe_Map getRecipes();

    public abstract int getEfficiency();

    public int consumedFluidPerOperation(final FluidStack aLiquid) {
        return 1;
    }

    public int getFuelValue(final FluidStack aLiquid) {
        if ((aLiquid == null) || (this.getRecipes() == null)) {
            return 0;
        }
        FluidStack tLiquid;
        final Collection<GT_Recipe> tRecipeList = this.getRecipes().mRecipeList;
        if (tRecipeList != null) {
            // Logger.INFO("Step A");
            for (final GT_Recipe tFuel : tRecipeList) {
                // Logger.INFO("Step B");
                if ((tLiquid = tFuel.mFluidInputs[0]) != null) {
                    // Logger.INFO("Step C");
                    if (aLiquid.isFluidEqual(tLiquid)) {
                        // Logger.INFO("Found some fuel?");
                        int aperOp = this.consumedFluidPerOperation(tLiquid);
                        int aConsume = (int) (((long) tFuel.mSpecialValue * this.getEfficiency() * aperOp) / 100);
                        // Logger.INFO("Fuel Value: "+tFuel.mSpecialValue);
                        // Logger.INFO("Efficiency: "+getEfficiency());
                        // Logger.INFO("Consumed per op: "+aperOp);
                        // Logger.INFO("Consuming "+aConsume);
                        return aConsume;
                    }
                }
            }
        }
        // Logger.INFO("No Fuel Value | Valid? "+(aLiquid != null));
        return 0;
    }

    public int getFuelValue(final ItemStack aStack) {
        if (GT_Utility.isStackInvalid(aStack) || (this.getRecipes() == null)) {
            return 0;
        }
        final GT_Recipe tFuel = this.getRecipes()
                .findRecipe(this.getBaseMetaTileEntity(), false, Long.MAX_VALUE, null, aStack);
        if (tFuel != null) {
            return (int) ((tFuel.mSpecialValue * 1000L * this.getEfficiency()) / 100);
        }
        return 0;
    }

    public ItemStack getEmptyContainer(final ItemStack aStack) {
        if (GT_Utility.isStackInvalid(aStack) || (this.getRecipes() == null)) {
            return null;
        }
        final GT_Recipe tFuel = this.getRecipes()
                .findRecipe(this.getBaseMetaTileEntity(), false, Long.MAX_VALUE, null, aStack);
        if (tFuel != null) {
            return GT_Utility.copy(tFuel.getOutput(0));
        }
        return GT_Utility.getContainerItem(aStack, true);
    }

    @Override
    public boolean allowPutStack(final IGregTechTileEntity aBaseMetaTileEntity, final int aIndex, final byte aSide,
            final ItemStack aStack) {
        return super.allowPutStack(aBaseMetaTileEntity, aIndex, aSide, aStack) && ((this.getFuelValue(aStack) > 0)
                || (this.getFuelValue(GT_Utility.getFluidForFilledItem(aStack, true)) > 0));
    }

    @Override
    public int getCapacity() {
        return 32000;
    }

    @Override
    public int getTankPressure() {
        return -100;
    }

    @Override
    public boolean useModularUI() {
        return true;
    }
}
