package game.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.Controller;

public class GameKeyListener implements KeyListener {
    private Controller cont;
    private Display disp;

    public GameKeyListener(Display d, Controller c) {
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