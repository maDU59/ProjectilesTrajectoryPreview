package com.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class ptpClient implements ClientModInitializer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Logger LOGGER = LogManager.getLogger("ptpClient");

    // Config simple (tu peux plus tard mettre un écran d’options)
    private static boolean SHOW_TRAJECTORY = true;  // true = ligne, false = seulement point impact
    private static boolean OUTLINE_TARGET = true;


    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            PlayerEntity player = client.player;
            if (player == null) return;

            Item item = player.getMainHandStack().getItem();

            ProjectileInfo projectileInfo = ProjectileInfo.getItemsInfo(item, player);
            if(!projectileInfo.isReadyToShoot) return;

            MatrixStack matrices = context.matrixStack();
            VertexConsumer consumer = context.consumers().getBuffer(RenderLayer.getLines());

            Vec3d eye = player.getEyePos();

            Vec3d vel = projectileInfo.initialVelocity.add(player.getVelocity());

            Vec3d pos = eye;
            Vec3d prevPos = pos;
            Vec3d handToEyeDelta = GetHandToEyeDelta(player, projectileInfo.offset, context);
            HitResult impact = null;
            List<Vec3d> trajectoryPoints = new ArrayList<>();

            // Simulation
            for (int i = 0; i < 200; i++) {
                trajectoryPoints.add(pos);
                pos = pos.add(vel);
                vel = vel.multiply(projectileInfo.drag).subtract(0, projectileInfo.gravity, 0);

                // collision
                HitResult hit = player.getWorld().raycast(
                    new RaycastContext(prevPos, pos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        player));

                if (hit.getType() != HitResult.Type.MISS) {
                    impact = hit;
                    pos = hit.getPos();
                    trajectoryPoints.add(pos);
                    break;
                }

                prevPos = pos;
            }

            Vec3d cam = context.camera().getPos();

            if (SHOW_TRAJECTORY) {
                context.matrixStack().push();
                matrices.translate(-cam.x, -cam.y, -cam.z);
                for (int i = 0; i < trajectoryPoints.size(); i++) {
                    Vec3d lerpedDelta = handToEyeDelta.multiply((trajectoryPoints.size()-(i * 1.0))/trajectoryPoints.size());
                    pos = trajectoryPoints.get(i);
                    Vec3d normal = pos.subtract(i==0?eye:trajectoryPoints.get(i-1)).normalize();
                    if(i!=0 && i!= trajectoryPoints.size()-1){
                        consumer.vertex(matrices.peek().getPositionMatrix(), (float) (pos.add(lerpedDelta)).x, (float) pos.add(lerpedDelta).y, (float) pos.add(lerpedDelta).z).color(0, 255, 0, 255).normal((float)normal.x,  (float)normal.y,  (float)normal.z);
                    }
                    consumer.vertex(matrices.peek().getPositionMatrix(), (float) (pos.add(lerpedDelta)).x, (float) pos.add(lerpedDelta).y, (float) pos.add(lerpedDelta).z).color(0, 255, 0, 255).normal((float)normal.x,  (float)normal.y,  (float)normal.z);
                }
                context.matrixStack().pop();
            }

            if (impact != null) {
                context.matrixStack().push();
                matrices.translate(-cam.x, -cam.y, -cam.z);

                double r = 0.1;
                double x = impact.getPos().x;
                double y = impact.getPos().y;
                double z = impact.getPos().z;

                consumer.vertex(matrices.peek().getPositionMatrix(), (float) (x - r), (float) y, (float) z).color(255, 0, 0, 255).normal(0,1,0);
                consumer.vertex(matrices.peek().getPositionMatrix(), (float) (x + r), (float) y, (float) z).color(255, 0, 0, 255).normal(0,1,0);

                consumer.vertex(matrices.peek().getPositionMatrix(), (float) x, (float) (y - r), (float) z).color(255, 0, 0, 255).normal(0,1,0);
                consumer.vertex(matrices.peek().getPositionMatrix(), (float) x, (float) (y + r), (float) z).color(255, 0, 0, 255).normal(0,1,0);

                consumer.vertex(matrices.peek().getPositionMatrix(), (float) x, (float) y, (float) (z - r)).color(255, 0, 0, 255).normal(0,1,0);
                consumer.vertex(matrices.peek().getPositionMatrix(), (float) x, (float) y, (float) (z + r)).color(255, 0, 0, 255).normal(0,1,0);

                context.matrixStack().pop();
            }
        });
    }

    private Vec3d GetHandToEyeDelta(PlayerEntity player, Vec3d offset, WorldRenderContext context) {

        float yaw = (float) Math.toRadians(-player.getYaw(1.0F));
        float pitch = (float) Math.toRadians(-player.getPitch(1.0F));

        Vec3d forward = player.getRotationVec(1.0F);
        Vec3d up = new Vec3d(-Math.sin(pitch) * Math.sin(yaw), Math.cos(pitch), -Math.sin(pitch) * Math.cos(yaw)).normalize();
        Vec3d right = forward.crossProduct(up).normalize();

        return right.multiply(offset.x).add(up.multiply(offset.y)).add(forward.multiply(offset.z)).add(context.camera().getPos().subtract(player.getEyePos()));
    }
}
