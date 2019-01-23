/*******************************************************************************
 * HellFirePvP / Modular Machinery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.tiles;

import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.api.IEnergyStorage;
import hellfirepvp.modularmachinery.common.block.prop.EnergyHatchSize;
import hellfirepvp.modularmachinery.common.integration.IntegrationIC2EventHandlerHelper;
import hellfirepvp.modularmachinery.common.machine.MachineComponent;
import hellfirepvp.modularmachinery.common.tiles.base.TileEnergyHatch;
import hellfirepvp.modularmachinery.common.util.IEnergyHandler;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEnergyOutputHatch
 * Created by HellFirePvP
 * Date: 08.07.2017 / 12:43
 */
@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "ic2")
public class TileEnergyOutputHatch extends TileEnergyHatch implements IEnergySource {

    public TileEnergyOutputHatch() {}

    public TileEnergyOutputHatch(EnergyHatchSize size) {
        super(size);
    }

    @Override
    public void update() {
        int transferCap = Math.min(this.size.transferLimit, convertDownEnergy(this.energy));
        for (EnumFacing face : EnumFacing.VALUES) {
            if(Loader.isModLoaded("redstoneflux")) {
                int transferred = attemptFERFTransfer(face, transferCap);
                transferCap -= transferred;
                this.energy -= transferred;
            } else {
                int transferred = attemptFETransfer(face, transferCap);
                transferCap -= transferred;
                this.energy -= transferred;
            }
            if(transferCap <= 0) {
                break;
            }
        }
    }

    private int attemptFETransfer(EnumFacing face, int maxTransferLeft) {
        BlockPos at = this.getPos().offset(face);
        EnumFacing accessingSide = face.getOpposite();

        int receivedEnergy = 0;
        TileEntity te = world.getTileEntity(at);
        if(te != null && !(te instanceof TileEnergyHatch)) {
            if(te.hasCapability(CapabilityEnergy.ENERGY, accessingSide)) {
                net.minecraftforge.energy.IEnergyStorage ce = te.getCapability(CapabilityEnergy.ENERGY, accessingSide);
                if(ce != null && ce.canReceive()) {
                    try {
                    receivedEnergy = ce.receiveEnergy(maxTransferLeft, false);
                    } catch (Exception ignored) {}
                }
            }
        }
        return receivedEnergy;
    }

    @Optional.Method(modid = "redstoneflux")
    private int attemptFERFTransfer(EnumFacing face, int maxTransferLeft) {
        BlockPos at = this.getPos().offset(face);
        EnumFacing accessingSide = face.getOpposite();

        int receivedEnergy = 0;
        TileEntity te = world.getTileEntity(at);
        if(te != null && !(te instanceof TileEnergyHatch)) {
            if(te instanceof cofh.redstoneflux.api.IEnergyReceiver && ((IEnergyReceiver) te).canConnectEnergy(accessingSide)) {
                try {
                    receivedEnergy = ((IEnergyReceiver) te).receiveEnergy(accessingSide, maxTransferLeft, false);
                } catch (Exception ignored) {}
            }
            if(receivedEnergy <= 0 && te instanceof IEnergyStorage) {
                try {
                    receivedEnergy = ((IEnergyStorage) te).receiveEnergy(maxTransferLeft, false);
                } catch (Exception ignored) {}
            }
            if(receivedEnergy <= 0 && te.hasCapability(CapabilityEnergy.ENERGY, accessingSide)) {
                net.minecraftforge.energy.IEnergyStorage ce = te.getCapability(CapabilityEnergy.ENERGY, accessingSide);
                if(ce != null && ce.canReceive()) {
                    try {
                        receivedEnergy = ce.receiveEnergy(maxTransferLeft, false);
                    } catch (Exception ignored) {}
                }
            }
        }
        return receivedEnergy;
    }

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
    public double getOfferedEnergy() {
        return Math.min(this.size.getEnergyTransmission(), this.getCurrentEnergy() / 4L);
    }

    @Override
    @Optional.Method(modid = "ic2")
    public void drawEnergy(double amount) {
        this.energy = MiscUtils.clamp(this.energy - (MathHelper.lfloor(amount) * 4L), 0, this.size.maxEnergy);
        markForUpdate();
    }

    @Override
    @Optional.Method(modid = "ic2")
    public int getSourceTier() {
        return size.energyTier;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
        return true;
    }

    @Nullable
    @Override
    public MachineComponent provideComponent() {
        return new MachineComponent.EnergyHatch(MachineComponent.IOType.OUTPUT) {
            @Override
            public IEnergyHandler getContainerProvider() {
                return TileEnergyOutputHatch.this;
            }
        };
    }

}
