package hellfirepvp.modularmachinery.common.block;

import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.CommonProxy;
import hellfirepvp.modularmachinery.common.block.prop.SmartInterfaceTypeEnum;
import hellfirepvp.modularmachinery.common.tiles.TileSmartInterface;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.LinkedList;

public class BlockSmartInterface extends BlockMachineComponent implements BlockCustomName, BlockVariants {
    protected static final PropertyEnum<SmartInterfaceTypeEnum> INTERFACE_TYPE = PropertyEnum.create("type", SmartInterfaceTypeEnum.class);

    public BlockSmartInterface() {
        super(Material.IRON);
        setHardness(5F);
        setResistance(10F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(CommonProxy.creativeTabMM);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (SmartInterfaceTypeEnum size : SmartInterfaceTypeEnum.values()) {
            items.add(new ItemStack(this, 1, size.ordinal()));
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(INTERFACE_TYPE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, INTERFACE_TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(INTERFACE_TYPE, SmartInterfaceTypeEnum.values()[meta]);
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        return getStateFromMeta(meta).getValue(INTERFACE_TYPE).getName();
    }

    @Override
    public Iterable<IBlockState> getValidStates() {
        LinkedList<IBlockState> states = new LinkedList<>();
        for (SmartInterfaceTypeEnum type : SmartInterfaceTypeEnum.values()) {
            states.add(getDefaultState().withProperty(INTERFACE_TYPE, type));
        }
        return states;
    }

    @Override
    public String getBlockStateName(IBlockState state) {
        return state.getValue(INTERFACE_TYPE).getName();
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileSmartInterface();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileSmartInterface) {
                playerIn.openGui(ModularMachinery.MODID, CommonProxy.GuiType.SMART_INTERFACE.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }
}
