package szkuwa.hcables.block;

import szkuwa.hcables.tileentity.TileEntityCableHook;
import szkuwa.hcables.tileentity.TileEntityGeneratorBC;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGeneratorBC extends Block implements ITileEntityProvider {

	public static BlockGeneratorBC create() {
		BlockGeneratorBC item = new BlockGeneratorBC(Material.ground);
		item.init();
		return item;
	}

	protected BlockGeneratorBC(Material material) {
		super(material);
		setCreativeTab(CreativeTabs.tabTools);
		setBlockName("blockGeneratorBC");
		setBlockTextureName("minecraft:bedrock");
	}

	protected void init() {
		GameRegistry.registerBlock(this, "blockGeneratorBC");
		GameRegistry.registerTileEntity(TileEntityGeneratorBC.class, "tileEntityGeneratorBC");
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityGeneratorBC();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

}
