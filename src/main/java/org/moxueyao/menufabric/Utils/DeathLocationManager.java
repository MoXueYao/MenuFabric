package org.moxueyao.menufabric.Utils;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathLocationManager {
    public static class DeathLocation {
        public final Vec3d pos;
        public final RegistryKey<World> dimension;
        public final long deathTime;

        public DeathLocation(Vec3d pos, RegistryKey<World> dimension) {
            this.pos = pos;
            this.dimension = dimension;
            this.deathTime = System.currentTimeMillis();
        }
    }

    // 存储每个玩家的最后死亡位置
    private static Map<UUID, DeathLocation> deathLocations = new HashMap<>();

    public static void recordDeathLocation(ServerPlayerEntity player) {
        DeathLocation loc = new DeathLocation(
                player.getPos(),
                player.getWorld().getRegistryKey()
        );
        deathLocations.put(player.getUuid(), loc);
    }

    public static DeathLocation getDeathLocation(UUID playerUuid) {
        return deathLocations.get(playerUuid);
    }

    public static void removeDeathLocation(UUID playerUuid) {
        deathLocations.remove(playerUuid);
    }
}
