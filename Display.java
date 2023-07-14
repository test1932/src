//import javax.swing.JPanel;

import javax.swing.JFrame;

import bodies.PhysicalBodyA;

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
        backBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
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
        paintBattle(g2D);

        g.drawImage(backBuffer, 0, 0, null);
    }

    public void setThickness(Graphics2D g2D, float strokeWidth) {
        BasicStroke stroke = new BasicStroke(strokeWidth);
        g2D.setStroke(stroke);
    }

    public void paintBattle(Graphics2D g2D) {
        g2D.clearRect(0,0, 1000, 700);
        g2D.draw(this.b.bounds);
        for (PhysicalBodyA wall : b.walls) {
            g2D.draw(wall.hitbox);
        }
        g2D.draw(this.b.players[0].getImage());
    }

    private void setup() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,600);
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
