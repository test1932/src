package bodies.characters.Sakuya.spellActions;

import actions.AbstractSpellAction;
import actions.ISpellActionFactory;
import bodies.characters.AbstractPlayer;
import bodies.projectiles.AbstractProjectile;
import bodies.projectiles.Projectile_Knife;

public class TestSpellActionFactory implements ISpellActionFactory {
    public class TestSpellAction extends AbstractSpellAction {

        public TestSpellAction(AbstractPlayer player) {
            super(player, 300l, 1000);
            castTime = 0l;
        }

        @Override
        protected void startAction() {
            double x = getOwner().getFacingDirection() ? -1.0 : 1.0;
            AbstractProjectile testProjectile = new Projectile_Knife(100, null, this.getOwner(), 
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

    public TestSpellActionFactory(AbstractPlayer player) {
        this.player = player;
    }

    @Override
    public AbstractSpellAction newSpell() {
        return new TestSpellAction(player);
    }
}
