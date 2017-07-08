package mcjty.efab.recipes;

import com.google.gson.*;
import mcjty.efab.EFab;
import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.compat.botania.BotaniaSupportSetup;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.*;
import java.util.*;

public class StandardRecipes {

    public static void init() {

//        GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.gridBlock, " i ", "ici", " i ", 'i', "ingotIron", 'c', Blocks.CRAFTING_TABLE));
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.baseBlock, 4), " i ", "isi", " i ", 'i', "ingotIron", 's', Blocks.STONE));

        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.gearBoxBlock),
                "iri", "rbr", "iri", 'i', "ingotIron", 'r', Items.REDSTONE, 'b', ModBlocks.baseBlock));
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.boilerBlock),
                "ici", "ibi", "iii", 'i', "ingotIron", 'b', ModBlocks.baseBlock, 'c', Blocks.CAULDRON)
                .tier(RecipeTier.GEARBOX)
                .time(20));
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.tankBlock),
                "iii", "i i", "iii", 'i', "ingotIron")
                .tier(RecipeTier.GEARBOX)
                .time(10));
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.steamEngineBlock),
                "ici", "ibi", "ici", 'i', "ingotIron", 'b', ModBlocks.baseBlock, 'c', Blocks.PISTON)
                .tier(RecipeTier.GEARBOX)
                .time(20));
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.pipeBlock),
                "ii ", "ii ", "   ", 'i', "ingotIron")
                .tier(RecipeTier.GEARBOX)
                .time(10));
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.storageBlock),
                "iii", " c ", "iii", 'i', "ingotIron", 'c', Blocks.CHEST)
                .tier(RecipeTier.GEARBOX)
                .time(40));
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.crafterBlock),
                "iii", " c ", "iii", 'i', "ingotIron", 'c', Blocks.CRAFTING_TABLE)
                .tier(RecipeTier.GEARBOX)
                .time(40));
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.rfControlBlock),
                "rrr", "rbr", "rrr", 'r', Items.REDSTONE, 'b', ModBlocks.baseBlock)
                .tier(RecipeTier.STEAM)
                .time(100));
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.processorBlock),
                "rqr", "rbr", "rqr", 'r', Items.REDSTONE, 'b', ModBlocks.baseBlock, 'q', Items.QUARTZ)
                .tier(RecipeTier.RF)
                .rfPerTick(10)
                .time(100));
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.monitorBlock),
                "dqd", "qbq", "dqd", 'd', "dyeBlack", 'b', ModBlocks.baseBlock, 'q', Items.QUARTZ)
                .tier(RecipeTier.RF)
                .tier(RecipeTier.COMPUTING)
                .rfPerTick(10)
                .time(100));
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(ModBlocks.rfStorageBlock),
                "rRr", "RbR", "rRr", 'r', Items.REDSTONE, 'b', ModBlocks.baseBlock, 'R', Blocks.REDSTONE_BLOCK)
                .tier(RecipeTier.STEAM)
                .tier(RecipeTier.LIQUID)
                .fluid(new FluidStack(FluidRegistry.LAVA, 1000))
                .time(100));
        if (EFab.botania) {
            RecipeManager.registerRecipe(new EFabShapedRecipe(
                    new ItemStack(BotaniaSupportSetup.getManaReceptacle()),
                    "drr", "rbd", "rdr", 'r', Items.REDSTONE, 'b', ModBlocks.baseBlock, 'd', Items.DIAMOND)
                    .tier(RecipeTier.RF)
                    .rfPerTick(40)
                    .time(100));
            RecipeManager.registerRecipe(new EFabShapelessRecipe(
                    new ItemStack(Blocks.DRAGON_EGG),
                    "   ", " e ", "   ", 'e', Items.EGG)
                    .tier(RecipeTier.MANA)
                    .manaPerTick(20)
                    .time(200));
        }

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
                .tier(RecipeTier.STEAM)
                .tier(RecipeTier.LIQUID)
                .tier(RecipeTier.RF)
                .rfPerTick(10)
                .fluid(new FluidStack(FluidRegistry.WATER, 1000))
                .fluid(new FluidStack(FluidRegistry.LAVA, 1000)));

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
            Logging.log("efab_recipes.cfg does not exist, usting defaults");
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

    private static FluidStack jsonToFluidStack(JsonObject obj) {
        String fluidName = obj.get("fluid").getAsString();
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        // @todo error checking
        int amount = obj.get("amount").getAsInt();
        NBTTagCompound nbt = null;
        if (obj.has("nbt")) {
            try {
                nbt = JsonToNBT.getTagFromJson(obj.get("nbt").getAsString());
            } catch (NBTException e) {
                // @todo What to do?
            }
        }
        return new FluidStack(fluid, amount, nbt);
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

    private static ItemStack jsonToItemStack(JsonObject obj) {
        String itemName = obj.get("item").getAsString();
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
        // @todo error checking
        int amount = 1;
        if (obj.has("amount")) {
            amount = obj.get("amount").getAsInt();
        }
        int meta = 0;
        if (obj.has("meta")) {
            meta = obj.get("meta").getAsInt();
        }
        ItemStack stack = new ItemStack(item, amount, meta);
        if (obj.has("nbt")) {
            try {
                NBTTagCompound nbt = JsonToNBT.getTagFromJson(obj.get("nbt").getAsString());
                stack.setTagCompound(nbt);
            } catch (NBTException e) {
                // @todo What to do?
            }
        }
        return stack;
    }

    private static JsonObject itemStackToJson(ItemStack item) {
        JsonObject object = new JsonObject();
        object.add("item", new JsonPrimitive(item.getItem().getRegistryName().toString()));
        if (item.getCount() != 1) {
            object.add("amount", new JsonPrimitive(item.getCount()));
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

    public static void writeDefaults(File file) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            Logging.logError("Error writing " + file.getName());
            return;
        }

        JsonArray array = new JsonArray();

        RecipeManager.getRecipes().stream().forEach(recipe -> {
            array.add(recipeToJson(recipe));
        });

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        writer.print(gson.toJson(array));

        writer.close();
    }

    private static IEFabRecipe jsonToRecipe(JsonObject obj) {
        String type = obj.get("type").getAsString();
        boolean shaped = "shaped".equals(type.toLowerCase());

        List<Object> input = new ArrayList<>();

        JsonArray inputArray = obj.get("input").getAsJsonArray();
        for (int i = 0 ; i < inputArray.size() ; i++) {
            input.add(inputArray.get(i).getAsString());
        }

        JsonObject inputMapObject = obj.get("inputmap").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : inputMapObject.entrySet()) {
            input.add(entry.getKey().charAt(0));
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                input.add(value.getAsString());
            } else if (value.isJsonObject()) {
                input.add(jsonToItemStack(value.getAsJsonObject()));
            }
        }

        ItemStack output = jsonToItemStack(obj.get("output").getAsJsonObject());
        IEFabRecipe recipe;
        if (shaped) {
            recipe = new EFabShapedRecipe(output, input.toArray(new Object[input.size()]));
        } else {
            recipe = new EFabShapelessRecipe(output, input.toArray(new Object[input.size()]));
        }
        recipe.time(obj.get("time").getAsInt());
        if (obj.has("tiers")) {
            JsonArray tiersArray = obj.get("tiers").getAsJsonArray();
            for (JsonElement element : tiersArray) {
                RecipeTier tier = RecipeTier.valueOf(element.getAsString());
                recipe.tier(tier);
            }
        }
        if (obj.has("rfpertick")) {
            recipe.rfPerTick(obj.get("rfpertick").getAsInt());
        }
        if (obj.has("manapertick")) {
            recipe.manaPerTick(obj.get("manapertick").getAsInt());
        }
        if (obj.has("fluids")) {
            JsonArray fluidArray = obj.get("fluids").getAsJsonArray();
            for (JsonElement element : fluidArray) {
                FluidStack stack = jsonToFluidStack(element.getAsJsonObject());
                recipe.fluid(stack);
            }
        }

        return recipe;
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
        if (recipe.getRequiredManaPerTick() > 0) {
            obj.add("manapertick", new JsonPrimitive(recipe.getRequiredManaPerTick()));
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
        RecipeManager.clear();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(br);
        for (JsonElement entry : element.getAsJsonArray()) {
            readRecipe(entry);
        }
    }

    private static void readRecipe(JsonElement element) {
        IEFabRecipe recipe = jsonToRecipe(element.getAsJsonObject());
        RecipeManager.registerRecipe(recipe);
    }


}
