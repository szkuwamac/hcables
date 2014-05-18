package szkuwa.hcables;

import szkuwa.hcables.block.BlockCableHook;
import szkuwa.hcables.block.BlockGeneratorBC;
import szkuwa.hcables.item.ItemCable;
import szkuwa.hcables.item.ItemCableTool;
import szkuwa.hcables.packet.DisconnectCablePacket;
import szkuwa.hcables.packet.PacketPipeline;
import szkuwa.hcables.packet.TileEntityUpdatePacket;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "hcables", useMetadata = true, dependencies = "required-after:BuildCraft|Core")
public class HCables {
	@Instance(value = "hcables")
	public static HCables instance;
	
	@SidedProxy(clientSide="szkuwa.hcables.ClientProxy", serverSide="szkuwa.hcables.CommonProxy")
	public static CommonProxy proxy;
	
	// Pipeline
	public static final PacketPipeline packetPipeline = new PacketPipeline();
	
	// Blocks
	public static BlockCableHook blockCableHook;
	public static BlockGeneratorBC blockGeneratorBC;
	
	// Items
	public static ItemCableTool itemCableTool;
	public static ItemCable itemCable;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// create blocks
		blockCableHook = BlockCableHook.create();
		blockGeneratorBC = BlockGeneratorBC.create();
		
		// create items
		itemCableTool = ItemCableTool.create();
		itemCable = ItemCable.create();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		packetPipeline.initialise();
		packetPipeline.registerPacket(DisconnectCablePacket.class);
		packetPipeline.registerPacket(TileEntityUpdatePacket.class);
		proxy.load();
		proxy.registerRecipes();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		packetPipeline.postInitialise();
	}
	
	// little helpers
	public static boolean isClient(){
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
	}
	
	public static boolean isServer(){
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
	}
}
