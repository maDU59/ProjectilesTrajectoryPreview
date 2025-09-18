package com.maDU59_;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import com.maDU59_.HandshakeNetworking.HANDSHAKE_C2SPayload;
import com.maDU59_.HandshakeNetworking.HANDSHAKE_S2CPayload;
import com.maDU59_.config.ClientCommands;
import com.maDU59_.config.SettingsManager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class ptpClient implements ClientModInitializer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Logger LOGGER = LogManager.getLogger("ptpClient");
    private static boolean serverHasMod = false;


    @Override
    public void onInitializeClient() {
        ClientCommands.register();

        PayloadTypeRegistry.playS2C().register(HANDSHAKE_S2CPayload.ID, HANDSHAKE_S2CPayload.CODEC);

        // Reset handshake state on join
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            serverHasMod = false;

            // Always enabled in singleplayer
            if (client.isIntegratedServerRunning()) {
                serverHasMod = true;
                return;
            }

            // Send handshake to server
            ClientPlayNetworking.send(new HANDSHAKE_C2SPayload("Check if is installed on server"));
            System.out.println("[PTP] Sending handshake to server...");
        });

        // Receive handshake reply
        ClientPlayNetworking.registerGlobalReceiver(HANDSHAKE_S2CPayload.ID,
            (payload, context) -> {
                System.out.println("[PTP] Received handshake from server...");
                serverHasMod = true;
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            PlayerEntity player = client.player;
            if (player == null || !isEnabled()) return;

            Item item = player.getMainHandStack().getItem();

            ProjectileInfo projectileInfo = ProjectileInfo.getItemsInfo(item, player);
            if(!projectileInfo.isReadyToShoot) return;

            Vec3d eye = player.getEyePos();

            Vec3d vel = projectileInfo.initialVelocity.add(player.getVelocity());

            Vec3d pos;

            if(projectileInfo.position == null){
                pos = eye;
            }
            else{
                pos = projectileInfo.position;
            }
            Vec3d prevPos = pos;
            Vec3d handToEyeDelta = GetHandToEyeDelta(player, projectileInfo.offset, context, pos);
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

                List<Entity> entities = client.world.getEntitiesByClass(Entity.class, box, e -> !e.isSpectator() && e.isAlive() && !(e instanceof ProjectileEntity) && !(e instanceof EnderDragonEntity));

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

                HitResult hit;
                if(projectileInfo.hasWaterCollision){
                    hit = player.getWorld().raycast(
                    new RaycastContext(prevPos, pos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.WATER,
                        player));
                }
                else{
                    hit = player.getWorld().raycast(
                    new RaycastContext(prevPos, pos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        player));
                }

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
                    renderFilled(context, entityBoundingBox.minX, entityBoundingBox.minY, entityBoundingBox.minZ, entityBoundingBox.maxX, entityBoundingBox.maxY, entityBoundingBox.maxZ, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.HIGHLIGHT_COLOR.getValue(), entityImpact)), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.HIGHLIGHT_OPACITY.getValue())));    
                }
            }

            if((boolean) SettingsManager.OUTLINE_TARGETS.getValue()){
                if(impact != null && impact.getType() == HitResult.Type.BLOCK  && impact instanceof BlockHitResult blockHitResult) {
                    BlockPos impactPos = blockHitResult.getBlockPos();
                    renderBox(context, impactPos.getX(), impactPos.getY(), impactPos.getZ(), impactPos.getX()+1, impactPos.getY()+1, impactPos.getZ()+1, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.OUTLINE_COLOR.getValue())), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.OUTLINE_OPACITY.getValue())));
                }
                else if(entityImpact != null) {
                    Box entityBoundingBox = entityImpact.getBoundingBox().expand(entityImpact.getTargetingMargin());
                    renderBox(context, entityBoundingBox.minX, entityBoundingBox.minY, entityBoundingBox.minZ, entityBoundingBox.maxX, entityBoundingBox.maxY, entityBoundingBox.maxZ, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.OUTLINE_COLOR.getValue(), entityImpact)), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.OUTLINE_OPACITY.getValue())));    
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
                    int color = SettingsManager.getARGBColorFromSetting((String)SettingsManager.TRAJECTORY_COLOR.getValue(), (String)SettingsManager.TRAJECTORY_OPACITY.getValue(), entityImpact);
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

    private Vec3d GetHandToEyeDelta(PlayerEntity player, Vec3d offset, WorldRenderContext context, Vec3d startPos) {

        float yaw = (float) Math.toRadians(-player.getYaw(1.0F));
        float pitch = (float) Math.toRadians(-player.getPitch(1.0F));

        Vec3d forward = player.getRotationVec(1.0F);
        Vec3d up = new Vec3d(-Math.sin(pitch) * Math.sin(yaw), Math.cos(pitch), -Math.sin(pitch) * Math.cos(yaw)).normalize();
        Vec3d right = forward.crossProduct(up).normalize();

        return right.multiply(offset.x).add(up.multiply(offset.y)).add(forward.multiply(offset.z)).add(context.camera().getPos().subtract(startPos));
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

    public static boolean isEnabled() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.isIntegratedServerRunning() || serverHasMod;
    }
}
