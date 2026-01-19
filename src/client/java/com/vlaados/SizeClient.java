package com.vlaados;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

public class SizeClient implements ClientModInitializer {
	private static final String KEY_CATEGORY = "size";
	private static final String KEY_OPEN_SCREEN = "open_stretch_settings";

	private static KeyMapping openStretchScreenKey;

	@Override
	public void onInitializeClient() {
		openStretchScreenKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			KEY_OPEN_SCREEN,
			GLFW.GLFW_KEY_O,
			KEY_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(this::handleClientTick);
	}

	private void handleClientTick(Minecraft client) {
		StretchScaler.tick();
		while (openStretchScreenKey.consumeClick()) {
			openStretchSettings(client);
		}
	}

	private void openStretchSettings(Minecraft client) {
		Screen current = client.screen;
		if (current instanceof StretchSettingsScreen) {
			return;
		}
		client.setScreen(new StretchSettingsScreen(current));
	}
}
