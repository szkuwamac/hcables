package szkuwa.hcables.packet;

import szkuwa.hcables.tileentity.TileEntityGenericCableHook;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

public class DisconnectCablePacket extends AbstractPacket {
	private int x, y, z;
	private int dx, dy, dz;
	
	public DisconnectCablePacket(){}
	public DisconnectCablePacket(int x, int y, int z, int dx, int dy, int dz){
		this.x = x;
		this.y = y;
		this.z = z;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer buff = new PacketBuffer(buffer);
		buff.writeInt(x);
		buff.writeInt(y);
		buff.writeInt(z);
		buff.writeInt(dx);
		buff.writeInt(dy);
		buff.writeInt(dz);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer buff = new PacketBuffer(buffer);
		x = buff.readInt();
		y = buff.readInt();
		z = buff.readInt();
		dx = buff.readInt();
		dy = buff.readInt();
		dz = buff.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		TileEntity te = player.worldObj.getTileEntity(x, y, z);
		if (te instanceof TileEntityGenericCableHook){
			((TileEntityGenericCableHook) te).removeConnection(dx, dy, dz);
		}
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

}
