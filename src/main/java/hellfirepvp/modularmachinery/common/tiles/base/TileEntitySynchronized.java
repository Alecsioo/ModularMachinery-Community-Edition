/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.tiles.base;

import github.kasuminova.mmce.common.concurrent.Sync;
import hellfirepvp.modularmachinery.ModularMachinery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntitySynchronized
 * Created by HellFirePvP
 * Date: 28.06.2017 / 17:15
 */
public class TileEntitySynchronized extends TileEntity {

    protected static final Random rand = new Random();

    public final void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
    }

    public void readCustomNBT(NBTTagCompound compound) {
    }

    public void readNetNBT(NBTTagCompound compound) {
    }

    public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        writeCustomNBT(compound);
        return compound;
    }

    public void writeCustomNBT(NBTTagCompound compound) {
    }

    public void writeNetNBT(NBTTagCompound compound) {
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        super.writeToNBT(compound);
        writeCustomNBT(compound);
        writeNetNBT(compound);
        return new SPacketUpdateTileEntity(getPos(), 255, compound);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        super.writeToNBT(compound);
        writeCustomNBT(compound);
        return compound;
    }

    public final void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
        super.onDataPacket(manager, packet);
        readCustomNBT(packet.getNbtCompound());
        readNetNBT(packet.getNbtCompound());
    }

    public void markForUpdate() {
        IBlockState thisState = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, thisState, thisState, 3);
        markDirty();
    }

    public void markForUpdateSync() {
        if (ModularMachinery.pluginServerCompatibleMode) {
            ModularMachinery.EXECUTE_MANAGER.addSyncTask(this::markForUpdate);
        } else {
            Sync.doSyncAction(this::markForUpdate);
        }
    }

}
