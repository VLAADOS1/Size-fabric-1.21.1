package com.vlaados;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;

public class StretchSettingsScreen extends Screen {
	private static final PresetRatio[] PRESET_RATIOS = new PresetRatio[]{
		new PresetRatio(4.0D / 3.0D, "4:3"),
		new PresetRatio(16.0D / 10.0D, "16:10"),
		new PresetRatio(16.0D / 9.0D, "16:9"),
		new PresetRatio(21.0D / 9.0D, "21:9")
	};

	private final Screen parent;
	private final double nativeRatio;

	private StretchSlider slider;

	public StretchSettingsScreen(Screen parent) {
		super(Component.translatable("title"));
		this.parent = parent;
		this.nativeRatio = StretchScaler.getNativeAspectRatio();
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int sliderWidth = 280;
		int sliderLeft = centerX - sliderWidth / 2;
		int sliderY = this.height / 2 - 50;

		this.slider = new StretchSlider(sliderLeft, sliderY, sliderWidth, 20, StretchScaler.getAspectRatio());
		this.addRenderableWidget(this.slider);

		int buttonY = sliderY + 28;
		int buttonWidth = 180;

		this.addRenderableWidget(Button.builder(Component.translatable("reset"), button -> this.slider.setRatio(this.nativeRatio))
			.bounds(centerX - buttonWidth / 2, buttonY, buttonWidth, 20)
			.build());

		int presetsY = buttonY + 32;
		int presetWidth = 60;
		int spacing = 8;
		for (int i = 0; i < PRESET_RATIOS.length; i++) {
			PresetRatio preset = PRESET_RATIOS[i];
			int x = sliderLeft + i * (presetWidth + spacing);
			this.addRenderableWidget(Button.builder(Component.literal(preset.label()), button -> this.slider.setRatio(preset.ratio()))
				.bounds(x, presetsY, presetWidth, 20)
				.build());
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.slider != null) {
			this.slider.refreshFromState();
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderBackground(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 90, 0xFFFFFF);
		super.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parent);
	}

	private record PresetRatio(double ratio, String label) {
	}

	private static class StretchSlider extends AbstractSliderButton {
		private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

		private double ratio;

		StretchSlider(int x, int y, int width, int height, double initialRatio) {
			super(x, y, width, height, Component.empty(), 0.0D);
			setRatioInternal(initialRatio);
		}

		@Override
		protected void updateMessage() {
			String ratioText = FORMAT.format(this.ratio);
			int width = StretchScaler.getTargetWidth();
			int height = StretchScaler.getTargetHeight();
			this.setMessage(Component.translatable("slider", ratioText, width, height));
		}

		@Override
		protected void applyValue() {
			double newRatio = ratioFromValue(this.value);
			StretchScaler.applyAspectRatio(newRatio);
			setRatioInternal(StretchScaler.getAspectRatio());
		}

		void setRatio(double ratio) {
			StretchScaler.applyAspectRatio(ratio);
			setRatioInternal(StretchScaler.getAspectRatio());
		}

		void refreshFromState() {
			setRatioInternal(StretchScaler.getAspectRatio());
		}

		private void setRatioInternal(double ratio) {
			this.ratio = Mth.clamp(ratio, StretchScaler.MIN_RATIO, StretchScaler.MAX_RATIO);
			this.value = valueFromRatio(this.ratio);
			updateMessage();
		}

		private static double valueFromRatio(double ratio) {
			double range = StretchScaler.MAX_RATIO - StretchScaler.MIN_RATIO;
			if (range <= 0.0D) {
				return 0.0D;
			}
			return Mth.clamp((ratio - StretchScaler.MIN_RATIO) / range, 0.0D, 1.0D);
		}

		private static double ratioFromValue(double value) {
			return StretchScaler.MIN_RATIO + value * (StretchScaler.MAX_RATIO - StretchScaler.MIN_RATIO);
		}
	}
}
