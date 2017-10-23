import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;


public class GameBoardPanel extends BoardPanel {
    private Point pickedPeg = null;
    private HashSet<Point> legitMoves = null;
    private boolean victory = false;
    private GameBoard _board;
    private MouseHandler mouseHandler;

    protected final Color CELL_HIGHLIGHTED_COLOR = new Color(231, 76, 60);
    protected final Color PEG_ACTIVE_COLOR = Color.red;
    protected final Color PEG_WIN_COLOR = Color.green;

    public GameBoardPanel(int[][] layout) {
        board = _board = new GameBoard( layout );

        mouseHandler = new MouseHandler(this);
        addMouseListener(mouseHandler);
    }

    @Override
    public void reset(int[][] layout) {
        _board.reset(layout);
        dropActivePeg();
        victory = false;

        if (getMouseListeners().length < 1) {
            addMouseListener(mouseHandler);
        }

        repaint();
    }

    public Point calcPoint(int x, int y) {
        return new Point(
                (int)Math.ceil( (x - this.X_DRAW_OFFSET - this.CELL_MARGIN) / (this.CELL_SIZE + this.CELL_MARGIN) ),
                (int)Math.ceil( (y - this.Y_DRAW_OFFSET - this.CELL_MARGIN) / (this.CELL_SIZE + this.CELL_MARGIN) )
        );
    }

    public boolean isPegPicked() {
        return pickedPeg != null;
    }

    public void tryPickPeg(Point cell) {
        if ( !_board.isPeg(cell) )
            return;

        pickPeg(cell);
    }

    public void tryMovePeg(Point cell) {
        if ( _board.isPeg(cell) ) {
            if ( cell.equals(pickedPeg) ) {
                dropActivePeg();
                repaint();
                return;
            }

            pickPeg(cell);
            return;
        }

        if ( _board.isMoveLegit(pickedPeg, cell) ) {
            movePeg(pickedPeg, cell);
        }
    }

    private void dropActivePeg() {
        pickedPeg = null;
        legitMoves = null;
    }

    private void movePeg(Point peg, Point hole) {
        _board.movePeg(peg, hole);
        dropActivePeg();

        if ( _board.isVictory() ) {
            execVictory();
        }

        repaint();
    }

    private void pickPeg(Point peg) {
        pickedPeg = peg;
        legitMoves = _board.getValidMoves(pickedPeg);
        repaint();
    }

    private boolean isCellLegitForMove(Point cell) {
        Iterator<Point> iterator = legitMoves.iterator();
        while(iterator.hasNext()) {
            Point temp = iterator.next();

            if ( temp.equals(cell) )
                return true;
        }
        return false;
    }

    private void execVictory() {
        victory = true;
        removeMouseListener(mouseHandler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (victory) {
            Point center = new Point(
                    (int)Math.ceil(_board.getSize() / 2),
                    (int)Math.ceil(_board.getSize() / 2)
            );
            super.paintPeg(center, PEG_WIN_COLOR);
            return;
        }

        if ( isPegPicked() ) {
            paintPeg(pickedPeg, PEG_ACTIVE_COLOR);

            if (legitMoves != null) {
                Iterator<Point> iter = legitMoves.iterator();

                while(iter.hasNext()) {
                    Point temp = iter.next();
                    paintHole(temp, CELL_HIGHLIGHTED_COLOR);
                }
            }
        }
    }
}
