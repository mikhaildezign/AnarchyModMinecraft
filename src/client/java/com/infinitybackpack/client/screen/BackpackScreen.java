package com.infinitybackpack.client.screen;

import com.infinitybackpack.screen.BackpackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    // Высоты частей текстуры (generic_54.png = 176×222)
    private static final int TOP_HEIGHT = 17 + 4 * 18;      // Рамка + 4 ряда = 89
    private static final int TEX_SEPARATOR_Y = 17 + 6 * 18; // Где начинается разделитель в оригинале = 125
    private static final int BOTTOM_HEIGHT = 222 - TEX_SEPARATOR_Y; // Разделитель + инвентарь + хотбар = 97

    public BackpackScreen(BackpackMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = TOP_HEIGHT + BOTTOM_HEIGHT; // 89 + 97 = 186
        this.inventoryLabelY = 1000;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Верхняя часть: рамка + 4 ряда рюкзака
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, TOP_HEIGHT);

        // Нижняя часть: разделитель + инвентарь + хотбар (пропускаем 2 лишних ряда)
        guiGraphics.blit(TEXTURE, x, y + TOP_HEIGHT, 0, TEX_SEPARATOR_Y, this.imageWidth, BOTTOM_HEIGHT);
    }
}