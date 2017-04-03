package mcjty.efab.compat.jei.grid;

import java.util.ArrayList;
import java.util.List;

class Tooltip {
    private final int x;
    private final int y;
    private final int w;
    private final int h;
    private final List<String> tooltips = new ArrayList<>();

    public Tooltip(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public Tooltip add(String s) {
        tooltips.add(s);
        return this;
    }

    public boolean in(int xx, int yy) {
        return xx >= x && xx < x + w && yy >= y && yy < y + h;
    }

    public List<String> getTooltips() {
        return tooltips;
    }
}
