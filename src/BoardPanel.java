import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class BoardPanel extends JPanel {
    protected Board board;
    protected Graphics2D g2d;

    protected int CELL_SIZE;
    protected int CELL_MARGIN;
    protected int CELL_BORDER;
    protected double CELL_INNER_SHADOW = .075;
    protected double CELL_INNER_GLOW = -.065;
    protected int X_DRAW_OFFSET;
    protected int Y_DRAW_OFFSET;
    protected final Color CELL_COLOR = new Color(52, 73, 94);
    protected final Color PEG_COLOR = Color.yellow;

    public BoardPanel() {}

    public BoardPanel(int[][] layout) {
        this.board = new Board(layout);
    }

    public void reset(int[][] layout) {
        board.reset(layout);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        recalculateSizes();
        paintBoard();
    }

    protected void paintBoard() {
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                paintCell(new Point(j, i));
            }
        }
    }

    protected void paintCell(Point cell) {
        if (board.isOut(cell)) {
            return;
        }

        if ( board.isPeg(cell) ) {
            paintPeg(cell, PEG_COLOR);
        } else {
            paintHole(cell, CELL_COLOR);
        }
    }

    protected void paintHole(Point cell, Color color) {
        Point offset = calcOffset(cell);
        Ellipse2D hole = new Ellipse2D.Double(offset.x, offset.y, this.CELL_SIZE, this.CELL_SIZE);

        g2d.setColor(color);
        g2d.fill(hole);

        paintCrescent(hole, CELL_INNER_SHADOW, color.darker().darker());
        paintCrescent(hole, CELL_INNER_GLOW, color.brighter());

        paintCellOutline(cell, CELL_COLOR.darker());
    }

    protected void paintCellOutline(Point cell, Color color) {
        Point offset = calcOffset(cell);

        g2d.setColor(color);
        g2d.setStroke( new BasicStroke(CELL_BORDER) );
        g2d.drawOval(offset.x, offset.y, this.CELL_SIZE, this.CELL_SIZE);
    }

    protected void paintPeg(Point cell, Color color) {
        Point offset = calcOffset(cell);
        Ellipse2D peg = new Ellipse2D.Double(offset.x, offset.y, this.CELL_SIZE, this.CELL_SIZE);

        double alpha = .75;
        Color GLARE_COLOR = new Color(
                255,
                255,
                255,
                (int)(color.getAlpha() * alpha)
        );

        g2d.setColor(color);
        g2d.fill(peg);

        paintGlare(peg, GLARE_COLOR, .375, .5, .225, .225);
        paintCrescent(peg, -CELL_INNER_SHADOW, color.darker());

        paintCellOutline(cell, CELL_COLOR.darker().darker());
    }

    protected void paintCrescent(Ellipse2D source, double fullness, Color color) {
        Area offsetArea = new Area(source);
        double offset = source.getWidth() * fullness;
        AffineTransform at = AffineTransform.getTranslateInstance(offset, offset);
        offsetArea.transform(at);

        Area crescent = new Area(source);
        crescent.subtract(offsetArea);

        g2d.setColor(color);
        g2d.fill(crescent);
    }

    protected void paintGlare(Ellipse2D source, Color color, double heightK, double widthK, double offsetXPerc, double offsetYPerc) {
        double GLARE_HEIGHT = source.getHeight() * heightK;
        double GLARE_WIDTH = source.getWidth() * widthK;
        double GLARE_X = source.getX() + offsetXPerc * source.getWidth();
        double GLARE_Y = source.getY() + offsetYPerc * source.getHeight();

        Ellipse2D glareShape = new Ellipse2D.Double(GLARE_X, GLARE_Y, GLARE_WIDTH, GLARE_HEIGHT);

        AffineTransform at = AffineTransform.getRotateInstance(
                Math.toRadians(315),
                GLARE_X + GLARE_HEIGHT / 2,
                GLARE_Y + GLARE_WIDTH / 2
        );

        Area glare = new Area(glareShape);
        glare.transform(at);

        g2d.setColor(color);
        g2d.fill(glare);
    }

    protected void recalculateSizes() {
        final Dimension dim = this.getPreferredSize();
        final int width = dim.width;
        final int height = dim.height;

        /*To center drawing*/
        this.X_DRAW_OFFSET = width > height ? (width - height) / 2 : 0;
        this.Y_DRAW_OFFSET = height > width ? (height - width) / 2 : 0;

        this.CELL_SIZE = (int)(Math.min(width, height) / board.getSize() / 1.1);
        this.CELL_BORDER = (int)(this.CELL_SIZE * .05);
        this.CELL_MARGIN = (int)(this.CELL_SIZE * .1);
    }

    protected Point calcOffset(Point cell) {
        return new Point(
            this.X_DRAW_OFFSET + this.CELL_MARGIN + (this.CELL_SIZE + this.CELL_MARGIN) * cell.x,
            this.Y_DRAW_OFFSET + this.CELL_MARGIN + (this.CELL_SIZE + this.CELL_MARGIN) * cell.y
        );
    }
}
