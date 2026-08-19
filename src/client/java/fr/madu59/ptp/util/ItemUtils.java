package fr.madu59.ptp.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ItemUtils {
    public static boolean hasEnchantment(ItemStack stack, Enchantment enchantment) {
        try{
            return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
