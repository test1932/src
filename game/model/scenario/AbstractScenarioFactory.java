package game.model.scenario;

import game.model.Controller;

//fake factory
public abstract class AbstractScenarioFactory {
    protected Battle battle;
    protected AbstractScenario next;
    protected Controller cont;

    public AbstractScenarioFactory(AbstractScenario nextScenario, Controller cont) {
        this.next = nextScenario;
        this.cont = cont;
    }

    public abstract AbstractScenario makeScenario();
}
