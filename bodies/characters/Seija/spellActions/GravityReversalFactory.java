package bodies.characters.Seija.spellActions;

import actions.AbstractSpellAction;
import actions.ISpellActionFactory;
import bodies.characters.AbstractPlayer;

public class GravityReversalFactory implements ISpellActionFactory {

    public static class GravityReversal extends AbstractSpellAction {

        public GravityReversal(AbstractPlayer player) {
            super(player, 300l, 1000, true);
            castTime = 0l;
        }

        @Override
        protected void startAction() {
            getOwner().reverseGravity();
        }

        @Override
        protected void endAction() {
            removeAllProjectiles();
        }

        @Override
        public void updateProjectilePositions(Long curTime) {
            //pass
        }
        
    }

    private AbstractPlayer player;

    public GravityReversalFactory(AbstractPlayer player) {
        this.player = player;
    }

    @Override
    public AbstractSpellAction newSpell() {
        return new GravityReversal(player);
    }
}
