package szkuwa.hcables.tileentity;

import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.mj.MjBattery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityGenerator extends TileEntity implements ISidedInventory {

	@MjBattery(maxCapacity = 10000, maxReceivedPerCycle = 200)
	protected double power = 0;

	private ItemStack[] inventory = new ItemStack[1];
	private static final int[] accessibleSlots = new int[] { 0 };
	
	private int currentBurn;
	private int currentItemBurnTime; 

	@Override
	public void updateEntity() {
		boolean invChanged = false;
		
		if (this.currentBurn > 0){
			this.currentBurn--;
			this.power++;
		}
		
		if (!worldObj.isRemote) {
			if (this.currentBurn <= 0){
				this.currentItemBurnTime = this.currentBurn = TileEntityFurnace.getItemBurnTime(inventory[0]);
				
				if (this.currentBurn > 0){
					invChanged = true;
					if (this.inventory[0] != null){
						this.inventory[0].stackSize--;
						if (this.inventory[0].stackSize == 0){
							this.inventory[0] = null;
						}
					}
				}
			}
			
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if (te instanceof TileEntityGenericCableHook) {
					IBatteryObject bat = MjAPI.getMjBattery(te);
					if (bat != null) { // sanity check
						power -= bat.addEnergy(power);
					}
				}
			}
		}
		
		if (invChanged){
			// oninvchanged? huh
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net,
			S35PacketUpdateTileEntity packet) {
		this.readFromNBT(packet.func_148857_g());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setDouble("power", power);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasKey("power")) {
			this.power = nbt.getDouble("power");
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot >= 0 && slot < getSizeInventory()) {
			return inventory[slot];
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null){
			if (stack.stackSize <= amount){
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amount);
				if (getStackInSlot(slot).stackSize == 0){
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		inventory[slot] = itemStack;
		if (itemStack != null && itemStack.stackSize > itemStack.getMaxStackSize()){
			// i don't think this will be ever called... but who cares, it was on wiki http://www.minecraftforge.net/wiki/Containers_and_GUIs
			itemStack.stackSize = itemStack.getMaxStackSize();
		}
	}

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		// TODO get player distance etc?
		return true;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		if (slot == 0) {
			return TileEntityFurnace.isItemFuel(itemStack);
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return accessibleSlots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
		return this.isItemValidForSlot(slot, itemStack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
		return false;
	}
}
