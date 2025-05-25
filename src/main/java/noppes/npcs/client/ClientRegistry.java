package noppes.npcs.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import mantle.lib.client.MantleClientRegistry;


public class ClientRegistry {
     public static Map<String, ItemStack> manualIcons = new HashMap<>();
	 public static void registerManualModifier(String name, ItemStack output, ItemStack topinput) {
	        registerManualModifier(name, output, topinput, null);
	    }

	    public static void registerManualModifier(String name, ItemStack output, ItemStack topinput,
	            ItemStack bottominput) {
	        ItemStack[] recipe = new ItemStack[3];
	        //recipe[0] = ModifyBuilder.instance.modifyItem(output, new ItemStack[] { topinput, bottominput }); // ToolBuilder.instance.buildTool(output,
	                                                                                                          // topinput,
	                                                                                                          // bottominput,
	                                                                                                          // "");
	        recipe[1] = topinput;
	        recipe[2] = bottominput;
	        MantleClientRegistry.recipeIcons.put(name, recipe);
	    }

	    public static void registerManualModifier(String name, ItemStack output, ItemStack input1, ItemStack input2,
	            ItemStack input3) {
	        registerManualModifier(name, output, input1, input2, input3, null);
	    }

	    public static void registerManualModifier(String name, ItemStack output, ItemStack input1, ItemStack input2,
	            ItemStack input3, ItemStack input4) {
	        ItemStack[] recipe = new ItemStack[5];
	        //recipe[0] = ModifyBuilder.instance.modifyItem(output, new ItemStack[] { input1, input2, input3, input4 });
	        recipe[1] = input1;
	        recipe[2] = input2;
	        recipe[3] = input3;
	        recipe[4] = input4;
	        MantleClientRegistry.recipeIcons.put(name, recipe);
	    }

	    public static void registerManualSmeltery(String name, ItemStack output, ItemStack liquid, ItemStack cast) {
	        ItemStack[] recipe = new ItemStack[3];
	        recipe[0] = output;
	        recipe[1] = liquid;
	        recipe[2] = cast;
	        MantleClientRegistry.recipeIcons.put(name, recipe);
	    }
	    
	    


}
