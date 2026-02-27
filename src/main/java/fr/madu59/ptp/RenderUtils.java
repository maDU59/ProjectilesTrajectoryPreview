package fr.madu59.ptp;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class RenderUtils {

    private static final Minecraft client = Minecraft.getInstance();

    public static void renderFilledBox(RenderLevelStageEvent.AfterEntities event, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha) {
        PoseStack matrices = event.getPoseStack();
        Vec3 camera = client.gameRenderer.getMainCamera().position();

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer quadConsumer = client.renderBuffers().bufferSource().getBuffer(RenderType.debugFilledBox());

        ShapeRenderer.addChainedFilledBoxVertices(matrices, quadConsumer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        matrices.popPose();
    }

    public static void renderBox(RenderLevelStageEvent.AfterEntities event, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha) {
        PoseStack matrices = event.getPoseStack();
        Vec3 camera = client.gameRenderer.getMainCamera().position();

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer quadConsumer = client.renderBuffers().bufferSource().getBuffer(RenderType.lineStrip());

        ShapeRenderer.renderLineBox(matrices.last(), quadConsumer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        matrices.popPose();
    }

    public static void renderVector(PoseStack poseStack, VertexConsumer vertexConsumer, Vector3f vector3f, Vec3 vec3, int i) {
        PoseStack.Pose pose = poseStack.last();
        vertexConsumer.addVertex(pose, vector3f).setColor(i).setNormal(pose, (float)vec3.x, (float)vec3.y, (float)vec3.z);
        vertexConsumer.addVertex(pose, (float)((double)vector3f.x() + vec3.x), (float)((double)vector3f.y() + vec3.y), (float)((double)vector3f.z() + vec3.z)).setColor(i).setNormal(pose, (float)vec3.x, (float)vec3.y, (float)vec3.z);
    }
}
