package com.erodev.sodiumrelief.gametest.mixin;

import com.erodev.sodiumrelief.gametest.EnchantmentDescriptionsClientGameTest;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
abstract class TooltipSubmissionMixin {
    @Inject(
        method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;Z)V",
        at = @At("HEAD")
    )
    private void sodiumRelief$observeTooltip(
        Font font, List<Component> lines, Optional<TooltipComponent> image,
        int mouseX, int mouseY, Identifier style, boolean force, CallbackInfo callback
    ) {
        EnchantmentDescriptionsClientGameTest.observeRenderedTooltip(lines);
    }
}
