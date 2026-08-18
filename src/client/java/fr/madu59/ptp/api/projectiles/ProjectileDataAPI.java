package fr.madu59.ptp.api.projectiles;

import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import fr.madu59.ptp.physics.ProjectileData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ProjectileDataAPI {
    
    private static final Map<Identifier, TriConsumer<ItemStack, Player, List<ProjectileData>>> projectileDataProviderMap = new HashMap<>();
    private static final Map<Identifier, BooleanSupplier> projectileTrajectoryStateSupplierMap = new HashMap<>();
    private static final List<Identifier> blacklistedProjectiles = new ArrayList<>();

    /*
     * Registers a projectile with the given ID and data provider.
     * @param id The unique identifier for the projectile or the projectile thrower (e.g. "minecraft:bow", "minecraft:snowball").
     * @param dataProvider The ProjectileData containing physics parameters and update order.
     * @since 1.0.35
     */
    public static void registerProjectile(Identifier id, TriConsumer<ItemStack, Player, List<ProjectileData>> dataProvider) {
        registerProjectile(id, dataProvider, () -> true);
    }

    /*
     * Registers a projectile with the given ID and data provider.
     * @param item The projectile or the projectile thrower
     * @param dataProvider The ProjectileData containing physics parameters and update order.
     * @since 1.0.35
     */
    public static void registerProjectile(Item item, TriConsumer<ItemStack, Player, List<ProjectileData>> dataProvider) {
        if(item == null) return;

        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        registerProjectile(itemId, dataProvider);
    }

    /*
     * Registers a projectile with the given ID and data provider.
     * @param id The unique identifier for the projectile or the projectile thrower (e.g. "minecraft:bow", "minecraft:snowball").
     * @param dataProvider The ProjectileData containing physics parameters and update order.
     * @param booleanSupplier The boolean supplier used to determine whether the trajectory for this item is currently enabled.
     * @since 1.0.35
     */
    public static void registerProjectile(Identifier id, TriConsumer<ItemStack, Player, List<ProjectileData>> dataProvider, BooleanSupplier booleanSupplier) {
        projectileDataProviderMap.put(id, dataProvider);
        projectileTrajectoryStateSupplierMap.put(id, booleanSupplier);
    }

    /*
     * Registers a projectile with the given ID and data provider.
     * @param item The projectile or the projectile thrower
     * @param dataProvider The ProjectileData containing physics parameters and update order.
     * @param booleanSupplier The boolean supplier used to determine whether the trajectory for this item is currently enabled.
     * @since 1.0.35
     */
    public static void registerProjectile(Item item, TriConsumer<ItemStack, Player, List<ProjectileData>> dataProvider, BooleanSupplier booleanSupplier) {
        if(item == null) return;

        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        registerProjectile(itemId, dataProvider, booleanSupplier);
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
    public static TriConsumer<ItemStack, Player, List<ProjectileData>> getProjectileDataProvider(Identifier id) {
        return projectileDataProviderMap.get(id);
    }

    /*
     * Retrieves the ProjectileData for a given projectile ID.
     * @param itemStack The projectile.
     * @return The ProjectileData associated with the ID, or null if not found.
     * @since 1.0.35
     */
    @ApiStatus.Internal
    public static void getProjectileData(ItemStack itemStack, Player player, List<ProjectileData> out) {
        if(itemStack == null) return;

        Item item = itemStack.getItem();
        if(item == null) return;

        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if(itemId == null) return;

        TriConsumer<ItemStack, Player, List<ProjectileData>> dataProvider = getProjectileDataProvider(itemId);
        if(dataProvider == null) return;

        dataProvider.accept(itemStack, player, out);
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
     * Checks if a projectile with the given ID is currently enabled.
     * @param id The unique identifier for the projectile.
     * @return whether the trajectory for this item is enabled or not.
     * @since 1.0.35
     */
    @ApiStatus.Internal
    public static boolean isEnabled(Identifier id) {
        return projectileTrajectoryStateSupplierMap.get(id).getAsBoolean();
    }

    /*
     * Checks if a projectile with the given ID is currently enabled.
     * @param itemStack The itemStack.
     * @return whether the trajectory for this item is enabled or not.
     * @since 1.0.35
     */
    @ApiStatus.Internal
    public static boolean isEnabled(ItemStack itemStack) {
        if(itemStack == null) return false;

        Item item = itemStack.getItem();
        if(item == null) return false;

        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if(itemId == null) return false;

        return isEnabled(itemId);
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
