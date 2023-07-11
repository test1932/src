//import javax.swing.JPanel;

import javax.swing.JFrame;

import bodies.PhysicalBodyA;

//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import listeners.Observer;

public class Display extends JFrame implements Observer {
    battle b;
    Controller cont;

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
        Graphics2D g2D = (Graphics2D)g;

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
