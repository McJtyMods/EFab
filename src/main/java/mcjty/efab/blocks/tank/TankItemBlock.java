package mcjty.efab.blocks.tank;

import mcjty.lib.container.GenericItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
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
                        FluidStack tankFluid = ((TankTE) tedown).getFluid();
                        if (tankFluid != null && !fluid.isFluidEqual(tankFluid)) {
                            // Not compatible
                            if (world.isRemote) {
                                ITextComponent component = new TextComponentString(TextFormatting.RED + "Can't place this tank there. Fluids are not compatible!");
                                player.sendStatusMessage(component, false);
                            }
                            return false;
                        }
                    }
                    TileEntity teup = world.getTileEntity(pos.up());
                    if (teup instanceof TankTE) {
                        FluidStack tankFluid = ((TankTE) teup).getFluid();
                        if (tankFluid != null && !fluid.isFluidEqual(tankFluid)) {
                            if (world.isRemote) {
                                ITextComponent component = new TextComponentString(TextFormatting.RED + "Can't place this tank there. Fluids are not compatible!");
                                player.sendStatusMessage(component, false);
                            }
                            return false;
                        }
                    }
                }
            }
        }

        // Check if the tank above and below have the same fluid
        TileEntity tedown = world.getTileEntity(pos.down());
        TileEntity teup = world.getTileEntity(pos.up());
        if (tedown instanceof TankTE && teup instanceof TankTE && ((TankTE) tedown).isAdvanced() == ((TankTE) teup).isAdvanced()) {
            FluidStack fluidDown = ((TankTE) tedown).getFluid();
            FluidStack fluidUp = ((TankTE) teup).getFluid();
            if (fluidDown != null && fluidUp != null && !fluidDown.isFluidEqual(fluidUp)) {
                if (world.isRemote) {
                    ITextComponent component = new TextComponentString(TextFormatting.RED + "Can't place this tank there. Fluids are not compatible!");
                    player.sendStatusMessage(component, false);
                }
                return false;
            }
        }


        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }
}
