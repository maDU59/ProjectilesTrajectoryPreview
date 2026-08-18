package fr.madu59.ptp.registry;

import fr.madu59.ptp.api.projectiles.ProjectileDataAPI;
import fr.madu59.ptp.config.SettingsManager;
import fr.madu59.ptp.projectiles.VanillaProjectiles;
import net.minecraft.world.item.Items;

public class ProjectileRegistry {
    public static void init(){
        ProjectileDataAPI.registerProjectile(Items.BOW, VanillaProjectiles::bowTrajectory, () -> SettingsManager.TOGGLE_BOW.getValue());
        ProjectileDataAPI.registerProjectile(Items.CROSSBOW, VanillaProjectiles::crossbowTrajectory, () -> SettingsManager.TOGGLE_CROSSBOW.getValue());
        ProjectileDataAPI.registerProjectile(Items.TRIDENT, VanillaProjectiles::tridentTrajectory, () -> SettingsManager.TOGGLE_TRIDENT.getValue());
    }
}
