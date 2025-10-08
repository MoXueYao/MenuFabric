package org.moxueyao.menufabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.moxueyao.menufabric.Player.BackPlayer;
import org.moxueyao.menufabric.Player.ColdPlayer;
import org.moxueyao.menufabric.Player.HomePlayer;
import org.moxueyao.menufabric.Player.TpPlayer;
import org.moxueyao.menufabric.Enum.PlayerType;
import org.moxueyao.menufabric.Event.PlayerClickItemFromInvCallback;
import org.moxueyao.menufabric.Utils.ConfigManager;
import org.moxueyao.menufabric.Utils.DeathLocationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.moxueyao.menufabric.Utils.InvCreate.CreateItem.createItem;
import static org.moxueyao.menufabric.Utils.InvCreate.CreateMenu.createMainMenu;
import static org.moxueyao.menufabric.Utils.InvCreate.CreateMenu.createTPMenu;
import static org.moxueyao.menufabric.Utils.DeathLocationManager.recordDeathLocation;

public class Menufabric implements ModInitializer {
    static List<TpPlayer> tpPlayers = new ArrayList<>();
    static List<HomePlayer> homePlayers = new ArrayList<>();
    static List<BackPlayer> backPlayers = new ArrayList<>();
    public static final String MOD_ID = "FabricMenu";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ConfigManager CONFIG;

    public static ColdPlayer getColdPlayer(ServerPlayerEntity player, PlayerType type){
        if(type == PlayerType.TP){
            for(TpPlayer p : tpPlayers){
                if(p.getName().equals(player.getName().getString())){
                    return p;
                }
            }
            TpPlayer new_p = new TpPlayer(player.getName().getString(), CONFIG.TP.coldTime);
            tpPlayers.add(new_p);
            return new_p;
        }
        if(type == PlayerType.BACK){
            for(BackPlayer p : backPlayers){
                if(p.getName().equals(player.getName().getString())){
                    return p;
                }
            }
            BackPlayer new_p = new BackPlayer(player.getName().getString(),CONFIG.back.coldTime);
            backPlayers.add(new_p);
            return new_p;
        }
        for(HomePlayer p : homePlayers){
            if(p.getName().equals(player.getName().getString())){
                return p;
            }
        }
        HomePlayer new_p = new HomePlayer(player.getName().getString(), CONFIG.home.coldTime);
        homePlayers.add(new_p);
        return new_p;
    }

    @Override
    public void onInitialize() {
        CONFIG = ConfigManager.load();
        // 玩家打开菜单
        UseItemCallback.EVENT.register((player, world, hand) -> {
            // 检查玩家是否是旁观者模式
            if(player.isSpectator()){
                return TypedActionResult.pass(ItemStack.EMPTY);
            }
            ItemStack item = player.getStackInHand(hand);
            if(item.getName().getString().equalsIgnoreCase("§a菜单")){
                SimpleNamedScreenHandlerFactory inv = createMainMenu();
                player.openHandledScreen(inv);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        // 玩家加入时给予菜单
        ServerPlayConnectionEvents.JOIN.register((handler,sender,server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            if(player.isSpectator()){
                return;
            }
            PlayerInventory inv = player.getInventory();
            for(ItemStack item : inv.main){
                if (item.getName().getString().equals("§a菜单")){
                    return;
                }
            }
            inv.insertStack(createItem(Text.of("§a菜单"), Items.CLOCK));
        });

        // 玩家死亡后保存死亡位置
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if(entity instanceof ServerPlayerEntity player){
                recordDeathLocation(player);
                PlayerInventory inv = player.getInventory();
                for(ItemStack item : inv.main){
                    if(item.getName().getString().equals("§a菜单")){
                        item.setCount(0);
                    }
                }
            }
            return true;
        });

        // 玩家重生后给予菜单
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            PlayerInventory inv = newPlayer.getInventory();
            for(ItemStack item : inv.main){
                if (item.getName().getString().equals("§a菜单")){
                    return;
                }
            }
            inv.insertStack(createItem(Text.of("§a菜单"), Items.CLOCK));
        });

        // 玩家点击菜单时执行对应的操作
        PlayerClickItemFromInvCallback.EVENT.register((player,  item, ci) -> {
            if(item.isEmpty()){
                return ActionResult.PASS;
            }
            String name =  item.getName().getString();
            // 玩家点击回家
            if(name.equals("§6回家")){
                HomePlayer p = (HomePlayer) getColdPlayer(player, PlayerType.HOME);
                if(p.isCold()){
                    String msg = String.format(CONFIG.inCold, p.getColdDown());
                    player.sendMessage(Text.literal(msg), false);
                    player.closeHandledScreen();
                    ci.cancel();
                    return ActionResult.PASS;
                }
                MinecraftServer server = player.getServer();
                BlockPos spawnPos = player.getSpawnPointPosition();
                RegistryKey<World> spawnDim = player.getSpawnPointDimension();
                if(spawnPos == null){
                    player.sendMessage(Text.literal(CONFIG.home.noHome), false);
                    player.closeHandledScreen();
                    ci.cancel();
                    return ActionResult.PASS;
                }
                ServerWorld targetWorld = null;
                if (server != null) {
                    targetWorld = server.getWorld(spawnDim);
                }
                player.teleport(targetWorld, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.getYaw(), player.getPitch());
                player.sendMessage(Text.literal(CONFIG.home.tpHomeSuccess), false);
                player.closeHandledScreen();
                ci.cancel();
                return ActionResult.PASS;
            }
            // 玩家点击返回
            if(name.equals("§c返回")) {
                BackPlayer p = (BackPlayer) getColdPlayer(player, PlayerType.BACK);
                if(p.isCold()){
                    String msg = String.format(CONFIG.inCold, p.getColdDown());
                    player.sendMessage(Text.literal(msg), false);
                    player.closeHandledScreen();
                    ci.cancel();
                    return ActionResult.PASS;
                }
                DeathLocationManager.DeathLocation deathLoc = DeathLocationManager.getDeathLocation(player.getUuid());
                if (deathLoc == null) {
                    player.sendMessage(Text.literal(CONFIG.back.noDeathLoc), false);
                    p.reSetColdDown();
                    player.closeHandledScreen();
                    ci.cancel();
                    return ActionResult.PASS;
                }
                MinecraftServer server = player.getServer();
                ServerWorld targetWorld = null;
                if (server != null) {
                    targetWorld = server.getWorld(deathLoc.dimension);
                }
                if (targetWorld == null) {
                    player.sendMessage(Text.literal(CONFIG.back.tpDeathFailed), false);
                    player.closeHandledScreen();
                    ci.cancel();
                    return ActionResult.PASS;
                }
                player.teleport(targetWorld, deathLoc.pos.getX(), deathLoc.pos.getY(), deathLoc.pos.getZ(), player.getYaw(), player.getPitch());
                player.sendMessage(Text.literal(CONFIG.back.tpDeathSuccess), false);
                DeathLocationManager.removeDeathLocation(player.getUuid());
                player.closeHandledScreen();
                ci.cancel();
                return ActionResult.PASS;
            }

            // 玩家点击传送
            if(name.equals("§a传送")){
                SimpleNamedScreenHandlerFactory inv = createTPMenu();
                player.openHandledScreen(inv);
                ci.cancel();
                return ActionResult.PASS;
            }

            String[] names = name.split(" §f");
            // 玩家传送其他玩家
            if(names.length != 1) {
                TpPlayer p = (TpPlayer) getColdPlayer(player, PlayerType.TP);
                if(p.isCold()){
                    String msg = String.format(CONFIG.inCold, p.getColdDown());
                    player.sendMessage(Text.literal(msg), false);
                    player.closeHandledScreen();
                    ci.cancel();
                    return ActionResult.PASS;
                }
                MinecraftServer server = player.getServer();
                ServerPlayerEntity target = null;
                if (server != null) {
                    target = server.getPlayerManager().getPlayer(names[1]);
                }

                Vec3d target_pos = null;
                ServerWorld targetWorld = null;
                if (target != null) {
                    target_pos = target.getPos();
                    targetWorld = target.getServerWorld();
                }
                if (target_pos != null) {
                    player.teleport(targetWorld, target_pos.getX(), target_pos.getY(), target_pos.getZ(), player.getYaw(), player.getPitch());
                    String msg =  String.format(CONFIG.TP.tpPlayerSuccess, target.getName().getString());
                    player.sendMessage(Text.literal(msg), false);
                    player.closeHandledScreen();
                    ci.cancel();
                    return ActionResult.PASS;
                }
                player.closeHandledScreen();
                player.sendMessage(Text.literal(CONFIG.TP.tpPlayerFailed), false);
                ci.cancel();
                return ActionResult.PASS;
            }
            return ActionResult.PASS;
        });
    }
}
