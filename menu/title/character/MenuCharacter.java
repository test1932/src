package menu.title.character;

import java.util.ArrayList;

import bodies.characters.AbstractCharacter;
import bodies.characters.AbstractPlayer;
import bodies.characters.Sakuya.Sakuya;
import bodies.characters.Sakuya.SakuyaScenario;
import game.Game;
import game.model.Pair;
import game.model.scenario.AbstractScenario;
import game.model.scenario.Battle;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.OptionBack;

public class MenuCharacter extends AbstractMenu {
    private ArrayList<Pair<AbstractScenario, AbstractCharacter>> names = new ArrayList<Pair<AbstractScenario, AbstractCharacter>>();
    private AbstractPlayer player;

    private void setupCharacters() {
        names.add(new Pair<AbstractScenario,AbstractCharacter>(new SakuyaScenario(new Battle(game.getCont()), null), new Sakuya(player)));
    }

    public MenuCharacter(Game game, AbstractMenu prevMenu, AbstractPlayer player) {
        super(game, "Character", prevMenu, "assets/images/backgrounds/character.jpg");
        this.selectedIndex = 0;
        this.player = player;
        setupCharacters();
        options = new AbstractOption[names.size() + 1];

        int i = 0;
        for (Pair<AbstractScenario, AbstractCharacter> name : names) {
            options[i] = new CharacterOption(game, this, name.snd.getName(), player, name.snd, name.fst);
        }

        options[names.size()] = new OptionBack(game, this);
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
