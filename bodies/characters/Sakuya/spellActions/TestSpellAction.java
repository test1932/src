package bodies.characters.Sakuya.spellActions;

import actions.AbstractSpellAction;
import bodies.characters.AbstractPlayer;
import bodies.projectiles.AbstractProjectile;
import bodies.projectiles.Projectile_Knife;

public class TestSpellAction extends AbstractSpellAction {

    public TestSpellAction(AbstractPlayer player) {
        super(player);
    }

    @Override
    protected void startAction() {
        AbstractProjectile testProjectile = new Projectile_Knife(10, null, this.getOwner(), 
            this.getOwner().getPosition(),  this.getOwner().getVelocity(), this);
        addProjectile(testProjectile);
    }

    @Override
    protected void endAction() {
        // TODO 
    }

    @Override
    public void updateProjectilePositions(Long curTime) {
        // TODO 
    }
    
}
