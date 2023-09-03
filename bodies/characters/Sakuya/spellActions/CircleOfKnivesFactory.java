package bodies.characters.Sakuya.spellActions;

import actions.AbstractSpellAction;
import actions.ISpellActionFactory;
import bodies.characters.AbstractPlayer;
import bodies.projectiles.AbstractProjectile;
import bodies.projectiles.Projectile_Knife;

public class CircleOfKnivesFactory implements ISpellActionFactory {
    public static class CircleOfKnives extends AbstractSpellAction {

        private int count = 20;

        public CircleOfKnives(AbstractPlayer player) {
            super(player, 300l, 2000, true);
            castTime = 0l;
        }

        @Override
        protected void startAction() {
            //pass
        }

        @Override
        protected void endAction() {
            removeAllProjectiles();
        }

        @Override
        public void updateProjectilePositions(Long curTime) {
            if (count * 25 + 1500 > durationRem && count > 0) {
                count--;
                double angle = count * Math.PI / 6;
                AbstractProjectile p = new Projectile_Knife(0, 0.9, false, 20, this.getOwner(), 
                    this.getOwner().getPosition(), new Double[]{Math.cos(angle) / 2 , Math.sin(angle) / 2}, this);
                addProjectile(p);
            }
        }
        
    }

    private AbstractPlayer player;

    public CircleOfKnivesFactory(AbstractPlayer player) {
        this.player = player;
    }

    @Override
    public AbstractSpellAction newSpell() {
        return new CircleOfKnives(player);
    }
}
