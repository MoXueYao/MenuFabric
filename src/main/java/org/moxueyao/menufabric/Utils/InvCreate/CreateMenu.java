package org.moxueyao.menufabric.Utils.InvCreate;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

import static org.moxueyao.menufabric.Utils.InvCreate.CreateItem.createItem;

public class CreateMenu {
    static public SimpleNamedScreenHandlerFactory createMainMenu() {

        SimpleNamedScreenHandlerFactory screenHandler = new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, player) -> {
                    Inventory mainMenu = new SimpleInventory(9);
                    ItemStack TPButton = createItem(Text.of("§a传送"),Items.PLAYER_HEAD);
                    ItemStack HomeButton = createItem(Text.of("§6回家"),Items.RED_BED);
                    ItemStack BackButton = createItem(Text.of("§c返回"),Items.REDSTONE_BLOCK);
                    mainMenu.setStack(0, TPButton);
                    mainMenu.setStack(4, HomeButton);
                    mainMenu.setStack(8, BackButton);

                    return new GenericContainerScreenHandler(
                            ScreenHandlerType.GENERIC_9X1,
                            syncId,
                            playerInventory,
                            mainMenu,
                            1
                    );
                },
                Text.literal("主菜单")
        );
        return screenHandler;
    }

    static public SimpleNamedScreenHandlerFactory createTPMenu() {
        SimpleNamedScreenHandlerFactory screenHandler = new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, player) -> {
                    Inventory tpMenu = new SimpleInventory(9);
                    MinecraftServer server = player.getServer();
                    List<ServerPlayerEntity> players = null;
                    if (server != null) {
                        players = server.getPlayerManager().getPlayerList();
                    }
                    int i = 0;
                    if (players != null) {
                        for (ServerPlayerEntity p : players) {
                            if(p.getName().getString().equals(player.getName().getString())){
                                continue;
                            }
                            Text playerName = Text.of("§b传送到 §f" + p.getName().getString());
                            ItemStack itemStack = createItem(playerName, Items.PLAYER_HEAD);
                            tpMenu.setStack(i, itemStack);
                            i++;
                        }
                    }

                    return new GenericContainerScreenHandler(
                            ScreenHandlerType.GENERIC_9X1,
                            syncId,
                            playerInventory,
                            tpMenu,
                            1
                    );
                },
                Text.literal("TP菜单")
        );
        return screenHandler;
    }


}
