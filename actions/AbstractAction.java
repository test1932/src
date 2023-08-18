// package
package actions;

// standard library imports
import java.util.LinkedList;

// local imports
import bodies.projectiles.AbstractProjectile;
import bodies.characters.AbstractPlayer;

/**
 * @author: ~Yukarin~ <3
 * abstract class representing actions which players can perform in battle.
 */
public abstract class AbstractAction extends Thread{
    protected Long durationRem; //To be set in subclasses
    private LinkedList<AbstractProjectile> projectiles;
    private AbstractPlayer player;

    public AbstractAction(AbstractPlayer player) {
        this.player = player;
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
    }

    public AbstractPlayer getPlayer() {
        return player;
    }
}