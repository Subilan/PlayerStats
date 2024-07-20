package cc.seati.PlayerStats.Events;

import cc.seati.PlayerStats.Database.Model.Enums.LoginRecordActionType;
import cc.seati.PlayerStats.Database.Model.LoginRecord;
import cc.seati.PlayerStats.Main;
import cc.seati.PlayerStats.Tracker.PlaytimeTracker;
import cc.seati.PlayerStats.Utils.*;
import cc.seati.PlayerStats.Utils.Records.MCIDUsage;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class PlayerEvents {
    public static Map<ServerPlayer, PlaytimeTracker> playtimeTrackerMap = new HashMap<>();

    @SubscribeEvent
    public static void handlePlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        ServerPlayer player = CommonUtil.getServerPlayer(e.getEntity());
        PlaytimeTracker tracker = new PlaytimeTracker(player, DBUtil.getManager());
        tracker.run();
        playtimeTrackerMap.put(player, tracker);
        Main.LOGGER.info("Starting playtime tracker for player {}", player.getName().getString());

        new LoginRecord(
                LoginRecordActionType.LOGIN,
                player.getName().getString(),
                ConfigUtil.getPeriodTag(),
                // Check if is first login in this period tag
                LoginRecord.isFirstLogin(DBUtil.getManager(), player.getName().getString(), ConfigUtil.getPeriodTag())
        ).saveAsync(DBUtil.getManager());

        Main.LOGGER.info("Checking player name binding state...");
        @Nullable MCIDUsage usage = CommonUtil.tryReturn(() -> CommonUtil.waitFor(LabUtil.getMCIDUsage(player.getName().getString())), null);
        if (usage == null) {
            Main.LOGGER.warn("Could not check player name binding state.");
        } else {
            Main.LOGGER.info("Check completed: used={}, verified={}", usage.used(), usage.verified());
            if (usage.used() && !usage.verified()) {
                player.sendSystemMessage(TextUtil.literal("[&b提示&f] 你有未验证的&a绑定请求&f，请输入 &e/seati verify&f 完成绑定。目标 Lab 用户名：&e" + usage.with()));
            }
        }
    }

    @SubscribeEvent
    public static void handlePlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        ServerPlayer player = CommonUtil.getServerPlayer(e.getEntity());
        playtimeTrackerMap.get(player).shutdown();
        playtimeTrackerMap.remove(player);
        Main.LOGGER.info("Shutting down playtime tracker for player {}", player.getName().getString());
        new LoginRecord(
                LoginRecordActionType.LOGOUT,
                player.getName().getString(),
                ConfigUtil.getPeriodTag(),
                false
        ).saveAsync(DBUtil.getManager());
    }
}
