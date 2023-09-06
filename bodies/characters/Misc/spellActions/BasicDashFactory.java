package bodies.characters.Misc.spellActions;

import actions.AbstractSpellAction;
import actions.ISpellActionFactory;
import bodies.Direction;
import bodies.characters.AbstractPlayer;
import effects.IEffect;
import effects.Invulnerable;
import effects.Stun;

public class BasicDashFactory implements ISpellActionFactory {
    protected Direction direction;

    public static class BasicDash extends AbstractSpellAction {
        protected Direction dir;
        protected double multiplier = 1d;
        private Double[] velocity;

        public BasicDash(AbstractPlayer player, Direction dir) {
            super(player, 500l, 500l, true);
            this.dir = dir;
            this.velocity = dirToVelocity();
            castTime = 0l;
        }

        private Double[] dirToVelocity() {
            boolean facingLeft = getOwner().getFacingDirection();
            double back = facingLeft? multiplier : -multiplier;

            switch(dir) {
                case Back:
                    return new Double[]{back, 0d};
                case Back_Down:
                    return new Double[]{back, multiplier};
                case Back_Up:
                    return new Double[]{back, -multiplier * 1.2};
                case Down:
                    return new Double[]{0d, multiplier};
                case Forward:
                    return new Double[]{-1 * back, 0d};
                case Forward_Down:
                    return new Double[]{-1 * back, multiplier};
                case Forward_Up:
                    return new Double[]{-1 * back, -multiplier * 1.2};
                case Up:
                    return new Double[]{0d, -multiplier * 1.2};
            }
            throw new RuntimeException("I haven't a clue how you got here");
        }

        @Override
        protected void startAction() {
            getOwner().setVelocity(velocity);
            // System.out.println( getOwner().getVelocity());
            IEffect stunEffect = new Stun(this.durationRem);
            IEffect invulnerableEffect = new Invulnerable(this.durationRem);
            getOwner().applyNewEffect(stunEffect);
            getOwner().applyNewEffect(invulnerableEffect);
            // System.out.println( getOwner().getVelocity()[0]);
        }

        @Override
        protected void endAction() {
            removeAllProjectiles();
        }

        @Override
        public void updateProjectilePositions(Long curTime) {
            
        }
        
    }

    private AbstractPlayer player;

    public BasicDashFactory(AbstractPlayer player, Direction dir) {
        this.player = player;
        this.direction = dir;
    }

    @Override
    public AbstractSpellAction newSpell() {
        // System.out.println("new dash");
        return new BasicDash(player, direction);
    }
}
