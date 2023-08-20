package game.view;

import javax.swing.JFrame;

import actions.AbstractSpellAction;
import bodies.AbstractPhysicalBody;
import bodies.characters.AbstractPlayer;
import bodies.projectiles.AbstractProjectile;
import game.model.scenario.Battle;
import game.model.Controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import listeners.Observer;
import menu.AbstractOption;
import menu.settings.config.KeyOption;

public class Display extends JFrame implements Observer {
    private Battle b;
    private Controller cont;
    private BufferedImage backBuffer;

    private Font mainFont = new Font("sansserif", Font.PLAIN, 20);
    private Rectangle menuArea = new Rectangle(50, 50, 250, 450);

    private final int OPTION_MIN_X = 75;
    private final int OPTION_MIN_Y = 200;
    private final int WIDTH = 960;
    private final int HEIGHT = 540;

    private ProgressBar[] healthBars = new ProgressBar[]{
        new ProgressBar(false, 50, 50, 300, 20),
        new ProgressBar(true, WIDTH - 300 - 50, 50, 300, 20)
    };

    private SegmentedProgressBar[] manaBars = new SegmentedProgressBar[] {
        new SegmentedProgressBar(false, 200, HEIGHT - 30, 200, 20, 1.0, 5),
        new SegmentedProgressBar(true, WIDTH - 200 - 200, HEIGHT - 30, 200, 20, 1.0, 5)
    };

    private Line2D.Double underline 
        = new Line2D.Double(OPTION_MIN_X, OPTION_MIN_Y, OPTION_MIN_X + 50, OPTION_MIN_Y);

    public Display(Battle b, Controller c) {
        this.b = b;
        this.cont = c;
        addKeyListener(new GameKeyListener(this, cont));
        setup();
        setupHealthBars();
        setupManaBars();
    }

    private void setupHealthBars() {
        healthBars[0].setFillColour(Color.CYAN);
        healthBars[0].setOutlineColour(Color.BLUE);
        healthBars[1].setFillColour(Color.CYAN);
        healthBars[1].setOutlineColour(Color.BLUE);
    }

    private void setupManaBars() {
        manaBars[0].setFillColour(Color.CYAN);
        manaBars[0].setOutlineColour(Color.BLUE);
        manaBars[1].setFillColour(Color.CYAN);
        manaBars[1].setOutlineColour(Color.BLUE);
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
        g2D.setFont(mainFont);
        g2D.clearRect(0,0, WIDTH, HEIGHT);
        switch (cont.game.gameState) {
            case Menu:
                g2D.setColor(Color.BLACK);
                paintMenu(g2D); break;
            case Playing:
                g2D.setColor(Color.WHITE);
                paintBattle(g2D); break;
        }
    }

    public void setThickness(Graphics2D g2D, float strokeWidth) {
        BasicStroke stroke = new BasicStroke(strokeWidth);
        g2D.setStroke(stroke);
    }

    public void paintBattle(Graphics2D g2D) {
        paintWalls(g2D); // paint walls
        paintPlayers(g2D); // paint players
        paintProjectiles(g2D); // paints projectiles
        paintStats(g2D);
    }

    private void paintStats(Graphics2D g2D) {
        paintHealthBars(g2D);
        paintManaBars(g2D);
    }

    private void paintHealthBars(Graphics2D g2D) {
        for (int i = 0; i < healthBars.length; i++) {
            healthBars[i].setProgress((double)b.players[i].getHealth() / (double)b.players[i].MAX_HEALTH);
            healthBars[i].paintProgress(g2D);
        }
    }

    private void paintManaBars(Graphics2D g2D) {
        for (int i = 0; i < manaBars.length; i++) {
            manaBars[i].setProgress((double)b.players[i].getCurMana() / (double)b.players[i].MAX_MANA);
            manaBars[i].paintProgress(g2D);
        }
    }

    private void paintWalls(Graphics2D g2D) {
        g2D.draw(this.b.bounds);
        for (AbstractPhysicalBody wall : b.walls) {
            g2D.draw(wall.hitbox);
        }
    }

    private void paintPlayers(Graphics2D g2D) {
        for (AbstractPlayer player : this.b.players) {
            g2D.draw(player.getImage());   
        }
    }

    private void paintProjectiles(Graphics2D g2D) {
        cont.bat.spellActionLock.lock();
        for (AbstractSpellAction spellAction : cont.bat.spellActions) {
            spellAction.projectileLock.lock();
            for (AbstractProjectile projectile : spellAction.getProjectiles()) {
                g2D.draw(projectile.getImage());
            }
            spellAction.projectileLock.unlock();
        }
        cont.bat.spellActionLock.unlock();
    }

    public void paintMenu(Graphics2D g2D) {
        g2D.drawImage(cont.game.getCurMenu().getBackground(), 0, 0, WIDTH, HEIGHT, this);
        Color temp = g2D.getColor();
        g2D.setColor(new Color(200, 200, 200, 100));
        g2D.fill(menuArea);
        g2D.setColor(temp);
        g2D.drawString("This is the " + cont.game.getCurMenu().getMenuName() + " menu", OPTION_MIN_X, 100);
        paintOptions(g2D);
    }

    private void paintOptions(Graphics2D g2D) {
        int offset = OPTION_MIN_Y;
        for (AbstractOption option : cont.game.getCurMenu().getOptions()) {
            g2D.drawString(option.displayText, OPTION_MIN_X, offset += 30);
            if (option instanceof KeyOption) {
                g2D.drawString(String.valueOf(((KeyOption)option).getKeyID()), OPTION_MIN_X + 100, offset);
            }
        }
        setYUnderline(cont.game.getCurMenu().getSelectedIndex());
        highlightSelected(g2D);
    }

    private void highlightSelected(Graphics2D g2D) {
        g2D.setColor(Color.RED);
        g2D.draw(underline);
        g2D.setColor(Color.BLACK);
    }

    private void setYUnderline(int index) {
        underline.y1 = OPTION_MIN_Y + 30 * (index + 1) + 10;
        underline.y2 = underline.y1;
    }

    private void setup() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
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
