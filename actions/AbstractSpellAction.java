// package
package actions;

// standard library imports
import java.util.LinkedList;

// local imports
import bodies.projectiles.AbstractProjectile;
import game.model.Battle;
import bodies.characters.AbstractPlayer;

/**
 * @author: ~Yukarin~ <3
 * abstract class representing actions which players can perform in battle.
 */
public abstract class AbstractSpellAction extends Thread{
    protected Long durationRem; //To be set in subclasses
    private LinkedList<AbstractProjectile> projectiles = new LinkedList<AbstractProjectile>();
    private AbstractPlayer owner;
    private long coolDown;
    private Battle bat;

    public AbstractSpellAction(AbstractPlayer player, long coolDown, Battle bat, long durationRem) {
        this.owner = player;
        this.coolDown = coolDown;
        this.bat = bat;
        this.durationRem = durationRem;
        bat.spellActions.add(this);
    }

    public void run() {
        startAction();
        long lastTime = System.currentTimeMillis();
        long timeDiff;
        while (durationRem > 0) {
            timeDiff = System.currentTimeMillis() - lastTime;
            lastTime = System.currentTimeMillis();
            durationRem -= timeDiff;
            updateProjectilePositions(durationRem);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.err.println("failed to sleep spell Action");
            }
        }
        endAction();
    }

    protected abstract void startAction();

    protected abstract void endAction();
    

    public void collision(AbstractPlayer player) {
        for (AbstractProjectile projectile : projectiles) {
            if (projectile.collides(player.hitbox)) {
                projectile.collisionEffect();
            }
        }
    }

    public abstract void updateProjectilePositions(Long curTime);

    public LinkedList<AbstractProjectile> getProjectiles() {
        return projectiles;
    }

    public void addProjectile(AbstractProjectile projectile) {
        this.projectiles.add(projectile);
        bat.bodiesLock.lock();
        bat.bodies.add(projectile);
        bat.bodiesLock.unlock();
    }

    public void removeProjectile(AbstractProjectile projectile) {
        this.projectiles.remove(projectile);
        bat.bodiesLock.lock();
        bat.bodies.remove(projectile);
        bat.bodiesLock.unlock();
    }

    public void removeAllProjectiles() {
        for (AbstractProjectile projectile : projectiles) {
            bat.bodiesLock.lock();
            bat.bodies.remove(projectile);
            bat.bodiesLock.unlock();
        }
        this.bat.spellActions.remove(this);
        this.projectiles = null;
    }

    public AbstractPlayer getOwner() {
        return owner;
    }

    public long getCoolDown() {
        return coolDown;
    }
}