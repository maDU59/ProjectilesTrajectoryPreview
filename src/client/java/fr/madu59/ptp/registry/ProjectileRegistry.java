package fr.madu59.ptp.registry;

import fr.madu59.ptp.api.projectiles.ProjectileDataAPI;
import fr.madu59.ptp.config.SettingsManager;
import fr.madu59.ptp.projectiles.VanillaProjectiles;
import net.minecraft.references.ItemIds;
import net.minecraft.world.item.Items;

public class ProjectileRegistry {
    public static void init(){
        ProjectileDataAPI.registerProjectile(Items.BOW, VanillaProjectiles::bowTrajectory, () -> SettingsManager.TOGGLE_BOW.getValue());

        ProjectileDataAPI.registerProjectile(Items.CROSSBOW, VanillaProjectiles::crossbowTrajectory, () -> SettingsManager.TOGGLE_CROSSBOW.getValue());
        
        ProjectileDataAPI.registerProjectile(Items.TRIDENT, VanillaProjectiles::tridentTrajectory, () -> SettingsManager.TOGGLE_TRIDENT.getValue());

        ProjectileDataAPI.registerProjectile(Items.SNOWBALL, VanillaProjectiles::snowballTrajectory, () -> SettingsManager.TOGGLE_SNOWBALL.getValue());
        ProjectileDataAPI.registerProjectile(Items.ENDER_PEARL, VanillaProjectiles::snowballTrajectory, () -> SettingsManager.TOGGLE_ENDERPEARL.getValue());
        ProjectileDataAPI.registerProjectile(Items.EGG, VanillaProjectiles::snowballTrajectory, () -> SettingsManager.TOGGLE_EGG.getValue());

        ProjectileDataAPI.registerProjectile(Items.FISHING_ROD, VanillaProjectiles::fishingRodTrajectory, () -> SettingsManager.TOGGLE_FISHINGROD.getValue());

        ProjectileDataAPI.registerProjectile(Items.EXPERIENCE_BOTTLE, VanillaProjectiles::expBottleTrajectory, () -> SettingsManager.TOGGLE_EXPPOTION.getValue());

        ProjectileDataAPI.registerProjectile(ItemIds.SPLASH_POTION.identifier(), VanillaProjectiles::expBottleTrajectory, () -> SettingsManager.TOGGLE_EXPPOTION.getValue());
        ProjectileDataAPI.registerProjectile(ItemIds.LINGERING_POTION.identifier(), VanillaProjectiles::expBottleTrajectory, () -> SettingsManager.TOGGLE_EXPPOTION.getValue());
    }
}
