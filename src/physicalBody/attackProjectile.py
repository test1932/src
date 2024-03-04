from physicalBody.abstractProjectile import abstractProjectile

class attackProjectile(abstractProjectile):
    def __init__(self, p, v, spellCard, owner, damage, hitbox, image) -> None:
        super().__init__(p, v, spellCard, owner, hitbox, image)
        self.damage = damage
        
    def applyEffect(self):
        self.getOwner().getOpponent().decrementHealth(self.damage)
        self.destroy()

class meleeProjectile(attackProjectile):
    def __init__(self, p, v, spellCard, owner, damage, hitbox, image) -> None:
        super().__init__(p, v, spellCard, owner, damage, hitbox, image)
        
    def applyEffect(self):
        self.getOwner().getOpponent().decrementHealth(self.damage)
        self.getSpellCard().removeAll()
        self.getSpellCard().end()
        # TODO knockback/wall slam/stun

class rangedProjectile(attackProjectile):
    def __init__(self, p, v, spellCard, owner, damage, hitbox, image) -> None:
        super().__init__(p, v, spellCard, owner, damage, hitbox, image)
        
    def applyEffect(self):
        if not self.getOwner().getOpponent().isDashing():
            self.getOwner().getOpponent().decrementHealth(self.damage)
            # TODO minor knockback, stun
        else:
            self.getOwner().getOpponent().decrementMana(self.damage)
        self.destroy()