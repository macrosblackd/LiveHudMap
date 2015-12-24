package org.gotti.wurmonline.clientmods.livehudmap.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map;

import com.wurmonline.client.game.CaveDataBuffer;
import com.wurmonline.mesh.Tiles.Tile;
import org.gotti.wurmonline.clientmods.livehudmap.RadarItem;

public class MapRendererCave extends AbstractCaveRenderer {
	public MapRendererCave(CaveDataBuffer buffer) {
		super(buffer);
	}

	@Override
	public BufferedImage createMapDump(int xo, int yo, int lWidth, int lHeight, int px, int py, Map<Long, RadarItem> groundItems) {
		if (yo < 0)
			yo = 0;
		if (xo < 0)
			xo = 0;

		final BufferedImage bi2 = new BufferedImage(lWidth, lWidth, BufferedImage.TYPE_INT_RGB);
		final float[] data = new float[lWidth * lWidth * 3];

		for (int x = 0; x < lWidth; x++) {
			for (int y = lWidth - 1; y >= 0; y--) {

				final short height = getHeight(x + xo, y + yo);
				Tile tile = getTileType(x + xo, y + yo);

				if (tile != Tile.TILE_CAVE_WALL && !isTunnel(tile) && isSurroundedByRock(x + xo, y + yo)) {
					tile = Tile.TILE_CAVE_WALL;
				}

				final Color color;
				if (tile != null) {
					color = CaveColors.getColorFor(tile);
				}
				else {
					color = CaveColors.getColorFor(Tile.TILE_CAVE);
				}
				int r = color.getRed();
				int g = color.getGreen();
				int b = color.getBlue();
				if (height < 0 && tile == Tile.TILE_CAVE) {
					r = (int) (r * 0.2f + 0.4f * 0.4f * 256f);
					g = (int) (g * 0.2f + 0.5f * 0.4f * 256f);
					b = (int) (b * 0.2f + 1.0f * 0.4f * 256f);
				}
				
				if (px == x + xo && py == y + yo) {
					r = Color.RED.getRed();
					g = 0;
					b = 0;
				}

				data[(x + y * lWidth) * 3 + 0] = r;
				data[(x + y * lWidth) * 3 + 1] = g;
				data[(x + y * lWidth) * 3 + 2] = b;
			}
		}

		bi2.getRaster().setPixels(0, 0, lWidth, lWidth, data);
		return bi2;
	}
	
	
	private boolean isTunnel(int x, int y) {
		Tile tileType = getTileType(x, y);
		return isTunnel(tileType);
	}

	private boolean isTunnel(Tile tileType) {
		return tileType == Tile.TILE_CAVE || tileType == Tile.TILE_CAVE_FLOOR_REINFORCED || tileType == Tile.TILE_CAVE_EXIT;
	}
	

	private boolean isSurroundedByRock(int x, int y) {
		return !isTunnel(x + 1, y) && !isTunnel(x - 1, y) && !isTunnel(x, y + 1) && !isTunnel(x, y - 1);
	}
}
