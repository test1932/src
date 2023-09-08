package game.model.scenario;

import game.model.Controller;

//fake factory
public abstract class AbstractScenarioFactory {
    protected Controller cont;

    public AbstractScenarioFactory(Controller cont) {
        this.cont = cont;
    }

    public abstract AbstractScenario makeScenario();
}
