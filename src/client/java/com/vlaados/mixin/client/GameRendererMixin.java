package com.vlaados.mixin.client;

import com.vlaados.StretchScaler;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(method = "getProjectionMatrix(D)Lorg/joml/Matrix4f;", at = @At("RETURN"), cancellable = true)
	private void size$adjustProjection(double tickDelta, CallbackInfoReturnable<Matrix4f> cir) {
		Matrix4f matrix = cir.getReturnValue();
		float scale = StretchScaler.getProjectionScale();
		if (Math.abs(scale - 1.0F) < 0.0001F) {
			return;
		}

		matrix.m00(matrix.m00() * scale);
		cir.setReturnValue(matrix);
	}
}
