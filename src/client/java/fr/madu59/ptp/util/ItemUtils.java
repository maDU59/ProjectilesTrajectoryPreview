package fr.madu59.ptp.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ItemUtils {
    public static boolean hasEnchantment(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        try{
            var enchantmentRegistry = Minecraft.getInstance().player.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT);

            Holder<Enchantment> enchantmentEntry = enchantmentRegistry
                .getOrThrow(enchantment);
            

            return EnchantmentHelper.getItemEnchantmentLevel(enchantmentEntry, stack) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
