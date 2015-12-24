package org.gotti.wurmonline.clientmods.livehudmap.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.wurmonline.client.game.NearTerrainDataBuffer;
import com.wurmonline.mesh.Tiles.Tile;
import org.gotti.wurmonline.clientmods.livehudmap.LiveHudMapMod;
import org.gotti.wurmonline.clientmods.livehudmap.RadarItem;

public class MapRendererFlat extends AbstractSurfaceRenderer {
	public MapRendererFlat(NearTerrainDataBuffer buffer) {
		super(buffer);
	}

	private HashMap<Long, RadarItem> getAreaItems(Map<Long, RadarItem> allItems, long start, long end) {
		HashMap<Long, RadarItem> areaItems = new HashMap<Long, RadarItem>();
		for(RadarItem item : allItems.values()) {
			long point = (long)(item.getX() / 4.0) * (long)(item.getY() / 4.0);
			if( point >= start && point <= end ) {
				areaItems.put(point, item);
			}
		}

		return areaItems;
	}

	@Override
	public BufferedImage createMapDump(int xo, int yo, int lWidth, int lHeight, int px, int py, Map<Long, RadarItem> groundItems) {
		if (yo < 0)
			yo = 0;
		if (xo < 0)
			xo = 0;

		final BufferedImage bi2 = new BufferedImage(lWidth, lWidth, BufferedImage.TYPE_INT_RGB);
		final float[] data = new float[lWidth * lWidth * 3];

		long start = xo * yo;
		long end = start + ((xo+lWidth) * (yo+lWidth));

		LiveHudMapMod.appendToFile(String.format("Looking for items between %d and %d", start, end));

		LiveHudMapMod.appendToFile(String.format("Player is at %d and %d", px, py));

		HashMap<Long, RadarItem> items = this.getAreaItems(groundItems, start, end);

		for (int x = 0; x < lWidth; x++) {
			for (int y = lWidth - 1; y >= 0; y--) {
				final short height = getSurfaceHeight(x + xo, y + yo);
				final Tile tile = getTileType(x + xo, y + yo);

				final Color color;
				if (tile != null) {
					color = tile.getColor();
				}
				else {
					color = Tile.TILE_DIRT.getColor();
				}
				int r = color.getRed();
				int g = color.getGreen();
				int b = color.getBlue();
				if (height < 0) {
					r = (int) (r * 0.2f + 0.4f * 0.4f * 256f);
					g = (int) (g * 0.2f + 0.5f * 0.4f * 256f);
					b = (int) (b * 0.2f + 1.0f * 0.4f * 256f);
				}

				RadarItem item = items.getOrDefault((long)((x + xo)*(y + yo)), null);

				if(item != null) {
					r = Color.red.getRed();
					g = 0;
					b = Color.blue.getBlue();
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
}
