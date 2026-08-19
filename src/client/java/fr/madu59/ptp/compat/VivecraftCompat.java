package fr.madu59.ptp.compat;

import fr.madu59.ptp.PtpClient;

import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.gameplay.trackers.BowTracker;
import org.vivecraft.server.ServerVRPlayers;
import org.vivecraft.server.ServerVivePlayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class VivecraftCompat {

    public static Vec3 getViewVector(Player player, float tickProgress){
        if(player == Minecraft.getInstance().player && VRClientAPI.instance().isVRActive()){
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if(BowTracker.isBow(player.getMainHandItem())){
                if(ClientDataHolderVR.getInstance().bowTracker.isActive(localPlayer)){
                    return new Vec3(ClientDataHolderVR.getInstance().bowTracker.getAimVector());
                }
            }
            else{
                return VRClientAPI.instance().getWorldRenderPose().getHand(PtpClient.getInteractionHand()).getDir();
            }
        }
        else if(player instanceof ServerPlayer serverPlayer){
            ServerVivePlayer serverVivePlayer = ServerVRPlayers.getVivePlayer(serverPlayer);
            if (serverVivePlayer != null && serverVivePlayer.isVR()) {
                return serverVivePlayer.getAimDir(true);
            }
        }

        return player.getViewVector(tickProgress);
    }

    public static float getViewXRot(Player player, float tickProgress){
        Vec3 viewVec = getViewVector(player, tickProgress);

        Vec3 norm = viewVec.normalize();
        double y = norm.y;

        float xRot = (float) (-Math.asin(y) * (180.0 / Math.PI));

        return xRot;
    }

    public static float getViewYRot(Player player, float tickProgress){
        Vec3 viewVec = getViewVector(player, tickProgress);

        Vec3 norm = viewVec.normalize();
        double x = norm.x;
        double z = norm.z;

        float yRot = (float) (-Math.atan2(x, z) * (180.0 / Math.PI));

        return yRot;
    }

    public static Vec3 getAimPos(Player player, float tickProgress){
        if(player == Minecraft.getInstance().player && VRClientAPI.instance().isVRActive()){
            return VRClientAPI.instance().getWorldRenderPose().getHand(PtpClient.getInteractionHand()).getPos();
        }
        else if(player instanceof ServerPlayer serverPlayer){
            ServerVivePlayer serverVivePlayer = ServerVRPlayers.getVivePlayer(serverPlayer);
            if (serverVivePlayer != null && serverVivePlayer.isVR()) {
                return serverVivePlayer.getAimPos(true);
            }
        }

        return player.getEyePosition(tickProgress).add(new Vec3(0,- 0.10000000149011612,0));
    }
}
