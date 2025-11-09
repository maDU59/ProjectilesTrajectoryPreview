package com.maDU59_;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.EggItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SnowballItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.item.WindChargeItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ProjectileInfo {

    public double gravity;
    public double drag;
    public Vec3d initialVelocity;
    public boolean isReadyToShoot;
    public Vec3d offset;
    public Vec3d position;
    public boolean hasWaterCollision;

    public ProjectileInfo(double gravity, double drag, Vec3d initialVelocity, boolean isReadyToShoot, Vec3d offset, Vec3d position, boolean hasWaterCollision) {
        this.gravity = gravity;
        this.drag = drag;
        this.initialVelocity = initialVelocity;
        this.isReadyToShoot = isReadyToShoot;
        this.offset = offset;
        this.position = position;
        this.hasWaterCollision = hasWaterCollision;
    }

    static public ProjectileInfo getItemsInfo(Item item, PlayerEntity player) {
        double gravity = 0.05;
        double drag = 0.99;
        if (item instanceof BowItem) {

            int useTicks = player.getItemUseTime();
            float pull = BowItem.getPullProgress(useTicks);

            Vec3d vel = player.getRotationVec(1.0F).multiply(3.0 * pull);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, pull > 0, offset, null, false);

        } else if (item instanceof CrossbowItem) {

            Vec3d vel = player.getRotationVec(1.0F).multiply(3.15);
            Vec3d offset = new Vec3d(0, -0.06, 0.03);

            ChargedProjectilesComponent chargedProjectilesComponent = player.getMainHandStack().get(DataComponentTypes.CHARGED_PROJECTILES);

            for (ItemStack projectile : chargedProjectilesComponent.getProjectiles()) {
                if (projectile.isOf(Items.FIREWORK_ROCKET)) {
                    vel = player.getRotationVec(1.0F).multiply(1.6F);
                    gravity = 0;
                } else if (projectile.getItem() instanceof ArrowItem) {
                    
                }
            }

            return new ProjectileInfo(gravity, drag, vel, CrossbowItem.isCharged(player.getMainHandStack()), offset, null, false);
            
        } else if (item instanceof TridentItem) {

            int useTicks = player.getItemUseTime();

            Vec3d vel = player.getRotationVec(1.0F).multiply(TridentItem.THROW_SPEED);
            Vec3d offset = new Vec3d(0.2, 0.1, 0.2);

            return new ProjectileInfo(gravity, drag, vel, useTicks >= TridentItem.MIN_DRAW_DURATION, offset, null, false);
            
        } else if (item instanceof SnowballItem || item instanceof EggItem || item instanceof EnderPearlItem) {

            gravity = 0.03;

            Vec3d vel = player.getRotationVec(1.0F).multiply(1.5F);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, true, offset, null, false);
            
        } else if (item instanceof WindChargeItem) {

            gravity = 0;
            drag = 1.0;

            Vec3d vel = player.getRotationVec(1.0F).multiply(1.0);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, true, offset, null, false);
            
        } else if (item instanceof ThrowablePotionItem) {

            Vec3d dir = player.getRotationVec(1.0F).add(0, 0.1, 0);
            dir = dir.normalize();

            Vec3d vel = dir.multiply(0.5);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, true, offset, null, false);
            
        }  else if (item instanceof ExperienceBottleItem) {

            gravity = 0.07;
            drag = 0.99;

            Vec3d dir = player.getRotationVec(1.0F).add(0, 0.1, 0);
            dir = dir.normalize();

            Vec3d vel = dir.multiply(0.7);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, true, offset, null, false);
            
        }  else if (item instanceof FishingRodItem && player.fishHook == null) {

            float f = player.getPitch();
            float g = player.getYaw();
            float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
            float i = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
            float j = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
            float k = MathHelper.sin(-f * (float) (Math.PI / 180.0));
            Vec3d pos = new Vec3d(player.getX() - i * 0.3,player.getEyeY(),player.getZ() - h * 0.3);
            Vec3d vec3d = new Vec3d(-i, MathHelper.clamp(-(k / j), -5.0F, 5.0F), -h);
            double m = vec3d.length();
            vec3d = vec3d.multiply(
                0.6 / m + 0.5,
                0.6 / m + 0.5,
                0.6 / m + 0.5
            );
            Vec3d vel = vec3d;

            gravity = 0.03;
            drag = 0.92;

            Vec3d offset = new Vec3d(0.16, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, true, offset, pos, true);
            
        }

        return new ProjectileInfo(0.0, 0.0, new Vec3d(0.0, 0.0, 0.0), false, new Vec3d(0.0, 0.0, 0.0), null, false);
    }
}