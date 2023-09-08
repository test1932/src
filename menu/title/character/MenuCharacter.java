package menu.title.character;

import java.util.ArrayList;

import bodies.characters.AbstractCharacter;
import bodies.characters.AbstractPlayer;
import bodies.characters.Sakuya.Sakuya;
import bodies.characters.Sakuya.SakuyaScenarioFactory;
import bodies.characters.Seija.Seija;
import bodies.characters.Seija.SeijaScenarioFactory;
import game.Game;
import game.model.Pair;
import game.model.scenario.AbstractScenarioFactory;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.OptionBack;

public class MenuCharacter extends AbstractMenu {
    private ArrayList<Pair<AbstractScenarioFactory, AbstractCharacter>> names = new ArrayList<Pair<AbstractScenarioFactory, AbstractCharacter>>();
    private AbstractPlayer player;

    private void setupCharacters() {
        names.add(new Pair<AbstractScenarioFactory,AbstractCharacter>(new SakuyaScenarioFactory(game.getCont()), new Sakuya(player)));
        names.add(new Pair<AbstractScenarioFactory,AbstractCharacter>(new SeijaScenarioFactory(game.getCont()), new Seija(player)));
    }

    public MenuCharacter(Game game, AbstractMenu prevMenu, AbstractPlayer player) {
        super(game, "Character", prevMenu, "assets/images/backgrounds/character.jpg");
        this.selectedIndex = 0;
        this.player = player;
        setupCharacters();
        options = new AbstractOption[names.size() + 1];

        int i = 0;
        for (Pair<AbstractScenarioFactory, AbstractCharacter> name : names) {
            options[i++] = new CharacterOption(game, this, name.snd.getName(), player, name.snd, name.fst);
        }

        options[names.size()] = new OptionBack(game, this);
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
