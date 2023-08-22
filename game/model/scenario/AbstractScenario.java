package game.model.scenario;

public abstract class AbstractScenario {
    public enum ScenarioState{PRE_BATTLE, BATTLE, POST_BATTLE};

    // dialogue for before and after the battle
    protected Dialogue preBattle;
    protected Dialogue postBattle;
    private Battle battle;
    private AbstractScenario nextScenario;
    private ScenarioState curScenarioState = ScenarioState.PRE_BATTLE;

    public AbstractScenario(Battle battle, AbstractScenario nextScenario) {
        this.battle = battle;
        this.nextScenario = nextScenario;
    }

    public boolean nextDialogue() {
        if (battle.isOver()) {
            postBattle = postBattle.getNext();
            return postBattle == null;
        }
        preBattle = preBattle.getNext();
        if (preBattle == null) curScenarioState = ScenarioState.BATTLE;
        return preBattle == null;
    }

    public Dialogue getDialogue() {
        if (curScenarioState == ScenarioState.PRE_BATTLE) {
            return preBattle;
        }
        return postBattle;
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

    public ScenarioState getCurScenarioState() {
        return curScenarioState;
    }

    public void checkIfBattleOver() {
        battle.checkIfOver();
        if (battle.isOver()) {
            curScenarioState = ScenarioState.POST_BATTLE;
        }
    }
}
