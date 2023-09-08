package game.model.scenario;

import game.model.Controller;

//TODO dialogue needs to be moved to files
public abstract class AbstractScenario {
    public enum ScenarioState{PRE_BATTLE, BATTLE, POST_BATTLE, SETUP};

    // dialogue for before and after the battle
    protected Dialogue preBattle;
    protected Dialogue postBattle;
    private Battle battle;
    private AbstractScenario nextScenario;
    private ScenarioState curScenarioState = ScenarioState.SETUP;

    public AbstractScenario(Battle battle, AbstractScenario nextScenario, Controller cont) {
        this.battle = battle;
        this.nextScenario = nextScenario;
    }

    public abstract void setCharacters();

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

    public void setCurScenarioState(ScenarioState curScenarioState) {
        this.curScenarioState = curScenarioState;
    }

    public void setup() {
        setCharacters();
        this.curScenarioState = ScenarioState.PRE_BATTLE;
    }
}
