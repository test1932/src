package bodies.characters.Sakuya.spellActions;

import actions.AbstractSpellAction;
import actions.AbstractSpellActionFactory;
import bodies.characters.AbstractPlayer;
import bodies.projectiles.AbstractProjectile;
import bodies.projectiles.Projectile_Knife;
import game.model.Battle;

public class TestSpellActionFactory extends AbstractSpellActionFactory {
    public class TestSpellAction extends AbstractSpellAction {

        public TestSpellAction(AbstractPlayer player, Battle bat) {
            super(player, 300l, bat, 1000);
        }

        @Override
        protected void startAction() {
            double x = getOwner().getFacingDirection() ? -1.0 : 1.0;
            AbstractProjectile testProjectile = new Projectile_Knife(10, null, this.getOwner(), 
                this.getOwner().getPosition(), new Double[]{x,-0.4}, this);
            addProjectile(testProjectile);
        }

        @Override
        protected void endAction() {
            removeAllProjectiles();
        }

        @Override
        public void updateProjectilePositions(Long curTime) {
            // TODO 
        }
        
    }

    private AbstractPlayer player;
    private Battle bat;

    public TestSpellActionFactory(AbstractPlayer player, Battle bat) {
        this.player = player;
        this.bat = bat;
    }

    @Override
    public AbstractSpellAction newSpell() {
        return new TestSpellAction(player, bat);
    }
}
