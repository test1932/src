package bodies.characters.Misc.spellActions;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Queue;

import actions.AbstractSpellAction;
import actions.ISpellActionFactory;
import bodies.characters.AbstractPlayer;
import bodies.characters.Misc.projectiles.MeleeProjectile;
import effects.Stun;

public class BasicMeleeFactory implements ISpellActionFactory {
    private final long COMBO_TIMEOUT = 500l;
    private int countInCombo = 0;
    private Queue<Long> times = new LinkedList<Long>();

    public abstract static class AbstractBasicMelee extends AbstractSpellAction {
        protected Long[] times = new Long[]{250l, 200l, 170l};
        protected Integer curTimeIndex = 0;
        protected MeleeProjectile[] projectiles;

        protected abstract  void setupProjectiles();
        protected abstract void setProjectilePositions();

        public AbstractBasicMelee(AbstractPlayer player, boolean isKnockback) {
            super(player, 300l, 300l, false);
            defineProjectiles(isKnockback);
            castTime = 0l;
        }

        private void defineProjectiles(boolean isKnockback) {
            projectiles = new MeleeProjectile[] {
                new MeleeProjectile(isKnockback, 100, getOwner(), getOwner().getPosition(), this),
                new MeleeProjectile(isKnockback, 100, getOwner(), getOwner().getPosition(), this),
                new MeleeProjectile(isKnockback, 100, getOwner(), getOwner().getPosition(), this)
            };
        }

        @Override
        protected void startAction() {
            getOwner().applyNewEffect(new Stun(500l));
            setupProjectiles();
        }

        @Override
        protected void endAction() {
            removeAllProjectiles();
        }

        @Override
        public void updateProjectilePositions(Long curTime) {
            if (curTimeIndex == null) return;
            if (durationRem < times[curTimeIndex]) {
                if (curTimeIndex >= projectiles.length) {
                    curTimeIndex = null;
                    return;
                }
                addProjectile(projectiles[curTimeIndex]);
                curTimeIndex = curTimeIndex == projectiles.length - 1 ? null : curTimeIndex + 1;
            }
            setProjectilePositions();
        }
        
    }

    public static class BasicMelee1 extends AbstractBasicMelee {
        public BasicMelee1(AbstractPlayer player) {
            super(player, false);
            castTime = 0l;
        }

        protected void setupProjectiles() {
            projectiles[0].setHitbox(new Rectangle(0, 0, 40, 40));
            projectiles[1].setHitbox(new Rectangle(0, 0, 30, 40));
            projectiles[2].setHitbox(new Rectangle(0, 0, 20, 40));
        }

        protected void setProjectilePositions() {
            Integer[] playerPos = getOwner().getPosition();
            int multiplier = getOwner().getFacingDirection() ? -1 : 1;
            projectiles[0].setPosition(new Integer[]{playerPos[0] + (30 * multiplier), playerPos[1] + 45});
            projectiles[1].setPosition(new Integer[]{playerPos[0] + (45 * multiplier), playerPos[1] + 15});
            projectiles[2].setPosition(new Integer[]{playerPos[0] + (55 * multiplier), playerPos[1]});
        }
    }

    public static class BasicMelee2 extends AbstractBasicMelee {
        public BasicMelee2(AbstractPlayer player) {
            super(player, false);
            castTime = 0l;
        }

        protected void setupProjectiles() {
            projectiles[0].setHitbox(new Rectangle(0, 0, 40, 40));
            projectiles[1].setHitbox(new Rectangle(0, 0, 30, 40));
            projectiles[2].setHitbox(new Rectangle(0, 0, 20, 40));
        }

        protected void setProjectilePositions() {
            Integer[] playerPos = getOwner().getPosition();
            int multiplier = getOwner().getFacingDirection() ? -1 : 1;
            projectiles[0].setPosition(new Integer[]{playerPos[0] + (30 * multiplier), playerPos[1]});
            projectiles[1].setPosition(new Integer[]{playerPos[0] + (45 * multiplier), playerPos[1] + 15});
            projectiles[2].setPosition(new Integer[]{playerPos[0] + (55 * multiplier), playerPos[1] + 45});
        }
    }

    public static class BasicMelee3 extends AbstractBasicMelee {
        public BasicMelee3(AbstractPlayer player) {
            super(player, true);
            castTime = 0l;
        }

        @Override
        protected void setupProjectiles() {
            projectiles[0].setHitbox(new Rectangle(0, 0, 20, 40));
            projectiles[1].setHitbox(new Rectangle(0, 0, 40, 20));
            projectiles[2].setHitbox(new Rectangle(0, 0, 70, 20));
        }

        @Override
        protected void setProjectilePositions() {
            Integer[] playerPos = getOwner().getPosition();
            int multiplier = getOwner().getFacingDirection() ? -1 : 1;
            projectiles[0].setPosition(new Integer[]{playerPos[0] + (30 * multiplier), playerPos[1] + 20});
            projectiles[1].setPosition(new Integer[]{playerPos[0] + (45 * multiplier), playerPos[1] + 30});
            projectiles[2].setPosition(new Integer[]{playerPos[0] + (55 * multiplier), playerPos[1] + 30});
        }
    }






    private AbstractPlayer player;

    public BasicMeleeFactory(AbstractPlayer player) {
        this.player = player;
    }

    @Override
    public AbstractSpellAction newSpell() {
        if (countInCombo == 0) {
            times.add(System.currentTimeMillis());
            countInCombo++;
        }
        else if (System.currentTimeMillis() > times.peek() + ((countInCombo + 1) * COMBO_TIMEOUT)
                || countInCombo >= 3) {
            countInCombo = 1;
            times.clear();
            times.add(System.currentTimeMillis());
        }
        else {
            countInCombo++;
            times.add(System.currentTimeMillis());
        }
        return correspondingAttack();
    }

    //TODO melee attacks
    private AbstractSpellAction correspondingAttack() {
        switch (countInCombo) {
            case 1:
                return new BasicMelee1(player);
            
            case 2:
                return new BasicMelee2(player);
        
            case 3:
                return new BasicMelee3(player);

            default:
                return null;
        }
    }
}
