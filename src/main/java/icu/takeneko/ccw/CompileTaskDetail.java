package icu.takeneko.ccw;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.SectionPos;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Optional;

public class CompileTaskDetail {
    private final SectionRenderDispatcher.RenderSection.CompileTask compileTask;
    private final Thread thread;
    private final SectionPos sectionPos;
    private final long startTime;
    private boolean finished = false;
    private boolean warned = false;

    public CompileTaskDetail(SectionRenderDispatcher.RenderSection.CompileTask compileTask, SectionRenderDispatcher.RenderSection section, Thread thread) {
        this.compileTask = compileTask;
        this.thread = thread;
        this.sectionPos = SectionPos.of(section.getOrigin());
        startTime = System.currentTimeMillis();
    }

    public String getWarningString() {
        long time = System.currentTimeMillis() - startTime;
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        Optional<String> dumpString = Arrays.stream(bean.dumpAllThreads(true, true))
            .filter(it -> it.getThreadId() == thread.threadId())
            .map(ThreadInfo::toString)
            .findFirst();
        return "CompileTask %d[%d,%d,%d] in thread %s has not responding for %d ms.\n%s".formatted(
            compileTask.hashCode(),
            sectionPos.x(),
            sectionPos.y(),
            sectionPos.z(),
            thread.toString(),
            time,
            dumpString.orElse("No thread dump available.")
        );
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isWarned() {
        return warned;
    }

    public boolean isFinished() {
        return finished;
    }

    public void markWarned() {
        warned = true;
    }

    public void markFinished() {
        finished = true;
    }

    public SectionPos getSectionPos() {
        return sectionPos;
    }
}
