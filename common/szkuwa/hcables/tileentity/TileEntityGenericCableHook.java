package szkuwa.hcables.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import szkuwa.hcables.HCables;
import szkuwa.hcables.packet.DisconnectCablePacket;
import szkuwa.hcables.packet.TileEntityUpdatePacket;
import szkuwa.hcables.utils.ConnectionType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityGenericCableHook extends TileEntity {
	public List<CableConnection> connections = new ArrayList<CableConnection>();
	private CableConnection powerSource;
	private int maxConnections = 8;
	private double battery = 0;
	private double batteryUsage = 1.0 / 20;
	private double batteryMinimumShare = 0.5;
	private double batterySize = 4;
	private boolean running = false;
	private boolean oldRunning = false;
	
	public class CableConnection {
		public int x;
		public int y;
		public int z;
		public ConnectionType type;
		
		public CableConnection(int ax, int ay, int az, ConnectionType atype){
			this.x = ax;
			this.y = ay;
			this.z = az;
			this.type = atype;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CableConnection other = (CableConnection) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (type != other.type && type != ConnectionType.ANY)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			if (z != other.z)
				return false;
			return true;
		}

		private TileEntityGenericCableHook getOuterType() {
			return TileEntityGenericCableHook.this;
		}
	}
	
	// collection specific actions
	
	public boolean exists(int x, int y, int z){
		return exists(x, y, z, ConnectionType.ANY);
	}
	
	public boolean exists(int x, int y, int z, ConnectionType type){
		return connections.contains(new CableConnection(x, y, z, type));
	}
	
	public boolean addConnection(ConnectionType type, TileEntity entity){
		if (connections.size() >= maxConnections){
			return false;
		}
		
		if (!(entity instanceof TileEntityGenericCableHook)){
			// we won't be connecting something that we can't be sure we can handle
			return false;
		}
		
		if (this.xCoord == entity.xCoord && this.yCoord == entity.yCoord && this.zCoord == entity.zCoord){
			// connecting the same entity shouldn't work
			return false;
		}
		
		if (exists(entity.xCoord, entity.yCoord, entity.zCoord)){
			// trying to add connection that already exists (doesn't matter if its inbound or outbound, two points can be connected only once)
			return false;
		}
		
		((TileEntityGenericCableHook)entity).markForUpdate();
		return connections.add(new CableConnection(entity.xCoord, entity.yCoord, entity.zCoord, type));
	}
	
	public boolean removeConnection(TileEntity entity){
		return removeConnection(entity, ConnectionType.ANY);
	}
	
	public boolean removeConnection(TileEntity entity, ConnectionType type){
		return removeConnection(entity.xCoord, entity.yCoord, entity.zCoord, type);
	}
	
	public boolean removeConnection(int x, int y, int z){
		return removeConnection(x, y, z, ConnectionType.ANY);
	}
	
	public boolean removeConnection(int x, int y, int z, ConnectionType type){
		TileEntity te = worldObj.getTileEntity(x, y, z);
		if (te instanceof TileEntityGenericCableHook){
			((TileEntityGenericCableHook) te).markForUpdate();
		}
		CableConnection item = new CableConnection(x, y, z, type);
		return connections.remove(item);
	}
	
	public void disconnectFromAll(){
		Iterator<CableConnection> it = connections.iterator();
		while (it.hasNext()){
			CableConnection item = it.next();
			TileEntity te = worldObj.getTileEntity(item.x, item.y, item.z);
			if (te instanceof TileEntityGenericCableHook){ // sanity check
				TileEntityGenericCableHook entity = (TileEntityGenericCableHook)te;
				entity.removeConnection(this.xCoord, this.yCoord, this.zCoord);
				if (!worldObj.isRemote){
					// for each connection update client-side
					HCables.packetPipeline.sendToDimension(new DisconnectCablePacket(item.x, item.y, item.z, this.xCoord, this.yCoord, this.zCoord), worldObj.provider.dimensionId);
				}
			}
		}
		connections.clear();
	}
	
	public boolean connectEntities(TileEntityGenericCableHook source, TileEntityGenericCableHook destination){
		if (source.addConnection(ConnectionType.OUTBOUND, destination) && destination.addConnection(ConnectionType.INBOUND, source)){
			return true;
		} else {
			source.removeConnection(destination, ConnectionType.OUTBOUND);
			destination.removeConnection(source, ConnectionType.INBOUND);
		}
		return false;
	}
	
	private CableConnection searchForPowerSource(){
		if (powerSource != null){
			return powerSource;
		}
		if (connections.size() > 0){
			CableConnection conn = null;
			Iterator<CableConnection> it = connections.iterator();
			while (it.hasNext()){
				CableConnection item = it.next();
				TileEntity te = worldObj.getTileEntity(item.x, item.y, item.z);
				if (te instanceof TileEntityGenericCableHook){
					conn = ((TileEntityGenericCableHook) te).searchForPowerSource();
					if (conn != null){
						return conn;
					}
				}
			}
		}
		return null;
	}
	
	public void invalidateNetwork(){
		// this will be heavy... but shouldn't be called too much
		powerSource = searchForPowerSource();
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
	
	public String listConnections(){
		String result = "Total: " + connections.size();
		Iterator<CableConnection> it = connections.iterator();
		while (it.hasNext()){
			CableConnection item = it.next();
			result += "[ " + item.type + " " + item.x + " / " + item.y + " / " + item.z + "] ";
		}
		return result;
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		if (!worldObj.isRemote){
			battery -= batteryUsage;
			
			if (battery <= 0){
				// nothing to do.
				battery = 0;
			} else {
				// do we have some additional power left to share?
				if (battery >= batteryMinimumShare){
					Iterator<CableConnection> it = connections.iterator();
					while (it.hasNext()){
						CableConnection item = it.next();
						// only for outgoing cables so we don't end up sharing energy with entity that give it us in the same tick
						if (item.type == ConnectionType.OUTBOUND){
							TileEntity te = worldObj.getTileEntity(item.x, item.y, item.z);
							if (te instanceof TileEntityGenericCableHook){
								TileEntityGenericCableHook entity = (TileEntityGenericCableHook)te;
								double space = entity.batterySize - entity.battery;
								double powerToShare = Math.min(space, battery / 2);
								
								if (powerToShare > 0 && powerToShare >= batteryMinimumShare){
									entity.battery += powerToShare;
									battery -= powerToShare;
								}
							}
						}
						
						if (battery < batteryMinimumShare){
							// not enough power
							break;
						}
					}
				}
			}
			
			running = battery > 0;
			if (running != oldRunning){
				markForUpdate();
				oldRunning = running;
			}
		}
	}
	
	public double getBattery(){
		return battery;
	}
	
	public double getBetterySize(){
		return batterySize;
	}
	
	public void addBatteryPower(double power){
		battery += power;
		if (battery > batterySize){
			battery = batterySize;
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		NBTTagList nbtList = new NBTTagList();
		
		Iterator<CableConnection> it = connections.iterator();
		while (it.hasNext()){
			CableConnection item = it.next();
			NBTTagCompound nbtCompound = new NBTTagCompound();
			nbtCompound.setInteger("x", item.x);
			nbtCompound.setInteger("y", item.y);
			nbtCompound.setInteger("z", item.z);
			nbtCompound.setInteger("type", item.type.ordinal());
			nbtList.appendTag(nbtCompound);
		}
		nbt.setTag("connections", nbtList);
		nbt.setDouble("battery", battery);
		nbt.setBoolean("running", running);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		if (nbt.hasKey("battery")){
			battery = nbt.getDouble("battery");
		}
		if (nbt.hasKey("running")){
			running = nbt.getBoolean("running");
		}
		if (nbt.hasKey("connections")){
			//disconnectFromAll();
			connections.clear();
			ConnectionType[] types = ConnectionType.values();
			NBTTagList list = (NBTTagList)nbt.getTag("connections");
			for (int i = 0; i < list.tagCount(); i++){
				NBTTagCompound tag = list.getCompoundTagAt(i);
				ConnectionType type = types[tag.getInteger("type")];
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				if (!exists(x, y, z, type)){
					connections.add(new CableConnection(x, y, z, type));
				}
			}
		}
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
