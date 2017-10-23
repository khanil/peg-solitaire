import java.awt.*;
import java.util.HashSet;

public class GameBoard extends Board {
    private int pegsAmount;

    public GameBoard(int[][] layout) {
        super(layout);
        initPegsAmount(layout);
    }

    @Override
    public void reset(int[][] layout) {
        super.reset(layout);
        initPegsAmount(layout);
    }

    public boolean isMoveLegit(Point peg, Point dest) {
        return isInBoard(dest)
                && isHole(dest)
                && isLegitDistance(peg, dest)
                && isPeg( new Point( (peg.x + dest.x) / 2, (peg.y + dest.y) / 2) );
    }

    public HashSet<Point> getValidMoves(Point peg) {
        HashSet<Point> moves = new HashSet<>();

        for (int shift = -2; shift <= 2; shift+=4) {
            Point xPoint = new Point(peg.x + shift, peg.y);
            if ( isInBoard(xPoint) && isHole(xPoint) && isPeg( new Point(peg.x + shift / 2, peg.y) ) ) {
                moves.add(xPoint);
            }

            Point yPoint = new Point(peg.x, peg.y + shift);
            if ( isInBoard(yPoint) && isHole(yPoint) && isPeg( new Point(peg.x, peg.y + shift / 2) ) ) {
                moves.add(yPoint);
            }
        }

        return moves;
    }

    public void movePeg(Point peg, Point hole) {
        setCell(peg, HOLE);
        setCell(new Point((peg.x + hole.x) / 2, (peg.y + hole.y) / 2), HOLE);
        setCell(hole, PEG);
        pegsAmount--;
    }

    public boolean isVictory() {
        if (pegsAmount == 1) {
            int center = (int)Math.ceil( size / 2 );
            return isPeg( new Point(center, center) );
        }

        return false;
    }

    private boolean isLegitDistance(Point a, Point b) {
        return Math.hypot( b.x - a.x, b.y - a.y ) == (double)2;
    }

    private boolean isInBoard(Point point) {
        return point.x > -1 && point.x < size && point.y > -1 && point.y < size;
    }

    private void initPegsAmount(int[][] layout) {
        pegsAmount = 0;

        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout.length; j++) {
                if ( isPeg(new Point(j, i)) ) {
                    pegsAmount++;
                }
            }
        }
    }
}
