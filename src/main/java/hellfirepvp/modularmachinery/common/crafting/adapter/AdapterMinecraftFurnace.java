/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.crafting.adapter;

import crafttweaker.util.IEventHandler;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementEnergy;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: AdapterMinecraftFurnace
 * Created by HellFirePvP
 * Date: 23.07.2017 / 14:48
 */
public class AdapterMinecraftFurnace extends RecipeAdapter {

    private int incId = 0;

    public AdapterMinecraftFurnace() {
        super(new ResourceLocation("minecraft", "furnace"));
    }

    @Nonnull
    @Override
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachineName,
                                                      List<RecipeModifier> modifiers,
                                                      List<ComponentRequirement<?, ?>> additionalRequirements,
                                                      Map<Class<?>, List<IEventHandler<RecipeEvent>>> eventHandlers,
                                                      List<String> recipeTooltips) {
        Map<ItemStack, ItemStack> inputOutputMap = FurnaceRecipes.instance().getSmeltingList();
        List<MachineRecipe> smeltingRecipes = new ArrayList<>(inputOutputMap.size());
        for (Map.Entry<ItemStack, ItemStack> smelting : inputOutputMap.entrySet()) {
            int tickTime = Math.round(Math.max(1, RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_DURATION, null, 120, false)));

            MachineRecipe recipe = createRecipeShell(
                    new ResourceLocation("minecraft", "smelting_recipe_" + incId),
                    owningMachineName,
                    tickTime, 0, false);

            int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, smelting.getKey().getCount(), false));
            if (inAmount > 0) {
                recipe.addRequirement(new RequirementItem(IOType.INPUT, ItemUtils.copyStackWithSize(smelting.getKey(), inAmount)));
            }

            int outAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, smelting.getKey().getCount(), false));
            if (outAmount > 0) {
                recipe.addRequirement(new RequirementItem(IOType.OUTPUT, ItemUtils.copyStackWithSize(smelting.getValue(), outAmount)));
            }

            int inEnergy = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ENERGY, IOType.INPUT, 20, false));
            if (inEnergy > 0) {
                recipe.addRequirement(new RequirementEnergy(IOType.INPUT, inEnergy));
            }

            RecipeAdapter.addAdditionalRequirements(recipe, additionalRequirements, eventHandlers, recipeTooltips);

            smeltingRecipes.add(recipe);
            incId++;
        }
        return smeltingRecipes;
    }
}
