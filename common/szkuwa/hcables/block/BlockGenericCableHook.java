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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGenericCableHook extends Block implements ITileEntityProvider {
	
	public static BlockGenericCableHook create() {
		BlockGenericCableHook item = new BlockGenericCableHook(Material.glass);
		item.init();
		return item;
	}

	protected BlockGenericCableHook(Material material) {
		super(material);
		setCreativeTab(CreativeTabs.tabTools);
		setLightOpacity(0);
		setBlockName("blockCableHook");
		setBlockTextureName("minecraft:glass");
		setTickRandomly(true);
	}

	protected void init() {
		//GameRegistry.registerBlock(this, "blockCableHook");
		//GameRegistry.registerTileEntity(TileEntityCableHook.class, "tileEntityCableHook");
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return null;
	}
	
	@Override
	public int getRenderType()
    {
        return -1;
    }

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }
	
	@Override
	public void onBlockDestroyedByPlayer(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_, int p_149664_5_) {
		if (p_149664_1_.isRemote){
			TileEntity te = p_149664_1_.getTileEntity(p_149664_2_, p_149664_3_, p_149664_4_);
			if (te instanceof TileEntityGenericCableHook){
				TileEntityGenericCableHook ch = (TileEntityGenericCableHook)te;
				ch.manager.disconnectFromAll();
			}
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_){
		if (!world.isRemote){
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityGenericCableHook){
				TileEntityGenericCableHook ch = (TileEntityGenericCableHook)te;
				ch.manager.disconnectFromAll();
			}
		}
		super.breakBlock(world, x, y, z, block, p_149749_6_);
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        int j1 = metadata;

        /*// probably top size
        if (side == 1 && this.func_150107_m(world, x, y - 1, z))
        {
            j1 = 5;
        }
        */

        if (side == 2 && world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH, true))
        {
            j1 = 2;
        }

        if (side == 3 && world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH, true))
        {
            j1 = 3;
        }

        if (side == 4 && world.isSideSolid(x + 1, y, z, ForgeDirection.WEST, true))
        {
            j1 = 4;
        }

        if (side == 5 && world.isSideSolid(x - 1, y, z, ForgeDirection.EAST, true))
        {
            j1 = 5;
        }

        return j1;
    }
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return world.isSideSolid(x - 1, y, z, ForgeDirection.EAST,  true) ||
               world.isSideSolid(x + 1, y, z, ForgeDirection.WEST,  true) ||
               world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH, true) ||
               world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH, true);// || func_150107_m(world, x, y - 1, z);
    }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess ba, int x, int y, int z) {
		int meta = ba.getBlockMetadata(x, y, z);
		switch (meta){
			case 2: {
				// north side
				// minX, minY, minZ, maxX, maxY, maxZ
				
				setBlockBounds(0.25F, 0.25F, 0.90F, 0.75F, 0.75F, 1F);
				break;
			}
			case 3: {
				// south side
				setBlockBounds(0.25F, 0.25F, 0F, 0.75F, 0.75F, 0.10F);
				break;
			}
			case 4: {
				// west side
				setBlockBounds(0.90F, 0.25F, 0.25F, 1F, 0.75F, 0.75F);
				break;
			}
			case 5: {
				// east side
				setBlockBounds(0F, 0.25F, 0.25F, 0.10F, 0.75F, 0.75F);
				break;
			}
			default: setBlockBounds(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
		}
	}

}
