package cc.seati.SeatiCore.Tasks;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Task {
    protected final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    protected final ScheduledExecutorService uptimeService = Executors.newSingleThreadScheduledExecutor();
    protected int uptime = 0;
    protected abstract void start();

    /**
     * 立即开始此任务并开始统计时间。
     */
    public void run() {
        this.start();
        uptimeService.scheduleAtFixedRate(() -> {
            this.uptime += 1;
        }, 0, 1, TimeUnit.SECONDS);
    }
    public void shutdown() {
        executorService.shutdown();
        uptimeService.shutdown();
    }
    public boolean isRunning() {
        return !executorService.isShutdown();
    }

    public abstract int getInterval();

    public abstract @Nullable LocalDateTime getLastExecution();

    public abstract TaskType getType();
}
