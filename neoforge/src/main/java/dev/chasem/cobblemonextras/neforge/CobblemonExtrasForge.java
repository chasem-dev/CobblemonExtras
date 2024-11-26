package dev.chasem.cobblemonextras.neforge;

import dev.chasem.cobblemonextras.CobblemonExtras;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
@Mod(CobblemonExtras.MODID)
public final class CobblemonExtrasForge {
    ////    init {
////        with(MOD_BUS) {
////            addListener(this@CobblemonExtrasForge::initialize)
////            addListener(this@CobblemonExtrasForge::serverInit)
////        }
////
////        NeoForge.EVENT_BUS.register(this);
////    }
//
//    public CobblemonExtrasForge(IEventBus modBus) {
//        CobblemonExtras.INSTANCE.getLogger().info("CobblemonExtras NeoForge Starting...");
//        NeoForge.EVENT_BUS.register(this);
//        modBus.addListener(CobblemonExtrasForge::initialize);
//    }
//    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    public void registerCommands(RegisterCommandsEvent event) {
//        CobblemonExtras.INSTANCE.registerCommands(event.getDispatcher());
//    }
//
//    public void serverInit(FMLDedicatedServerSetupEvent event) {
//    }
//
//    public static void initialize(FMLCommonSetupEvent event) {
//        CobblemonExtras.INSTANCE.initialize();
//        CobblemonExtras.INSTANCE.getLogger().info("CobblemonExtras NeoForge initialize");
//    }
//
//}
    public CobblemonExtrasForge() {
        CobblemonExtras.INSTANCE.getLogger().info("CobblemonExtras NeoForge Starting...");
        CobblemonExtras.INSTANCE.initialize();
        NeoForge.EVENT_BUS.register(CobblemonExtrasForge.class);
    }

    @SubscribeEvent
    public static void onCommandRegistration(final RegisterCommandsEvent event) {
        CobblemonExtras.INSTANCE.registerCommands(event.getDispatcher());
    }
}