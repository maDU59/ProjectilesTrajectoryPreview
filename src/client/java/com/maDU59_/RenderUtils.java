package com.maDU59_;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;

public class RenderUtils {

    private static final float LINE_WIDTH = 2.0f;
    private static final Minecraft client = Minecraft.getInstance();

    public static void renderFilledBox(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha) {
        PoseStack matrices = context.matrices();
        Vec3 camera = client.gameRenderer.getMainCamera().position();

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer quadConsumer = context.consumers().getBuffer(RenderTypes.debugFilledBox());

        addChainedFilledBoxVertices(matrices, quadConsumer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        matrices.popPose();
    }

    public static void renderBox(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha) {
        PoseStack matrices = context.matrices();
        Vec3 camera = client.gameRenderer.getMainCamera().position();

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer quadConsumer = context.consumers().getBuffer(RenderTypes.lines());

        renderLineBox(matrices.last(), quadConsumer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        matrices.popPose();
    }

    public static void renderVector(PoseStack poseStack, VertexConsumer vertexConsumer, Vector3f vector3f, Vec3 vec3, int i) {
        PoseStack.Pose pose = poseStack.last();
        vertexConsumer.addVertex(pose, vector3f).setColor(i).setNormal(pose, (float)vec3.x, (float)vec3.y, (float)vec3.z).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, (float)((double)vector3f.x() + vec3.x), (float)((double)vector3f.y() + vec3.y), (float)((double)vector3f.z() + vec3.z)).setColor(i).setNormal(pose, (float)vec3.x, (float)vec3.y, (float)vec3.z).setLineWidth(LINE_WIDTH);
    }



    private static void addChainedFilledBoxVertices(PoseStack poseStack, VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        addChainedFilledBoxVertices(poseStack, vertexConsumer, (float)d, (float)e, (float)f, (float)g, (float)h, (float)i, j, k, l, m);
    }

    private static void addChainedFilledBoxVertices(PoseStack poseStack, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o) {
        Matrix4f matrix4f = poseStack.last().pose();

        // X-
        vertexConsumer.addVertex(matrix4f, f, g, h).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, f, g, k).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, f, j, k).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, f, j, h).setColor(l, m, n, o);

        // X +
        vertexConsumer.addVertex(matrix4f, i, g, h).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, i, j, h).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, i, j, k).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, i, g, k).setColor(l, m, n, o);

        // Y-
        vertexConsumer.addVertex(matrix4f, f, g, h).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, i, g, h).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, i, g, k).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, f, g, k).setColor(l, m, n, o);

        // Y+
        vertexConsumer.addVertex(matrix4f, f, j, k).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, i, j, k).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, i, j, h).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, f, j, h).setColor(l, m, n, o);

        // Z-
        vertexConsumer.addVertex(matrix4f, i, g, k).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, i, j, k).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, f, j, k).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, f, g, k).setColor(l, m, n, o);

        // Z+
        vertexConsumer.addVertex(matrix4f, f, g, h).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, f, j, h).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, i, j, h).setColor(l, m, n, o);
        vertexConsumer.addVertex(matrix4f, i, g, h).setColor(l, m, n, o);
    }

    private static void renderLineBox(PoseStack.Pose pose, VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        renderLineBox(pose, vertexConsumer, d, e, f, g, h, i, j, k, l, m, j, k, l);
    }

    private static void renderLineBox(PoseStack.Pose pose, VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m, float n, float o, float p) {
        float q = (float)d;
        float r = (float)e;
        float s = (float)f;
        float t = (float)g;
        float u = (float)h;
        float v = (float)i;
        vertexConsumer.addVertex(pose, q, r, s).setColor(j, o, p, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, r, s).setColor(j, o, p, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, r, s).setColor(n, k, p, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, u, s).setColor(n, k, p, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, r, s).setColor(n, o, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, r, v).setColor(n, o, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, r, s).setColor(j, k, l, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, u, s).setColor(j, k, l, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, u, s).setColor(j, k, l, m).setNormal(pose, -1.0F, 0.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, u, s).setColor(j, k, l, m).setNormal(pose, -1.0F, 0.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, u, s).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, u, v).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, u, v).setColor(j, k, l, m).setNormal(pose, 0.0F, -1.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, r, v).setColor(j, k, l, m).setNormal(pose, 0.0F, -1.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, r, v).setColor(j, k, l, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, r, v).setColor(j, k, l, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, r, v).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, -1.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, r, s).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, -1.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, q, u, v).setColor(j, k, l, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, u, v).setColor(j, k, l, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, r, v).setColor(j, k, l, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, u, v).setColor(j, k, l, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, u, s).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(LINE_WIDTH);
        vertexConsumer.addVertex(pose, t, u, v).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(LINE_WIDTH);
    }
}
