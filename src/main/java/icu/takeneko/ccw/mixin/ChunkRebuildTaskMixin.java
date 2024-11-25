package icu.takeneko.ccw.mixin;

import icu.takeneko.ccw.ModClient;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(SectionRenderDispatcher.RenderSection.RebuildTask.class)
public class ChunkRebuildTaskMixin {
    @Shadow @Final
    SectionRenderDispatcher.RenderSection this$1;

    @Inject(method = "doTask", at = @At("HEAD"))
    void onTaskStarted(
        SectionBufferBuilderPack sectionBufferBuilderPack,
        CallbackInfoReturnable<CompletableFuture<SectionRenderDispatcher.SectionTaskResult>> cir
    ) {
        ModClient.getWatchdog().taskStarted((SectionRenderDispatcher.RenderSection.RebuildTask)(Object)this, this$1);
    }

    @Inject(method = "doTask", at = @At("RETURN"), cancellable = true)
    void onTaskFinished(
        SectionBufferBuilderPack sectionBufferBuilderPack,
        CallbackInfoReturnable<CompletableFuture<SectionRenderDispatcher.SectionTaskResult>> cir
    ) {
        cir.setReturnValue(cir.getReturnValue().thenApply(r -> {
            ModClient.getWatchdog().taskFinished((SectionRenderDispatcher.RenderSection.RebuildTask)(Object)this, r);
            return r;
        }));
    }
}
