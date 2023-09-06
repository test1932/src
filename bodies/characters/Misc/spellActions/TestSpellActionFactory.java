package bodies.characters.Misc.spellActions;

import actions.AbstractSpellAction;
import actions.ISpellActionFactory;
import bodies.characters.AbstractPlayer;
import bodies.projectiles.AbstractProjectile;
import bodies.projectiles.Projectile_Knife;

public class TestSpellActionFactory implements ISpellActionFactory {
    public static class TestSpellAction extends AbstractSpellAction {

        public TestSpellAction(AbstractPlayer player) {
            super(player, 300l, 1000, true);
            castTime = 0l;
        }

        @Override
        protected void startAction() {
            double x = getOwner().getFacingDirection() ? -1.0 : 1.0;
            AbstractProjectile testProjectile = new Projectile_Knife(1, 0.5, true, 100, this.getOwner(), 
                this.getOwner().getPosition(), new Double[]{x,-0.4}, this);
            addProjectile(testProjectile);
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

    public TestSpellActionFactory(AbstractPlayer player) {
        this.player = player;
    }

    @Override
    public AbstractSpellAction newSpell() {
        return new TestSpellAction(player);
    }
}
