package game;
import java.util.ArrayList;
import java.util.List;

import game.model.scenario.Battle;
import game.model.Controller;
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
    private Controller cont;

    private List<Observer> observers;
    public AbstractMenu titleMenu;
    private AbstractMenu curMenu;

    public Game(Difficulty diff, Controller cont) {
        observers = new ArrayList<Observer>();
        this.cont = cont;
        this.titleMenu = new MenuTitle(this, null);
        this.curMenu = titleMenu;
        this.difficulty = diff;
        this.gameState = GameState.Menu;
    }

    public static void main(String[] args) {
        Game theWorld = new Game(Difficulty.Normal, new Controller());
        theWorld.getCont().activateController(theWorld);
        theWorld.screen = new Display(theWorld.cont);
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

    public Controller getCont() {
        return cont;
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }
}