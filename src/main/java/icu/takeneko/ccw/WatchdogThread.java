package icu.takeneko.ccw;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.SectionPos;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class WatchdogThread extends Thread {
    //need typealias here...
    private final Map<SectionRenderDispatcher.RenderSection.CompileTask, CompileTaskDetail> map = new ConcurrentHashMap<>();
    private boolean running = true;
    private final Logger logger = LogUtils.getLogger();

    @Override
    public void run() {
        logger.info("Starting watchdog thread.");
        while (running) {
            if (!map.isEmpty()) {
                List<SectionRenderDispatcher.RenderSection.CompileTask> removal = new ArrayList<>();
                map.forEach((compileTask, compileTaskDetail) -> {
                    if (compileTaskDetail.isFinished()) {
                        removal.add(compileTask);
                        return;
                    }
                    if (System.currentTimeMillis() - compileTaskDetail.getStartTime() > ClientConfig.chunkCompileTimeLimit) {
                        if (compileTaskDetail.isWarned()) return;
                        compileTaskDetail.markWarned();
                        logger.warn(compileTaskDetail.getWarningString());
                    }
                });
                removal.forEach(map::remove);
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));
        }
        logger.info("Watchdog thread stopped.");
    }

    public void taskStarted(SectionRenderDispatcher.RenderSection.CompileTask task, SectionRenderDispatcher.RenderSection section) {
        Thread thread = Thread.currentThread();
        CompileTaskDetail detail = new CompileTaskDetail(task, section, thread);
        SectionPos sectionPos = detail.getSectionPos();
        logger.debug("Chunk compile task {}[{},{},{}] started on thread {}", task.hashCode(), sectionPos.x(), sectionPos.y(), sectionPos.z(), thread);
        map.put(
            task,
            detail
        );
    }

    public void taskFinished(SectionRenderDispatcher.RenderSection.CompileTask task, SectionRenderDispatcher.SectionTaskResult result) {
        if (map.containsKey(task)) {
            CompileTaskDetail detail = map.get(task);
            detail.markFinished();
            SectionPos sectionPos = detail.getSectionPos();
            logger.debug("CompileTask {}[{},{},{}] finished with result {}", task.hashCode(), sectionPos.x(), sectionPos.y(), sectionPos.z(), result);
            return;
        }
        logger.warn("CompileTask {} was not recorded.", task);
    }

    public void end() {
        running = false;
    }
}
