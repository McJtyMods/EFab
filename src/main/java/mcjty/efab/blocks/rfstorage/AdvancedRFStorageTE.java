package mcjty.efab.blocks.rfstorage;

import mcjty.efab.config.GeneralConfiguration;

public class AdvancedRFStorageTE extends RFStorageTE {

    @Override
    protected int getMaxStorage() {
        return GeneralConfiguration.advancedRfStorageMax;
    }

    @Override
    public int getMaxInternalConsumption() {
        return GeneralConfiguration.advancedRfStorageInternalFlow;
    }
}
