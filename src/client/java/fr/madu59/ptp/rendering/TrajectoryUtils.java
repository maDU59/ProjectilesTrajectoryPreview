package fr.madu59.ptp.rendering;

import fr.madu59.ptp.compat.ModCompat;
import fr.madu59.ptp.compat.VivecraftCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TrajectoryUtils {

    public static Vec3 getViewVector(float tickProgress){
        return getViewVector(Minecraft.getInstance().player, tickProgress);
    }

    public static Vec3 getViewVector(Player player, float tickProgress){
        if(ModCompat.isVivecraftLoaded()) return VivecraftCompat.getViewVector(player, tickProgress);
        return player.getViewVector(tickProgress);
    }

    public static float getViewXRot(float tickProgress){
        return getViewXRot(Minecraft.getInstance().player, tickProgress);
    }

    public static float getViewXRot(Player player, float tickProgress){
        if(ModCompat.isVivecraftLoaded()) return VivecraftCompat.getViewXRot(player, tickProgress);
        return player.getViewXRot(tickProgress);
    }

    public static float getViewYRot(float tickProgress){
        return getViewYRot(Minecraft.getInstance().player, tickProgress);
    }

    public static float getViewYRot(Player player, float tickProgress){
        if(ModCompat.isVivecraftLoaded()) return VivecraftCompat.getViewYRot(player, tickProgress);
        return player.getViewYRot(tickProgress);
    }

    public static Vec3 getAimPos(float tickProgress){
        return getAimPos(null, tickProgress);
    }

    public static Vec3 getAimPos(Player player, float tickProgress){
        if(ModCompat.isVivecraftLoaded()) return VivecraftCompat.getAimPos(player, tickProgress);
        return player.getEyePosition(tickProgress).add(new Vec3(0,- 0.10000000149011612,0));
    }
}
