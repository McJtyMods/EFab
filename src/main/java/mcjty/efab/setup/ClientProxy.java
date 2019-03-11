package mcjty.efab.setup;

import mcjty.efab.EFab;
import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.compat.botania.BotaniaSupportSetup;
import mcjty.efab.items.ModItems;
import mcjty.efab.sound.SoundController;
import mcjty.lib.McJtyLibClient;
import mcjty.lib.setup.DefaultClientProxy;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(this);
        OBJLoader.INSTANCE.addDomain(EFab.MODID);
        McJtyLibClient.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModItems.initModels();
        ModBlocks.initModels();
        if (CommonSetup.botania) {
            BotaniaSupportSetup.initModels();
        }
    }


    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
        SoundController.init(sounds.getRegistry());
    }


    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
//        ModBlocks.initItemModels();
    }
}
