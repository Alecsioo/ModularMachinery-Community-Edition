/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.util;

import com.google.common.collect.Lists;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockInformationVariable
 * Created by HellFirePvP
 * Date: 27.06.2017 / 22:06
 */
public class BlockInformationVariable {

    private final Map<String, BlockArray.BlockInformation> variables = new HashMap<>();

    public Map<String, BlockArray.BlockInformation> getDefinedVariables() {
        return variables;
    }

    public static class Deserializer implements JsonDeserializer<BlockInformationVariable> {

        @Override
        public BlockInformationVariable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            BlockInformationVariable var = new BlockInformationVariable();
            JsonObject root = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                JsonElement variableElement = entry.getValue();
                if (variableElement.isJsonArray()) {
                    List<BlockArray.IBlockStateDescriptor> descriptors = Lists.newArrayList();
                    JsonArray elements = variableElement.getAsJsonArray();
                    for (int i = 0; i < elements.size(); i++) {
                        JsonElement p = elements.get(i);
                        if (!p.isJsonPrimitive() || !p.getAsJsonPrimitive().isString()) {
                            throw new JsonParseException("Elements of a variable have to be Blockstate descriptions! You cannot nest variables!");
                        }
                        descriptors.add(BlockArray.BlockInformation.getDescriptor(p.getAsString()));
                    }
                    var.variables.put(entry.getKey(), new BlockArray.BlockInformation(descriptors));
                } else if (variableElement.isJsonPrimitive() && variableElement.getAsJsonPrimitive().isString()) {
                    var.variables.put(entry.getKey(), new BlockArray.BlockInformation(
                            Lists.newArrayList(BlockArray.BlockInformation.getDescriptor(variableElement.getAsString()))));
                } else {
                    throw new JsonParseException("Variable '" + entry.getKey() + "' has as its value neither an array of BlockState definitions nor a single BlockState as String!");
                }
            }
            return var;
        }
    }

}
