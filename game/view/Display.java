package game.view;


import javax.swing.JFrame;

import bodies.AbstractPhysicalBody;
import bodies.characters.AbstractPlayer;
import game.Battle;
import game.Controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import listeners.Observer;
import menu.AbstractOption;

public class Display extends JFrame implements Observer {
    private Battle b;
    private Controller cont;
    private BufferedImage backBuffer;
    private final int OPTION_MIN_X = 100;
    private final int OPTION_MIN_Y = 300;

    private Line2D.Double underline 
        = new Line2D.Double(OPTION_MIN_X, OPTION_MIN_Y, OPTION_MIN_X + 50, OPTION_MIN_Y);

    public Display(Battle b, Controller c) {
        this.b = b;
        this.cont = c;
        addKeyListener(new GameKeyListener(this, cont));
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
        for (AbstractPlayer player : this.b.players) {
            g2D.draw(player.getImage());   
        }
    }

    public void paintMenu(Graphics2D g2D) {
        g2D.drawString("This is the " + cont.game.getCurMenu().getMenuName() + " menu", OPTION_MIN_X, 100);
        paintOptions(g2D);
    }

    private void paintOptions(Graphics2D g2D) {
        int offset = OPTION_MIN_Y;
        for (AbstractOption option : cont.game.getCurMenu().getOptions()) {
            g2D.drawString(option.displayText, OPTION_MIN_X, offset += 30);
        }
        setYUnderline(cont.game.getCurMenu().getSelectedIndex());
        highlightSelected(g2D);
    }

    private void highlightSelected(Graphics2D g2D) {
        g2D.setColor(Color.YELLOW);
        g2D.draw(underline);
        g2D.setColor(Color.WHITE);
    }

    private void setYUnderline(int index) {
        underline.y1 = OPTION_MIN_Y + 30 * (index + 1) + 10;
        underline.y2 = underline.y1;
    }

    private void setup() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,600);
        backBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        setResizable(false);
        setTitle("Touhou ??.? - Second Realm of Fallen Star");
        cont.game.addObserver(this);

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
