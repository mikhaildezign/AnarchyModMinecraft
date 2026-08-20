package com.infinitybackpack.client.screen;

import com.infinitybackpack.registry.ModItems;
import com.infinitybackpack.InfinityBackpackMod;
import com.infinitybackpack.screen.ExpExchangeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ExpExchangeScreen extends AbstractContainerScreen<ExpExchangeMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final int CONTAINER_ROWS = 3;

    private final List<ExchangeButton> buttons = new ArrayList<>();
    private Rect infoBottleRect;

    public ExpExchangeScreen(ExpExchangeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 114 + CONTAINER_ROWS * 18;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos;
        int y = this.topPos;
        int rowY = y + 18 + 18;

        buttons.add(new ExchangeButton(0, x + 8,  rowY, new ItemStack(Items.EXPERIENCE_BOTTLE),        7));
        buttons.add(new ExchangeButton(1, x + 44, rowY, new ItemStack(ModItems.EXP_BOTTLE_15), 315));
        buttons.add(new ExchangeButton(2, x + 80, rowY, new ItemStack(ModItems.EXP_BOTTLE_30), 1395));
        buttons.add(new ExchangeButton(3, x + 116,rowY, new ItemStack(ModItems.EXP_BOTTLE_50), 5345));
        buttons.add(new ExchangeButton(4, x + 152,rowY, new ItemStack(ModItems.EXP_BOTTLE_100),30971));

        infoBottleRect = new Rect(x + 80, y + 18 + 36, 16, 16);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        int containerHeight = CONTAINER_ROWS * 18 + 17;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, containerHeight);
        guiGraphics.blit(TEXTURE, x, y + containerHeight, 0, 126, this.imageWidth, 96);

        ItemStack pane = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);

        // Верхний ряд — полностью панели
        for (int col = 0; col < 9; col++) {
            guiGraphics.renderItem(pane, x + 8 + col * 18, y + 18);
        }

        // Средний ряд — панели только между бутылками (пустые слоты), под бутылками — нет
        int[] middleEmpty = {1, 3, 5, 7};
        for (int col : middleEmpty) {
            guiGraphics.renderItem(pane, x + 8 + col * 18, y + 18 + 18);
        }

        // Нижний ряд — панели везде, кроме инфо-бутылки посередине
        for (int col = 0; col < 9; col++) {
            if (col != 4) {
                guiGraphics.renderItem(pane, x + 8 + col * 18, y + 18 + 36);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Кнопки с бутылками
        for (ExchangeButton btn : buttons) {
            guiGraphics.renderItem(btn.icon, btn.x, btn.y);
            if (btn.contains(mouseX, mouseY)) {
                guiGraphics.fill(btn.x, btn.y, btn.x + 16, btn.y + 16, 0x80FFFFFF);
            }
        }

        // Инфо-бутылка снизу
        guiGraphics.renderItem(new ItemStack(Items.EXPERIENCE_BOTTLE), infoBottleRect.x, infoBottleRect.y);
        if (infoBottleRect.contains(mouseX, mouseY) && this.minecraft.player != null) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Ваш опыт:"));
            tooltip.add(Component.literal("Уровень: " + this.minecraft.player.experienceLevel));
            tooltip.add(Component.literal("XP: " + this.minecraft.player.totalExperience));
            guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }

        // Тултипы кнопок — оригинальное название предмета + стоимость
        for (ExchangeButton btn : buttons) {
            if (btn.contains(mouseX, mouseY)) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(btn.icon.getHoverName());
                tooltip.add(Component.literal("Стоимость: " + btn.cost + " XP"));
                if (hasShiftDown()) {
                    tooltip.add(Component.literal("§eShift+ПКМ — обменять максимум"));
                }
                guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
                break;
            }
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ExchangeButton btn : buttons) {
            if (btn.contains((int) mouseX, (int) mouseY)) {
                int id = btn.id;
                if (hasShiftDown()) id += 100;
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private record ExchangeButton(int id, int x, int y, ItemStack icon, int cost) {
        boolean contains(int mx, int my) {
            return mx >= x && mx < x + 16 && my >= y && my < y + 16;
        }
    }

    private record Rect(int x, int y, int w, int h) {
        boolean contains(int mx, int my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }
}