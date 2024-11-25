package icu.takeneko.ccw;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


@EventBusSubscriber(modid = ModClient.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue COMPILE_TIME_LIMIT = BUILDER
        .comment("Chunk compile time limit, in milliseconds")
        .defineInRange("compileTimeLimit", 100, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int chunkCompileTimeLimit;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        chunkCompileTimeLimit = COMPILE_TIME_LIMIT.get();
    }
}
