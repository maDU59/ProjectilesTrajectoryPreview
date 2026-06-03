package fr.madu59.ptp.api.projectiles;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.madu59.ptp.physics.ProjectileInfo;
import net.minecraft.resources.Identifier;

public class ProjectileData {
    
    public static final Map<Identifier, ProjectileInfo> projectileInfoMap = new HashMap<>();
    public static final List<Identifier> blacklistedProjectiles = new ArrayList<>();

    /*
     * Registers a projectile with the given ID and info.
     * @param id The unique identifier for the projectile (e.g., "minecraft:arrow").
     * @param info The ProjectileInfo containing physics parameters and update order.
     * @since 1.0.29
     */
    public static void registerProjectile(Identifier id, ProjectileInfo info) {
        projectileInfoMap.put(id, info);
    }

    /*
     * Adds a projectile ID to the blacklisted list, it will prevent it from showing a trajectory.
     * @param id The unique identifier for the projectile.
     * @since 1.0.29
     */
    public static void blacklistProjectile(Identifier id) {
        blacklistedProjectiles.add(id);
    }

    /*
     * Retrieves the ProjectileInfo for a given projectile ID.
     * @param id The unique identifier for the projectile.
     * @return The ProjectileInfo associated with the ID, or null if not found.
     * @since 1.0.29
     */
    @ApiStatus.Internal
    public static ProjectileInfo getProjectileInfo(Identifier id) {
        return projectileInfoMap.get(id);
    }

    /*
     * Checks if a projectile with the given ID exists.
     * @param id The unique identifier for the projectile.
     * @return true if the projectile exists, false otherwise.
     * @since 1.0.29
     */
    @ApiStatus.Internal
    public static boolean hasProjectileInfo(Identifier id) {
        return projectileInfoMap.containsKey(id);
    }

    /*
     * Checks if a projectile with the given ID is blacklisted.
     * @param id The unique identifier for the projectile.
     * @return true if the projectile is blacklisted, false otherwise.
     * @since 1.0.29
     */
    @ApiStatus.Internal
    public static boolean isBlacklisted(Identifier id) {
        return blacklistedProjectiles.contains(id);
    }

}
