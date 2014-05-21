package szkuwa.hcables.item;

import java.util.List;

import szkuwa.hcables.block.BlockCableHookWithLight;
import szkuwa.hcables.tileentity.TileEntityCableHook;
import szkuwa.hcables.tileentity.TileEntityGeneratorBC;
import szkuwa.hcables.tileentity.TileEntityGenericCableHook;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.tools.IToolWrench;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemCableTool extends Item{

	public static ItemCableTool create(){
		ItemCableTool item = new ItemCableTool();
		item.init();
		return item;
	}
	
	protected ItemCableTool(){
		super();
		setCreativeTab(CreativeTabs.tabTools);
		setUnlocalizedName("hcables.cableTool");
		setMaxStackSize(1);
	}
	
	protected void init(){
		GameRegistry.registerItem(this, "hcables.cableTool");
	}
	
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10){
		if (!world.isRemote){
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityGenericCableHook){
				TileEntityCableHook tech = (TileEntityCableHook)te;
				player.addChatMessage(new ChatComponentText("This cable:"));
				player.addChatMessage(new ChatComponentText("  is running = " + tech.isRunning()));
				player.addChatMessage(new ChatComponentText("  has battery = " + tech.battery.getEnergyStored() + " / " + tech.battery.maxCapacity()));
				player.addChatMessage(new ChatComponentText("  connections = " + tech.manager.listConnections()));
				player.addChatMessage(new ChatComponentText("  meta = " + tech.getBlockMetadata()));
			}
			if (te instanceof TileEntityGeneratorBC){
				TileEntityGeneratorBC tech = (TileEntityGeneratorBC)te;
				player.addChatMessage(new ChatComponentText("This generator energy: " + tech.getEnergy() + " / " + tech.getMaxEnergy()));
			}
			IBatteryObject bat = MjAPI.getMjBattery(te);
			if (bat != null){
				player.addChatMessage(new ChatComponentText("Bat: " + bat.getEnergyStored() + "/" + bat.maxCapacity()));
			}
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		itemIcon = iconRegister.registerIcon("minecraft:apple");
	}

}