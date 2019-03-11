package mcjty.efab.blocks;

import mcjty.efab.blocks.base.BaseBlock;
import mcjty.efab.blocks.boiler.BoilerBlock;
import mcjty.efab.blocks.crafter.CrafterBlock;
import mcjty.efab.blocks.gearbox.GearBoxBlock;
import mcjty.efab.blocks.grid.GridBlock;
import mcjty.efab.blocks.monitor.AutoCraftingMonitorBlock;
import mcjty.efab.blocks.monitor.MonitorBlock;
import mcjty.efab.blocks.pipes.PipeBlock;
import mcjty.efab.blocks.poweroptimizer.PowerOptimizerBlock;
import mcjty.efab.blocks.processor.ProcessorBlock;
import mcjty.efab.blocks.rfcontrol.RfControlBlock;
import mcjty.efab.blocks.rfstorage.AdvancedRFStorageTE;
import mcjty.efab.blocks.rfstorage.RFStorageBlock;
import mcjty.efab.blocks.rfstorage.RFStorageTE;
import mcjty.efab.blocks.steamengine.SteamEngineBlock;
import mcjty.efab.blocks.storage.StorageBlock;
import mcjty.efab.blocks.tank.AdvancedTankTE;
import mcjty.efab.blocks.tank.TankBlock;
import mcjty.efab.blocks.tank.TankTE;
import mcjty.efab.config.ConfigSetup;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static BaseBlock baseBlock;
    public static GearBoxBlock gearBoxBlock;
    public static PipeBlock pipeBlock;
    public static GridBlock gridBlock;
    public static TankBlock tankBlock;
    public static TankBlock tank2Block;
    public static BoilerBlock boilerBlock;
    public static SteamEngineBlock steamEngineBlock;
    public static RfControlBlock rfControlBlock;
    public static RFStorageBlock rfStorageBlock;
    public static RFStorageBlock advancedRfStorageBlock;
    public static ProcessorBlock processorBlock;
    public static MonitorBlock monitorBlock;
    public static AutoCraftingMonitorBlock autoCraftingMonitorBlock;
    public static StorageBlock storageBlock;
    public static CrafterBlock crafterBlock;
    public static PowerOptimizerBlock powerOptimizerBlock;

    public static void init() {
        baseBlock = new BaseBlock();
        gearBoxBlock = new GearBoxBlock();
        pipeBlock = new PipeBlock();
        gridBlock = new GridBlock();
        tankBlock = new TankBlock("tank", ConfigSetup.tankCapacity.get(), TankTE.class);
        tank2Block = new TankBlock("tank2", ConfigSetup.tank2Capacity.get(), AdvancedTankTE.class);
        boilerBlock = new BoilerBlock();
        steamEngineBlock = new SteamEngineBlock();
        rfControlBlock = new RfControlBlock();
        rfStorageBlock = new RFStorageBlock("rfstorage", false, RFStorageTE.class);
        advancedRfStorageBlock = new RFStorageBlock("rfstorage_advanced", true, AdvancedRFStorageTE.class);
        processorBlock = new ProcessorBlock();
        monitorBlock = new MonitorBlock();
        autoCraftingMonitorBlock = new AutoCraftingMonitorBlock();
        storageBlock = new StorageBlock();
        crafterBlock = new CrafterBlock();
        powerOptimizerBlock = new PowerOptimizerBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        baseBlock.initModel();
        gearBoxBlock.initModel();
        pipeBlock.initModel();
        gridBlock.initModel();
        tankBlock.initModel();
        tank2Block.initModel();
        boilerBlock.initModel();
        steamEngineBlock.initModel();
        rfControlBlock.initModel();
        rfStorageBlock.initModel();
        advancedRfStorageBlock.initModel();
        processorBlock.initModel();
        monitorBlock.initModel();
        autoCraftingMonitorBlock.initModel();
        storageBlock.initModel();
        crafterBlock.initModel();
        powerOptimizerBlock.initModel();
    }
}
