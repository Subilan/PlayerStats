package cc.seati.PlayerStats.Utils;

import cc.seati.PlayerStats.Utils.Enums.BackendCodes;
import cc.seati.PlayerStats.Utils.Records.MCIDUsage;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LabUtil {
    private static final String ENDPOINT_MCID_USAGE = "/user/mcid-usage";
    private static final String ENDPOINT_MCID_VERIFY = "/user/mcid-verify";

    private static boolean success(JsonObject resp) {
        return BackendCodes.OK.getCode() == resp.get("code").getAsInt();
    }

    private static JsonObject getDataAsJsonObject(JsonObject resp) {
        return resp.get("data").getAsJsonObject();
    }

    public static CompletableFuture<@Nullable MCIDUsage> getMCIDUsage(String playername) {
        return WebUtil.GET(ConfigUtil.getApiHost() + ENDPOINT_MCID_USAGE + "?playername=" + playername).thenApply(s -> {
            JsonObject parsed = WebUtil.parseJson(s);
            if (!LabUtil.success(parsed)) {
                return null;
            }

            JsonObject data = getDataAsJsonObject(parsed);

            return new MCIDUsage(data.get("used").getAsBoolean(), data.get("verified").getAsBoolean(), data.get("with").getAsString());
        });
    }

    public static CompletableFuture<Boolean> verifyMCID(String playername) {
        return WebUtil.GET(ConfigUtil.getApiHost() + ENDPOINT_MCID_VERIFY + "?playername=" + playername).thenApply(s -> {
            JsonObject parsed = WebUtil.parseJson(s);
            return LabUtil.success(parsed);
        });
    }
}
