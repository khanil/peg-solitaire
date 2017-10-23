import java.awt.*;

public class Board {
    protected final static int PEG = 2;
    protected final static int HOLE = 1;
    protected final static int OUT = 0;
    protected int size;

    private int[][] layout;

    public Board(int[][] layout) {
        reset(layout);
    }

    public void reset(int[][] layout) {
        this.layout = cloneArray( layout );
        size = layout.length;
    }

    public boolean isPeg(Point cell) {
        return getCell(cell) == PEG;
    }

    public boolean isHole(Point cell) {
        return getCell(cell) == HOLE;
    }

    public boolean isOut(Point cell) {
        return getCell(cell) == OUT;
    }

    public int getSize() {
        return size;
    }

    protected int getCell(Point cell) {
        return layout[cell.y][cell.x];
    }

    protected void setCell(Point cell, int label) { layout[cell.y][cell.x] = label; }

    protected int[][] cloneArray(int[][] source) {
        int[][] copy = new int[source.length][source.length];

        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source.length; j++) {
                copy[i][j] = source[i][j];
            }
        }

        return copy;
    }
}
