from effects.knockBack import knockback
from effects.wallslam import wallslam
from physicalBody.abstractProjectile import abstractProjectile

class attackProjectile(abstractProjectile):
    def __init__(self, p, v, spellCard, owner, damage, hitbox, image, direction) -> None:
        super().__init__(p, v, spellCard, owner, hitbox, image, direction)
        self.damage = damage
        
    def applyEffect(self):
        opp = self.getOwner().getOpponent()
        opp.decrementHealth(self.damage)
        opp.addEffect(wallslam(#TODO
            opp, 0.1, (1500,-500) if opp.isFacingLeft() else (-1500,-500)
        ))
        opp.setStun(0.5)
        self.destroy()

class meleeProjectile(attackProjectile):
    ID = -1
    def __init__(self, p, v, spellCard, owner, damage, hitbox, image, direction) -> None:
        super().__init__(p, v, spellCard, owner, damage, hitbox, image, direction)
        
    def applyEffect(self):
        super().applyEffect()
        self.getSpellCard().removeAll()
        self.getSpellCard().end()

class wallSlamProjectile(attackProjectile):
    def __init__(self, p, v, spellCard, owner, damage, hitbox, image, direction) -> None:
        super().__init__(p, v, spellCard, owner, damage, hitbox, image, direction)
    
    def applyEffect(self):
        opp = self.getOwner().getOpponent()
        opp.decrementHealth(self.damage)
        opp.addEffect(wallslam(
            opp, 0.1, (100,0) if opp.isFacingLeft() else (-100,0)
        ))
        self.destroy()

class rangedProjectile(attackProjectile):
    def __init__(self, p, v, spellCard, owner, damage, hitbox, image, direction, mass) -> None:
        super().__init__(p, v, spellCard, owner, damage, hitbox, image, direction)
        self.mass = mass
        
    def applyEffect(self):
        super().applyEffect()
        self.destroy()
    
    #override
    def collideProjectile(self, otherProjectile):
        if self.mass <= 0:
            self.destroy()
            return
        
        if otherProjectile.isDestroyed():
            return
        
        if isinstance(otherProjectile, meleeProjectile):
            return
        if isinstance(otherProjectile, rangedProjectile):
            mass = self.mass
            self.mass -= otherProjectile.mass
            otherProjectile.mass -= mass
        
        if self.mass <= 0:
            self.destroy()
            return