package szkuwa.hcables.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import buildcraft.api.mj.BatteryObject;
import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.IOMode;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.mj.MjBattery;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import szkuwa.hcables.HCables;
import szkuwa.hcables.packet.DisconnectCablePacket;
import szkuwa.hcables.packet.TileEntityUpdatePacket;
import szkuwa.hcables.utils.CableConnection;
import szkuwa.hcables.utils.CableConnectionManager;
import szkuwa.hcables.utils.CableConnectionType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityGenericCableHook extends TileEntity {
	private int maxConnections = 8;
	
	@MjBattery(maxCapacity = 200, maxReceivedPerCycle = 200, minimumConsumption = 0, mode = IOMode.Both)
	protected double power;
	protected double powerUsage = 0.05;
	public IBatteryObject battery = MjAPI.getMjBattery(this);
	
	private boolean running = false;
	private boolean oldRunning = false;
	public CableConnectionManager manager;
	
	public TileEntityGenericCableHook(){
		manager = new CableConnectionManager(this);
	}
	
	public boolean hasRedstonePower(){
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}
	
	public int getMaxConnections(){
		return maxConnections;
	}
	
	public CableConnectionManager xgetConnectionManager(){
		return manager;
	}
	
	public void markForUpdate(){
		markForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}
	
	public void markForUpdate(int x, int y, int z){
		this.worldObj.markBlockForUpdate(x, y, z);
		this.worldObj.markBlockRangeForRenderUpdate(x-1, y-1, z-1, x+1, y+1, z+1);
		
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		HCables.packetPipeline.sendToDimension(new TileEntityUpdatePacket(x, y, z, nbt), worldObj.provider.dimensionId);
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		if (!worldObj.isRemote){
			power -= powerUsage;
			if (power <= 0){
				power = 0;
			} else {
			
				// share energy only if not turned off by redstone
				if (!hasRedstonePower()){
					int destinations = 0;
					double energyToShare = 0;
					
					// get how much energy we are requested to share among our connections
					IBatteryObject[] batteries = manager.getBatteries();
					for (IBatteryObject bat : batteries){
						if (bat != null){
							destinations++;
							energyToShare += bat.getEnergyRequested();
						}
					}
					
					if (destinations == 0){
						// nothing is connected or network is invalid?
						// also divby0 if not checked here
						return;
					}
					
					// compute how much we actually can share, per connection
					energyToShare = Math.min(energyToShare, power - 1);
					energyToShare /= destinations;
					double overflowFromPrevious = 0;
					double energyUsed = 0;
					double energy = 0;
					for (IBatteryObject bat : batteries){
						if (bat != null){
							energy = energyToShare + overflowFromPrevious;
							energyUsed = bat.addEnergy(energy);
							power -= energyUsed;
							//overflowFromPrevious = energy - energyUsed;
						}
					}
				}
				
			}
			
			running = power > 0;
			if (running != oldRunning){
				markForUpdate();
				oldRunning = running;
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		manager.writeToNBT(nbt);
		nbt.setDouble("power", power);
		nbt.setBoolean("running", running);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		if (nbt.hasKey("power")){
			power = nbt.getDouble("power");
		}
		if (nbt.hasKey("running")){
			running = nbt.getBoolean("running");
		}
		manager.readFromNBT(nbt);
	}
	
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        return bb;
    }
	
	public boolean isRunning(){
		return running;
	}
}
