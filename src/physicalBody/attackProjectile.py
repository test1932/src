from effects.knockBack import knockback
from physicalBody.abstractProjectile import abstractProjectile

class attackProjectile(abstractProjectile):
    def __init__(self, p, v, spellCard, owner, damage, hitbox, image, direction) -> None:
        super().__init__(p, v, spellCard, owner, hitbox, image, direction)
        self.damage = damage
        
    def applyEffect(self):
        opp = self.getOwner().getOpponent()
        opp.decrementHealth(self.damage)
        opp.addEffect(knockback(
            opp, 0.1, (100,0) if opp.isFacingLeft() else (-100,0)
        ))
        self.destroy()

class meleeProjectile(attackProjectile):
    ID = -1
    def __init__(self, p, v, spellCard, owner, damage, hitbox, image, direction) -> None:
        super().__init__(p, v, spellCard, owner, damage, hitbox, image, direction)
        
    def applyEffect(self):
        super().applyEffect()
        self.getSpellCard().removeAll()
        self.getSpellCard().end()
        # TODO knockback/wall slam/stun

class rangedProjectile(attackProjectile):
    def __init__(self, p, v, spellCard, owner, damage, hitbox, image, direction) -> None:
        super().__init__(p, v, spellCard, owner, damage, hitbox, image, direction)
        
    def applyEffect(self):
        super().applyEffect()
        self.destroy()