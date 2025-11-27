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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;

public class PtpClient implements ClientModInitializer {
    private static final Minecraft client = Minecraft.getInstance();
    public static final Logger LOGGER = LogManager.getLogger("ptpClient");
    private static boolean serverHasMod = false;


    @Override
    public void onInitializeClient() {
        ClientCommands.register();

        // Reset handshake state on join
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            serverHasMod = false;

            // Always enabled in singleplayer
            if (client.hasSingleplayerServer()) {
                serverHasMod = true;
                return;
            }

            // Send handshake to server
            ClientPlayNetworking.send(new HANDSHAKE_C2SPayload("Check if is installed on server"));
            LOGGER.info("[PTP] Sending handshake to server...");
        });

        // Receive handshake reply
        ClientPlayNetworking.registerGlobalReceiver(HANDSHAKE_S2CPayload.ID,
            (payload, context) -> {
                LOGGER.info("[PTP] Received handshake from server...");
                serverHasMod = true;
        });
        
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            renderOverlay(context);
        });
    }

    public static void renderOverlay(WorldRenderContext context) {
        Player player = client.player;
        if (player == null || !isEnabled()) return;

        ItemStack itemStack = player.getMainHandItem();

        List<ProjectileInfo> projectileInfoList = ProjectileInfo.getItemsInfo(itemStack, player, true);
        int handMultiplier = client.options.mainHand().get() == HumanoidArm.RIGHT? 1:-1;
        if(projectileInfoList.isEmpty()){
            if(Boolean.FALSE.equals(SettingsManager.ENABLE_OFFHAND.getValue())) return;

            itemStack = player.getOffhandItem();
            handMultiplier = -handMultiplier;
            projectileInfoList = ProjectileInfo.getItemsInfo(itemStack, player, false);

            if(projectileInfoList.isEmpty()) return;
        }

        float tickProgress = client.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 eye = player.getEyePosition(tickProgress);

        for(ProjectileInfo projectileInfo : projectileInfoList){

            Vec3 vel = projectileInfo.initialVelocity.add(player.getDeltaMovement());

            Vec3 pos = projectileInfo.position == null? player.getEyePosition() : projectileInfo.position;
            Vec3 prevPos = pos;

            Vec3 handToEyeDelta = GetHandToEyeDelta(player, projectileInfo.offset, context, pos, eye, handMultiplier, tickProgress);
            HitResult impact = null;
            Entity entityImpact = null;
            boolean hasHit = false;
            List<Vec3> trajectoryPoints = new ArrayList<>();

            // Simulation
            for (int i = 0; i < 200; i++) {
                trajectoryPoints.add(pos);
                pos = pos.add(vel);
                vel = vel.scale(projectileInfo.drag).subtract(0, projectileInfo.gravity, 0);

                AABB box = new AABB(prevPos, pos).inflate(1.0);

                List<Entity> entities = client.level.getEntitiesOfClass(Entity.class, box, e -> !e.isSpectator() && e.isAlive() && !(e instanceof Projectile) && !(e instanceof ItemEntity) && !(e instanceof ExperienceOrb) && !(e instanceof EnderDragon) && !(e instanceof LocalPlayer));

                Entity closest = null;
                double closestDistance = 99999.0;
                Vec3 entityHitPos = null;

                for (Entity entity : entities) {
                    AABB entityBox = entity.getBoundingBox().inflate(entity.getPickRadius());
                    Optional<Vec3> entityRaycastHit = entityBox.clip(prevPos, pos);

                    if (entityRaycastHit.isPresent()) {
                        double distance = prevPos.distanceToSqr(entityRaycastHit.get());
                        if (distance < closestDistance) {
                            entityHitPos = entityRaycastHit.get();
                            closest = entity;
                            closestDistance = distance;
                        }
                    }
                }

                HitResult hit;
                if(projectileInfo.hasWaterCollision){
                    hit = player.level().clip(
                    new ClipContext(prevPos, pos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.WATER,
                        player));
                }
                else{
                    hit = player.level().clip(
                    new ClipContext(prevPos, pos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        player));
                }

                if (hit.getType() != HitResult.Type.MISS && prevPos.distanceToSqr(hit.getLocation()) < closestDistance) {
                    impact = hit;
                    pos = hit.getLocation();
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

                if (pos.y < player.level().getMinY() - 20) 
                    break;

                prevPos = pos;
            }

            Vec3 cam = client.gameRenderer.getMainCamera().position();

            Object value = SettingsManager.HIGHLIGHT_TARGETS.getValue();
            if("TargetIsEntity".equals(value) || Boolean.TRUE.equals(value)){
                if(!"TargetIsEntity".equals(value) && impact != null && impact.getType() == HitResult.Type.BLOCK  && impact instanceof BlockHitResult blockHitResult) {
                    BlockPos impactPos = blockHitResult.getBlockPos();
                    RenderUtils.renderFilledBox(context, impactPos.getX(), impactPos.getY(), impactPos.getZ(), impactPos.getX()+1, impactPos.getY()+1, impactPos.getZ()+1, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.HIGHLIGHT_COLOR.getValue())), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.HIGHLIGHT_OPACITY.getValue())));
                }
                else if(entityImpact != null) {
                    AABB entityBoundingBox = entityImpact.getBoundingBox().inflate(entityImpact.getPickRadius());
                    RenderUtils.renderFilledBox(context, entityBoundingBox.minX, entityBoundingBox.minY, entityBoundingBox.minZ, entityBoundingBox.maxX, entityBoundingBox.maxY, entityBoundingBox.maxZ, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.HIGHLIGHT_COLOR.getValue(), entityImpact)), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.HIGHLIGHT_OPACITY.getValue())));    
                }
            }

            value = SettingsManager.OUTLINE_TARGETS.getValue();
            if("TargetIsEntity".equals(value) || Boolean.TRUE.equals(value)){
                if(!"TargetIsEntity".equals(value) && impact != null && impact.getType() == HitResult.Type.BLOCK  && impact instanceof BlockHitResult blockHitResult) {
                    BlockPos impactPos = blockHitResult.getBlockPos();
                    RenderUtils.renderBox(context, impactPos.getX(), impactPos.getY(), impactPos.getZ(), impactPos.getX()+1, impactPos.getY()+1, impactPos.getZ()+1, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.OUTLINE_COLOR.getValue())), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.OUTLINE_OPACITY.getValue())));
                }
                else if(entityImpact != null) {
                    AABB entityBoundingBox = entityImpact.getBoundingBox().inflate(entityImpact.getPickRadius());
                    RenderUtils.renderBox(context, entityBoundingBox.minX, entityBoundingBox.minY, entityBoundingBox.minZ, entityBoundingBox.maxX, entityBoundingBox.maxY, entityBoundingBox.maxZ, SettingsManager.convertColorToFloat(SettingsManager.getColorFromSetting((String)SettingsManager.OUTLINE_COLOR.getValue(), entityImpact)), SettingsManager.convertAlphaToFloat(SettingsManager.getAlphaFromSetting((String)SettingsManager.OUTLINE_OPACITY.getValue())));    
                }
            }

            value = SettingsManager.SHOW_TRAJECTORY.getValue();
            if (("TargetIsEntity".equals(value) && entityImpact!=null) || Boolean.TRUE.equals(value)) {
                PoseStack matrices = context.matrices();
                matrices.pushPose();
                matrices.translate(-cam.x, -cam.y, -cam.z);

                VertexConsumer lineConsumer = context.consumers().getBuffer(RenderTypes.lines());

                int color = SettingsManager.getARGBColorFromSetting((String)SettingsManager.TRAJECTORY_COLOR.getValue(), (String)SettingsManager.TRAJECTORY_OPACITY.getValue(), entityImpact);

                for (int i = 0; i < trajectoryPoints.size()-1; i++) {
                    Vec3 lerpedDelta = handToEyeDelta.scale((trajectoryPoints.size()-(i * 1.0))/trajectoryPoints.size());
                    Vec3 nextLerpedDelta = handToEyeDelta.scale((trajectoryPoints.size()-(i+1 * 1.0))/trajectoryPoints.size());
                    pos = trajectoryPoints.get(i).add(lerpedDelta);
                    Vec3 dir = (trajectoryPoints.get(i+1).add(nextLerpedDelta)).subtract(pos);
                    System.out.println(SettingsManager.TRAJECTORY_STYLE.getValueAsString());
                    if(SettingsManager.TRAJECTORY_STYLE.getValueAsString().equals("Dashed")){
                        dir = dir.scale(0.5);
                    }
                    else if(SettingsManager.TRAJECTORY_STYLE.getValueAsString().equals("Dotted")){
                        dir = dir.scale(0.15);
                    }
                    Vector3f floatPos = new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);

                    RenderUtils.renderVector(matrices, lineConsumer, floatPos, dir, color);
                }

                if (hasHit) {
                    lineConsumer = context.consumers().getBuffer(RenderTypes.lines());

                    pos = trajectoryPoints.getLast();

                    double r = 0.1;
                    double x = pos.x;
                    double y = pos.y;
                    double z = pos.z;

                    Vector3f floatPos = new Vector3f((float) (x - r), (float) y, (float) z);
                    Vec3 dir = new Vec3(2*r,0,0);
                    RenderUtils.renderVector(matrices, lineConsumer, floatPos, dir, color);

                    floatPos = new Vector3f((float) x, (float) (y - r), (float) z);
                    dir = new Vec3(0,2*r,0);
                    RenderUtils.renderVector(matrices, lineConsumer, floatPos, dir, color);

                    floatPos = new Vector3f((float) x, (float) y, (float) (z - r));
                    dir = new Vec3(0,0,2 * r);
                    RenderUtils.renderVector(matrices, lineConsumer, floatPos, dir, color);
                }

                matrices.popPose();
            }
        }
    }

    private static Vec3 GetHandToEyeDelta(Player player, Vec3 offset, WorldRenderContext context, Vec3 startPos, Vec3 eye, int handMultiplier, float tickProgress) {

        float yaw = (float) Math.toRadians(-player.getViewYRot(tickProgress));
        float pitch = (float) Math.toRadians(-player.getViewXRot(tickProgress));

        Vec3 forward = player.getViewVector(tickProgress);
        Vec3 up = new Vec3(-Math.sin(pitch) * Math.sin(yaw), Math.cos(pitch), -Math.sin(pitch) * Math.cos(yaw)).normalize();
        Vec3 right = forward.cross(up).normalize();

        return right.scale(handMultiplier * offset.x).add(up.scale(offset.y)).add(forward.scale(offset.z)).add(eye.subtract(startPos));
    }

    public static boolean isEnabled() {
        Minecraft client = Minecraft.getInstance();
        return client.hasSingleplayerServer() || serverHasMod;
    }
}
