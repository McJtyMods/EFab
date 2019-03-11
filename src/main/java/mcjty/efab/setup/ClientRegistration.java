package mcjty.efab.setup;


import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.compat.botania.BotaniaSupportSetup;
import mcjty.efab.items.ModItems;
import mcjty.efab.sound.SoundController;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientRegistration {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModItems.initModels();
        ModBlocks.initModels();
        if (CommonSetup.botania) {
            BotaniaSupportSetup.initModels();
        }
    }


    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
        SoundController.init(sounds.getRegistry());
    }



}
