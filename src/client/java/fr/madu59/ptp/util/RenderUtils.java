package fr.madu59.ptp.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;

public class RenderUtils {

    private static final Minecraft client = Minecraft.getInstance();

    public static void renderFilledBox(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha) {
        PoseStack poseStack = context.matrixStack();
        Vec3 camera = client.gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer quadConsumer = context.consumers().getBuffer(RenderType.debugFilledBox());

        LevelRenderer.addChainedFilledBoxVertices(poseStack, quadConsumer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        poseStack.popPose();
    }

    public static void renderBox(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha) {
        PoseStack poseStack = context.matrixStack();
        Vec3 camera = client.gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer quadConsumer = context.consumers().getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(poseStack, quadConsumer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        poseStack.popPose();
    }

    public static void renderVector(PoseStack poseStack, VertexConsumer vertexConsumer, Vector3f vector3f, Vec3 vec3, int i) {
        Matrix4f pose = poseStack.last().pose();
        vertexConsumer.vertex(pose, vector3f.x(), vector3f.y(), vector3f.z()).color(i).normal((float)vec3.x, (float)vec3.y, (float)vec3.z);
        vertexConsumer.vertex(pose, (float)((double)vector3f.x() + vec3.x), (float)((double)vector3f.y() + vec3.y), (float)((double)vector3f.z() + vec3.z)).color(i).normal((float)vec3.x, (float)vec3.y, (float)vec3.z);
    }
}
