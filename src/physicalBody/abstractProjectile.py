from physicalBody.abstractPhysicalBody import abstractPhysicalBody

class abstractProjectile(abstractPhysicalBody):
    #will have static list of frames per projectile type
    #will have static ID per projectile type
    
    def __init__(self, p, v, spellCard, owner, hitbox, image) -> None:
        super().__init__(p, v, hitbox, image)
        self.effect = None
        self.damage = 0
        self.priority = 0 # 'weight' of projectile
        self.__spellcard = spellCard
        self.__owner = owner
        self.frames = []
        self.frameNo = 0
        self.__spellcard.add(self)
        
    def applyEffect(self):
        pass
    
    def getOwner(self):
        return self.__owner
    
    def getFrameNo(self):
        return self.frameNo
    
    def getFrame(self, index):
        return self.frames[index]
    
    def destroy(self):
        self.__spellcard.remove(self)
        
    def getSpellCard(self):
        return self.__spellcard
    
    def collideProjectile(self, otherProjectile):
        self.priority -= otherProjectile.priority
        if self.priority <= 0:
            self.destroy()