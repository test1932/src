package bodies.characters.Seija;

import game.model.Controller;
import game.model.scenario.AbstractScenario;
import game.model.scenario.AbstractScenarioFactory;

public class SeijaScenarioFactory extends AbstractScenarioFactory{

    public SeijaScenarioFactory(AbstractScenario nextScenario, Controller cont) {
        super(nextScenario, cont);
        //TODO Auto-generated constructor stub
    }

    @Override
    public AbstractScenario makeScenario() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'makeScenario'");
    }
    
}
