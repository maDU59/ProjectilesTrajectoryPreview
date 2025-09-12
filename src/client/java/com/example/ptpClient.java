package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import com.example.config.SettingsManager;

public class ptpClient implements ClientModInitializer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Logger LOGGER = LogManager.getLogger("ptpClient");


    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            PlayerEntity player = client.player;
            if (player == null) return;

            Item item = player.getMainHandStack().getItem();

            ProjectileInfo projectileInfo = ProjectileInfo.getItemsInfo(item, player);
            if(!projectileInfo.isReadyToShoot) return;

            Vec3d eye = player.getEyePos();

            Vec3d vel = projectileInfo.initialVelocity.add(player.getVelocity());

            Vec3d pos = eye;
            Vec3d prevPos = pos;
            Vec3d handToEyeDelta = GetHandToEyeDelta(player, projectileInfo.offset, context);
            HitResult impact = null;
            Entity entityImpact = null;
            boolean hasHit = false;
            List<Vec3d> trajectoryPoints = new ArrayList<>();

            // Simulation
            for (int i = 0; i < 200; i++) {
                trajectoryPoints.add(pos);
                pos = pos.add(vel);
                vel = vel.multiply(projectileInfo.drag).subtract(0, projectileInfo.gravity, 0);

                Box box = new Box(prevPos, pos).expand(1.0);

                List<Entity> entities = client.world.getEntitiesByClass(Entity.class, box, e -> !e.isSpectator() && e.isAlive() && !(e instanceof ProjectileEntity));

                Entity closest = null;
                double closestDistance = 99999.0;
                Vec3d entityHitPos = null;

                for (Entity entity : entities) {
                    Box entityBox = entity.getBoundingBox().expand(entity.getTargetingMargin());
                    Optional<Vec3d> entityRaycastHit = entityBox.raycast(prevPos, pos);

                    if (entityRaycastHit.isPresent()) {
                        double distance = prevPos.squaredDistanceTo(entityRaycastHit.get());
                        if (distance < closestDistance) {
                            entityHitPos = entityRaycastHit.get();
                            closest = entity;
                            closestDistance = distance;
                        }
                    }
                }

                // collision
                HitResult hit = player.getWorld().raycast(
                    new RaycastContext(prevPos, pos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        player));

                if (hit.getType() != HitResult.Type.MISS && prevPos.squaredDistanceTo(hit.getPos()) < closestDistance) {
                    impact = hit;
                    pos = hit.getPos();
                    trajectoryPoints.add(pos);
                    hasHit = true;
                    break;
                }

                if(entityHitPos != null){
                    entityImpact = closest;
                    pos = entityHitPos;
                    trajectoryPoints.add(pos);
                    hasHit = true;
                    break;
                }

                prevPos = pos;
            }

            Vec3d cam = context.camera().getPos();

            if((boolean) SettingsManager.HIGHLIGHT_TARGETS.getValue()){
                if(impact != null && impact.getType() == HitResult.Type.BLOCK  && impact instanceof BlockHitResult blockHitResult) {
                    BlockPos impactPos = blockHitResult.getBlockPos();
                    renderFilled(context, impactPos.getX(), impactPos.getY(), impactPos.getZ(), impactPos.getX()+1, impactPos.getY()+1, impactPos.getZ()+1, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.HIGHLIGHT_COLOR.getValue())), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.HIGHLIGHT_OPACITY.getValue())));
                }
                else if(entityImpact != null) {
                    Box entityBoundingBox = entityImpact.getBoundingBox().expand(entityImpact.getTargetingMargin());
                    renderFilled(context, entityBoundingBox.minX, entityBoundingBox.minY, entityBoundingBox.minZ, entityBoundingBox.maxX, entityBoundingBox.maxY, entityBoundingBox.maxZ, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.HIGHLIGHT_COLOR.getValue())), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.HIGHLIGHT_OPACITY.getValue())));    
                }
            }

            if((boolean) SettingsManager.OUTLINE_TARGETS.getValue()){
                if(impact != null && impact.getType() == HitResult.Type.BLOCK  && impact instanceof BlockHitResult blockHitResult) {
                    BlockPos impactPos = blockHitResult.getBlockPos();
                    renderBox(context, impactPos.getX(), impactPos.getY(), impactPos.getZ(), impactPos.getX()+1, impactPos.getY()+1, impactPos.getZ()+1, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.OUTLINE_COLOR.getValue())), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.OUTLINE_OPACITY.getValue())));
                }
                else if(entityImpact != null) {
                    Box entityBoundingBox = entityImpact.getBoundingBox().expand(entityImpact.getTargetingMargin());
                    renderBox(context, entityBoundingBox.minX, entityBoundingBox.minY, entityBoundingBox.minZ, entityBoundingBox.maxX, entityBoundingBox.maxY, entityBoundingBox.maxZ, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.OUTLINE_COLOR.getValue())), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.OUTLINE_OPACITY.getValue())));    
                }
            }

            MatrixStack matrices = context.matrixStack();
            VertexConsumer lineConsumer = context.consumers().getBuffer(RenderLayer.getLines());

            if ((boolean) SettingsManager.SHOW_TRAJECTORY.getValue()) {
                matrices.push();
                matrices.translate(-cam.x, -cam.y, -cam.z);
                for (int i = 0; i < trajectoryPoints.size()-1; i++) {
                    Vec3d lerpedDelta = handToEyeDelta.multiply((trajectoryPoints.size()-(i * 1.0))/trajectoryPoints.size());
                    Vec3d nextLerpedDelta = handToEyeDelta.multiply((trajectoryPoints.size()-(i+1 * 1.0))/trajectoryPoints.size());
                    pos = trajectoryPoints.get(i).add(lerpedDelta);
                    int color = SettingsManager.getARGBColorFromSetting((String)SettingsManager.TRAJECTORY_COLOR.getValue(), (String)SettingsManager.TRAJECTORY_OPACITY.getValue());
                    Vec3d dir = (trajectoryPoints.get(i+1).add(nextLerpedDelta)).subtract(pos);
                    if(SettingsManager.TRAJECTORY_STYLE.getValueAsString() == "Dashed"){
                        dir = dir.multiply(0.5);
                    }
                    else if(SettingsManager.TRAJECTORY_STYLE.getValueAsString() == "Dotted"){
                        dir = dir.multiply(0.15);
                    }
                    Vector3f floatPos = new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);
                    VertexRendering.drawVector(matrices, lineConsumer, floatPos, dir, color);
                }
                matrices.pop();
            }

            if (hasHit) {
                matrices.push();
                matrices.translate(-cam.x, -cam.y, -cam.z);
                pos = trajectoryPoints.getLast();

                double r = 0.1;
                double x = pos.x;
                double y = pos.y;
                double z = pos.z;

                lineConsumer.vertex(matrices.peek().getPositionMatrix(), (float) (x - r), (float) y, (float) z).color(255, 0, 0, 255).normal(0,1,0);
                lineConsumer.vertex(matrices.peek().getPositionMatrix(), (float) (x + r), (float) y, (float) z).color(255, 0, 0, 255).normal(0,1,0);

                lineConsumer.vertex(matrices.peek().getPositionMatrix(), (float) x, (float) (y - r), (float) z).color(255, 0, 0, 255).normal(0,1,0);
                lineConsumer.vertex(matrices.peek().getPositionMatrix(), (float) x, (float) (y + r), (float) z).color(255, 0, 0, 255).normal(0,1,0);

                lineConsumer.vertex(matrices.peek().getPositionMatrix(), (float) x, (float) y, (float) (z - r)).color(255, 0, 0, 255).normal(0,1,0);
                lineConsumer.vertex(matrices.peek().getPositionMatrix(), (float) x, (float) y, (float) (z + r)).color(255, 0, 0, 255).normal(0,1,0);

                matrices.pop();
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

    private static void renderFilled(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha) {
        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer quadConsumer = context.consumers().getBuffer(RenderLayer.getDebugFilledBox());

        VertexRendering.drawFilledBox(matrices, quadConsumer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        matrices.pop();
    }

    private static void renderBox(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha) {
        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer quadConsumer = context.consumers().getBuffer(RenderLayer.getLineStrip());

        VertexRendering.drawBox(matrices, quadConsumer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        matrices.pop();
    }
}
