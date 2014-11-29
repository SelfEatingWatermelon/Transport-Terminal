package transportterminal.models;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import transportterminal.proxy.ClientProxy.BlockRenderIDs;

@SideOnly(Side.CLIENT)
public class SummonerBlockRender implements ISimpleBlockRenderingHandler {
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		BlockRenderHelper.renderSimpleBlock(block, 0, renderer);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		switch (world.getBlockMetadata(x, y, z)) {
		case 0:
			renderer.uvRotateTop = 0;
			break;
		case 1:
			renderer.uvRotateTop = 0;
			break;
		case 2:
			renderer.uvRotateTop = 3;
			break;
		case 3:
			renderer.uvRotateTop = 0;
			break;
		case 4:
			renderer.uvRotateTop = 1;
			break;
		case 5:
			renderer.uvRotateTop = 2;
			break;
		}

		boolean flag = renderer.renderStandardBlock(block, x, y, z);
		renderer.uvRotateTop = 0;
		return flag;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.SUMMON_BLOCK.id();
	}
}