/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.crafting;

import com.google.common.collect.Iterables;
import hellfirepvp.modularmachinery.common.crafting.helper.RecipeCraftingContext;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.RecipeFailureActions;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.tiles.TileMachineController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveMachineRecipe
 * Created by HellFirePvP
 * Date: 29.06.2017 / 15:50
 */
public class ActiveMachineRecipe {

    private final MachineRecipe recipe;
    private final Map<ResourceLocation, NBTTagCompound> dataMap = new HashMap<>();
    private int tick = 0;
    private int totalTick;

    public ActiveMachineRecipe(MachineRecipe recipe) {
        this.recipe = recipe;
        this.totalTick = recipe.getRecipeTotalTickTime();
    }

    public ActiveMachineRecipe(NBTTagCompound serialized) {
        this.recipe = RecipeRegistry.getRecipe(new ResourceLocation(serialized.getString("recipeName")));
        this.tick = serialized.getInteger("tick");
        this.totalTick = serialized.getInteger("totalTick");
        if (serialized.hasKey("data", Constants.NBT.TAG_LIST)) {
            NBTTagList listData = serialized.getTagList("data", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < listData.tagCount(); i++) {
                NBTTagCompound tag = listData.getCompoundTagAt(i);
                String key = tag.getString("key");
                NBTTagCompound data = tag.getCompoundTag("data");
                if (!key.isEmpty()) {
                    dataMap.put(new ResourceLocation(key), data);
                }
            }
        }
    }

    public void reset() {
        this.tick = 0;
    }

    public MachineRecipe getRecipe() {
        return recipe;
    }

    @Nonnull
    public TileMachineController.CraftingStatus tick(TileMachineController ctrl, RecipeCraftingContext context) {
        //Skip per-tick logic until controller can finish the recipe
        if (this.isCompleted()) {
            return TileMachineController.CraftingStatus.working();
        }

        RecipeCraftingContext.CraftingCheckResult check;
        if ((check = context.ioTick(tick)).isFailure()) {
            //On Failure
            DynamicMachine machine = ctrl.getFoundMachine();
            //Some Actions
            if (machine != null) {
                RecipeFailureActions action = machine.getFailureAction();
                doFailureAction(action);
            } else {
                doFailureAction(RecipeFailureActions.getDefaultAction());
            }
            return TileMachineController.CraftingStatus.failure(
                    Iterables.getFirst(check.getUnlocalizedErrorMessages(), ""));
        } else {
            //Success
            this.tick++;
            return TileMachineController.CraftingStatus.working();
        }
    }

    public void doFailureAction(RecipeFailureActions action) {
        switch (action) {
            case RESET:
                this.tick = 0;
                break;
            case DECREASE:
                this.tick = Math.min(tick - 1, 0);
                break;
        }
    }

    public int getTick() {
        return tick;
    }

    public int getTotalTick() {
        return totalTick;
    }

    @Nonnull
    public Map<ResourceLocation, NBTTagCompound> getData() {
        return dataMap;
    }

    @Nonnull
    public NBTTagCompound getOrCreateData(ResourceLocation key) {
        return dataMap.computeIfAbsent(key, k -> new NBTTagCompound());
    }

    public boolean isCompleted() {
        return this.tick >= totalTick;
    }

    public void start(RecipeCraftingContext context) {
        context.startCrafting();
        int time = this.recipe.getRecipeTotalTickTime();
        totalTick = Math.round(RecipeModifier.applyModifiers(context.getModifiers(RequirementTypesMM.REQUIREMENT_DURATION), RequirementTypesMM.REQUIREMENT_DURATION, null, time, false));
    }

    public void complete(RecipeCraftingContext completionContext) {
        completionContext.finishCrafting();
    }

    public NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("tick", this.tick);
        tag.setInteger("totalTick", this.totalTick);
        tag.setString("recipeName", this.recipe.getRegistryName().toString());

        NBTTagList listData = new NBTTagList();
        for (Map.Entry<ResourceLocation, NBTTagCompound> dataEntry : this.dataMap.entrySet()) {
            NBTTagCompound tagData = new NBTTagCompound();
            tagData.setString("key", dataEntry.getKey().toString());
            tagData.setTag("data", dataEntry.getValue());
        }
        tag.setTag("data", listData);
        return tag;
    }
}
