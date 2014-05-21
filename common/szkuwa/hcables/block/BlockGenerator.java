package szkuwa.hcables.block;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import szkuwa.hcables.HCables;
import szkuwa.hcables.tileentity.TileEntityGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGenerator extends Block implements ITileEntityProvider{

	protected BlockGenerator(Material material) {
		super(material);
		setCreativeTab(CreativeTabs.tabTools);
		setBlockName("blockGenerator");
		setBlockTextureName("minecraft:brick");
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float offsetX, float offsetY, float offsetZ){
		if (!world.isRemote){
			//FMLNetworkHandler.openGui(player, HCables.instance, HCables.guiIdGenerator, world, x, y, z);
			player.openGui(HCables.instance, HCables.guiIdGenerator, world, x, y, z);
			return true;
		}
		
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityGenerator();
	}

	public static BlockGenerator create() {
		BlockGenerator item = new BlockGenerator(Material.glass);
		item.init();
		return item;
	}
	
	private void init(){
		GameRegistry.registerBlock(this, "blockGenerator");
		GameRegistry.registerTileEntity(TileEntityGenerator.class, "tileEntityGenerator");
	}

}
