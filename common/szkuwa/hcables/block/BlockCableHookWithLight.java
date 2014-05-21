package szkuwa.hcables.block;

import buildcraft.api.gates.IAction;
import buildcraft.api.mj.MjBattery;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import szkuwa.hcables.tileentity.TileEntityCableHook;
import szkuwa.hcables.tileentity.TileEntityGenericCableHook;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCableHookWithLight extends BlockGenericCableHook {

	public static BlockCableHookWithLight create() {
		BlockCableHookWithLight item = new BlockCableHookWithLight(Material.rock);
		item.init();
		return item;
	}

	protected BlockCableHookWithLight(Material material) {
		super(material);
		setCreativeTab(CreativeTabs.tabTools);
		setLightOpacity(0);
		setBlockName("blockCableHookWithLight");
		setBlockTextureName("minecraft:glass");
		setTickRandomly(true);
	}

	protected void init() {
		GameRegistry.registerBlock(this, "blockCableHookWithLight");
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityCableHook();
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z){
		TileEntityCableHook tech = (TileEntityCableHook)world.getTileEntity(x, y, z);
		if (tech != null){
			if (tech.isRunning()){
				return 14;
			} else {
				return 0;
			}
		}
		return super.getLightValue(world, x, y, z);
	}

}
