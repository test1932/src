package game;
import java.util.ArrayList;
import java.util.List;

import game.view.Display;
import listeners.Observer;
import menu.AbstractMenu;
import menu.title.MenuTitle;

public class Game {
    public enum GameState {Playing, Menu};
    public enum Difficulty {Easy, Normal, Hard, Lunatic};

    public Display screen;
    public GameState gameState;
    public Difficulty difficulty;
    public Battle b;
    public Controller c;

    private List<Observer> observers;
    private AbstractMenu curMenu = new MenuTitle(this, null);

    public Game(Difficulty diff) {
        observers = new ArrayList<Observer>();
        this.difficulty = diff;
        this.gameState = GameState.Menu;
    }

    public static void main(String[] args) {
        Game theWorld = new Game(Difficulty.Normal);
        theWorld.b = new Battle(theWorld);
        theWorld.c = new Controller(theWorld.b, theWorld);
        theWorld.screen = new Display(theWorld.b, theWorld.c);
    }

    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }

    public AbstractMenu getCurMenu() {
        return curMenu;
    }

    public void setCurMenu(AbstractMenu curMenu) {
        this.curMenu = curMenu;
    }

    public void addObserver(Observer o) {
        this.observers.add(o);
    }
}