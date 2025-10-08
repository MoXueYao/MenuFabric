package org.moxueyao.menufabric.Utils.InvCreate;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class CreateItem {
    static public ItemStack createItem(Text displayName, ItemConvertible item){
        ItemStack itemStack = new ItemStack(item);
        itemStack.setCustomName(displayName);
        return itemStack;
    }
}
