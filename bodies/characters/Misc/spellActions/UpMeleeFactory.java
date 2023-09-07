package bodies.characters.Misc.spellActions;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Queue;

import actions.AbstractSpellAction;
import actions.ISpellActionFactory;
import bodies.characters.AbstractPlayer;
import bodies.characters.Misc.projectiles.MeleeProjectile;

public class UpMeleeFactory implements ISpellActionFactory {
    public static class UpMelee extends BasicMeleeFactory.AbstractBasicMelee {
        public UpMelee(AbstractPlayer player) {
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
            projectiles[0].setPosition(new Integer[]{playerPos[0] + (10 * multiplier), playerPos[1] - 25});
            projectiles[1].setPosition(new Integer[]{playerPos[0] + (30 * multiplier), playerPos[1] - 20});
            projectiles[2].setPosition(new Integer[]{playerPos[0] + (50 * multiplier), playerPos[1] - 10});
        }
    }

    private AbstractPlayer player;

    public UpMeleeFactory(AbstractPlayer player) {
        this.player = player;
    }

    @Override
    public AbstractSpellAction newSpell() {
        return new UpMelee(player);
    }
}