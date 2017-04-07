package mcjty.efab.blocks.crafter;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class CrafterContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_CRAFTINPUT = 0;            // 9 slots
    public static final int SLOT_CRAFTOUTPUT = 9;           // 3 slots
    public static final int SLOT_GHOSTOUT = SLOT_CRAFTOUTPUT + 3;             // 1 slot

    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            addSlotBox(new SlotDefinition(SlotType.SLOT_GHOST), CONTAINER_INVENTORY, SLOT_CRAFTINPUT, 23, 12, 3, 18, 3, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_CONTAINER), CONTAINER_INVENTORY, SLOT_CRAFTOUTPUT, 132, 12, 1, 18, 3, 18);
            addSlot(new SlotDefinition(SlotType.SLOT_GHOSTOUT), CONTAINER_INVENTORY, SLOT_GHOSTOUT, 95, 45);
            layoutPlayerInventorySlots(6, 97);
        }
    };

    public CrafterContainer(EntityPlayer player, IInventory inventory) {
        super(factory);
        addInventory(CONTAINER_INVENTORY, inventory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }
}
