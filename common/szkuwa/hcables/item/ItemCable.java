package szkuwa.hcables.item;

import java.util.List;

import codechicken.lib.vec.BlockCoord;

import szkuwa.hcables.HCables;
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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemCable extends Item {

	public static ItemCable create(){
		ItemCable item = new ItemCable();
		item.init();
		return item;
	}
	
	protected ItemCable(){
		super();
		setCreativeTab(CreativeTabs.tabTools);
		setUnlocalizedName("hcables.cable");
		setMaxStackSize(1);
		setMaxDamage(512);
	}
	
	protected void init(){
		GameRegistry.registerItem(this, "hcables.cable");
	}
	
	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player){
		if (itemStack.stackTagCompound == null){
			itemStack.stackTagCompound = new NBTTagCompound();
		}
		
		itemStack.stackTagCompound.setString("owner", player.getDisplayName());
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4){
		if (itemStack.stackTagCompound != null){
			String owner = itemStack.stackTagCompound.getString("owner");
			list.add(EnumChatFormatting.GRAY + "owner: " + owner);
			if (itemStack.stackTagCompound.getBoolean("selected")){
				list.add(EnumChatFormatting.GREEN + "Block X:" + itemStack.stackTagCompound.getInteger("x"));
				list.add(EnumChatFormatting.GREEN + "Block Y:" + itemStack.stackTagCompound.getInteger("y"));
				list.add(EnumChatFormatting.GREEN + "Block Z:" + itemStack.stackTagCompound.getInteger("z"));
			}
		}
	}
	
	public BlockCoord getSelected(ItemStack itemStack){
		if (itemStack != null && itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey("selected") && itemStack.stackTagCompound.getBoolean("selected")){
			NBTTagCompound tag = itemStack.stackTagCompound;
			BlockCoord bc = new BlockCoord(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
			return bc;
		}
		return null;
	}
	
	public void setSelected(ItemStack itemStack, int x, int y, int z, boolean is){
		if (itemStack != null && itemStack.stackTagCompound != null){
			NBTTagCompound tag = itemStack.stackTagCompound;
			tag.setBoolean("selected", is);
			tag.setInteger("x", x);
			tag.setInteger("y", y);
			tag.setInteger("z", z);
		}
	}
	
	public void setSelected(ItemStack itemStack, TileEntity entity){
		if (itemStack != null && itemStack.stackTagCompound != null){
			NBTTagCompound tag = itemStack.stackTagCompound;
			if (entity != null){
				setSelected(itemStack, entity.xCoord, entity.yCoord, entity.zCoord, true);
			} else {
				setSelected(itemStack, 0, 0, 0, false);
			}
		}
	}
	
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float offsetX, float offsetY, float offsetZ){
		
		Item hook = Item.getItemFromBlock(HCables.blockCableHook);		
		// lets try to place something from our inventory first
		for (int i = 0; i < player.inventory.mainInventory.length; i++){
			ItemStack stack = player.inventory.mainInventory[i];
			if (stack != null && stack.getItem() == hook){
				// its our cable hook, yosha! lets try to place it
				boolean placed = stack.tryPlaceItemIntoWorld(player, world, x, y, z, side, offsetX, offsetY, offsetZ);
				if (placed){
					if (stack.stackSize < 1){
						player.inventory.setInventorySlotContents(i, null);
					}
				}
			}
		}
		
		if (!world.isRemote){
			TileEntity thisTileEntity = world.getTileEntity(x, y, z);
			if (thisTileEntity instanceof TileEntityGenericCableHook){
				// if it's our generic cable hook we can proceed with selections or connections
				if (player.isSneaking()){
					// if we are shift+right clicking, select this tile and return
					setSelected(itemStack, thisTileEntity);
					return true;
				} else {
					// if we're not sneaking check if something was selected before on this tool
					BlockCoord bc = getSelected(itemStack);
					if (bc != null){
						// if it was, try to connect entities
						TileEntity lastSelected = world.getTileEntity(bc.x, bc.y, bc.z);
						if (lastSelected instanceof TileEntityGenericCableHook){
							// if it still is our entity try to connect
							boolean connected = ((TileEntityGenericCableHook) thisTileEntity).connectEntities((TileEntityGenericCableHook)lastSelected, (TileEntityGenericCableHook)thisTileEntity);
							if (connected){
								// if everything went fine, decrease tool durability
								itemStack.setItemDamage(itemStack.getItemDamage()+1);
							}
							// select this entity so we can chain connections 
							setSelected(itemStack, thisTileEntity);
							return true;
						}
					}
					return false;
				}
			}
		}
		
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		itemIcon = iconRegister.registerIcon("minecraft:apple_golden");
	}
}
