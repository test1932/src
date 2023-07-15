
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BattleKeyListener implements KeyListener {
    Controller cont;
    Display disp;

    public BattleKeyListener(Display d, Controller c) {
        this.cont = c;
        this.disp = d;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyNo = e.getKeyCode();
        switch(keyNo){
            default:
                // System.out.println(keyNo);
                cont.holdKey(keyNo);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyNo = e.getKeyCode();
        switch(keyNo){
            default:
                // System.out.println(keyNo);
                cont.releaseKey(keyNo);
                break;
        }
    }
}