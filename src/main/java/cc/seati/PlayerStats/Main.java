package cc.seati.PlayerStats;

import cc.seati.PlayerStats.Database.Database;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MOD_ID)
public final class Main {
    public static final String MOD_ID = "playerstats";
    public static final Logger LOGGER = LogManager.getLogger("PlayerStats");
    public Main() {
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            LOGGER.info("Initializing PlayerStats...");
            Config.init(false);
            Database.init();
            LOGGER.info("Initialization end.");
        });
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
            LOGGER.warn("Invalid dist. This mod can only be enabled on server side.");
        });
    }
}
