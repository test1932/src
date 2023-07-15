package game;
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
    private AbstractMenu curMenu = new MenuTitle(this);

    public Game(Difficulty diff) {
        this.difficulty = diff;
        this.gameState = GameState.Menu;
    }

    public static void main(String[] args) {
        Game theWorld = new Game(Difficulty.Normal);
        theWorld.b = new Battle();
        theWorld.c = new Controller(theWorld.b, theWorld);
        theWorld.screen = new Display(theWorld.b, theWorld.c);
    }

    public AbstractMenu getCurMenu() {
        return curMenu;
    }

    public void setCurMenu(AbstractMenu curMenu) {
        this.curMenu = curMenu;
    }
}