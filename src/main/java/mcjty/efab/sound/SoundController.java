package mcjty.efab.sound;

import com.google.common.collect.Maps;
import mcjty.efab.EFab;
import mcjty.efab.config.GeneralConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

@SideOnly(Side.CLIENT)
public final class SoundController {

    private static SoundEvent machine;
    private static SoundEvent sparks;
    private static SoundEvent steam;

    public static void init() {
        machine = registerSound(new ResourceLocation(EFab.MODID, "machine"));
        sparks = registerSound(new ResourceLocation(EFab.MODID, "sparks"));
        steam = registerSound(new ResourceLocation(EFab.MODID, "steam"));
    }

    private static final Map<Pair<Integer, BlockPos>, EFabSound> sounds = Maps.newHashMap();

    private static SoundEvent registerSound(ResourceLocation rl){
        SoundEvent ret = new SoundEvent(rl).setRegistryName(rl);
        ((FMLControlledNamespacedRegistry) SoundEvent.REGISTRY).register(ret);
        return ret;
    }

    public static void stopSound(World worldObj, BlockPos pos) {
        Pair<Integer, BlockPos> g = fromPosition(worldObj, pos);
        if (sounds.containsKey(g)) {
            MovingSound movingSound = sounds.get(g);
            Minecraft.getMinecraft().getSoundHandler().stopSound(movingSound);
            sounds.remove(g);
        }
    }

    private static void playSound(World worldObj, BlockPos pos, SoundEvent soundType, float volume, float baseVolume, int ticks) {
        EFabSound sound = new EFabSound(soundType, worldObj, pos, baseVolume, ticks);
        sound.setVolume(volume);
        stopSound(worldObj, pos);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        Pair<Integer, BlockPos> g = Pair.of(worldObj.provider.getDimension(), pos);
        sounds.put(g, sound);
    }


    public static void playMachineSound(World worldObj, BlockPos pos, float volume) {
        playSound(worldObj, pos, machine, volume, GeneralConfiguration.baseMachineVolume, GeneralConfiguration.soundMachineTicks);
    }

    public static void playSparksSound(World worldObj, BlockPos pos, float volume) {
        playSound(worldObj, pos, sparks, volume, GeneralConfiguration.baseSparksVolume, GeneralConfiguration.soundSparksTicks);
    }

    public static void playSteamSound(World worldObj, BlockPos pos, float volume) {
        playSound(worldObj, pos, steam, volume, GeneralConfiguration.baseSteamVolume, GeneralConfiguration.soundSteamTicks);
    }

    public static void updateVolume(World worldObj, BlockPos pos, float volume) {
        EFabSound sound = getSoundAt(worldObj, pos);
        if (sound != null) {
            sound.setVolume(volume);
        }
    }

    public static boolean isMachinePlaying(World worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(machine, worldObj, pos);
    }

    public static boolean isSparksPlaying(World worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(sparks, worldObj, pos);
    }

    public static boolean isSteamPlaying(World worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(steam, worldObj, pos);
    }


    private static boolean isSoundTypePlayingAt(SoundEvent event, World world, BlockPos pos){
        EFabSound s = getSoundAt(world, pos);
        if (s == null) {
            return false;
        }
        if (s.isDonePlaying()) {
            return false;
        }
        return s.isSoundType(event);
    }

    private static EFabSound getSoundAt(World world, BlockPos pos){
        return sounds.get(fromPosition(world, pos));
    }

    private static Pair<Integer, BlockPos> fromPosition(World world, BlockPos pos){
        return Pair.of(world.provider.getDimension(), pos);
    }

}
