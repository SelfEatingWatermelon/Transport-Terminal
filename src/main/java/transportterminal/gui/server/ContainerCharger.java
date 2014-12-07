package transportterminal.gui.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import transportterminal.tileentites.TileEntityCharger;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerCharger extends Container {

	private final TileEntityCharger tile;

	public ContainerCharger(InventoryPlayer playerInventory, TileEntityCharger tile) {
		this.tile = tile;
		addSlotToContainer(new Slot(tile, 0, 62, 9));
		addSlotToContainer(new Slot(tile, 1, 62, 31));
		addSlotToContainer(new Slot(tile, 2, 62, 53));
		addSlotToContainer(new Slot(tile, 3, 62 + 36, 9));
		addSlotToContainer(new Slot(tile, 4, 62 + 36, 31));
		addSlotToContainer(new Slot(tile, 5, 62 + 36, 53));

		for (int j = 0; j < 3; j++)
			for (int k = 0; k < 9; k++)
				addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 86 + j * 18));
		for (int j = 0; j < 9; j++)
			addSlotToContainer(new Slot(playerInventory, j, 8 + j * 18, 144));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < crafters.size(); i++)
			((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 0, tile.getEnergyStored(ForgeDirection.UNKNOWN));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		if (id == 0)
			tile.setEnergy(value);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();

			if (slotIndex < 6) {
				if (!mergeItemStack(stack1, 6, inventorySlots.size(), true))
					return null;
			} else if (stack1.getItem() instanceof IEnergyContainerItem) {
				if (!mergeItemStack(stack1, 0, 6, false))
					return null;
			} else
				return null;

			if (stack1.stackSize == 0)
				slot.putStack(null);
			else
				slot.onSlotChanged();
		}

		return stack;
	}
}