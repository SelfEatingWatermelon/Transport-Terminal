package transportterminal.tileentites;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import transportterminal.TransportTerminal;
import transportterminal.core.confighandler.ConfigHandler;

public class TileEntityTransportTerminal extends TileEntityInventoryEnergy {

	private String chipName = "Blank";
	private int tempSlot = 0;

	public TileEntityTransportTerminal() {
		super(ConfigHandler.TERMINAL_MAX_ENERGY, 16);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {
		inventory[slot] = is;
		if (is != null && is.stackSize > getInventoryStackLimit())
			is.stackSize = getInventoryStackLimit();
		if (is != null && slot == 0 && is.getItem() == TransportTerminal.remote || is != null && slot == 0 && is.getItem() == TransportTerminal.remoteTerminal) {
			ItemStack stack = is.copy();
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("homeX", pos.getX());
			stack.getTagCompound().setInteger("homeY", pos.getY());
			stack.getTagCompound().setInteger("homeZ", pos.getZ());
			setInventorySlotContents(1, stack);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		tempSlot = nbt.getInteger("tempSlot");
		chipName = nbt.getString("chipName");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("tempSlot", tempSlot);
		nbt.setString("chipName", chipName);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		if (slot == 0 && is.getItem() == TransportTerminal.remote || slot == 0 && is.getItem() == TransportTerminal.remoteTerminal)
			return true;
		return false;
	}

	public String getInventoryName() {
		return "Location X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ();
	}

	public void setName(String text) {
		chipName = text;
		ItemStack is = getStackInSlot(getTempSlot());
		if (is != null && is.getItem() == TransportTerminal.chip)
			is.getTagCompound().setString("description", chipName);
	}

	public int getTempSlot() {
		return tempSlot;
	}

	public void setTempSlot(int slot) {
		tempSlot = slot;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(pos, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	public boolean canTeleport() {
		return !TransportTerminal.IS_RF_PRESENT;// TODO Fixy || getEnergyStored(ForgeDirection.UNKNOWN) >= ConfigHandler.ENERGY_PER_TELEPORT;
	}

	@Override
	public boolean canRenderBreaking() {
		return true;
	}

	/**
	 * This is not a battery, energy should not be extractable
	 *
	 * @Override public int extractEnergy(ForgeDirection from, int maxExtract,
	 *           boolean simulate) { return 0; }
	 */
}