package szkuwa.hcables;

import szkuwa.hcables.block.BlockCableHookWithLight;
import szkuwa.hcables.gui.GuiHandler;
import szkuwa.hcables.item.ItemCable;
import szkuwa.hcables.tileentity.TileEntityCableHook;
import szkuwa.hcables.tileentity.TileEntityGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	public void registerRecipes(){
		ItemStack cobble = new ItemStack(Blocks.cobblestone);
		ItemStack iron = new ItemStack(Blocks.iron_bars);
		ItemStack glass = new ItemStack(Blocks.glass_pane);
		
		GameRegistry.addRecipe(new ItemStack(HCables.blockCableHook, 32),
					"ccc",
					"cgc",
					"cic",
					'c', cobble,
					'g', glass,
					'i', iron
				);
		GameRegistry.addRecipe(new ItemStack(HCables.itemCableTool),
					" h",
					"s ",
					'h', new ItemStack(HCables.blockCableHook),
					's', new ItemStack(Items.stick)
				);
		GameRegistry.addRecipe(new ItemStack(HCables.itemCable),
					"iii",
					"iii",
					'i', iron
				);
		GameRegistry.addRecipe(new ItemStack(HCables.blockGeneratorBC),
					"i i",
					" h ",
					"i i",
					'h', new ItemStack(HCables.blockCableHook),
					'i', iron
				);
	}
	
	public CommonProxy(){
		// stub
	}
	
	public void load(){
		NetworkRegistry.INSTANCE.registerGuiHandler(HCables.instance, new GuiHandler());
		
		GameRegistry.registerTileEntity(TileEntityCableHook.class, "tileEntityCableHook");
	}
}
