package github.kasuminova.mmce.common.event.recipe;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.Phase;
import hellfirepvp.modularmachinery.common.crafting.ActiveMachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftingStatus;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.modularmachinery.FactoryRecipeTickEvent")
public class FactoryRecipeTickEvent extends FactoryRecipeEvent {
    public final Phase phase;
    private boolean preventProgressing = false;
    private boolean isFailure = false;
    private boolean destructRecipe = false;
    private String failureReason = null;

    public FactoryRecipeTickEvent(FactoryRecipeThread recipeThread, TileMultiblockMachineController controller, Phase phase) {
        super(recipeThread, controller);
        this.phase = phase;
    }

    @Override
    public void postEvent() {
        super.postEvent();

        if (preventProgressing) {
            ActiveMachineRecipe activeRecipe = recipeThread.getActiveRecipe();
            activeRecipe.setTick(activeRecipe.getTick() - 1);
            recipeThread.setStatus(CraftingStatus.working(failureReason));
            return;
        }

        if (isFailure) {
            if (destructRecipe) {
                recipeThread.setActiveRecipe(null)
                        .setContext(null)
                        .setStatus(CraftingStatus.failure(failureReason))
                        .getSemiPermanentModifiers().clear();
                return;
            }
            recipeThread.setStatus(CraftingStatus.failure(failureReason));
        }
    }

    @ZenMethod
    public void setFailed(boolean destructRecipe, String reason) {
        this.isFailure = true;
        this.destructRecipe = destructRecipe;
        this.failureReason = reason;
        setCanceled(true);
    }

    @ZenMethod
    public void preventProgressing(String reason) {
        this.preventProgressing = true;
        this.failureReason = reason;
        setCanceled(true);
    }

    public boolean isFailure() {
        return isFailure;
    }

    public boolean isDestructRecipe() {
        return destructRecipe;
    }

    public boolean isPreventProgressing() {
        return preventProgressing;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
