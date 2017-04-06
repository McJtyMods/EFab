package mcjty.efab.blocks.monitor;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.blocks.ISpeedBooster;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.network.PacketGetMonitorText;
import mcjty.lib.network.Argument;
import mcjty.typed.Type;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonitorTE extends GenericEFabTile implements ITickable, ISpeedBooster {

    private float speed = 1.0f;
    private int speedBoost = 0;

    private long lastHudTime = 0;
    private List<String> clientHudLog = new ArrayList<>();

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
        markDirtyClient();
    }

    @Override
    public int getSpeedBoost() {
        return speedBoost;
    }

    @Override
    public void setSpeedBoost(int speedBoost) {
        this.speedBoost = speedBoost;
        markDirtyClient();
    }

    @Override
    public void update() {
        if (speed > 1.0f) {
            speed -= GeneralConfiguration.steamWheelSpinDown;
            if (speed < 1.0f) {
                speed = 1.0f;
            }
            markDirtyQuick();
        }
        if (speedBoost > 0) {
            speedBoost--;
            speed += GeneralConfiguration.steamWheelSpeedUp;
            if (speed > GeneralConfiguration.maxSteamWheelSpeed) {
                speed = GeneralConfiguration.maxSteamWheelSpeed;
            }
            markDirtyQuick();
        }
    }

    public List<String> getClientLog() {
        return clientHudLog;
    }

    public long getLastUpdateTime() {
        return lastHudTime;
    }

    public void setLastUpdateTime(long t) {
        lastHudTime = t;
    }

    private List<String> getMessages() {
        List<String> list = new ArrayList<>();
        list.add(TextFormatting.DARK_GREEN + "Mode:");
        list.add(TextFormatting.DARK_GREEN + "crafting...");
        list.add(TextFormatting.DARK_GREEN + "test 1");
        list.add(TextFormatting.DARK_GREEN + "test 2");
        list.add(TextFormatting.DARK_GREEN + "test 3");
        return list;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        speed = tagCompound.getFloat("speed");
        speedBoost = tagCompound.getInteger("boost");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setFloat("speed", speed);
        tagCompound.setInteger("boost", speedBoost);
        return super.writeToNBT(tagCompound);
    }

    @Nonnull
    @Override
    public <T> List<T> executeWithResultList(String command, Map<String, Argument> args, Type<T> type) {
        List<T> rc = super.executeWithResultList(command, args, type);
        if (!rc.isEmpty()) {
            return rc;
        }
        if (PacketGetMonitorText.CMD_GETMESSAGES.equals(command)) {
            return type.convert(getMessages());
        }
        return rc;
    }

    @Override
    public <T> boolean execute(String command, List<T> list, Type<T> type) {
        boolean rc = super.execute(command, list, type);
        if (rc) {
            return true;
        }
        if (PacketGetMonitorText.CLIENTCMD_GETMESSAGES.equals(command)) {
            clientHudLog = Type.STRING.convert(list);
            return true;
        }
        return false;
    }

}
