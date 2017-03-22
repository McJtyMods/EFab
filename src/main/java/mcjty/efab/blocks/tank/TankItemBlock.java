package mcjty.efab.blocks.tank;

import mcjty.lib.container.GenericItemBlock;
import mcjty.lib.tools.ChatTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class TankItemBlock extends GenericItemBlock {

    public TankItemBlock(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        // If this stack has a fluid then we check if it is compatible with both the tank above and the tank below
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("fluid");
            if (!nbt.hasKey("Empty")) {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                if (fluid != null) {
                    TileEntity tedown = world.getTileEntity(pos.down());
                    if (tedown instanceof TankTE) {
                        if (!fluid.isFluidEqual(((TankTE) tedown).getFluid())) {
                            // Not compatible
                            ChatTools.addChatMessage(player, new TextComponentString(TextFormatting.RED + "Can't place this tank there. Fluids are not compatible!"));
                            return false;
                        }
                    }
                    TileEntity teup = world.getTileEntity(pos.up());
                    if (teup instanceof TankTE) {
                        if (!fluid.isFluidEqual(((TankTE) teup).getFluid())) {
                            ChatTools.addChatMessage(player, new TextComponentString(TextFormatting.RED + "Can't place this tank there. Fluids are not compatible!"));
                            return false;
                        }
                    }
                }
            }
        }

        // Check if the tank above and below have the same fluid
        TileEntity tedown = world.getTileEntity(pos.down());
        TileEntity teup = world.getTileEntity(pos.up());
        if (tedown instanceof TankTE && teup instanceof TankTE) {
            FluidStack fluidDown = ((TankTE) tedown).getFluid();
            FluidStack fluidUp = ((TankTE) teup).getFluid();
            if (fluidDown != null && fluidUp != null && !fluidDown.isFluidEqual(fluidUp)) {
                ChatTools.addChatMessage(player, new TextComponentString(TextFormatting.RED + "Can't place this tank there. Fluids are not compatible!"));
                return false;
            }
        }


        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }
}
