/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.client.ClientProxy;
import hellfirepvp.modularmachinery.client.util.DynamicMachineRenderContext;
import hellfirepvp.modularmachinery.client.util.RenderingUtils;
import hellfirepvp.modularmachinery.common.lib.BlocksMM;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.modifier.ModifierReplacement;
import hellfirepvp.modularmachinery.common.util.BlockArray;
import hellfirepvp.modularmachinery.common.util.BlockCompatHelper;
import hellfirepvp.modularmachinery.common.util.IBlockStateDescriptor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiScreenBlueprint
 * Created by HellFirePvP
 * Date: 09.07.2017 / 21:08
 */
public class GuiScreenBlueprint extends GuiScreen {

    public static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(ModularMachinery.MODID, "textures/gui/guiblueprint.png");
    private static final ResourceLocation ic2TileBlock = new ResourceLocation("ic2", "te");
    protected final int xSize = 176;
    protected final int ySize = 144;
    private final DynamicMachine machine;
    private final DynamicMachineRenderContext renderContext;
    protected int guiLeft;
    protected int guiTop;
    private int frameCount = 0;

    public GuiScreenBlueprint(DynamicMachine machine) {
        this.machine = machine;
        this.renderContext = DynamicMachineRenderContext.createContext(this.machine);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        frameCount++;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE_BACKGROUND);
        int x = (this.width - this.xSize) / 2;
        int z = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, z, 0, 0, this.xSize, this.ySize);

        if (renderContext.doesRenderIn3D()) {
            if (Mouse.isButtonDown(0) && frameCount > 20) {
                renderContext.rotateRender(0.25 * Mouse.getDY(), 0.25 * Mouse.getDX(), 0);
            }
        } else {
            if (Mouse.isButtonDown(0) && frameCount > 20) {
                renderContext.moveRender(0.25 * Mouse.getDX(), 0, -0.25 * Mouse.getDY());
            }
        }
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            renderContext.zoomOut();
        } else if (dWheel > 0) {
            renderContext.zoomIn();
        }

        if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
            if (renderContext.getShiftSnap() == -1) {
                renderContext.snapSamples();
            }
        } else {
            renderContext.releaseSamples();
        }

        ScaledResolution res = new ScaledResolution(mc);
        Rectangle scissorFrame = new Rectangle((guiLeft + 8) * res.getScaleFactor(), (guiTop + 43) * res.getScaleFactor(),
                160 * res.getScaleFactor(), 94 * res.getScaleFactor());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorFrame.x, scissorFrame.y, scissorFrame.width, scissorFrame.height);
        x = 88;
        z = 66;
        renderContext.renderAt(this.guiLeft + x, this.guiTop + z, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        drawButtons(mouseX, mouseY);

        if (!machine.getModifiers().isEmpty()) {
            this.mc.getTextureManager().bindTexture(TEXTURE_BACKGROUND);
            this.drawTexturedModalRect(guiLeft + 5, guiTop + 124, 0, 145, 100, 15);

            String reqBlueprint = I18n.format("tooltip.machinery.blueprint.upgrades");
            fontRenderer.drawStringWithShadow(reqBlueprint, this.guiLeft + 10, this.guiTop + 127, 0xFFFFFF);

            if (mouseX >= guiLeft + 5 && mouseX <= guiLeft + 105 &&
                    mouseY >= guiTop + 124 && mouseY <= guiTop + 139) {
                List<Tuple<ItemStack, String>> descriptionList = new LinkedList<>();
                boolean first = true;
                for (List<ModifierReplacement> modifiers : machine.getModifiers().values()) {
                    for (ModifierReplacement mod : modifiers) {
                        List<String> description = mod.getDescriptionLines();
                        if (description.isEmpty()) {
                            continue;
                        }
                        if (!first) {
                            descriptionList.add(new Tuple<>(ItemStack.EMPTY, ""));
                        }
                        first = false;
                        ItemStack stack = mod.getBlockInformation().getDescriptiveStack(renderContext.getShiftSnap() == -1 ? Optional.empty() : Optional.of(renderContext.getShiftSnap()));
                        List<String> tooltip = stack.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ?
                                ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
                        descriptionList.add(new Tuple<>(
                                stack,
                                Iterables.getFirst(tooltip, "")));
                        for (String str : description) {
                            descriptionList.add(new Tuple<>(ItemStack.EMPTY, str));
                        }
                    }
                }

                RenderingUtils.renderBlueStackTooltip(mouseX, mouseY, descriptionList, fontRenderer, Minecraft.getMinecraft().getRenderItem());
            }
        }

        fontRenderer.drawStringWithShadow(machine.getLocalizedName(), this.guiLeft + 10, this.guiTop + 11, 0xFFFFFFFF);
        if (machine.requiresBlueprint()) {
            String reqBlueprint = I18n.format("tooltip.machinery.blueprint.required");
            fontRenderer.drawStringWithShadow(reqBlueprint, this.guiLeft + 10, this.guiTop + 106, 0xFFFFFF);
        }

        scissorFrame = new Rectangle(MathHelper.floor(this.guiLeft + 8), MathHelper.floor(this.guiTop + 8), 160, 94);
        if (!renderContext.doesRenderIn3D() && scissorFrame.contains(mouseX, mouseY)) {
            render2DHover(mouseX, mouseY, x, z);
        }
    }

    private void render2DHover(int mouseX, int mouseY, int x, int z) {
        double scale = renderContext.getScale();
        Vec2f offset = renderContext.getCurrentRenderOffset(guiLeft + x, guiTop + z);
        int jumpWidth = 14;
        double scaleJump = jumpWidth * scale;
        Map<BlockPos, BlockArray.BlockInformation> slice = machine.getPattern().getPatternSlice(renderContext.getRenderSlice());
        if (renderContext.getRenderSlice() == 0) {
            slice.put(BlockPos.ORIGIN, new BlockArray.BlockInformation(Lists.newArrayList(new IBlockStateDescriptor(BlocksMM.blockController.getDefaultState()))));
        }
        for (BlockPos pos : slice.keySet()) {
            int xMod = pos.getX() + 1 + this.renderContext.getMoveOffset().getX();
            int zMod = pos.getZ() + 1 + this.renderContext.getMoveOffset().getZ();
            Rectangle.Double rct = new Rectangle2D.Double(offset.x - xMod * scaleJump, offset.y - zMod * scaleJump, scaleJump, scaleJump);
            if (rct.contains(mouseX, mouseY)) {
                IBlockState state = slice.get(pos).getSampleState(renderContext.getShiftSnap() == -1 ? Optional.empty() : Optional.of(renderContext.getShiftSnap()));
                Tuple<IBlockState, TileEntity> recovered = BlockCompatHelper.transformState(state, slice.get(pos).previewTag,
                        new BlockArray.TileInstantiateContext(Minecraft.getMinecraft().world, pos));
                state = recovered.getFirst();
                Block type = state.getBlock();
                int meta = type.getMetaFromState(state);

                ItemStack stack = ItemStack.EMPTY;

                //TODO 意义不明的 catch 块
                try {
                    if (ic2TileBlock.equals(type.getRegistryName())) {
                        stack = BlockCompatHelper.tryGetIC2MachineStack(state, recovered.getSecond());
                    } else {
                        stack = state.getBlock().getPickBlock(state, null, null, pos, null);
                    }
                } catch (Exception exc) {
                }

                if (stack.isEmpty()) {
                    if (type instanceof BlockFluidBase) {
                        stack = FluidUtil.getFilledBucket(new FluidStack(((IFluidBlock) type).getFluid(), 1000));
                    } else if (type instanceof BlockLiquid) {
                        Material material = state.getMaterial();
                        if (material == Material.WATER) {
                            stack = new ItemStack(Items.WATER_BUCKET);
                        } else if (material == Material.LAVA) {
                            stack = new ItemStack(Items.LAVA_BUCKET);
                        } else {
                            stack = ItemStack.EMPTY;
                        }
                    } else {
                        Item item = Item.getItemFromBlock(type);
                        if (item == Items.AIR) continue;
                        if (item.getHasSubtypes()) {
                            stack = new ItemStack(item, 1, meta);
                        } else {
                            stack = new ItemStack(item);
                        }
                    }
                }

                renderToolTip(stack, mouseX, mouseY);
                break;
            }
        }
    }

    private void drawButtons(int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE_BACKGROUND);

        boolean drawPopoutInfo = false, drawContents = false;

        //3D view
        int add = 0;
        if (!renderContext.doesRenderIn3D()) {
            if (mouseX >= this.guiLeft + 132 && mouseX <= this.guiLeft + 132 + 16 &&
                    mouseY >= this.guiTop + 106 && mouseY < this.guiTop + 106 + 16) {
                add = 16;
            }
        } else {
            add = 32;
        }
        this.drawTexturedModalRect(guiLeft + 132, guiTop + 106, 176 + add, 16, 16, 16);

        //Pop out
        add = 0;
        if (mouseX >= this.guiLeft + 116 && mouseX <= this.guiLeft + 116 + 16 &&
                mouseY >= this.guiTop + 106 && mouseY < this.guiTop + 106 + 16) {
            if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
                add = 16;
            }
            drawPopoutInfo = true;
        }
        this.drawTexturedModalRect(guiLeft + 116, guiTop + 106, 176 + add, 48, 16, 16);

        //2D view
        add = 0;
        if (renderContext.doesRenderIn3D()) {
            if (mouseX >= this.guiLeft + 132 && mouseX <= this.guiLeft + 132 + 16 &&
                    mouseY >= this.guiTop + 122 && mouseY <= this.guiTop + 122 + 16) {
                add = 16;
            }
        } else {
            add = 32;
        }
        this.drawTexturedModalRect(guiLeft + 132, guiTop + 122, 176 + add, 32, 16, 16);

        //Show amount
        add = 0;
        if (mouseX >= this.guiLeft + 116 && mouseX <= this.guiLeft + 116 + 16 &&
                mouseY >= this.guiTop + 122 && mouseY <= this.guiTop + 122 + 16) {
            add = 16;
            drawContents = true;
        }
        this.drawTexturedModalRect(guiLeft + 116, guiTop + 122, 176 + add, 64, 16, 16);

        if (renderContext.doesRenderIn3D()) {
            GlStateManager.color(0.3F, 0.3F, 0.3F, 1.0F);
        } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (renderContext.hasSliceUp()) {
            if (!renderContext.doesRenderIn3D() && mouseX >= this.guiLeft + 150 && mouseX <= this.guiLeft + 150 + 16 &&
                    mouseY >= this.guiTop + 102 && mouseY <= this.guiTop + 102 + 16) {
                GlStateManager.color(0.7F, 0.7F, 1.0F, 1.0F);
            }
            this.drawTexturedModalRect(guiLeft + 150, guiTop + 102, 192, 0, 16, 16);
            GlStateManager.color(1F, 1F, 1F, 1F);
        }
        if (renderContext.hasSliceDown()) {
            if (!renderContext.doesRenderIn3D() && mouseX >= this.guiLeft + 150 && mouseX <= this.guiLeft + 150 + 16 &&
                    mouseY >= this.guiTop + 124 && mouseY <= this.guiTop + 124 + 16) {
                GlStateManager.color(0.7F, 0.7F, 1.0F, 1.0F);
            }
            this.drawTexturedModalRect(guiLeft + 150, guiTop + 124, 176, 0, 16, 16);
            GlStateManager.color(1F, 1F, 1F, 1F);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int width = fontRenderer.getStringWidth(String.valueOf(renderContext.getRenderSlice()));
        fontRenderer.drawStringWithShadow(String.valueOf(renderContext.getRenderSlice()), guiLeft + 159 - (width / 2), guiTop + 118, 0x222222);
        if (drawPopoutInfo) {
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            List<String> out = fontRenderer.listFormattedStringToWidth(
                    I18n.format("gui.blueprint.popout.info"),
                    Math.min(res.getScaledWidth() - mouseX, 200));
            RenderingUtils.renderBlueTooltip(mouseX, mouseY, out, fontRenderer);
        }
        if (drawContents) {
            List<ItemStack> contents = this.renderContext.getDescriptiveStacks();
            List<Tuple<ItemStack, String>> contentMap = Lists.newArrayList();
            ItemStack ctrl = new ItemStack(BlocksMM.blockController);
            contentMap.add(new Tuple<>(ctrl, "1x " + Iterables.getFirst(ctrl.getTooltip(Minecraft.getMinecraft().player,
                    Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL), "")));
            for (ItemStack stack : contents) {
                contentMap.add(new Tuple<>(stack, stack.getCount() + "x " + Iterables.getFirst(stack.getTooltip(Minecraft.getMinecraft().player,
                        Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL), "")));
            }
            RenderingUtils.renderBlueStackTooltip(mouseX, mouseY,
                    contentMap,
                    fontRenderer, Minecraft.getMinecraft().getRenderItem());
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            if (!renderContext.doesRenderIn3D()) {
                if (mouseX >= this.guiLeft + 132 && mouseX <= this.guiLeft + 132 + 16 &&
                        mouseY >= this.guiTop + 106 && mouseY <= this.guiTop + 106 + 16) {
                    renderContext.setTo3D();
                }
                if (renderContext.hasSliceUp() && mouseX >= this.guiLeft + 150 && mouseX <= this.guiLeft + 150 + 16 &&
                        mouseY >= this.guiTop + 102 && mouseY <= this.guiTop + 102 + 16) {
                    renderContext.sliceUp();
                }
                if (renderContext.hasSliceDown() && mouseX >= this.guiLeft + 150 && mouseX <= this.guiLeft + 150 + 16 &&
                        mouseY >= this.guiTop + 124 && mouseY <= this.guiTop + 124 + 16) {
                    renderContext.sliceDown();
                }
            } else {
                if (mouseX >= this.guiLeft + 132 && mouseX <= this.guiLeft + 132 + 16 &&
                        mouseY >= this.guiTop + 122 && mouseY <= this.guiTop + 122 + 16) {
                    renderContext.setTo2D();
                }
            }
            if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak) &&
                    mouseX >= this.guiLeft + 116 && mouseX <= this.guiLeft + 116 + 16 &&
                    mouseY >= this.guiTop + 106 && mouseY < this.guiTop + 106 + 16) {
                if (ClientProxy.renderHelper.startPreview(this.renderContext)) {
                    Minecraft.getMinecraft().displayGuiScreen(null);
                }
            }
        } else if (mouseButton == 1) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

}
