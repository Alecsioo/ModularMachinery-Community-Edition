package github.kasuminova.mmce.common.block.appeng;

import appeng.api.implementations.items.IMemoryCard;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.CommonProxy;
import hellfirepvp.modularmachinery.common.lib.ItemsMM;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMEPatternProvider extends BlockMEMachineComponent {

    @Override
    public boolean onBlockActivated(
            @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state,
            @Nonnull EntityPlayer player, @Nonnull EnumHand hand,
            @Nonnull EnumFacing facing,
            float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)return false;
        if (hand == EnumHand.MAIN_HAND && !player.getHeldItem(hand).isEmpty()) {
            var heldItem = player.getHeldItem(hand);
            if (player.isSneaking()) {
                if (heldItem.getItem() instanceof IMemoryCard memoryCard) {
                    final String name = this.getTranslationKey();

                    final NBTTagCompound data = new NBTTagCompound();
                    data.setLong("Pos",pos.toLong());
                    memoryCard.setMemoryCardContents(heldItem, name, data);
                    player.sendMessage(new TextComponentTranslation("message.blockmepatternprovider.save"));

                    return true;
                }
            }
        }
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof MEPatternProvider) {
            player.openGui(ModularMachinery.MODID, CommonProxy.GuiType.ME_PATTERN_PROVIDER.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final World world, final IBlockState state) {
        return new MEPatternProvider();
    }

    @Override
    public void dropBlockAsItemWithChance(@Nonnull final World worldIn, @Nonnull final BlockPos pos, @Nonnull final IBlockState state, final float chance, final int fortune) {
    }

    @Override
    public void breakBlock(final World worldIn,
                           @Nonnull final BlockPos pos,
                           @Nonnull final IBlockState state)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te == null) {
            super.dropBlockAsItemWithChance(worldIn, pos, state, 1.0F, 0);
            worldIn.removeTileEntity(pos);
            return;
        }
        if (!(te instanceof final MEPatternProvider provider) || provider.isAllDefault()) {
            super.dropBlockAsItemWithChance(worldIn, pos, state, 1.0F, 0);
            worldIn.removeTileEntity(pos);
            return;
        }

        ItemStack dropped = new ItemStack(ItemsMM.mePatternProvider);
        dropped.setTagInfo("patternProvider", provider.writeProviderNBT(new NBTTagCompound()));

        spawnAsEntity(worldIn, pos, dropped);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public void onBlockPlacedBy(@Nonnull final World worldIn,
                                @Nonnull final BlockPos pos,
                                @Nonnull final IBlockState state,
                                @Nonnull final EntityLivingBase placer,
                                @Nonnull final ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        TileEntity te = worldIn.getTileEntity(pos);
        NBTTagCompound tag = stack.getTagCompound();
        if (te instanceof final MEPatternProvider provider && tag != null && tag.hasKey("patternProvider")) {
            provider.readProviderNBT(tag.getCompoundTag("patternProvider"));
        }
    }
}
