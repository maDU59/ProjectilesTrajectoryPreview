package fr.madu59.ptp.util;

import fr.madu59.ptp.compat.ModCompat;
import fr.madu59.ptp.compat.VivecraftCompat;
import fr.madu59.ptp.physics.PhysicsOrder;
import fr.madu59.ptp.physics.PhysicsStep;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TrajectoryUtils {

    public final static PhysicsOrder ORDER_PDG = new PhysicsOrder(new PhysicsStep[]{PhysicsStep.POSITION, PhysicsStep.DRAG, PhysicsStep.GRAVITY});
    public final static PhysicsOrder ORDER_GPD = new PhysicsOrder(new PhysicsStep[]{PhysicsStep.GRAVITY, PhysicsStep.POSITION, PhysicsStep.DRAG});
    public final static PhysicsOrder ORDER_GDP = new PhysicsOrder(new PhysicsStep[]{PhysicsStep.GRAVITY, PhysicsStep.DRAG, PhysicsStep.POSITION});

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

    public static Vec3 angleFromRot(float f, float g, float h){
        float k = -Mth.sin((double)(g * 0.017453292F)) * Mth.cos((double)(f * 0.017453292F));
        float l = -Mth.sin((double)((f + h) * 0.017453292F));
        float m = Mth.cos((double)(g * 0.017453292F)) * Mth.cos((double)(f * 0.017453292F));

        return new Vec3((double)k, (double)l, (double)m).normalize();
    }
}
