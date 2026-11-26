package com.erodev.sodiumrelief.gametest;

import com.erodev.sodiumrelief.client.SodiumReliefClient;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

@SuppressWarnings("UnstableApiUsage")
public final class EnchantmentDescriptionsClientGameTest implements FabricClientGameTest {
    private static final String HINT = "Hold shift to view enchantment descriptions.";
    private static final String DESCRIPTION = "Increases attack damage.";
    private static final TooltipLines HINT_ONLY = new TooltipLines(true, false);
    private static final TooltipLines DESCRIPTION_ONLY = new TooltipLines(false, true);
    private static final AtomicReference<String> WATCHED_NAME = new AtomicReference<>();
    private static final AtomicReference<List<String>> OBSERVED = new AtomicReference<>();

    @Override
    public void runTest(ClientGameTestContext context) {
        if (!Boolean.getBoolean("sodiumrelief.testEnchantmentDescriptions")) {
            return;
        }

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);
            context.runOnClient(client -> {
                if (!SodiumReliefClient.runtime().configManager().config().enableTooltipLayoutCache) {
                    throw new AssertionError("This test requires Sodium Relief's tooltip cache to be enabled");
                }
                setRequireKeybind(true);
            });
            try {
                assertShiftUpdatesTooltip(context, false);
                assertShiftUpdatesTooltip(context, true);
            } finally {
                WATCHED_NAME.set(null);
                OBSERVED.set(null);
                context.runOnClient(client -> setRequireKeybind(false));
            }
        }
    }

    public static void observeRenderedTooltip(List<Component> lines) {
        String name = WATCHED_NAME.get();
        if (name != null && !lines.isEmpty() && name.equals(lines.getFirst().getString())) {
            OBSERVED.set(lines.stream().map(Component::getString).toList());
        }
    }

    private static void assertShiftUpdatesTooltip(ClientGameTestContext context, boolean stored) {
        WATCHED_NAME.set(null);
        OBSERVED.set(null);
        ItemStack watched = context.computeOnClient(client -> {
            var sharpness = client.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS);
            ItemStack stack = new ItemStack(stored ? Items.ENCHANTED_BOOK : Items.DIAMOND_SWORD);
            ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            enchantments.set(sharpness, 1);
            stack.set(stored ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS, enchantments.toImmutable());
            client.player.getInventory().setItem(0, stack);
            WATCHED_NAME.set(stack.getHoverName().getString());
            return stack;
        });
        context.setScreen(() -> new InventoryScreen(Minecraft.getInstance().player));
        context.waitForScreen(InventoryScreen.class);
        double[] cursor = context.computeOnClient(client -> {
            var screen = (InventoryScreen) client.gui.screen();
            for (Slot slot : screen.getMenu().slots) {
                if (slot.getItem() == watched) {
                    return TooltipBenchmarkClientGameTest.cursorTargetForSlot(client, slot);
                }
            }
            throw new AssertionError("Enchanted stack did not reach the inventory screen");
        });
        context.getInput().setCursorPos(cursor[0], cursor[1]);
        waitForTooltip(context, HINT_ONLY);
        context.getInput().holdShift();
        try {
            waitForTooltip(context, DESCRIPTION_ONLY);
        } finally {
            context.getInput().releaseShift();
        }
        waitForTooltip(context, HINT_ONLY);
    }

    private static void waitForTooltip(ClientGameTestContext context, TooltipLines expected) {
        try {
            context.waitFor(client -> {
                List<String> lines = OBSERVED.get();
                return lines != null && expected.equals(new TooltipLines(lines.contains(HINT), lines.contains(DESCRIPTION)));
            }, 40);
        } catch (AssertionError exception) {
            throw new AssertionError("Expected " + expected + " for " + WATCHED_NAME.get()
                + "; last submitted tooltip: " + OBSERVED.get(), exception);
        }
    }

    private static void setRequireKeybind(boolean required) {
        try {
            // Test-only access to the pinned optional mod; production code uses no EnchDesc classes.
            Class<?> mod = Class.forName("net.darkhax.enchdesc.common.impl.EnchantmentDescriptionsMod");
            Object config = mod.getField("config").get(null);
            Field setting = config.getClass().getField("require_keybind");
            setting.setBoolean(config, required);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not set Enchantment Descriptions' require_keybind", exception);
        }
    }

    private record TooltipLines(boolean hint, boolean description) {
    }
}
