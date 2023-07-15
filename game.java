public class Game {
    public enum GameState {Playing, Menu};
    public enum Difficulty {Easy, Normal, Hard, Lunatic};

    Display screen;
    public GameState gameState;
    public Difficulty difficulty;
    battle b;
    Controller c;

    public Game(Difficulty diff) {
        this.difficulty = diff;
        this.gameState = GameState.Playing;
    }

    public static void main(String[] args) {
        Game theWorld = new Game(Difficulty.Normal);
        theWorld.b = new battle();
        theWorld.c = new Controller(theWorld.b, theWorld);
        theWorld.screen = new Display(theWorld.b, theWorld.c);
    }
}