package szkuwa.hcables.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.MjAPI;

import scala.Array;
import szkuwa.hcables.HCables;
import szkuwa.hcables.packet.DisconnectCablePacket;
import szkuwa.hcables.tileentity.TileEntityGenericCableHook;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class CableConnectionManager {
	public List<CableConnection> connections = new ArrayList<CableConnection>();
	protected TileEntityGenericCableHook creator;

	public CableConnectionManager(TileEntityGenericCableHook entity){
		this.creator = entity;
	}
	

	public boolean exists(int x, int y, int z){
		return exists(x, y, z, CableConnectionType.ANY);
	}
	
	public boolean exists(int x, int y, int z, CableConnectionType type){
		return connections.contains(new CableConnection(x, y, z, type));
	}
	
	public boolean addConnection(TileEntityGenericCableHook entity, CableConnectionType type){
		if (!(entity instanceof TileEntityGenericCableHook)){
			// we won't be connecting something that we can't be sure we can handle
			return false;
		}
		
		if (entity.manager.connections.size() >= ((TileEntityGenericCableHook) entity).getMaxConnections()){
			return false;
		}
		
		if (creator.xCoord == entity.xCoord && creator.yCoord == entity.yCoord && creator.zCoord == entity.zCoord){
			// connecting the same entity shouldn't work
			return false;
		}
		
		if (entity.manager.exists(entity.xCoord, entity.yCoord, entity.zCoord)){
			// trying to add connection that already exists (doesn't matter if its inbound or outbound, two points can be connected only once)
			return false;
		}
		
		((TileEntityGenericCableHook)entity).markForUpdate();
		return connections.add(new CableConnection(entity.xCoord, entity.yCoord, entity.zCoord, type));
	}
	
	public boolean removeConnection(TileEntity entity){
		return removeConnection(entity, CableConnectionType.ANY);
	}
	
	public boolean removeConnection(TileEntity entity, CableConnectionType type){
		return removeConnection(entity.xCoord, entity.yCoord, entity.zCoord, type);
	}
	
	public boolean removeConnection(int x, int y, int z){
		return removeConnection(x, y, z, CableConnectionType.ANY);
	}
	
	public boolean removeConnection(int x, int y, int z, CableConnectionType type){
		TileEntity te = creator.getWorldObj().getTileEntity(x, y, z);
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
			TileEntity te = creator.getWorldObj().getTileEntity(item.x, item.y, item.z);
			if (te instanceof TileEntityGenericCableHook){ // sanity check
				TileEntityGenericCableHook entity = (TileEntityGenericCableHook)te;
				entity.manager.removeConnection(entity.xCoord, entity.yCoord, entity.zCoord);
				if (!entity.getWorldObj().isRemote){
					// for each connection update client-side
					HCables.packetPipeline.sendToDimension(new DisconnectCablePacket(item.x, item.y, item.z, entity.xCoord, entity.yCoord, entity.zCoord), entity.getWorldObj().provider.dimensionId);
				}
			}
		}
		connections.clear();
	}
	
	public static boolean connectEntities(TileEntityGenericCableHook source, TileEntityGenericCableHook destination){
		if (source.manager.addConnection(destination, CableConnectionType.OUTBOUND) && destination.manager.addConnection(source, CableConnectionType.INBOUND)){
			return true;
		} else {
			source.manager.removeConnection(destination, CableConnectionType.OUTBOUND);
			destination.manager.removeConnection(source, CableConnectionType.INBOUND);
		}
		return false;
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
	
	public IBatteryObject[] getBatteries(){
		IBatteryObject[] result = new IBatteryObject[connections.size()];
		Iterator<CableConnection> it = connections.iterator();
		int i = 0;
		while (it.hasNext()){
			CableConnection item = it.next();
			TileEntity te = creator.getWorldObj().getTileEntity(item.x, item.y, item.z);
			result[i] = MjAPI.getMjBattery(te);
			i++;
		}
		return result;
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
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
	}

	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("connections")){
			connections.clear();
			CableConnectionType[] types = CableConnectionType.values();
			NBTTagList list = (NBTTagList)nbt.getTag("connections");
			for (int i = 0; i < list.tagCount(); i++){
				NBTTagCompound tag = list.getCompoundTagAt(i);
				CableConnectionType type = types[tag.getInteger("type")];
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				if (!exists(x, y, z, type)){
					connections.add(new CableConnection(x, y, z, type));
				}
			}
		}
	}
}
