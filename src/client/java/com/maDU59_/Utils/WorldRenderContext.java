package com.maDU59_.Utils;

import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class WorldRenderContext {

    public final Matrix4f positionMatrix;
    public final Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
    public MatrixStack matrixStack = new MatrixStack();

    public WorldRenderContext(Matrix4f positionMatrix) {
        this.positionMatrix = positionMatrix;
        this.matrixStack.multiplyPositionMatrix(positionMatrix);
    }

    public Camera camera() {
        return camera;
    }

    public MatrixStack matrixStack() {
        return matrixStack;
    }

    public VertexConsumerProvider.Immediate consumers() {
        return MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
    }
}
