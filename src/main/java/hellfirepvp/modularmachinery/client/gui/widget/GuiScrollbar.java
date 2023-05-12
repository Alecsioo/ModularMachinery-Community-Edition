package hellfirepvp.modularmachinery.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiScrollbar {
    public static final ResourceLocation TEXTURES_TABS = new ResourceLocation("minecraft", "textures/gui/container/creative_inventory/tabs.png");

    private int displayX = 0;
    private int displayY = 0;
    private int width = 12;
    private int height = 16;
    private int pageSize = 1;

    private int maxScroll = 0;
    private int minScroll = 0;
    private int currentScroll = 0;

    public void draw(final GuiScreen g, Minecraft mc) {
        mc.getTextureManager().bindTexture(TEXTURES_TABS);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        if (this.getRange() == 0) {
            g.drawTexturedModalRect(this.displayX, this.displayY, 232 + this.width, 0, this.width, 15);
        } else {
            final int offset = (this.currentScroll - this.minScroll) * (this.height - 15) / this.getRange();
            g.drawTexturedModalRect(this.displayX, offset + this.displayY, 232, 0, this.width, 15);
        }
    }

    private int getRange() {
        return this.maxScroll - this.minScroll;
    }

    public int getLeft() {
        return this.displayX;
    }

    public GuiScrollbar setLeft(final int v) {
        this.displayX = v;
        return this;
    }

    public int getTop() {
        return this.displayY;
    }

    public GuiScrollbar setTop(final int v) {
        this.displayY = v;
        return this;
    }

    public int getWidth() {
        return this.width;
    }

    public GuiScrollbar setWidth(final int v) {
        this.width = v;
        return this;
    }

    public int getHeight() {
        return this.height;
    }

    public GuiScrollbar setHeight(final int v) {
        this.height = v;
        return this;
    }

    public void setRange(final int min, final int max, final int pageSize) {
        this.minScroll = min;
        this.maxScroll = max;
        this.pageSize = pageSize;

        if (this.minScroll > this.maxScroll) {
            this.maxScroll = this.minScroll;
        }

        this.applyRange();
    }

    private void applyRange() {
        this.currentScroll = Math.max(Math.min(this.currentScroll, this.maxScroll), this.minScroll);
    }

    public int getCurrentScroll() {
        return this.currentScroll;
    }

    public void click(final int x, final int y) {
        if (this.getRange() == 0) {
            return;
        }

        if (isMouseOver(x, y)) {
            this.currentScroll = (y - this.displayY);
            this.currentScroll = this.minScroll + ((this.currentScroll * 2 * this.getRange() / this.height));
            this.currentScroll = (this.currentScroll + 1) >> 1;
            this.applyRange();
        }
    }

    public boolean isMouseOver(final int x, final int y) {
        if (x > this.displayX && x <= this.displayX + this.width) {
            return y > this.displayY && y <= this.displayY + this.height;
        }
        return false;
    }

    public void wheel(int delta) {
        delta = Math.max(Math.min(-delta, 1), -1);
        this.currentScroll += delta * this.pageSize;
        this.applyRange();
    }
}