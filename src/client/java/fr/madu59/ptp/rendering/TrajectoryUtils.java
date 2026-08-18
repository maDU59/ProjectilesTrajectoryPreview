package fr.madu59.ptp.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TrajectoryUtils {

    public static Vec3 getViewVector(float tickProgress){
        return getViewVector(Minecraft.getInstance().player, tickProgress);
    }

    public static Vec3 getViewVector(Player player, float tickProgress){
        return player.getViewVector(tickProgress);
    }

    public static float getViewXRot(float tickProgress){
        return getViewXRot(Minecraft.getInstance().player, tickProgress);
    }

    public static float getViewXRot(Player player, float tickProgress){
        return player.getViewYRot(tickProgress);
    }

    public static float getViewYRot(float tickProgress){
        return getViewXRot(Minecraft.getInstance().player, tickProgress);
    }

    public static float getViewYRot(Player player, float tickProgress){
        return player.getViewYRot(tickProgress);
    }
}
