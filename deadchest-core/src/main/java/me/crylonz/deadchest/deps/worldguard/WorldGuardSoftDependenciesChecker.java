package me.crylonz.deadchest.deps.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.crylonz.deadchest.DeadChestLoader;
import me.crylonz.deadchest.utils.ConfigKey;
import org.bukkit.entity.Player;

import static me.crylonz.deadchest.DeadChestLoader.config;
import static me.crylonz.deadchest.utils.Utils.generateLog;

public class WorldGuardSoftDependenciesChecker {

    public static StateFlag DEADCHEST_GUEST_FLAG;
    public static StateFlag DEADCHEST_OWNER_FLAG;
    public static StateFlag DEADCHEST_MEMBER_FLAG;

    public void load() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag owner_flag = new StateFlag("dc-owner", false);
            registry.register(owner_flag);
            DEADCHEST_OWNER_FLAG = owner_flag;

            StateFlag nobody_flag = new StateFlag("dc-guest", false);
            registry.register(nobody_flag);
            DEADCHEST_GUEST_FLAG = nobody_flag;

            StateFlag member_flag = new StateFlag("dc-member", false);
            registry.register(member_flag);
            DEADCHEST_MEMBER_FLAG = member_flag;

        } catch (FlagConflictException e) {
            DeadChestLoader.log.warning("Conflict in Deadchest flags");
        }
    }

    public boolean worldGuardChecker(Player p) {

        if (!config.getBoolean(ConfigKey.WORLD_GUARD_DETECTION)) {
            return true;
        }

        try {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(p.getLocation()));
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);

            if (set.testState(localPlayer, DEADCHEST_OWNER_FLAG) && set.isOwnerOfAll(localPlayer)) return true;
            if (set.testState(localPlayer, DEADCHEST_MEMBER_FLAG) && set.isMemberOfAll(localPlayer)) return true;
            if (set.testState(localPlayer, DEADCHEST_GUEST_FLAG)) return true;

            if (!p.isOp())
                generateLog("Player [" + p.getName() + "] died without [WorldGuard] permission: No Deadchest generated");

            return p.isOp();
        } catch (NoClassDefFoundError e) {
            return true;
        }
    }

}
