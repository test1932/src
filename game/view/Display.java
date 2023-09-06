package game.view;

import javax.swing.JFrame;

import actions.AbstractSpellAction;
import bodies.AbstractPhysicalBody;
import bodies.characters.AbstractPlayer;
import bodies.projectiles.AbstractProjectile;
import game.model.Controller;
import game.model.scenario.Dialogue;

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
    private Controller cont;
    private BufferedImage backBuffer;

    private Font mainFont = new Font("sansserif", Font.PLAIN, 20);
    private Font monospaceFont = new Font("monospace", Font.PLAIN, 20);
    private Rectangle menuArea = new Rectangle(50, 50, 300, 450);
    private Rectangle dialogueArea = new Rectangle(25, HEIGHT - 150, WIDTH - 50, 150);
    private Color menuAreaColour = new Color(200, 200, 200, 150);

    private boolean menuIsReset = false;
    private int optionDisplayOffset = 0; 

    private final int OPTION_MIN_X = 75;
    private final int OPTION_MIN_Y = 200;
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 650;

    private ProgressBar[] healthBars = new ProgressBar[]{
        new ProgressBar(false, 50, 50, 300, 20),
        new ProgressBar(true, WIDTH - 300 - 50, 50, 300, 20)
    };

    private SegmentedProgressBar[] manaBars = new SegmentedProgressBar[] {
        new SegmentedProgressBar(false, 250, HEIGHT - 30, 200, 20, 5),
        new SegmentedProgressBar(true, WIDTH - 250 - 200, HEIGHT - 30, 200, 20, 5)
    };

    private VerticalSegmentedProgressBar[] cardBars = new VerticalSegmentedProgressBar[] {
        new VerticalSegmentedProgressBar(false, 40, HEIGHT - 70, 200, 50, 5),
        new VerticalSegmentedProgressBar(true, WIDTH - 40 - 200, HEIGHT - 70, 200, 50, 5)
    };

    private Line2D.Double underline 
        = new Line2D.Double(OPTION_MIN_X, OPTION_MIN_Y, OPTION_MIN_X + 50, OPTION_MIN_Y);

    public Display(Controller cont) {
        this.cont = cont;
        addKeyListener(new GameKeyListener(this, cont));
        setup();
        setupHealthBars();
        setupManaBars();
        setupCardBars();
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

    private void setupCardBars() {
        cardBars[0].setFillColour(Color.RED);
        cardBars[0].setOutlineColour(Color.ORANGE);
        cardBars[1].setFillColour(Color.RED);
        cardBars[1].setOutlineColour(Color.ORANGE);
    }

    private void paintDialogue(Graphics2D g2D, Dialogue dialogue) {
        Color tempColor = g2D.getColor();
        Font tempFont = g2D.getFont();

        drawSprite(dialogue.getSpriteLeft(), g2D, true, Dialogue.LEFT_X);
        drawSprite(dialogue.getSpriteRight(), g2D, false, Dialogue.LEFT_X);
        g2D.setColor(menuAreaColour);
        g2D.fill(dialogueArea);

        g2D.setFont(monospaceFont);
        g2D.setColor(Color.BLACK);
        g2D.draw(dialogueArea);
        g2D.drawString(dialogue.getTextLine1(), Dialogue.TEXT_X, Dialogue.LINE_1_TEXT_Y);
        g2D.drawString(dialogue.getTextLine2(), Dialogue.TEXT_X, Dialogue.LINE_2_TEXT_Y);

        g2D.setColor(tempColor);
        g2D.setFont(tempFont);

    }

    private void resetMenu() {
        this.optionDisplayOffset = 0;
        this.menuIsReset = true;
    }

    private void drawSprite(BufferedImage img, Graphics2D g2D, boolean isLeft, int leftX) {
        double ratio = (double)HEIGHT / img.getHeight();
        int width = (int)(img.getWidth() * ratio);
        int x = isLeft ? leftX : WIDTH - width;
        g2D.drawImage(img, x , 50, width, HEIGHT, this);
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
                if (!this.menuIsReset) resetMenu();
                g2D.setColor(Color.WHITE);
                paintScenario(g2D); break;
        }
    }

    public void setThickness(Graphics2D g2D, float strokeWidth) {
        BasicStroke stroke = new BasicStroke(strokeWidth);
        g2D.setStroke(stroke);
    }

    public void paintScenario(Graphics2D g2D) {
        // System.out.println(cont.getScenario().getCurScenarioState());
        switch (cont.getScenario().getCurScenarioState()) {
            case BATTLE:
                paintBattle(g2D);
                break;
            default:
                paintBattle(g2D);
                paintStats(g2D);
                paintDialogue(g2D, cont.getScenario().getDialogue());
                break;
        }
    }

    public void paintBattle(Graphics2D g2D) {
        // paintWalls(g2D); // paint walls
        g2D.drawImage(cont.getBattle().getBackBackground(), 0, 0, WIDTH, HEIGHT, this);
        g2D.drawImage(cont.getBattle().getBackground(), 0, 0, WIDTH, HEIGHT, this);
        paintPlayers(g2D); // paint players
        paintProjectiles(g2D); // paints projectiles
        paintStats(g2D);
    }

    private void paintStats(Graphics2D g2D) {
        paintHealthBars(g2D);
        paintManaBars(g2D);
        paintCardBars(g2D);
    }

    private void paintHealthBars(Graphics2D g2D) {
        for (int i = 0; i < healthBars.length; i++) {
            healthBars[i].setProgress((double)cont.players[i].getHealth() / (double)AbstractPlayer.MAX_HEALTH);
            healthBars[i].paintProgress(g2D);
        }
    }

    private void paintCardBars(Graphics2D g2D) {
        for (int i = 0; i < manaBars.length; i++) {
            cardBars[i].setProgress((double)cont.players[i].getCurCardProgress() / (double)AbstractPlayer.MAX_CARD_PROGRESS);
            cardBars[i].paintProgress(g2D);
        }
    }

    private void paintManaBars(Graphics2D g2D) {
        for (int i = 0; i < manaBars.length; i++) {
            manaBars[i].setProgress((double)cont.players[i].getCurMana() / (double)AbstractPlayer.MAX_MANA);
            manaBars[i].paintProgress(g2D);
        }
    }

    private void paintWalls(Graphics2D g2D) {
        g2D.draw(this.cont.getBattle().bounds);
        for (AbstractPhysicalBody wall : cont.getBattle().walls) {
            g2D.draw(wall.hitbox);
        }
    }

    private void paintPlayers(Graphics2D g2D) {
        for (AbstractPlayer player : this.cont.players) {
            g2D.draw(player.getImage());   
        }
    }

    private void paintProjectiles(Graphics2D g2D) {
        for (AbstractPlayer player : cont.players) {
            paintPlayerProjectiles(g2D, player);
        }
    }

    private void paintPlayerProjectiles(Graphics2D g2D, AbstractPlayer player) {
        player.spellActionLock.lock();
        for (AbstractSpellAction spellAction : player.spellActions) {
            spellAction.projectileLock.lock();
            for (AbstractProjectile projectile : spellAction.getProjectiles()) {
                g2D.draw(projectile.getImage());
            }
            spellAction.projectileLock.unlock();
        }
        player.spellActionLock.unlock();
    }

    public void paintMenu(Graphics2D g2D) {
        g2D.drawImage(cont.game.getCurMenu().getBackground(), 0, 0, WIDTH, HEIGHT, this);
        Color temp = g2D.getColor();
        g2D.setColor(menuAreaColour);
        g2D.fill(menuArea);
        g2D.setColor(temp);
        g2D.drawString("This is the " + cont.game.getCurMenu().getMenuName() + " menu", OPTION_MIN_X, 100);
        paintOptions(g2D);
    }

    //TODO
    private void paintOptions(Graphics2D g2D) {
        int offset = OPTION_MIN_Y - (optionDisplayOffset * 30);
        for (AbstractOption option : cont.game.getCurMenu().getOptions()) {
            if ((offset += 30) < OPTION_MIN_Y) continue;
            g2D.drawString(option.displayText, OPTION_MIN_X, offset);
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
