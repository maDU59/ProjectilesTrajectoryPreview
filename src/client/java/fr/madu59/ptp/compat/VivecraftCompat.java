package fr.madu59.ptp.compat;

import org.vivecraft.client.network.ClientNetworking;
import org.vivecraft.server.ServerVRPlayers;
import org.vivecraft.server.ServerVivePlayer;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class VivecraftCompat {

    public static Vec3 getViewVector(Player player, float tickProgress){
        if(player instanceof ServerPlayer serverPlayer){
            ServerVivePlayer serverVivePlayer = ServerVRPlayers.getVivePlayer(serverPlayer);
            if (serverVivePlayer != null && serverVivePlayer.isVR()) {
                return serverVivePlayer.getAimDir(true);
            }
        }
        else if(player == Minecraft.getInstance().player){
            return new Vec3(ClientNetworking.getActiveAimDir());
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
        if(player instanceof ServerPlayer serverPlayer){
            ServerVivePlayer serverVivePlayer = ServerVRPlayers.getVivePlayer(serverPlayer);
            if (serverVivePlayer != null && serverVivePlayer.isVR()) {
                return serverVivePlayer.getAimPos(true);
            }
        }
        else if(player == Minecraft.getInstance().player){
            return ClientNetworking.getActiveAimPos();
        }

        return player.getEyePosition(tickProgress).add(new Vec3(0,- 0.10000000149011612,0));
    }
}
