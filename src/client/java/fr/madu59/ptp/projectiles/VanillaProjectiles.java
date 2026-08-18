package fr.madu59.ptp.projectiles;

import java.util.List;

import fr.madu59.ptp.PtpClient;
import fr.madu59.ptp.physics.ProjectileData;
import fr.madu59.ptp.util.ItemUtils;
import fr.madu59.ptp.util.TrajectoryUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
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

        double gravity = 0.05;
        double waterDrag = 0.6;

        Vec3 position = TrajectoryUtils.getAimPos(player, tickProgress);

        Vec3 vel = TrajectoryUtils.getViewVector(player, tickProgress).scale(3.15);
        Vec3 offset = new Vec3(0, -0.06, 0.03);

        ChargedProjectiles chargedProjectilesComponent = itemStack.get(DataComponents.CHARGED_PROJECTILES);

        if(chargedProjectilesComponent != null){
            for (ItemStackTemplate projectile : chargedProjectilesComponent.items()) {
                if (projectile.is(Items.FIREWORK_ROCKET)) {
                    vel = TrajectoryUtils.getViewVector(player, tickProgress).scale(1.6F);
                    gravity = 0;
                    waterDrag = DEFAULT_DRAG;
                } else if (projectile.item().value() instanceof ArrowItem) {
                    
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

        Vec3 vel = TrajectoryUtils.getViewVector(player, tickProgress).scale(TridentItem.PROJECTILE_SHOOT_POWER);
        Vec3 offset = new Vec3(0.2, 0.1, 0.2);

        if(useTicks >= TridentItem.THROW_THRESHOLD_TIME && !ItemUtils.hasEnchantment(itemStack, Enchantments.RIPTIDE)){
            out.add(new ProjectileData(DEFAULT_GRAVITY, DEFAULT_DRAG, vel, offset, position, false, waterDrag, TrajectoryUtils.ORDER_PDG, false));
        }
    }

}