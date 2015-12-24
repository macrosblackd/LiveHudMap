package org.gotti.wurmonline.clientmods.livehudmap;

public class RadarItem {

    private final long id;
    private final String name;
    private final float x;
    private final float y;
    private final int layer;

    public RadarItem(long id, String name, float x, float y, int layer) {

        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getLayer() {
        return layer;
    }
}
