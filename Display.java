

import javax.swing.JFrame;

import bodies.AbstractPhysicalBody;

import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import listeners.Observer;

public class Display extends JFrame implements Observer {
    private battle b;
    private Controller cont;
    private BufferedImage backBuffer;

    public Display(battle b, Controller c) {
        this.b = b;
        this.cont = c;
        addKeyListener(new BattleKeyListener(this, cont));
        setup();
    }

    @Override
    public void paint(Graphics g) {
        redraw();
        if (g == null) {
            return;
        }

        Graphics backBufferGraphics = backBuffer.getGraphics();
        Graphics2D g2D = (Graphics2D)backBufferGraphics;
        setThickness(g2D, 2.0f);
        paintScreen(g2D);

        g.drawImage(backBuffer, 0, 0, null);
    }

    public void paintScreen(Graphics2D g2D) {
        g2D.clearRect(0,0, 1000, 700);
        switch (cont.game.gameState) {
            case Menu:
                paintMenu(g2D); break;
            case Playing:
                paintBattle(g2D); break;
        }
    }

    public void setThickness(Graphics2D g2D, float strokeWidth) {
        BasicStroke stroke = new BasicStroke(strokeWidth);
        g2D.setStroke(stroke);
    }

    public void paintBattle(Graphics2D g2D) {
        g2D.draw(this.b.bounds);
        for (AbstractPhysicalBody wall : b.walls) {
            g2D.draw(wall.hitbox);
        }
        g2D.draw(this.b.players[0].getImage());
    }

    public void paintMenu(Graphics2D g2D) {
        g2D.drawString("This is the menu", 100, 100);
    }

    private void setup() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,600);
        backBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        setResizable(false);
        setTitle("Touhou ??.? - Second Realm of Fallen Star");
        b.addObserver(this);

        update();
    }
    
    private void redraw() {
        getContentPane().removeAll();
        setVisible(true);
    }

    @Override
    public void update() {
        paint(getGraphics());
    }
}
