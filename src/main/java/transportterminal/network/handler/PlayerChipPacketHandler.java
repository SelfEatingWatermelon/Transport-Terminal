package transportterminal.network.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import transportterminal.TransportTerminal;
import transportterminal.network.TransportTerminalTeleporter;
import transportterminal.network.message.PlayerChipMessage;
import transportterminal.tileentites.TileEntityTransportTerminal;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PlayerChipPacketHandler implements IMessageHandler<PlayerChipMessage, IMessage> {

	@Override
	public IMessage onMessage(PlayerChipMessage message, MessageContext ctx) {

		EntityPlayer playerOnChip = MinecraftServer.getServer().getConfigurationManager().func_152612_a(message.playerOnChip);
		World world = DimensionManager.getWorld(message.dimension);

		if (world == null || playerOnChip == null)
			return null;

		else if (!world.isRemote)
			if (ctx.getServerHandler().playerEntity.getEntityId() == message.entityID) {
				EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				WorldServer worldserver = (WorldServer) world;
				if (playerOnChip.dimension != player.dimension && player.dimension != 1)
					player.mcServer.getConfigurationManager().transferPlayerToDimension(player, playerOnChip.dimension, new TransportTerminalTeleporter(worldserver));
				if (playerOnChip.dimension != player.dimension && player.dimension == 1) {
					player.mcServer.getConfigurationManager().transferPlayerToDimension(player, playerOnChip.dimension, new TransportTerminalTeleporter(worldserver));
					player.mcServer.getConfigurationManager().transferPlayerToDimension(player, playerOnChip.dimension, new TransportTerminalTeleporter(worldserver));
				}
				TileEntityTransportTerminal console = (TileEntityTransportTerminal) world.getTileEntity(message.tileX, message.tileY, message.tileZ);
				if (console != null && console.canTeleport())
					console.setEnergy(console.getEnergyStored(ForgeDirection.UNKNOWN) - TransportTerminal.ENERGY_PER_TELEPORT);
				teleportPlayer(player, playerOnChip.posX, playerOnChip.posY, playerOnChip.posZ, player.rotationYaw, player.rotationPitch);
			}
		return null;
	}

	private void teleportPlayer(EntityPlayerMP player, double x, double y, double z, float yaw, float pitch) {
		player.playerNetServerHandler.setPlayerLocation(x, y, z, yaw, pitch);
		player.worldObj.playSoundEffect(x, y, z, "transportterminal:teleportsound", 1.0F, 1.0F);
	}
}