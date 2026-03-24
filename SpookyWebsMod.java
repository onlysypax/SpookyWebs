package com.spooky.webs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;

import org.lwjgl.glfw.GLFW;

public class SpookyWebsMod implements ClientModInitializer {

    private static KeyBinding key;
    private static int delayTimer = 0;

    @Override
    public void onInitializeClient() {

        key = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.spookywebs.place",
                GLFW.GLFW_KEY_C,
                "category.spookywebs")
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            if (delayTimer > 0) delayTimer--;

            while (key.wasPressed()) {
                placeWeb(client);
            }
        });
    }

    private void placeWeb(MinecraftClient client) {
        if (delayTimer > 0) return;

        HitResult hit = client.crosshairTarget;

        if (!(hit instanceof BlockHitResult blockHit)) return;

        BlockPos pos = blockHit.getBlockPos().offset(blockHit.getSide());

        double dist = client.player.squaredDistanceTo(
                pos.getX(), pos.getY(), pos.getZ()
        );

        if (dist > Config.range * Config.range) return;

        client.interactionManager.interactBlock(
                client.player,
                Hand.MAIN_HAND,
                blockHit
        );

        delayTimer = Config.legitMode ? Config.delay : 0;
    }
}
