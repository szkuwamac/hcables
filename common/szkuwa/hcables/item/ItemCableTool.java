package szkuwa.hcables.item;

import java.util.List;

import codechicken.lib.vec.BlockCoord;

import szkuwa.hcables.block.BlockCableHook;
import szkuwa.hcables.tileentity.TileEntityCableHook;
import szkuwa.hcables.tileentity.TileEntityGeneratorBC;
import szkuwa.hcables.tileentity.TileEntityGenericCableHook;
import szkuwa.hcables.tileentity.TileEntityGenericCableHook.CableConnection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
				player.addChatMessage(new ChatComponentText("  has battery = " + tech.getBattery() + " / " + tech.getBetterySize()));
				player.addChatMessage(new ChatComponentText("  connections = " + tech.listConnections()));
				player.addChatMessage(new ChatComponentText("  meta = " + tech.getBlockMetadata()));
			}
			if (te instanceof TileEntityGeneratorBC){
				TileEntityGeneratorBC tech = (TileEntityGeneratorBC)te;
				player.addChatMessage(new ChatComponentText("This generator energy: " + tech.getEnergy() + " / " + tech.getMaxEnergy()));
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