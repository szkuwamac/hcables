package szkuwa.hcables.gui;

import szkuwa.hcables.HCables;
import szkuwa.hcables.tileentity.TileEntityGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {

	public GuiHandler() {
		
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		
		switch (ID){
			case HCables.guiIdGenerator: {
				if (te instanceof TileEntityGenerator){
					return new ContainerGenerator(player.inventory, (TileEntityGenerator)te);
				}
				break;
			}
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		
		switch (ID){
			case HCables.guiIdGenerator: {
				if (te instanceof TileEntityGenerator){
					return new GuiGenerator(player.inventory, (TileEntityGenerator)te);
				}
				break;
			}
		}
		
		return null;
	}

}
