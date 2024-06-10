from physicalBody.abstractPhysicalBody import abstractPhysicalBody

class abstractProjectile(abstractPhysicalBody):
    #will have static list of frames per projectile type
    #will have static ID per projectile type
    frames = [] # loaded in with setFrames()
    
    def __init__(self, p, v, spellCard, owner, hitbox, image, direction) -> None:
        super().__init__(p, v, hitbox, image, direction)
        self.effect = None
        self.__isDestroyed = False
        self.__spellcard = spellCard
        self.__owner = owner
        self.frameNo = 0
        self.__spellcard.add(self)
        
    def isDestroyed(self):
        return self.isDestroyed
        
    def applyEffect(self):
        pass
    
    def getOwner(self):
        return self.__owner
    
    def getFrameNo(self):
        return self.frameNo
    
    def getFrame(self, index):
        return self.frames[index]
    
    def destroy(self):
        self.__isDestroyed = True
        self.__spellcard.remove(self)
        
    def getSpellCard(self):
        return self.__spellcard
    
    def collideProjectile(self, otherProjectile):
        self.destroy()
            
    def getFrameNo(self):
        return self.frameNo