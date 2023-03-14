/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.crafting.adapter;

import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.data.Config;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeAdapter
 * Created by HellFirePvP
 * Date: 23.07.2017 / 14:16
 */
public abstract class RecipeAdapter implements IForgeRegistryEntry<RecipeAdapter> {

    private final ResourceLocation registryName;

    public RecipeAdapter(@Nonnull ResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Nonnull
    @Override
    public final ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public RecipeAdapter setRegistryName(ResourceLocation registryName) {
        return this;
    }

    @Override
    public Class<RecipeAdapter> getRegistryType() {
        return RecipeAdapter.class;
    }

    @Nonnull
    public abstract Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachineName,
                                                               List<RecipeModifier> modifiers,
                                                               List<ComponentRequirement<?, ?>> additionalRequirements);

    @Nonnull
    public MachineRecipe createRecipeShell(ResourceLocation uniqueRecipeName, ResourceLocation owningMachineName, int tickTime, int priority, boolean voidPerTickFailure) {
        return new MachineRecipe("internal/adapter/" + registryName.getNamespace() + "/" + registryName.getPath(),
                uniqueRecipeName, owningMachineName, tickTime, priority, voidPerTickFailure, Config.recipeParallelizeEnabledByDefault);
    }

}
