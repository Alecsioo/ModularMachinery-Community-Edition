package github.kasuminova.mmce.common.event.recipe;

import crafttweaker.annotations.ZenRegister;
import hellfirepvp.modularmachinery.common.machine.RecipeThread;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.modularmachinery.RecipeFinishEvent")
public class RecipeFinishEvent extends RecipeEvent {
    public RecipeFinishEvent(TileMultiblockMachineController controller, RecipeThread thread) {
        super(controller, thread.getContext());
    }
}
