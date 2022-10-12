package com.diamantino.stevevsalex.client.gui;

import com.diamantino.stevevsalex.containers.VehicleInventoryContainer;
import com.diamantino.stevevsalex.upgrades.base.Upgrade;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.diamantino.stevevsalex.SteveVsAlex.MODID;

public class VehicleInventoryScreen extends AbstractContainerScreen<VehicleInventoryContainer> {

    public static final ResourceLocation GUI = new ResourceLocation(MODID, "textures/gui/vehicle_inventory.png");

    public VehicleInventoryScreen(VehicleInventoryContainer screenContainer, Inventory inventory, Component title) {
        super(screenContainer, inventory, title);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        if (menu.planeEntity != null) {
            for (Upgrade upgrade : menu.planeEntity.upgrades.values()) {
                upgrade.renderScreen(poseStack, mouseX, mouseY, partialTicks, this);
            }
        }
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (menu.planeEntity != null) {
            for (Upgrade upgrade : menu.planeEntity.upgrades.values()) {
                upgrade.renderScreenBg(poseStack, x, y, partialTicks, this);
            }
        }
    }

    @Override
    public boolean isHovering(int p_97768_, int p_97769_, int p_97770_, int p_97771_, double p_97772_, double p_97773_) {
        return super.isHovering(p_97768_, p_97769_, p_97770_, p_97771_, p_97772_, p_97773_);
    }
}