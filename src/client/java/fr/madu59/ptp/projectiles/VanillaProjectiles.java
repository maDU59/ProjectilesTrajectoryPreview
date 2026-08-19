package fr.madu59.ptp.projectiles;

import java.util.List;

import fr.madu59.ptp.PtpClient;
import fr.madu59.ptp.physics.ProjectileData;
import fr.madu59.ptp.util.ItemUtils;
import fr.madu59.ptp.util.TrajectoryUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

public class VanillaProjectiles {

    private static double DEFAULT_GRAVITY = 0.05;
    private static double DEFAULT_DRAG = 0.99;
    private static double DEFAULT_WATER_DRAG = 0.6;

    public static void bowTrajectory(ItemStack itemStack, Player player, List<ProjectileData> out){
        float tickProgress = PtpClient.getTickProgress();

        Vec3 position = TrajectoryUtils.getAimPos(player, tickProgress);
            
        int useTicks = player.getTicksUsingItem();
        float pull = BowItem.getPowerForTime(useTicks);

        Vec3 vel = TrajectoryUtils.getViewVector(player, tickProgress).scale(3.0 * pull);
        Vec3 offset = new Vec3(0.2, -0.06, 0.2);

        if(pull >= 0.1) out.add(new ProjectileData(DEFAULT_GRAVITY, DEFAULT_DRAG, vel, offset, position, false, DEFAULT_WATER_DRAG, TrajectoryUtils.ORDER_PDG, false));
    }

    public static void crossbowTrajectory(ItemStack itemStack, Player player, List<ProjectileData> out){
        float tickProgress = PtpClient.getTickProgress();

        double gravity = DEFAULT_GRAVITY;
        double waterDrag = DEFAULT_WATER_DRAG;

        Vec3 position = TrajectoryUtils.getAimPos(player, tickProgress);

        Vec3 vel = TrajectoryUtils.getViewVector(player, tickProgress).scale(3.15);
        Vec3 offset = new Vec3(0, -0.06, 0.03);

        ChargedProjectiles chargedProjectilesComponent = itemStack.get(DataComponents.CHARGED_PROJECTILES);

        if(chargedProjectilesComponent != null){
            for (ItemStack projectile : chargedProjectilesComponent.getItems()) {
                if (projectile.is(Items.FIREWORK_ROCKET)) {
                    vel = TrajectoryUtils.getViewVector(player, tickProgress).scale(1.6F);
                    gravity = 0;
                    waterDrag = DEFAULT_DRAG;
                } else if (projectile.getItem() instanceof ArrowItem) {
                    
                }
            }
        }

        if(CrossbowItem.isCharged(itemStack)) {
            out.add(new ProjectileData(gravity, DEFAULT_DRAG, vel, offset, position, false, waterDrag, TrajectoryUtils.ORDER_PDG, false));
            if (ItemUtils.hasEnchantment(itemStack, Enchantments.MULTISHOT)){
                float angleOffset = 10f;
                Vec3 vel1 = vel.yRot((float) Math.toRadians(angleOffset));
                Vec3 vel2 = vel.yRot((float) Math.toRadians(-angleOffset));
                out.add(new ProjectileData(gravity, DEFAULT_DRAG, vel1, offset, position, false, waterDrag, TrajectoryUtils.ORDER_PDG, false));
                out.add(new ProjectileData(gravity, DEFAULT_DRAG, vel2, offset, position, false, waterDrag, TrajectoryUtils.ORDER_PDG, false));
            }
        }
    }

    public static void tridentTrajectory(ItemStack itemStack, Player player, List<ProjectileData> out){
        float tickProgress = PtpClient.getTickProgress();

        double waterDrag = 0.99;

        int useTicks = player.getTicksUsingItem();

        Vec3 position = TrajectoryUtils.getAimPos(player, tickProgress);

        Vec3 vel = TrajectoryUtils.getViewVector(player, tickProgress).scale(TridentItem.SHOOT_POWER);
        Vec3 offset = new Vec3(0.2, 0.1, 0.2);

        if(useTicks >= TridentItem.THROW_THRESHOLD_TIME && !ItemUtils.hasEnchantment(itemStack, Enchantments.RIPTIDE)){
            out.add(new ProjectileData(DEFAULT_GRAVITY, DEFAULT_DRAG, vel, offset, position, false, waterDrag, TrajectoryUtils.ORDER_PDG, false));
        }
    }

    public static void snowballTrajectory(ItemStack itemStack, Player player, List<ProjectileData> out){
        float tickProgress = PtpClient.getTickProgress();
        
        boolean bypassAntiCheat = itemStack.getItem() instanceof EnderpearlItem;
        double waterDrag = 0.8;
        double gravity = 0.03;

        Vec3 position = TrajectoryUtils.getAimPos(player, tickProgress);

        Vec3 vel = TrajectoryUtils.getViewVector(player, tickProgress).scale(1.5);
        Vec3 offset = new Vec3(0.2, -0.06, 0.2);

        out.add(new ProjectileData(gravity, DEFAULT_DRAG, vel, offset, position, false, waterDrag, TrajectoryUtils.ORDER_GDP, bypassAntiCheat));
    }

    public static void windChargeTrajectory(ItemStack itemStack, Player player, List<ProjectileData> out){
        float tickProgress = PtpClient.getTickProgress();
        Vec3 position = TrajectoryUtils.getAimPos(player, tickProgress);

        double gravity = 0;
        double drag = 0.95;
        double waterDrag = 0.8;

        Vec3 vel = TrajectoryUtils.getViewVector(player, tickProgress);
        Vec3 offset = new Vec3(0.2, -0.06, 0.2);

        out.add(new ProjectileData(gravity, drag, vel, offset, position, false, waterDrag, TrajectoryUtils.ORDER_PDG, false));
    }

    public static void fishingRodTrajectory(ItemStack itemStack, Player player, List<ProjectileData> out){
        float tickProgress = PtpClient.getTickProgress();
        Vec3 position = TrajectoryUtils.getAimPos(player, tickProgress);

        float f = TrajectoryUtils.getViewXRot(player, tickProgress);
        float g = TrajectoryUtils.getViewYRot(player, tickProgress);
        float h = Mth.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
        float i = Mth.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
        float j = -Mth.cos(-f * (float) (Math.PI / 180.0));
        float k = Mth.sin(-f * (float) (Math.PI / 180.0));
        Vec3 p = position.add(new Vec3(0, 0.10000000149011612, 0));
        position = new Vec3(p.x - i * 0.3,p.y,p.z - h * 0.3);
        Vec3 vec3d = new Vec3(-i, Mth.clamp(-(k / j), -5.0F, 5.0F), -h);
        double m = vec3d.length();
        vec3d = vec3d.multiply(
            0.6 / m + 0.5,
            0.6 / m + 0.5,
            0.6 / m + 0.5
        );
        Vec3 vel = vec3d;

        double gravity = 0.03;
        double drag = 0.92;

        Vec3 offset = new Vec3(0.16, -0.06, 0.2);

        out.add(new ProjectileData(gravity, drag, vel, offset, position, true, drag, TrajectoryUtils.ORDER_GPD, true));
    }

    public static void expBottleTrajectory(ItemStack itemStack, Player player, List<ProjectileData> out){
        float tickProgress = PtpClient.getTickProgress();
        Vec3 position = TrajectoryUtils.getAimPos(player, tickProgress);

        double gravity = 0.07;
        double waterDrag = 0.8;

        Vec3 dir = TrajectoryUtils.angleFromRot(TrajectoryUtils.getViewXRot(player, tickProgress), TrajectoryUtils.getViewYRot(player, tickProgress), -20.0F);
        dir = dir.normalize();

        Vec3 vel = dir.scale(0.7);
        Vec3 offset = new Vec3(0.2, -0.06, 0.2);

        out.add(new ProjectileData(gravity, DEFAULT_DRAG, vel, offset, position, false, waterDrag, TrajectoryUtils.ORDER_GDP, true));
    }

    public static void splashPotionsTrajectory(ItemStack itemStack, Player player, List<ProjectileData> out){
        float tickProgress = PtpClient.getTickProgress();
        Vec3 position = TrajectoryUtils.getAimPos(player, tickProgress);    
        double waterDrag = 0.8;

        Vec3 dir = TrajectoryUtils.angleFromRot(TrajectoryUtils.getViewXRot(player, tickProgress), TrajectoryUtils.getViewYRot(player, tickProgress), -20.0F);

        Vec3 vel = dir.scale(0.5f);
        Vec3 offset = new Vec3(0.2, -0.06, 0.2);

        out.add(new ProjectileData(DEFAULT_GRAVITY, DEFAULT_DRAG, vel, offset, position, false, waterDrag, TrajectoryUtils.ORDER_GDP, false));
    }
}