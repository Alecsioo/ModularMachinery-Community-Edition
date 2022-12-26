/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.lib;

import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.crafting.ComponentType;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.requirement.type.RequirementType;
import hellfirepvp.modularmachinery.common.crafting.tooltip.RequirementTip;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistriesMM
 * Created by HellFirePvP
 * Date: 13.07.2019 / 09:03
 */
public class RegistriesMM {

    public static final ResourceLocation ADAPTER_REGISTRY_NAME = new ResourceLocation(ModularMachinery.MODID, "recipeadapters");
    public static final ResourceLocation COMPONENT_TYPE_REGISTRY_NAME = new ResourceLocation(ModularMachinery.MODID, "componenttypes");
    public static final ResourceLocation REQUIREMENT_TYPE_REGISTRY_NAME = new ResourceLocation(ModularMachinery.MODID, "requirementtypes");
    public static final ResourceLocation REQUIREMENT_TIPS_REGISTRY_NAME = new ResourceLocation(ModularMachinery.MODID, "requirementtips");
    public static ForgeRegistry<RecipeAdapter> ADAPTER_REGISTRY;
    public static ForgeRegistry<ComponentType> COMPONENT_TYPE_REGISTRY;
    public static ForgeRegistry<RequirementType<?, ?>> REQUIREMENT_TYPE_REGISTRY;
    public static ForgeRegistry<RequirementTip> REQUIREMENT_TIPS_REGISTRY;
    private RegistriesMM() {
    }

}
