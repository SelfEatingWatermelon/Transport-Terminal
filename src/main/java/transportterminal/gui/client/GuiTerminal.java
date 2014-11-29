package transportterminal.gui.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import transportterminal.TransportTerminal;
import transportterminal.core.confighandler.ConfigHandler;
import transportterminal.gui.server.ContainerTerminal;
import transportterminal.network.message.EnergyMessage;
import transportterminal.network.message.PlayerChipMessage;
import transportterminal.network.message.TeleportMessage;
import transportterminal.tileentites.TileEntityTransportTerminal;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTerminal extends GuiContainer {

	private static final ResourceLocation GUI_TRANSPORTER = new ResourceLocation("transportterminal:textures/gui/transportTerminalGui.png");
	private final TileEntityTransportTerminal tile;

	public GuiTerminal(InventoryPlayer playerInventory, TileEntityTransportTerminal tile, int id) {
		super(new ContainerTerminal(playerInventory, tile, id));
		this.tile = tile;
		allowUserInput = false;
		ySize = 168;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int xOffSet = (width - xSize) / 2;
		int yOffSet = (height - ySize) / 2;
		for (int rowTop = 2; rowTop <= 8; ++rowTop)
			buttonList.add(new GuiButton(rowTop, xOffSet + 44 + rowTop * 18 - 36, yOffSet + 18, 16, 7, ""));
		for (int rowBottom = 9; rowBottom <= 15; ++rowBottom)
			buttonList.add(new GuiButton(rowBottom, xOffSet + 44 + rowBottom * 18 - 162, yOffSet + 63, 16, 7, ""));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		fontRendererObj.drawString(StatCollector.translateToLocal(tile.getInventoryName()), 8, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
		if (TransportTerminal.IS_RF_PRESENT)
			fontRendererObj.drawString(StatCollector.translateToLocal("RF: " + tile.getEnergyStored(ForgeDirection.UNKNOWN)), 100, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TRANSPORTER);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		int xx = tile.xCoord;
		int yy = tile.yCoord;
		int zz = tile.zCoord;

		if (guibutton instanceof GuiButton)
			if (guibutton.id >= 2 && guibutton.id <= 15) {
				if (tile.getStackInSlot(guibutton.id) != null && tile.getStackInSlot(guibutton.id).stackTagCompound.hasKey("chipX")) {
					int newDim = tile.getStackInSlot(guibutton.id).getTagCompound().getInteger("chipDim");
					int x = tile.getStackInSlot(guibutton.id).getTagCompound().getInteger("chipX");
					int y = tile.getStackInSlot(guibutton.id).getTagCompound().getInteger("chipY");
					int z = tile.getStackInSlot(guibutton.id).getTagCompound().getInteger("chipZ");

					if (tile.canTeleport()) {
						if (TransportTerminal.IS_RF_PRESENT)
							TransportTerminal.networkWrapper.sendToServer(new EnergyMessage(mc.thePlayer, xx, yy, zz));
						TransportTerminal.networkWrapper.sendToServer(new TeleportMessage(mc.thePlayer, x, y, z, newDim));
					}
				}

				if (tile.getStackInSlot(guibutton.id) != null && tile.getStackInSlot(guibutton.id).hasDisplayName())
					if (tile.canTeleport() && ConfigHandler.ALLOW_TELEPORT_TO_PLAYER)
						TransportTerminal.networkWrapper.sendToServer(new PlayerChipMessage(mc.thePlayer, tile.getStackInSlot(guibutton.id).getDisplayName(), xx, yy, zz));

				mc.thePlayer.closeScreen();
			}
	}
}