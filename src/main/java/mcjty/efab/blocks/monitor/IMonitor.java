package mcjty.efab.blocks.monitor;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IMonitor {

    BlockPos getPos();

    List<String> getClientLog();

    long getLastUpdateTime();

    void setLastUpdateTime(long t);
}
