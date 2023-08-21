package game.model.scenario;

public abstract class AbstractScenario {
    // dialogue for before and after the battle
    private AbstractDialogue preBattle;
    private AbstractDialogue postBattle;
    private Battle battle;
    private AbstractScenario nextScenario;

    public AbstractScenario(Battle battle, AbstractDialogue preBattle, 
            AbstractDialogue postBattle, AbstractScenario nextScenario) {
        this.preBattle = preBattle;
        this.postBattle = postBattle;
        this.battle = battle;
        this.nextScenario = nextScenario;
    }

    public AbstractDialogue getNextDialogue() {
        if (battle.isOver()) {
            return preBattle.getNext();
        }
        return postBattle.getNext();
    }

    public AbstractScenario getNextScenario() {
        return nextScenario;
    }

    public void setNextScenario(AbstractScenario nextScenario) {
        this.nextScenario = nextScenario;
    }

    public Battle getBattle() {
        return battle;
    }
}
