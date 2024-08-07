package cc.seati.SeatiCore.Tasks;

import cc.seati.SeatiCore.Main;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum TaskType {
    BACKUP_SERVER("backupServer"),
    EMPTY_SERVER("emptyServer"),
    PLAYER_SNAPSHOT("playerSnapshot"),
    PLAYTIME("__private__playtime");

    public final String name;

    TaskType(String name) {
        this.name = name;
    }

    public static @Nullable TaskType of(String value) {
        try {
            return Arrays.stream(TaskType.values())
                    .filter(x -> x.toString().equals(value))
                    .toList()
                    .get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Task toMainTask() {
        return switch (this.name) {
            case "backupServer" -> Main.backupServerTask;
            case "emptyServer" -> Main.emptyServerTask;
            case "playerSnapshot" -> Main.playersSnapshotTask;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return name;
    }
}
