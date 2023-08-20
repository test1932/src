// package
package actions;

import java.util.ArrayList;
// standard library imports
import java.util.concurrent.locks.ReentrantLock;

// local imports
import bodies.projectiles.AbstractProjectile;
import game.model.scenario.Battle;
import bodies.characters.AbstractPlayer;

/**
 * @author: ~Yukarin~ <3
 * abstract class representing actions which players can perform in battle.
 */
public abstract class AbstractSpellAction extends Thread{
    protected Long durationRem; //To be set in subclasses
    private ArrayList<AbstractProjectile> projectiles = new ArrayList<AbstractProjectile>();
    private AbstractPlayer owner;
    private long coolDown;
    private Battle bat;
    protected long castTime = 0l;

    public ReentrantLock projectileLock = new ReentrantLock();

    public AbstractSpellAction(AbstractPlayer player, long coolDown, Battle bat, long durationRem) {
        this.owner = player;
        this.coolDown = coolDown;
        this.bat = bat;
        this.durationRem = durationRem;
        bat.spellActionLock.lock();
        bat.spellActions.add(this);
        bat.spellActionLock.unlock();
    }

    public void run() {
        handleManaBlocking();
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

    private void handleManaBlocking() {
        owner.setManaBlocked(true);
        try {
            Thread.sleep(castTime);
        } catch (InterruptedException e) {
            System.err.println("cast time thread failed to sleep");
        }
        owner.setManaBlocked(false);
    }

    protected abstract void startAction();

    protected abstract void endAction();
    

    public void collision(AbstractPlayer player) {
        for (int i = 0; i < projectiles.size(); i++) {
            if (projectiles.get(i).collides(player.hitbox)) {
                projectiles.get(i).collisionEffect(player);
            }
        }
    }

    public abstract void updateProjectilePositions(Long curTime);

    public ArrayList<AbstractProjectile> getProjectiles() {
        return projectiles;
    }

    public void addProjectile(AbstractProjectile projectile) {
        projectileLock.lock();
        this.projectiles.add(projectile);
        projectileLock.unlock();
    }

    public void removeProjectile(AbstractProjectile projectile) {
        projectileLock.lock();
        this.projectiles.remove(projectile);
        projectileLock.unlock();
    }

    public void removeAllProjectiles() {
        bat.spellActionLock.lock();
        this.bat.spellActions.remove(this);
        bat.spellActionLock.unlock();
        projectileLock.lock();
        this.projectiles = null;
        projectileLock.unlock();
    }

    public AbstractPlayer getOwner() {
        return owner;
    }

    public long getCoolDown() {
        return coolDown;
    }
}