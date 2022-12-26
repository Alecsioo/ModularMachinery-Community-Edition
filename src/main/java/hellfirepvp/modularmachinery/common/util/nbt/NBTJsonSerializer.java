/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.util.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.Set;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: NBTJsonSerializer
 * Created by HellFirePvP
 * Date: 19.08.2017 / 13:56
 */
public class NBTJsonSerializer {

    public static String serializeNBT(NBTBase nbtBase) {
        StringBuilder sb = new StringBuilder();
        switch (nbtBase.getId()) {
            case Constants.NBT.TAG_BYTE:
            case Constants.NBT.TAG_SHORT:
            case Constants.NBT.TAG_INT:
            case Constants.NBT.TAG_LONG:
            case Constants.NBT.TAG_FLOAT:
            case Constants.NBT.TAG_DOUBLE: {
                sb.append(NBTTagString.quoteAndEscape(nbtBase.toString()));
                break;
            }
            case Constants.NBT.TAG_STRING: {
                sb.append(nbtBase);
                break;
            }
            case Constants.NBT.TAG_LIST: {
                StringBuilder stringbuilder = new StringBuilder("[");
                NBTTagList listTag = (NBTTagList) nbtBase;

                for (int i = 0; i < listTag.tagCount(); ++i) {
                    if (i != 0) {
                        stringbuilder.append(',');
                    }

                    stringbuilder.append(serializeNBT(listTag.get(i)));
                }
                sb.append(stringbuilder.append(']'));
                break;
            }
            case Constants.NBT.TAG_COMPOUND: {
                StringBuilder stringbuilder = new StringBuilder("{");
                NBTTagCompound cmpTag = (NBTTagCompound) nbtBase;
                Set<String> collection = cmpTag.getKeySet();

                for (String s : collection) {
                    if (stringbuilder.length() != 1) {
                        stringbuilder.append(',');
                    }

                    stringbuilder.append(NBTTagString.quoteAndEscape(s)).append(':').append(serializeNBT(cmpTag.getTag(s)));
                }

                sb.append(stringbuilder.append('}'));
                break;
            }
        }
        return sb.toString();
    }

}
