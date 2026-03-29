package com.minecrafttas.lotas_light.mixin;

//# 1.21.8
//$$import org.joml.Matrix3x2fStack;
//# def
import com.mojang.blaze3d.vertex.PoseStack;
//# end
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//# 1.21.1
//$$import net.minecraft.client.DeltaTracker;
//# end
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
//# 26.1
//$$import net.minecraft.client.gui.GuiGraphicsExtractor;
//# def
import net.minecraft.client.gui.GuiGraphics;
//# end

@Mixin(Gui.class)
public class MixinPlayerList {

	//# 1.20.6
	//## 26.1
//$$	@Inject(method = "extractHotbarAndDecorations", at = @At("HEAD"))
	//## 1.21.8
//$$	@Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
	//## def
//$$	@Inject(method = "renderExperienceLevel", at = @At("HEAD"))
	//## end
//$$	private void onRenderExperienceLevel(
	//## 26.1
//$$			GuiGraphicsExtractor memPose,
	//## def
//$$			GuiGraphics memPose,
	//## end
//$$			float deltaTracker, CallbackInfo ci) { //@GraphicsDelta;
	//# def
	@Inject(method = "renderExperienceBar", at = @At("HEAD"))
	private void onRenderExperienceLevel(GuiGraphics memPose, int deltaTracker, CallbackInfo ci) {
		//# end
		float memOffsetX = (Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2) - 6;
		float memOffsetY = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 31 - 19;

		float flip = 1.02f;
		float flipOffset = .3f;
		memOffsetX += 1.7f;
		memOffsetY += -.7;
		flip += 0.26;

		//# 1.21.8
		//## 26.1
//$$		Matrix3x2fStack memstack = memPose.pose();
		//## def
//$$		Matrix3x2fStack memstack = memPose.pose();
		//## end
//$$
//$$		memstack.pushMatrix();
//$$		memstack.translate(memOffsetX, memOffsetY);
//$$		memstack.scale(flip, flip);
//$$		memstack.rotate(flipOffset);
		//# def
		PoseStack memstack = memPose.pose();

		memstack.pushPose();
		memstack.translate(memOffsetX, memOffsetY, 0);
		memstack.scale(flip, flip, flip);
		memstack.mulPose(fromYXZ(0F, 0F, (float) flipOffset));
		//# end
		int oB = 0xE35720;
		int o = 0xC24218;
		int oD = 0x9A3212;

		int w = 0xFFFFFF;
		int c = 0x546980;

		int y = 0;
		setRegistryState(memPose, 8, y, oB);
		setRegistryState(memPose, 9, y, oB);

		y = 1;
		setRegistryState(memPose, 7, y, o);
		setRegistryState(memPose, 8, y, o);
		setRegistryState(memPose, 9, y, oB);

		y = 2;
		setRegistryState(memPose, 6, y, o);
		setRegistryState(memPose, 7, y, o);
		setRegistryState(memPose, 8, y, o);
		setRegistryState(memPose, 9, y, o);

		y = 3;
		setRegistryState(memPose, 5, y, w);
		setRegistryState(memPose, 8, y, oD);
		setRegistryState(memPose, 9, y, o);

		y = 4;
		setRegistryState(memPose, 4, y, w);
		setRegistryState(memPose, 7, y, w);
		setRegistryState(memPose, 8, y, oD);
		setRegistryState(memPose, 9, y, oD);

		y = 5;
		setRegistryState(memPose, 3, y, w);
		setRegistryState(memPose, 7, y, w);

		y = 6;
		setRegistryState(memPose, 2, y, w);
		setRegistryState(memPose, 4, y, w);
		setRegistryState(memPose, 8, y, w);

		y = 7;
		setRegistryState(memPose, 1, y, w);
		setRegistryState(memPose, 3, y, w);
		setRegistryState(memPose, 9, y, w);

		y = 8;
		setRegistryState(memPose, 1, y, w);
		setRegistryState(memPose, 3, y, w);
		setRegistryState(memPose, 9, y, w);

		y = 9;
		setRegistryState(memPose, 1, y, w);
		setRegistryState(memPose, 2, y, c);
		setRegistryState(memPose, 3, y, c);
		setRegistryState(memPose, 4, y, c);
		setRegistryState(memPose, 5, y, c);
		setRegistryState(memPose, 6, y, c);
		setRegistryState(memPose, 7, y, w);
		setRegistryState(memPose, 8, y, c);
		setRegistryState(memPose, 9, y, w);

		y = 10;
		setRegistryState(memPose, 1, y, w);
		setRegistryState(memPose, 2, y, c);
		setRegistryState(memPose, 3, y, c);
		setRegistryState(memPose, 4, y, c);
		setRegistryState(memPose, 5, y, c);
		setRegistryState(memPose, 6, y, c);
		setRegistryState(memPose, 7, y, w);
		setRegistryState(memPose, 8, y, c);
		setRegistryState(memPose, 9, y, w);

		y = 11;
		setRegistryState(memPose, 1, y, w);
		setRegistryState(memPose, 2, y, w);
		setRegistryState(memPose, 3, y, c);
		setRegistryState(memPose, 4, y, c);
		setRegistryState(memPose, 5, y, c);
		setRegistryState(memPose, 6, y, w);
		setRegistryState(memPose, 7, y, c);
		setRegistryState(memPose, 8, y, w);
		setRegistryState(memPose, 9, y, w);

		y = 12;
		setRegistryState(memPose, 3, y, w);
		setRegistryState(memPose, 4, y, w);
		setRegistryState(memPose, 5, y, w);
		setRegistryState(memPose, 6, y, w);
		setRegistryState(memPose, 7, y, w);

		//# 1.21.8
//$$		memstack.rotate(-(float) flipOffset);
//$$		memstack.scale(1 / flip, 1 / flip);
//$$		memstack.translate(-memOffsetX, -memOffsetY);
//$$		memstack.popMatrix();
		//# def
		memstack.mulPose(fromYXZ(0F, 0F, -(float) flipOffset));
		memstack.scale(1 / flip, 1 / flip, 1 / flip);
		memstack.translate(-memOffsetX, -memOffsetY, 0);
		memstack.popPose();
		//# end
	}

	public void setRegistryState(
	//# 26.1
//$$			GuiGraphicsExtractor guiGraphics,
			//# def
			GuiGraphics guiGraphics,
			//# end
			int x, int y, int color) {
		int alpha = 0x60000000;
		guiGraphics.fill(x, y, x + 1, y + 1, alpha + color);
	}

	public org.joml.Quaternionf fromYXZ(float f, float g, float h) {
		org.joml.Quaternionf quaternion = new org.joml.Quaternionf(0.0f, 0.0f, 0.0f, 1.0f);
		quaternion.mul(new org.joml.Quaternionf(0.0f, (float) Math.sin(f / 2.0f), 0.0f, (float) Math.cos(f / 2.0f)));
		quaternion.mul(new org.joml.Quaternionf((float) Math.sin(g / 2.0f), 0.0f, 0.0f, (float) Math.cos(g / 2.0f)));
		quaternion.mul(new org.joml.Quaternionf(0.0f, 0.0f, (float) Math.sin(h / 2.0f), (float) Math.cos(h / 2.0f)));
		return quaternion;
	}
}
