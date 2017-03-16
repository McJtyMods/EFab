package mcjty.efab.blocks.grid;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class GridContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_CRAFTINPUT = 0;            // 9 slots
    public static final int SLOT_CRAFTOUTPUT = 9;           // 3 slots

    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            addSlotBox(new SlotDefinition(SlotType.SLOT_CONTAINER), CONTAINER_INVENTORY, SLOT_CRAFTINPUT, 42, 27, 3, 18, 3, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_CRAFTRESULT), CONTAINER_INVENTORY, SLOT_CRAFTOUTPUT, 114, 45, 1, 18, 3, 18);
            layoutPlayerInventorySlots(91, 157);
        }
    };

    public GridContainer(EntityPlayer player, IInventory inventory) {
        super(factory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }
}
