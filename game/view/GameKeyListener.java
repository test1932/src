package game.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.model.Controller;

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
        // System.out.println(keyNo);
        cont.holdKey(keyNo);
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyNo = e.getKeyCode();
        // System.out.println(keyNo);
        cont.releaseKey(keyNo);
    }
}