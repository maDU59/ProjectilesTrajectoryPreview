package com.example;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.EggItem;
import net.minecraft.item.Item;
import net.minecraft.item.SnowballItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.item.WindChargeItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.FireChargeItem;
import net.minecraft.util.math.Vec3d;

public class ProjectileInfo {
    // Fields
    public double gravity;
    public double drag;
    public Vec3d initialVelocity;
    public boolean isReadyToShoot;
    public Vec3d offset;

    // Constructor
    public ProjectileInfo(double gravity, double drag, Vec3d initialVelocity, boolean isReadyToShoot, Vec3d offset) {
        this.gravity = gravity;
        this.drag = drag;
        this.initialVelocity = initialVelocity;
        this.isReadyToShoot = isReadyToShoot;
        this.offset = offset;
    }

    static public ProjectileInfo getItemsInfo(Item item, PlayerEntity player) {
        double gravity = 0.05;
        double drag = 0.99;
        if (item instanceof BowItem) {

            int useTicks = player.getItemUseTime();
            float pull = BowItem.getPullProgress(useTicks);

            Vec3d vel = player.getRotationVec(1.0F).multiply(3.0 * pull);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, pull > 0, offset);

        } else if (item instanceof CrossbowItem) {

            Vec3d vel = player.getRotationVec(1.0F).multiply(3.15);
            Vec3d offset = new Vec3d(0, -0.06, 0.03);

            return new ProjectileInfo(gravity, drag, vel, CrossbowItem.isCharged(player.getMainHandStack()), offset);
            
        } else if (item instanceof TridentItem) {

            int useTicks = player.getItemUseTime();

            Vec3d vel = player.getRotationVec(1.0F).multiply(TridentItem.THROW_SPEED);
            Vec3d offset = new Vec3d(0.2, 0.1, 0.2);

            return new ProjectileInfo(gravity, drag, vel, useTicks >= TridentItem.MIN_DRAW_DURATION, offset);
            
        } else if (item instanceof SnowballItem || item instanceof EggItem || item instanceof EnderPearlItem) {

            gravity = 0.03;

            Vec3d vel = player.getRotationVec(1.0F).multiply(SnowballItem.POWER);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, true, offset);
            
        } else if (item instanceof FireChargeItem) {

            gravity = 0;
            drag = 0.95;

            Vec3d vel = player.getRotationVec(1.0F).multiply(1.0);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, false, offset);
            
        } else if (item instanceof WindChargeItem) {

            gravity = 0;
            drag = 1.0;

            Vec3d vel = player.getRotationVec(1.0F).multiply(1.0);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, true, offset);
            
        } else if (item instanceof ThrowablePotionItem) {

            gravity = 0.05;
            drag = 0.99;

            Vec3d dir = player.getRotationVec(1.0F).add(0, 0.1, 0);
            dir = dir.normalize();

            Vec3d vel = dir.multiply(0.5);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, true, offset);
            
        }  else if (item instanceof ExperienceBottleItem) {

            gravity = 0.07;
            drag = 0.99;

            Vec3d dir = player.getRotationVec(1.0F).add(0, 0.1, 0);
            dir = dir.normalize();

            Vec3d vel = dir.multiply(0.7);
            Vec3d offset = new Vec3d(0.2, -0.06, 0.2);

            return new ProjectileInfo(gravity, drag, vel, true, offset);
            
        } else {
            ptrClient.LOGGER.info("Other Item");
        }

        return new ProjectileInfo(0.0, 0.0, new Vec3d(0.0, 0.0, 0.0), false, new Vec3d(0.0, 0.0, 0.0));
    }
}