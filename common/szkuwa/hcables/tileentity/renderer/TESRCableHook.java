package szkuwa.hcables.tileentity.renderer;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import szkuwa.hcables.HCables;
import szkuwa.hcables.item.ItemCable;
import szkuwa.hcables.tileentity.TileEntityGenericCableHook;
import szkuwa.hcables.tileentity.TileEntityGenericCableHook.CableConnection;
import szkuwa.hcables.utils.ConnectionType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class TESRCableHook extends TileEntitySpecialRenderer{
	private IModelCustom model;
	private ResourceLocation texture;
	
	private IModelCustom lightbulb;
	private ResourceLocation texL1;
	private ResourceLocation texL2;
	
	public TESRCableHook(){
		model = AdvancedModelLoader.loadModel(new ResourceLocation("hcables", "models/stone_cube.obj"));
		texture = new ResourceLocation("hcables", "textures/blocks/stone.png");
		
		lightbulb = AdvancedModelLoader.loadModel(new ResourceLocation("hcables", "models/lightbulb.obj"));
		texL1 = new ResourceLocation("hcables", "textures/blocks/bedrock.png");
		texL2 = new ResourceLocation("hcables", "textures/blocks/glass_orange.png");
	}
	
	private double getSideAdjustmentX(int side){
		switch (side){
			case 2: return 0.5;
			case 3: return 0.5;
			case 4: return 0.8;
			case 5: return 0.2;
			default: return 0;
		}
	}
	private double getSideAdjustmentY(int side){
		switch (side){
			case 2: return 0.3;
			case 3: return 0.3;
			case 4: return 0.3;
			case 5: return 0.3;
			default: return 0;
		}
	}
	private double getSideAdjustmentZ(int side){
		switch (side){
			case 2: return 0.8;
			case 3: return 0.2;
			case 4: return 0.5;
			case 5: return 0.5;
			default: return 0;
		}
	}
	
	private void drawHookModel(int side){ this.drawHookModel(side, 0, 0, 0); }
	private void drawHookModel(int side, double x, double y, double z){
		GL11.glTranslated(x, y, z);
		GL11.glTranslated(1, 0, 0);
		bindTexture(texture);
		
		// model specific transformation etc
		switch (side){
			case 2: {
				// north side
				GL11.glRotatef(-90F, 0F, 1F, 0F);
				GL11.glTranslated(1, 0, 0);
				model.renderAll();
				
				bindTexture(texL2);
				GL11.glTranslated(-0.22, 0.35, 0.5);
				lightbulb.renderAll();
				GL11.glTranslated(0.22, -0.35, -0.5);
				
				GL11.glTranslated(-1, 0, 0);
				GL11.glRotatef(90F, 0F, 1F, 0F);
				break;
			}
			case 3: {
				// south side
				GL11.glRotatef(90F, 0F, 1F, 0F);
				GL11.glTranslated(0, 0, -1);
				model.renderAll();
				
				bindTexture(texL2);
				GL11.glTranslated(-0.22, 0.35, 0.5);
				lightbulb.renderAll();
				GL11.glTranslated(0.22, -0.35, -0.5);
				
				GL11.glTranslated(0, 0, 1);
				GL11.glRotatef(-90F, 0F, 1F, 0F);
				break;
			}
			default:
			case 4: {
				// west side
				// nothing to do
				model.renderAll();
				
				bindTexture(texL2);
				GL11.glTranslated(-0.22, 0.35, 0.5);
				lightbulb.renderAll();
				GL11.glTranslated(0.22, -0.35, -0.5);
				
				break;
			}
			case 5: {
				// east side
				GL11.glRotatef(180F, 0F, 1F, 0F);
				GL11.glTranslated(1, 0, -1);
				model.renderAll();
				
				bindTexture(texL2);
				GL11.glTranslated(-0.22, 0.35, 0.5);
				lightbulb.renderAll();
				GL11.glTranslated(0.22, -0.35, -0.5);
				
				GL11.glTranslated(-1, 0, 1);
				GL11.glRotatef(-180F, 0F, -1F, 0F);
			}
		}
		
		GL11.glTranslated(-1, 0, 0);
		GL11.glTranslated(-x, -y, -z);
	}

	private double getBezierPoint(double t, double p0, double p1, double p2, double p3){
		double u = 1 - t;
		double uu = u*u;
		double uuu = uu*u;
		double tt = t*t;
		double ttt = tt*t;
		double result = p0 * uuu;
			result += p1 * 3 * uu * t;
			result += p2 * 3 * u * tt;
			result += p3 * ttt;
		return result;
	}
	private void drawBezierLine(double p0x, double p0y, double p0z, double p3x, double p3y, double p3z, double gravity, double step){
		double px, py, pz;
		GL11.glVertex3d(p0x, p0y, p0z);
		for (int i = 0; i <= step; i++){
			double t = i / step;
			px = getBezierPoint(t, p0x, p0x, p3x, p3x);
			py = getBezierPoint(t, p0y, p0y-gravity, p3y-gravity, p3y);
			pz = getBezierPoint(t, p0z, p0z, p3z, p3z);
			GL11.glVertex3d(px, py, pz);
		}
		GL11.glVertex3d(p3x, p3y, p3z);
	}
	
	private double getDistance(double p0x, double p0y, double p0z, double p1x, double p1y, double p1z){
		double x = p0x - p1x;
		double y = p0y - p1y;
		double z = p0z - p1z;
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	private void drawConnection(TileEntity source, TileEntity destination){ this.drawConnection(source.getBlockMetadata(), source.xCoord, source.yCoord, source.zCoord, destination.getBlockMetadata(), destination.xCoord, destination.yCoord, destination.zCoord); }
	private void drawConnection(int aside, double ax, double ay, double az, int bside, double bx, double by, double bz){
		double startx = getSideAdjustmentX(aside);
		double starty = getSideAdjustmentY(aside);
		double startz = getSideAdjustmentZ(aside);
		double endx = bx - ax + getSideAdjustmentX(bside);
		double endy = by - ay + getSideAdjustmentY(bside);
		double endz = bz - az + getSideAdjustmentZ(bside);
		
		GL11.glPushMatrix();
		boolean isAlphaTest = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
		float lineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		boolean isTexture2d = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
		
		if (isAlphaTest) { GL11.glDisable(GL11.GL_ALPHA_TEST); }
		if (isTexture2d) { GL11.glDisable(GL11.GL_TEXTURE_2D); }
		GL11.glLineWidth(3F);
		
		GL11.glBegin(GL11.GL_LINE_STRIP);
//			if (this.getDistance(startx, starty, startz, endx, endy, endz) < 10){
				GL11.glColor3f(0, 0.1F, 0.1F);
//			} else {
//				GL11.glColor3f(1F, 0.1F, 0.1F);
//			}
			this.drawBezierLine(startx, starty, startz, endx, endy, endz, 0.5, 30);
		GL11.glEnd();
		
		if (isAlphaTest) { GL11.glEnable(GL11.GL_ALPHA_TEST); }
		if (isTexture2d) { GL11.glEnable(GL11.GL_TEXTURE_2D); }
		GL11.glLineWidth(lineWidth);
		
		GL11.glPopMatrix();
	}
	
	private void drawConnections(TileEntity tileEntity){
		if (tileEntity instanceof TileEntityGenericCableHook){
			TileEntityGenericCableHook source = (TileEntityGenericCableHook)tileEntity;
			Iterator<CableConnection> it = source.connections.iterator();
			while (it.hasNext()){
				CableConnection item = it.next();
				TileEntity destination = source.getWorldObj().getTileEntity(item.x, item.y, item.z);
				if (destination instanceof TileEntityGenericCableHook && item.type == ConnectionType.OUTBOUND){
					this.drawConnection(source, destination);
				}
			}
		}
	}
	
	private void drawPlayerConnection(TileEntity tileEntity, float partialTick){
		// shit load of sanity checks ahead!
		if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null && !Minecraft.getMinecraft().thePlayer.isDead && tileEntity != null){
			EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
			// lets check what our player is holding in his hand
			if (player.inventory != null && player.inventory.getCurrentItem() != null){
				ItemStack stack = player.inventory.getCurrentItem();
				if (stack.getItem() == HCables.itemCable){
					// its our cable!
					NBTTagCompound tag = stack.stackTagCompound;
					if (tag != null && tag.getBoolean("selected")){
						int x = tag.getInteger("x");
						int y = tag.getInteger("y");
						int z = tag.getInteger("z");
						
						// now we know that player exists, we know he's holding our cable and that the cable has some entity stored...
						// lets see if this is our current entity
						if (tileEntity.xCoord == x && tileEntity.yCoord == y && tileEntity.zCoord == z){
							// it is... lets roll
							
							// lets check if our player is "watching" something
							MovingObjectPosition mop = player.rayTrace(10, partialTick);
							if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK && mop.sideHit >= 2 && mop.sideHit <= 5){
								// he is and he's looking at some kind of block on sides that are supported by our hooks
								// so lets try and draw a connection and hook model
								
								// but first translate x/y/z depending on side so we will draw on the surface of the block that we are looking at not inside it
								double dx = mop.blockX;
								double dy = mop.blockY;
								double dz = mop.blockZ;
								
								switch (mop.sideHit){
									case 2: { dz--; break; }
									case 3: { dz++; break; }
									case 4: { dx--; break; }
									case 5: { dx++; break; }
								}
								
								// lets first check if this place is suitable for our block
								if (!HCables.blockCableHook.canPlaceBlockAt(player.worldObj, (int)dx, (int)dy, (int)dz)){
									// can't place = can't draw.
									return;
								}
								
								this.drawHookModel(mop.sideHit, dx - tileEntity.xCoord, dy - tileEntity.yCoord, dz - tileEntity.zCoord);
								this.drawConnection(tileEntity.getBlockMetadata(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, mop.sideHit, dx, dy, dz);
							}
							
						}
					}
				}
			}
		}
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z); // lets set this so all our draw* will use (0,0,0) as starting point
		
		this.drawHookModel(tileEntity.getBlockMetadata());
		this.drawConnections(tileEntity);
		this.drawPlayerConnection(tileEntity, partialTick);
		
		GL11.glPopMatrix();
	}
	
}
