package com.maDU59_;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

public class ProjectileInfo {

    public double gravity;
    public double drag;
    public Vec3 initialVelocity;
    public Vec3 offset;
    public Vec3 position;
    public boolean hasWaterCollision;

    public ProjectileInfo(double gravity, double drag, Vec3 initialVelocity, Vec3 offset, Vec3 position, boolean hasWaterCollision) {
        this.gravity = gravity;
        this.drag = drag;
        this.initialVelocity = initialVelocity;
        this.offset = offset;
        this.position = position;
        this.hasWaterCollision = hasWaterCollision;
    }

    static public List<ProjectileInfo> getItemsInfo(ItemStack itemStack, Player player, boolean isMainHand) {

        List<ProjectileInfo> projectileInfoList = new ArrayList<>(); 

        double gravity = 0.05;
        double drag = 0.99;

        Item item = itemStack.getItem();

        if (item instanceof BowItem) {

            int useTicks = player.getTicksUsingItem();
            float pull = BowItem.getPowerForTime(useTicks);

            Vec3 vel = player.getViewVector(1.0F).scale(3.0 * pull);
            Vec3 offset = new Vec3(0.2, -0.06, 0.2);

            if(pull > 0) projectileInfoList.add(new ProjectileInfo(gravity, drag, vel, offset, null, false));

        } else if (item instanceof CrossbowItem) {

            Vec3 vel = player.getViewVector(1.0F).scale(3.15);
            Vec3 offset = new Vec3(0, -0.06, 0.03);

            ChargedProjectiles chargedProjectilesComponent = itemStack.get(DataComponents.CHARGED_PROJECTILES);

            if(chargedProjectilesComponent != null){
                for (ItemStack projectile : chargedProjectilesComponent.getItems()) {
                    if (projectile.is(Items.FIREWORK_ROCKET)) {
                        vel = player.getViewVector(1.0F).scale(1.6F);
                        gravity = 0;
                    } else if (projectile.getItem() instanceof ArrowItem) {
                        
                    }
                }
            }

            if(CrossbowItem.isCharged(itemStack)) {
                projectileInfoList.add(new ProjectileInfo(gravity, drag, vel, offset, null, false));
                if (hasMultishot(itemStack)){
                    float angleOffset = 10f;
                    Vec3 vel1 = vel.yRot((float) Math.toRadians(angleOffset));
                    Vec3 vel2 = vel.yRot((float) Math.toRadians(-angleOffset));
                    projectileInfoList.add(new ProjectileInfo(gravity, drag, vel1, offset, null, false));
                    projectileInfoList.add(new ProjectileInfo(gravity, drag, vel2, offset, null, false));
                }
            }
            
        } else if (item instanceof TridentItem) {

            int useTicks = player.getTicksUsingItem();

            Vec3 vel = player.getViewVector(1.0F).scale(TridentItem.PROJECTILE_SHOOT_POWER);
            Vec3 offset = new Vec3(0.2, 0.1, 0.2);

            if(useTicks >= TridentItem.THROW_THRESHOLD_TIME) projectileInfoList.add(new ProjectileInfo(gravity, drag, vel, offset, null, false));
            
        } else if (item instanceof SnowballItem || item instanceof EggItem || item instanceof EnderpearlItem) {

            gravity = 0.03;

            Vec3 vel = player.getViewVector(1.0F).scale(SnowballItem.PROJECTILE_SHOOT_POWER);
            Vec3 offset = new Vec3(0.2, -0.06, 0.2);

            projectileInfoList.add(new ProjectileInfo(gravity, drag, vel, offset, null, false));
            
        } else if (item instanceof WindChargeItem) {

            gravity = 0;
            drag = 1.0;

            Vec3 vel = player.getViewVector(1.0F).scale(1.0);
            Vec3 offset = new Vec3(0.2, -0.06, 0.2);

            projectileInfoList.add(new ProjectileInfo(gravity, drag, vel, offset, null, false));
            
        } else if (item instanceof ThrowablePotionItem) {

            Vec3 dir = player.getViewVector(1.0F).add(0, 0.1, 0);
            dir = dir.normalize();

            Vec3 vel = dir.scale(0.5);
            Vec3 offset = new Vec3(0.2, -0.06, 0.2);

            projectileInfoList.add(new ProjectileInfo(gravity, drag, vel, offset, null, false));
            
        }  else if (item instanceof ExperienceBottleItem) {

            gravity = 0.07;
            drag = 0.99;

            Vec3 dir = player.getViewVector(1.0F).add(0, 0.1, 0);
            dir = dir.normalize();

            Vec3 vel = dir.scale(0.7);
            Vec3 offset = new Vec3(0.2, -0.06, 0.2);

            projectileInfoList.add(new ProjectileInfo(gravity, drag, vel, offset, null, false));
            
        }  else if (item instanceof FishingRodItem && player.fishing == null) {

            float f = player.getXRot();
            float g = player.getYRot();
            float h = Mth.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
            float i = Mth.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
            float j = -Mth.cos(-f * (float) (Math.PI / 180.0));
            float k = Mth.sin(-f * (float) (Math.PI / 180.0));
            Vec3 p = player.getEyePosition(1.0F);
            Vec3 pos = new Vec3(p.x - i * 0.3,p.y,p.z - h * 0.3);
            Vec3 vec3d = new Vec3(-i, Mth.clamp(-(k / j), -5.0F, 5.0F), -h);
            double m = vec3d.length();
            vec3d = vec3d.multiply(
                0.6 / m + 0.5,
                0.6 / m + 0.5,
                0.6 / m + 0.5
            );
            Vec3 vel = vec3d;

            gravity = 0.03;
            drag = 0.92;

            Vec3 offset = new Vec3(0.16, -0.06, 0.2);

            projectileInfoList.add(new ProjectileInfo(gravity, drag, vel, offset, pos, true));
            
        }

        return projectileInfoList;
    }

    public static boolean hasMultishot(ItemStack stack) {
        var enchantmentRegistry = Minecraft.getInstance().player.level().registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT);

        Holder<Enchantment> multishotEntry = enchantmentRegistry
            .getOrThrow(Enchantments.MULTISHOT);

        return EnchantmentHelper.getItemEnchantmentLevel(multishotEntry, stack) > 0;
    }
}