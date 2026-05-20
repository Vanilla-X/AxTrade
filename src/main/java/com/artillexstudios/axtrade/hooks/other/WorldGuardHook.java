package com.artillexstudios.axtrade.hooks.other;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public final class WorldGuardHook {
    private static final String FLAG_NAME = "axtrade-trade";
    private static StateFlag tradeFlag;

    private WorldGuardHook() {
    }

    public static void register() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) return;
        if (tradeFlag != null) return;

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        StateFlag flag = new StateFlag(FLAG_NAME, true);
        try {
            registry.register(flag);
            tradeFlag = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get(FLAG_NAME);
            if (existing instanceof StateFlag stateFlag) tradeFlag = stateFlag;
        } catch (Throwable t) {
            Bukkit.getLogger().warning("[AxTrade] Could not register WorldGuard flag: " + t.getMessage());
        }
    }

    public static boolean canTradeAt(@NotNull Location loc) {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) return true;
        if (tradeFlag == null || loc.getWorld() == null) return true;

        try {
            RegionAssociable associable = Associables.constant(Association.NON_MEMBER);
            StateFlag.State state = WorldGuard.getInstance().getPlatform().getRegionContainer()
                    .createQuery()
                    .queryState(BukkitAdapter.adapt(loc), associable, tradeFlag);
            return state != StateFlag.State.DENY;
        } catch (Throwable ignored) {
            return true;
        }
    }
}
