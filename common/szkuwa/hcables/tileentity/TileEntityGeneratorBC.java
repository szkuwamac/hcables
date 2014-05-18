package szkuwa.hcables.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import buildcraft.api.gates.IAction;
import buildcraft.api.mj.MjBattery;
import buildcraft.core.IMachine;

public class TileEntityGeneratorBC extends TileEntity implements IMachine {
	protected static double OUTPUT_PER_TICK = 8;
	protected static final int[][] neighbours = {
		{ 1, 0, 0 },
		{ 0, 1, 0 },
		{ 0, 0, 1 },
		{ -1, 0, 0 },
		{ 0, -1, 0 },
		{ 0, 0, -1 }
	};
	
	@MjBattery(maxCapacity = 10000, maxReceivedPerCycle = 200)
	protected double power = 0;
	
	public TileEntityGeneratorBC() {
		// stub
	}
	
	public double getEnergy(){
		return power;
	}
	
	public double getMaxEnergy(){
		return 10000;
	}
	
	@Override
	public void updateEntity(){
		if (!worldObj.isRemote){
			// only if we have enough energy
			if (power > OUTPUT_PER_TICK){
				int dx, dy, dz;
				for (int i = 0; i < neighbours.length; i++){
					dx = neighbours[i][0];
					dy = neighbours[i][1];
					dz = neighbours[i][2];
					TileEntity te = worldObj.getTileEntity(xCoord+dx, yCoord+dy, zCoord+dz);
					if (te instanceof TileEntityCableHook){
						TileEntityCableHook tech = (TileEntityCableHook)te;
						// if there is a space in receiver power
						if (tech.getBattery() < tech.getBetterySize()){
							// take output per tick or remaining power
							double avail = Math.min(this.power, OUTPUT_PER_TICK);
							// select power or available space in receiver whichever is less
							double insert = Math.min(avail, tech.getBetterySize() - tech.getBattery());
							
							// transmit energy
							tech.addBatteryPower(insert);
							this.power -= insert;
						}
						
						// break if there's nothing left
						if (this.power <= 0){
							break;
						}
					}
				}
			}
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
		nbt.setDouble("power", power);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasKey("power")){
			this.power = nbt.getDouble("power");
		}
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public boolean manageFluids() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean manageSolids() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowAction(IAction action) {
		// TODO Auto-generated method stub
		return false;
	}
}
