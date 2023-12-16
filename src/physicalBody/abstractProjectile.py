from src.physicalBody.abstractPhysicalBody import abstractPhysicalBody

class abstractProjectile(abstractPhysicalBody):
    def __init__(self, p, v, spellCard, owner) -> None:
        super().__init__(p, v)
        self.effect = None
        self.damage = 0
        self.priority = 0 # 'weight' of projectile
        self.__spellcard = spellCard
        self.__owner = owner
        
    def effect(self):
        pass
    
    def destroy(self):
        self.__spellcard.remove(self)
    
    def collideProjectile(self, otherProjectile):
        self.priority -= otherProjectile.priority
        if self.priority <= 0:
            self.destroy()