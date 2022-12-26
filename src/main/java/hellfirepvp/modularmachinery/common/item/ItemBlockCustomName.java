/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.item;

import hellfirepvp.modularmachinery.common.block.BlockCustomName;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockCustomName
 * Created by HellFirePvP
 * Date: 28.06.2017 / 20:57
 */
public class ItemBlockCustomName extends ItemBlock {

    public ItemBlockCustomName(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        Block block = getBlock();
        if (block instanceof BlockCustomName) {
            String identifier = ((BlockCustomName) block).getIdentifierForMeta(stack.getItemDamage());
            return super.getUnlocalizedNameInefficiently(stack) + "." + identifier;
        }
        return super.getUnlocalizedNameInefficiently(stack);
    }
}
