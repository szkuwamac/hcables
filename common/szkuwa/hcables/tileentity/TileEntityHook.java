package szkuwa.hcables.tileentity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry;

import buildcraft.api.gates.IAction;
import buildcraft.api.mj.MjBattery;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import szkuwa.hcables.HCables;
import szkuwa.hcables.block.BlockCableHookWithLight;
import szkuwa.hcables.block.BlockGeneratorBC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityHook extends TileEntityGenericCableHook {
	
	public TileEntityHook() {
		super();
		battery.reconfigure(10, 10, 0);
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		if (!worldObj.isRemote){
			
		}
	}
	
	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet){
		this.readFromNBT(packet.func_148857_g());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}
}
