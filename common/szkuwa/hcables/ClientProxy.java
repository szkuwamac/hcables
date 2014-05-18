package szkuwa.hcables;

import net.minecraftforge.client.MinecraftForgeClient;
import szkuwa.hcables.tileentity.TileEntityCableHook;
import szkuwa.hcables.tileentity.TileEntityGeneratorBC;
import szkuwa.hcables.tileentity.renderer.TESRCableHook;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void load(){
		super.load();
		
		// register TESRs
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCableHook.class, new TESRCableHook());
	}
}
