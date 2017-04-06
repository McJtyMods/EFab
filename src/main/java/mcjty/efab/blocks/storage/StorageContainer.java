package mcjty.efab.blocks.storage;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class StorageContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_STORAGE = 0;            // 3x9 slots

    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            addSlotBox(new SlotDefinition(SlotType.SLOT_CONTAINER), CONTAINER_INVENTORY, SLOT_STORAGE, 6, 20, 9, 18, 3, 18);
            layoutPlayerInventorySlots(6, 97);
        }
    };

    public StorageContainer(EntityPlayer player, IInventory inventory) {
        super(factory);
        addInventory(CONTAINER_INVENTORY, inventory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }
}
