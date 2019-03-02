/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.tiles;

import hellfirepvp.modularmachinery.common.block.prop.EnergyHatchSize;
import hellfirepvp.modularmachinery.common.integration.IntegrationIC2EventHandlerHelper;
import hellfirepvp.modularmachinery.common.machine.MachineComponent;
import hellfirepvp.modularmachinery.common.tiles.base.TileEnergyHatch;
import hellfirepvp.modularmachinery.common.util.IEnergyHandler;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEnergyInputHatch
 * Created by HellFirePvP
 * Date: 08.07.2017 / 12:47
 */
@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2")
public class TileEnergyInputHatch extends TileEnergyHatch implements IEnergySink {

    public TileEnergyInputHatch() {}

    public TileEnergyInputHatch(EnergyHatchSize size) {
        super(size, MachineComponent.IOType.INPUT);
    }

    @Override
    public void update() {}

    @Override
    @Optional.Method(modid = "ic2")
    public void onLoad() {
        super.onLoad();
        IntegrationIC2EventHandlerHelper.fireLoadEvent(world, this);
    }

    @Override
    @Optional.Method(modid = "ic2")
    public void invalidate() {
        super.invalidate();
        if(!world.isRemote) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
        }
    }

    @Override
    @Optional.Method(modid = "ic2")
    public double getDemandedEnergy() {
        return Math.min((this.size.maxEnergy - this.energy) / 4, this.size.getIC2EnergyTransmission());
    }

    @Override
    @Optional.Method(modid = "ic2")
    public int getSinkTier() {
        return this.size.ic2EnergyTier;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
        long addable = Math.min((this.size.maxEnergy - this.energy) / 4L, MathHelper.lfloor(amount));
        amount -= addable;
        this.energy = MiscUtils.clamp(this.energy + MathHelper.lfloor(addable * 4), 0, this.size.maxEnergy);
        markForUpdate();
        return amount;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
        return true;
    }

    @Nullable
    @Override
    public MachineComponent provideComponent() {
        return new MachineComponent.EnergyHatch(MachineComponent.IOType.INPUT) {
            @Override
            public IEnergyHandler getContainerProvider() {
                return TileEnergyInputHatch.this;
            }
        };
    }
}
