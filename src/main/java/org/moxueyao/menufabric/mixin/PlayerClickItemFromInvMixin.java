package org.moxueyao.menufabric.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.moxueyao.menufabric.Event.PlayerClickItemFromInvCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class PlayerClickItemFromInvMixin {

    @Inject(
            method = "onClickSlot",
            at=@At("HEAD"),
            cancellable = true
    )
    private void onMouseClick(ClickSlotC2SPacket packet, CallbackInfo ci) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
        int actionType = packet.getActionType().ordinal(); // 0=左键, 1=右键, 2=拖拽等
        ItemStack item = packet.getStack();
        ServerPlayerEntity player = handler.getPlayer();
        if(actionType == 0){
            PlayerClickItemFromInvCallback.EVENT.invoker().interact(player, item, ci);
        }
    }
}
