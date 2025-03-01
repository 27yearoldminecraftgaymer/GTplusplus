package gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.GT_HatchElement.*;
import static gregtech.api.util.GT_StructureUtility.buildHatchAdder;
import static gregtech.api.util.GT_StructureUtility.filterByMTETier;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Input;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Muffler;
import gregtech.api.util.GTPP_Recipe;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.lib.CORE;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.GregtechMeta_MultiBlockBase;

public class GregtechMetaTileEntity_Refinery extends GregtechMeta_MultiBlockBase<GregtechMetaTileEntity_Refinery>
        implements ISurvivalConstructable {

    private int mCasing;
    private static IStructureDefinition<GregtechMetaTileEntity_Refinery> STRUCTURE_DEFINITION = null;

    public GregtechMetaTileEntity_Refinery(final int aID, final String aName, final String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GregtechMetaTileEntity_Refinery(final String aName) {
        super(aName);
    }

    @Override
    public String getMachineType() {
        return "Fuel Refinery";
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType(getMachineType()).addInfo("Controller Block for the Fission Fuel Processing Unit")
                .addPollutionAmount(getPollutionPerSecond(null)).addSeparator().beginStructureBlock(3, 9, 3, false)
                .addController("Bottom Center").addCasingInfo("Hastelloy-X Structural Casing", 7)
                .addCasingInfo("Incoloy-DS Fluid Containment Block", 5).addCasingInfo("Zeron-100 Reactor Shielding", 4)
                .addCasingInfo("Hastelloy-N Sealant Blocks", 17).addInputHatch("Base platform", 1)
                .addOutputHatch("Base platform", 1).addOutputBus("Base platform", 1).addMufflerHatch("Base platform", 1)
                .addMaintenanceHatch("Base platform", 1).addEnergyHatch("Base platform", 1)
                .addStructureInfo("Muffler's Tier must be IV+")
                .addStructureInfo("4x Input Hatches, 2x Output Hatches, 1x Output Bus")
                .addStructureInfo("1x Muffler, 1x Maintenance Hatch, 1x Energy Hatch")
                .toolTipFinisher(CORE.GT_Tooltip_Builder.get());
        return tt;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return Textures.BlockIcons.OVERLAY_FRONT_MULTI_SMELTER_ACTIVE;
    }

    @Override
    protected IIconContainer getInactiveOverlay() {
        return Textures.BlockIcons.OVERLAY_FRONT_MULTI_SMELTER;
    }

    @Override
    protected int getCasingTextureId() {
        return TAE.GTPP_INDEX(18);
    }

    @Override
    public GT_Recipe_Map getRecipeMap() {
        return GTPP_Recipe.GTPP_Recipe_Map.sFissionFuelProcessing;
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        // this.resetRecipeMapForAllInputHatches();
        for (GT_MetaTileEntity_Hatch_Input g : this.mInputHatches) {
            g.mRecipeMap = null;
        }
        boolean ab = super.checkRecipeGeneric();
        // Logger.INFO("Did Recipe? "+ab);
        return ab;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public int getEuDiscountForParallelism() {
        return 0;
    }

    @Override
    public boolean addMufflerToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        } else {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Muffler
                    && ((GT_MetaTileEntity_Hatch_Muffler) aMetaTileEntity).mTier >= 5) {
                return addToMachineList(aTileEntity, aBaseCasingIndex);
            }
        }
        return false;
    }

    @Override
    public IStructureDefinition<GregtechMetaTileEntity_Refinery> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GregtechMetaTileEntity_Refinery>builder().addShape(
                    mName,
                    transpose(
                            new String[][] { { "   ", " N ", "   " }, { " N ", "NIN", " N " }, { " N ", "NIN", " N " },
                                    { " N ", "NIN", " N " }, { " Z ", "ZIZ", " Z " }, { " N ", "NIN", " N " },
                                    { "XXX", "XXX", "XXX" }, { "X~X", "XXX", "XXX" }, }))
                    .addElement(
                            'X',
                            ofChain(
                                    buildHatchAdder(GregtechMetaTileEntity_Refinery.class)
                                            .atLeast(Energy, Maintenance, OutputHatch, OutputBus, InputHatch)
                                            .casingIndex(TAE.GTPP_INDEX(18)).dot(1).build(),
                                    buildHatchAdder(GregtechMetaTileEntity_Refinery.class).atLeast(Muffler)
                                            .adder(GregtechMetaTileEntity_Refinery::addMufflerToMachineList)
                                            .hatchItemFilterAnd(t -> filterByMTETier(6, Integer.MAX_VALUE))
                                            .casingIndex(TAE.GTPP_INDEX(18)).dot(1).build(),
                                    onElementPass(x -> ++x.mCasing, ofBlock(ModBlocks.blockCasings2Misc, 2))))
                    .addElement('I', ofBlock(ModBlocks.blockCasings2Misc, 3))
                    .addElement('N', ofBlock(ModBlocks.blockCasings2Misc, 1))
                    .addElement('Z', ofBlock(ModBlocks.blockCasingsMisc, 13)).build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(mName, stackSize, hintsOnly, 1, 7, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(mName, stackSize, 1, 7, 0, elementBudget, env, false, true);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCasing = 0;
        if (checkPiece(mName, 1, 7, 0) && mCasing >= 7) {
            if (this.mInputHatches.size() == 4 && this.mOutputHatches.size() == 2
                    && this.mOutputBusses.size() == 1
                    && this.mMufflerHatches.size() == 1
                    && this.mMaintenanceHatches.size() == 1
                    && this.mEnergyHatches.size() == 1) {
                this.resetRecipeMapForAllInputHatches(this.getRecipeMap());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCorrectMachinePart(final ItemStack aStack) {
        return true;
    }

    @Override
    public int getMaxEfficiency(final ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return CORE.ConfigSwitches.pollutionPerSecondMultiRefinery;
    }

    @Override
    public int getDamageToComponent(final ItemStack aStack) {
        return 0;
    }

    public int getAmountOfOutputs() {
        return 5;
    }

    @Override
    public boolean explodesOnComponentBreak(final ItemStack aStack) {
        return false;
    }

    @Override
    public IMetaTileEntity newMetaEntity(final IGregTechTileEntity aTileEntity) {
        return new GregtechMetaTileEntity_Refinery(this.mName);
    }
}
