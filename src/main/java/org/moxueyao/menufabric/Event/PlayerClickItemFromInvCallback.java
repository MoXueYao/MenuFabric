package org.moxueyao.menufabric.Event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface PlayerClickItemFromInvCallback {
    Event<PlayerClickItemFromInvCallback> EVENT = EventFactory.createArrayBacked(PlayerClickItemFromInvCallback.class,
            (listeners) -> (player, Item, ci) -> {
                for (PlayerClickItemFromInvCallback listener : listeners) {
                    ActionResult result = listener.interact(player, Item, ci);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(ServerPlayerEntity player, ItemStack item, CallbackInfo ci);
}
