package hellfirepvp.modularmachinery.common.integration.crafttweaker.helper;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@FunctionalInterface
@ZenClass("mods.modularmachinery.IFunction")
public interface IFunction<T, R> {
    R apply(T param);
}
