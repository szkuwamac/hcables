package szkuwa.hcables.gui;

import szkuwa.hcables.tileentity.TileEntityGenerator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiGenerator extends GuiContainer {
	
	public TileEntityGenerator entity;
	public static final ResourceLocation background = new ResourceLocation("hcables", "gui/generator.png");
	public static final int backgroundWidth = 176;
	public static final int backgroundHeight = 166;
	public static final int playerInvHeight = 96;

	public GuiGenerator(InventoryPlayer inventoryPlayer, TileEntityGenerator entity) {
		super(new ContainerGenerator(inventoryPlayer, entity));
		this.entity = entity;
		
		this.xSize = backgroundWidth;
		this.ySize = backgroundHeight;
	}
	
	private void doDrawForeground(float v1, int v2, int v3){
		String name = "Generator";
		
		// i18n.func_135053_a
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
		this.fontRendererObj.drawString("container.inventory", 8, this.ySize - playerInvHeight + 2, 4210752);
	}
	
	private void doDrawBackground(float v1, int v2, int v3){
		this.mc.renderEngine.bindTexture(background);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		this.doDrawBackground(var1, var2, var3);
		this.doDrawForeground(var1, var2, var3);
	}

}
