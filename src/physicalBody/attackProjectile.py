from physicalBody.abstractProjectile import abstractProjectile

class attackProjectile(abstractProjectile):
    def __init__(self, p, v, spellCard, owner, damage) -> None:
        super().__init__(p, v, spellCard, owner)
        self.damage = damage
        
    def effect(self):
        self.getOwner().getOpponent().decrementHealth(self.damage)
        self.destroy()

class meleeProjectile(attackProjectile):
    def __init__(self, p, v, spellCard, owner, damage) -> None:
        super().__init__(p, v, spellCard, owner, damage)
        
    def effect(self):
        self.getOwner().getOpponent().decrementHealth(self.damage)
        # TODO knockback/wall slam/stun
        self.destroy()

class rangedProjectile(attackProjectile):
    def __init__(self, p, v, spellCard, owner, damage) -> None:
        super().__init__(p, v, spellCard, owner, damage)
        
    def effect(self):
        if not self.getOwner().getOpponent().isDashing():
            self.getOwner().getOpponent().decrementHealth(self.damage)
            # TODO minor knockback, stun
        else:
            self.getOwner().getOpponent().decrementMana(self.damage)
        self.destroy()