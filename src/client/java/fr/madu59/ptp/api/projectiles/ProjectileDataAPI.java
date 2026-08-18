package fr.madu59.ptp.api.projectiles;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import fr.madu59.ptp.physics.ProjectileData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ProjectileDataAPI {
    
    public static final Map<Identifier, Function<ItemStack, ProjectileData>> projectileDataProviderMap = new HashMap<>();
    public static final List<Identifier> blacklistedProjectiles = new ArrayList<>();

    /*
     * Registers a projectile with the given ID and Data provider.
     * @param id The unique identifier for the projectile (e.g., "minecraft:arrow").
     * @param Data The ProjectileData containing physics parameters and update order.
     * @since 1.0.35
     */
    public static void registerProjectile(Identifier id, Function<ItemStack, ProjectileData> dataProvider) {
        projectileDataProviderMap.put(id, dataProvider);
    }

    /*
     * Adds a projectile ID to the blacklisted list, it will prevent it from showing a trajectory.
     * @param id The unique identifier for the projectile.
     * @since 1.0.35
     */
    public static void blacklistProjectile(Identifier id) {
        blacklistedProjectiles.add(id);
    }

    /*
     * Retrieves the ProjectileData for a given projectile ID.
     * @param id The unique identifier for the projectile.
     * @return The ProjectileData associated with the ID, or null if not found.
     * @since 1.0.35
     */
    @ApiStatus.Internal
    public static Function<ItemStack, ProjectileData> getProjectileDataProvider(Identifier id) {
        return projectileDataProviderMap.get(id);
    }

    /*
     * Retrieves the ProjectileData for a given projectile ID.
     * @param itemStack The projectile.
     * @return The ProjectileData associated with the ID, or null if not found.
     * @since 1.0.35
     */
    @ApiStatus.Internal
    public static ProjectileData getProjectileData(ItemStack itemStack) {
        if(itemStack == null) return null;

        Item item = itemStack.getItem();
        if(item == null) return null;

        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if(itemId == null) return null;

        Function<ItemStack, ProjectileData> dataProvider = getProjectileDataProvider(itemId);
        if(dataProvider == null) return null;

        return dataProvider.apply(itemStack);
    }

    /*
     * Checks if a projectile with the given ID exists.
     * @param id The unique identifier for the projectile.
     * @return true if the projectile exists, false otherwise.
     * @since 1.0.35
     */
    @ApiStatus.Internal
    public static boolean hasProjectileDataProvider(Identifier id) {
        return projectileDataProviderMap.containsKey(id);
    }

    /*
     * Checks if a projectile with the given ID exists.
     * @param itemStack The projectile.
     * @return true if the projectile exists, false otherwise.
     * @since 1.0.35
     */
    @ApiStatus.Internal
    public static boolean hasProjectileData(ItemStack itemStack) {
        if(itemStack == null) return false;

        Item item = itemStack.getItem();
        if(item == null) return false;

        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if(itemId == null) return false;

        return getProjectileDataProvider(itemId) != null;
    }

    /*
     * Checks if a projectile with the given ID is blacklisted.
     * @param id The unique identifier for the projectile.
     * @return true if the projectile is blacklisted, false otherwise.
     * @since 1.0.35
     */
    @ApiStatus.Internal
    public static boolean isBlacklisted(Identifier id) {
        return blacklistedProjectiles.contains(id);
    }

}
