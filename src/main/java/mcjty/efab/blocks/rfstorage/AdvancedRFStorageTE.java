package mcjty.efab.blocks.rfstorage;

import mcjty.efab.config.ConfigSetup;

public class AdvancedRFStorageTE extends RFStorageTE {

    @Override
    protected int getMaxStorage() {
        return ConfigSetup.advancedRfStorageMax.get();
    }

    @Override
    public int getMaxInternalConsumption() {
        return ConfigSetup.advancedRfStorageInternalFlow.get();
    }
}
