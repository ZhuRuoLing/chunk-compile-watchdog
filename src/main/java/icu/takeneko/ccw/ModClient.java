package icu.takeneko.ccw;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ModClient.MODID, dist = Dist.CLIENT)
public class ModClient {
    public static final String MODID = "chunkcompilewatchdog";
    private static WatchdogThread thread;

    public ModClient(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        thread = new WatchdogThread();
        thread.setDaemon(true);
        thread.start();
    }

    public static WatchdogThread getWatchdog() {
        return thread;
    }
}
