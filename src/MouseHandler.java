import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener {
    GameBoardPanel panel;

    MouseHandler(GameBoardPanel panel) {
        super();
        this.panel = panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point click = panel.calcPoint(e.getX(), e.getY());

        if ( !panel.isPegPicked() ) {
            panel.tryPickPeg(click);
        } else {
            panel.tryMovePeg(click);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
