class game {
    private enum GameState {Title, Playing, Paused};
    private enum Difficulty {Easy, Normal, Hard, Lunatic};

    Display screen;
    GameState gameState;
    Difficulty difficulty;
    battle b;
    Controller c;

    public game(Difficulty diff) {
        this.difficulty = diff;
    }

    public static void main(String[] args) {
        game theWorld = new game(Difficulty.Normal);
        theWorld.b = new battle();
        theWorld.c = new Controller(theWorld.b);
        theWorld.screen = new Display(theWorld.b, theWorld.c);
    }
}