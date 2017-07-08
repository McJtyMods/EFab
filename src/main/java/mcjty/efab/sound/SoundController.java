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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

@SideOnly(Side.CLIENT)
public final class SoundController {

    private static SoundEvent machine;
    private static SoundEvent sparks;
    private static SoundEvent steam;
    private static SoundEvent beeps1;
    private static SoundEvent beeps2;

    public static void init(IForgeRegistry<SoundEvent> registry) {
        machine = registerSound(registry, new ResourceLocation(EFab.MODID, "machine"));
        sparks = registerSound(registry, new ResourceLocation(EFab.MODID, "sparks"));
        steam = registerSound(registry, new ResourceLocation(EFab.MODID, "steam"));
        beeps1 = registerSound(registry, new ResourceLocation(EFab.MODID, "beeps1"));
        beeps2 = registerSound(registry, new ResourceLocation(EFab.MODID, "beeps2"));
    }

    private static final Map<Pair<Integer, BlockPos>, EFabSound> sounds = Maps.newHashMap();

    private static SoundEvent registerSound(IForgeRegistry<SoundEvent> registry, ResourceLocation rl) {
        SoundEvent ret = new SoundEvent(rl).setRegistryName(rl);
        registry.register(ret);
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

    private static void playSound(World worldObj, BlockPos pos, SoundEvent soundType, float baseVolume, int ticks) {
        EFabSound sound = new EFabSound(soundType, worldObj, pos, baseVolume, ticks);
        stopSound(worldObj, pos);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        Pair<Integer, BlockPos> g = Pair.of(worldObj.provider.getDimension(), pos);
        sounds.put(g, sound);
    }


    public static void playMachineSound(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, machine, GeneralConfiguration.baseMachineVolume, GeneralConfiguration.soundMachineTicks);
    }

    public static void playSparksSound(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, sparks, GeneralConfiguration.baseSparksVolume, GeneralConfiguration.soundSparksTicks);
    }

    public static void playSteamSound(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, steam, GeneralConfiguration.baseSteamVolume, GeneralConfiguration.soundSteamTicks);
    }

    public static void playBeeps1Sound(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, beeps1, GeneralConfiguration.baseBeepsVolume, GeneralConfiguration.soundBeepsTicks);
    }

    public static void playBeeps2Sound(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, beeps2, GeneralConfiguration.baseBeepsVolume, GeneralConfiguration.soundBeepsTicks);
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

    public static boolean isBeepsPlaying(World worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(beeps1, worldObj, pos) || isSoundTypePlayingAt(beeps2, worldObj, pos);
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
