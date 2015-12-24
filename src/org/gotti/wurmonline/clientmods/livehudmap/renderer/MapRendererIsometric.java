package org.gotti.wurmonline.clientmods.livehudmap.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map;

import com.wurmonline.client.game.NearTerrainDataBuffer;
import com.wurmonline.mesh.Tiles.Tile;
import org.gotti.wurmonline.clientmods.livehudmap.RadarItem;

public class MapRendererIsometric extends AbstractSurfaceRenderer {
	
	public MapRendererIsometric(NearTerrainDataBuffer buffer) {
		super(buffer);
	}
	
    public BufferedImage createMapDump(int xo, int yo, int lWidth, int lHeight, int px, int py, Map<Long, RadarItem> groundItems) {
        if (yo < 0)
            yo = 0;
        if (xo < 0)
            xo = 0;

        final BufferedImage bi2 = new BufferedImage(lWidth, lHeight, BufferedImage.TYPE_INT_RGB);
        final float[] data = new float[lWidth * lHeight * 3];
                
        int y0 = lHeight * 2;
        
        for (int x = 0; x < lWidth; x++) {
            int alt = y0 - 1;
            for (int y = y0 - 1; y >= -lHeight && alt >= 0; y--) {
                float node = (float) (getSurfaceHeight(x + xo, y + yo) / (Short.MAX_VALUE / 3.3f));
                float node2 = y == y0 - 1 ? node : (float) (getSurfaceHeight(x + 1 + xo, y + 1 + yo) / (Short.MAX_VALUE / 3.3f));


                final float hh = node;

                float h = ((node2 - node) * 1500) / 256.0f * 0x1000 / 128 + hh / 2 + 1.0f;
                h *= 0.4f;

                float r = h;
                float g = h;
                float b = h;

                final Tile tile = getTileType(x + xo, y + yo);
                final Color color;
                if (tile != null) {
                    color = tile.getColor();
                }
                else {
                    color = Tile.TILE_DIRT.getColor();
                }
                r *= (color.getRed() / 255.0f) * 2;
                g *= (color.getGreen() / 255.0f) * 2;
                b *= (color.getBlue() / 255.0f) * 2;

                if (r < 0)
                    r = 0;
                if (r > 1)
                    r = 1;
                if (g < 0)
                    g = 0;
                if (g > 1)
                    g = 1;
                if (b < 0)
                    b = 0;
                if (b > 1)
                    b = 1;

                if (node < 0) {
                    r = r * 0.2f + 0.4f * 0.4f;
                    g = g * 0.2f + 0.5f * 0.4f;
                    b = b * 0.2f + 1.0f * 0.4f;
                }

				if (px == x + xo && py == y + yo) {
					r = 1.0f;
					g = 0;
					b = 0;
				}
                
                final int altTarget = y - (int) (getSurfaceHeight(x + xo, y + yo) * MAP_HEIGHT / 4  / (Short.MAX_VALUE / 2.5f));
                while (alt > altTarget && alt >= 0) {
                	if (alt < lHeight) {
	                    data[(x + alt * lWidth) * 3 + 0] = r * 255;
	                    data[(x + alt * lWidth) * 3 + 1] = g * 255;
	                    data[(x + alt * lWidth) * 3 + 2] = b * 255;
                	}
                    alt--;
                }
            }
        }

        bi2.getRaster().setPixels(0, 0, lWidth, lHeight, data);
        return bi2;
    }
	

}
