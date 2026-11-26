package com.erodev.sodiumrelief.compat;

import net.fabricmc.loader.api.FabricLoader;

public final class TooltipCompatibility {
    // Enchantment Descriptions reads live input while building enchanted-item tooltips.
    private static final boolean ENCHANTMENT_DESCRIPTIONS_LOADED = FabricLoader.getInstance().isModLoaded("enchdesc");

    private TooltipCompatibility() {
    }

    public static boolean enchantmentDescriptionsLoaded() {
        return ENCHANTMENT_DESCRIPTIONS_LOADED;
    }
}
