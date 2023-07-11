package actions;

import java.util.LinkedList;

import bodies.projectiles.ProjectileA;
import bodies.characters.PlayerA;

public abstract class ActionA extends Thread{
    protected Long durationRem; //To be set in subclasses
    private LinkedList<ProjectileA> projectiles;
    private PlayerA player;

    public ActionA(PlayerA player) {
        this.player = player;
    }

    protected abstract void startAction();

    protected abstract void endAction();

    public void collision(PlayerA player) {
        for (ProjectileA projectile : projectiles) {
            if (projectile.collides(player.hitbox)) {
                projectile.collisionEffect();
            }
        }
    }

    public abstract void updateProjectilePositions(Long curTime);

    public LinkedList<ProjectileA> getProjectiles() {
        return projectiles;
    }

    public void addProjectile(ProjectileA projectile) {
        this.projectiles.add(projectile);
    }

    public PlayerA getPlayer() {
        return player;
    }
}