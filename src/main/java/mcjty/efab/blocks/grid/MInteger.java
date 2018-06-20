package mcjty.efab.blocks.grid;

public class MInteger {
    private int i;

    public MInteger(int i) {
        this.i = i;
    }

    // This can return an index out of bounds!
    public int get() {
        return i;
    }

    // Safe get that is always in bounds
    public int getSafe(int bounds) {
        return bounds <= 0 ? i : (i % bounds);
    }

    public void set(int i) {
        this.i = i;
    }

    public void inc() {
        i++;
    }

    public void dec() {
        i--;
    }

    public void dec(int amount) {
        i -= amount;
    }
}
