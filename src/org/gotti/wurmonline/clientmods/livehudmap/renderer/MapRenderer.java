package org.gotti.wurmonline.clientmods.livehudmap.renderer;

import org.gotti.wurmonline.clientmods.livehudmap.RadarItem;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface MapRenderer {
	BufferedImage createMapDump(int xo, int yo, int lWidth, int lHeight, int px, int py, Map<Long, RadarItem> groundItems);
}
