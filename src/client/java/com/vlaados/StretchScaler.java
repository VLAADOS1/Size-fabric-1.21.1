package com.vlaados;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

public final class StretchScaler {
	public static final double MIN_RATIO = 0.5D;
	public static final double MAX_RATIO = 3.0D;

	private static double targetRatio = Double.NaN;

	private StretchScaler() {
	}

	public static void tick() {
		if (Double.isNaN(targetRatio)) {
			targetRatio = getNativeAspectRatio();
		}
	}

	public static double getAspectRatio() {
		if (Double.isNaN(targetRatio)) {
			targetRatio = getNativeAspectRatio();
		}
		return targetRatio;
	}

	public static void applyAspectRatio(double ratio) {
		targetRatio = clampRatio(ratio);
	}

	public static double getNativeAspectRatio() {
		Window window = Minecraft.getInstance().getWindow();
		int height = Math.max(1, window.getHeight());
		return (double) window.getWidth() / (double) height;
	}

	public static int getTargetWidth() {
		Window window = Minecraft.getInstance().getWindow();
		int height = Math.max(1, window.getHeight());
		return Math.max(1, (int) Math.round(height * getAspectRatio()));
	}

	public static int getTargetHeight() {
		return Math.max(1, Minecraft.getInstance().getWindow().getHeight());
	}

	public static float getProjectionScale() {
		Minecraft mc = Minecraft.getInstance();
		Window window = mc.getWindow();
		int width = Math.max(1, window.getWidth());
		int height = Math.max(1, window.getHeight());
		double actualAspect = (double) width / (double) height;
		double target = getAspectRatio();
		if (target <= 0.0D) {
			return 1.0F;
		}
		return (float) (actualAspect / target);
	}

	private static double clampRatio(double ratio) {
		return Math.max(MIN_RATIO, Math.min(MAX_RATIO, ratio));
	}
}
