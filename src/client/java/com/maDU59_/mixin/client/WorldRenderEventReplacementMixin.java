package com.maDU59_.mixin.client;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;

import com.maDU59_.ptpClient;
import com.maDU59_.Utils.WorldRenderContextReplacement;
import com.mojang.blaze3d.buffers.GpuBufferSlice;

@Mixin(WorldRenderer.class)
public class WorldRenderEventReplacementMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void afterRenderMain(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
        //ptpClient.renderOverlay(new WorldRenderContextReplacement(positionMatrix));
    }
}
