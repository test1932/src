package bodies.characters.Misc.spellActions;

import java.awt.Rectangle;

import actions.AbstractSpellAction;
import actions.ISpellActionFactory;
import bodies.characters.AbstractPlayer;

public class ForwardMeleeFactory implements ISpellActionFactory {
    public static class ForwardMelee extends BasicMeleeFactory.AbstractBasicMelee {
        public ForwardMelee(AbstractPlayer player) {
            super(player, false);
            castTime = 0l;
        }

        @Override
        protected void setupProjectiles() {
            projectiles[0].setHitbox(new Rectangle(0, 0, 40, 30));
            projectiles[1].setHitbox(new Rectangle(0, 0, 40, 20));
            projectiles[2].setHitbox(new Rectangle(0, 0, 30, 20));
        }

        @Override
        protected void setProjectilePositions() {
            Integer[] playerPos = getOwner().getPosition();
            int multiplier = getOwner().getFacingDirection() ? -1 : 1;
            projectiles[0].setPosition(new Integer[]{playerPos[0] + (30 * multiplier), playerPos[1] + 45});
            projectiles[1].setPosition(new Integer[]{playerPos[0] + (50 * multiplier), playerPos[1] + 30});
            projectiles[2].setPosition(new Integer[]{playerPos[0] + (70 * multiplier), playerPos[1] + 15});
        }
    }

    private AbstractPlayer player;

    public ForwardMeleeFactory(AbstractPlayer player) {
        this.player = player;
    }

    @Override
    public AbstractSpellAction newSpell() {
        return new ForwardMelee(player);
    }
}