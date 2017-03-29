package mcjty.efab.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import java.util.*;

public class EFabShapedRecipe extends ShapedOreRecipe implements IEFabRecipe {

    @Nonnull private final Set<RecipeTier> requiredTiers = EnumSet.noneOf(RecipeTier.class);
    @Nonnull private final List<FluidStack> requiredFluids = new ArrayList<>();
    private int requiredRfPerTick = 0;
    private int craftTime = 0;

    private List<String> inputs = new ArrayList<>();
    private Map<String,Object> inputMap = new HashMap<>();

    public EFabShapedRecipe(ItemStack output, Object... recipe) {
        super(output, recipe);

        int idx = 0;
        while (idx < recipe.length) {
            Object o = recipe[idx];
            idx++;
            if (o instanceof String) {
                inputs.add((String) o);
            } else if (o instanceof Character) {
                Object item = recipe[idx];
                idx++;
                inputMap.put(String.valueOf(o), item);
            }
        }
    }

    @Override
    public List<List<ItemStack>> getInputLists() {
        List<List<ItemStack>> inputLists = new ArrayList<>();
        for (Object o : input) {
            if (o instanceof ItemStack) {
                inputLists.add(Collections.singletonList((ItemStack) o));
            } else if (o instanceof List) {
                inputLists.add((List) o);
            } else {
                System.out.println("WHAT?");
            }
        }

        return inputLists;
    }

    @Override
    public List<String> getInputs() {
        return inputs;
    }

    @Override
    public Map<String, Object> getInputMap() {
        return inputMap;
    }

    @Override
    public IEFabRecipe tier(RecipeTier tier) {
        requiredTiers.add(tier);
        return this;
    }

    @Override
    public IEFabRecipe fluid(FluidStack stack) {
        requiredFluids.add(stack);
        return this;
    }

    @Override
    public IEFabRecipe rfPerTick(int rf) {
        requiredRfPerTick = rf;
        return this;
    }

    @Override
    public IEFabRecipe time(int t) {
        craftTime = t;
        return this;
    }

    @Nonnull
    @Override
    public Collection<FluidStack> getRequiredFluids() {
        return requiredFluids;
    }

    @Override
    public int getRequiredRfPerTick() {
        return requiredRfPerTick;
    }

    @Override
    public int getCraftTime() {
        return craftTime;
    }

    @Nonnull
    @Override
    public Set<RecipeTier> getRequiredTiers() {
        return requiredTiers;
    }

    @Nonnull
    @Override
    public IRecipe cast() {
        return this;
    }
}
