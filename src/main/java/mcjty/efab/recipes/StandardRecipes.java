package mcjty.efab.recipes;

import com.google.gson.*;
import mcjty.lib.tools.ItemStackTools;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StandardRecipes {

    public static void init() {
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(Blocks.FURNACE),
                "ccc", "c c", "ccc", 'c', Blocks.COBBLESTONE));

        RecipeManager.registerRecipe(new EFabShapelessRecipe(
                new ItemStack(Items.GLOWSTONE_DUST),
                "r", 'r', Items.REDSTONE)
                .tier(RecipeTier.RF)
                .rfPerTick(10)
                .time(100));

        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(Blocks.PISTON),
                "ppp", "crc", "cic", 'p', "plankWood", 'c', "cobblestone", 'r', Items.REDSTONE, 'i', "ingotIron")
                .time(20)
                .tier(RecipeTier.GEARBOX));

        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(Blocks.OBSIDIAN),
                "ccc", "ccc", "ccc", 'c', "cobblestone")
                .time(60)
                .tier(RecipeTier.STEAM));

        RecipeManager.registerRecipe(new EFabShapelessRecipe(
                new ItemStack(Items.WATER_BUCKET),
                "b", 'b', Items.BUCKET)
                .time(40)
                .tier(RecipeTier.LIQUID)
                .fluid(new FluidStack(FluidRegistry.WATER, 1000)));
        RecipeManager.registerRecipe(new EFabShapelessRecipe(
                new ItemStack(Items.LAVA_BUCKET),
                "b", 'b', Items.BUCKET)
                .time(40)
                .tier(RecipeTier.LIQUID)
                .fluid(new FluidStack(FluidRegistry.LAVA, 1000)));
    }

    public static void readRecipesConfig(File file) {
        if (!file.exists()) {
            Logging.log("Created file " + file.getName());
            writeExample(file);
            return;
        }
        FileInputStream inputstream;
        try {
            inputstream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Logging.log("Note: No file " + file.getName());
            return;
        }
        readRecipesFromStream(inputstream, file.getName());
    }

    private static JsonObject fluidStackToJson(FluidStack fluid) {
        JsonObject object = new JsonObject();
        object.add("fluid", new JsonPrimitive(fluid.getFluid().getName()));
        object.add("amount", new JsonPrimitive(fluid.amount));
        if (fluid.tag != null) {
            String string = fluid.tag.toString();
            object.add("nbt", new JsonPrimitive(string));
        }
        return object;
    }

    private static JsonObject itemStackToJson(ItemStack item) {
        JsonObject object = new JsonObject();
        object.add("item", new JsonPrimitive(item.getItem().getRegistryName().toString()));
        if (ItemStackTools.getStackSize(item) != 1) {
            object.add("amount", new JsonPrimitive(ItemStackTools.getStackSize(item)));
        }
        if (item.getItemDamage() != 0) {
            object.add("meta", new JsonPrimitive(item.getItemDamage()));
        }
        if (item.hasTagCompound()) {
            String string = item.getTagCompound().toString();
            object.add("nbt", new JsonPrimitive(string));
        }
        return object;
    }

    private static void writeExample(File file) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            Logging.logError("Error writing " + file.getName());
            return;
        }

        JsonArray array = new JsonArray();

        System.out.println("StandardRecipes.writeExample: #################");
        RecipeManager.getShapedRecipes().forEachRemaining(recipe -> {
            System.out.println("StandardRecipes.writeExample: 1");
            array.add(recipeToJson(recipe));
        });
        RecipeManager.getShapelessRecipes().forEachRemaining(recipe -> {
            System.out.println("StandardRecipes.writeExample: 2");
            array.add(recipeToJson(recipe));
        });

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        writer.print(gson.toJson(array));

        writer.close();
    }

    private static JsonObject recipeToJson(IEFabRecipe recipe) {
        JsonObject obj = new JsonObject();
        obj.add("type", new JsonPrimitive(recipe instanceof EFabShapedRecipe ? "shaped" : "shapeless"));
        obj.add("output", itemStackToJson(recipe.cast().getRecipeOutput()));

        List<String> inputs = recipe.getInputs();
        JsonArray inputArray = new JsonArray();
        for (String input : inputs) {
            inputArray.add(new JsonPrimitive(input));
        }
        obj.add("input", inputArray);

        Map<String, Object> inputMap = recipe.getInputMap();
        JsonObject inputMapObject = new JsonObject();
        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            JsonElement element;
            Object value = entry.getValue();
            if (value instanceof String) {
                element = new JsonPrimitive((String) value);
            } else if (value instanceof ItemStack) {
                element = itemStackToJson((ItemStack) value);
            } else if (value instanceof Item) {
                element = itemStackToJson(new ItemStack((Item) value));
            } else if (value instanceof Block) {
                element = itemStackToJson(new ItemStack((Block) value));
            } else {
                element = new JsonPrimitive("ERROR");
            }
            inputMapObject.add(entry.getKey(), element);
        }
        obj.add("inputmap", inputMapObject);
        obj.add("time", new JsonPrimitive(recipe.getCraftTime()));

        Set<RecipeTier> tiers = recipe.getRequiredTiers();
        if (!tiers.isEmpty()) {
            JsonArray tierArray = new JsonArray();
            for (RecipeTier tier : tiers) {
                tierArray.add(new JsonPrimitive(tier.name()));
            }
            obj.add("tiers", tierArray);
        }

        if (recipe.getRequiredRfPerTick() > 0) {
            obj.add("rfpertick", new JsonPrimitive(recipe.getRequiredRfPerTick()));
        }

        Collection<FluidStack> fluids = recipe.getRequiredFluids();
        if (!fluids.isEmpty()) {
            JsonArray fluidArray = new JsonArray();
            for (FluidStack fluid : fluids) {
                fluidArray.add(fluidStackToJson(fluid));
            }
            obj.add("fluids", fluidArray);
        }

        return obj;
    }

    private static void readRecipesFromStream(InputStream inputstream, String name) {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Logging.logError("Error reading file: " + name);
            return;
        }
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(br);
        for (JsonElement entry : element.getAsJsonArray()) {
            readRecipe(entry);
        }
    }

    private static void readRecipe(JsonElement ruleElement) {
        JsonElement output = ruleElement.getAsJsonObject().get("output");
        JsonElement input = ruleElement.getAsJsonObject().get("input");
    }


}
