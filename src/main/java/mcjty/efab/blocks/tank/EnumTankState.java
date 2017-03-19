package mcjty.efab.blocks.tank;

import net.minecraft.util.IStringSerializable;

public enum EnumTankState implements IStringSerializable {
    FULL("full"),
    BOTTOM("bottom"),
    TOP("top"),
    MIDDLE("middle");

    private final String name;

    EnumTankState(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
