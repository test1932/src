from threading import Lock, Thread
from physicalBody.abstractPhysicalBody import abstractPhysicalBody

class abstractPlayer(Thread, abstractPhysicalBody):
    LEFT = 0
    RIGHT = 1
    
    MAX_HEALTH = 1000
    MAX_MANA = 1000
    
    #
    
    def __init__(self, gameObj) -> None:
        abstractPhysicalBody.__init__(self, (0,0), (0,0))
        self.animationNumber = 0
        self.animationFrame = 0
        self.timeSinceLastFrame = 0
        self.character = None # character class for move set and skills
        self.health = 1000
        self.mana = 1000
        self.facingDirection = abstractPlayer.RIGHT
        
        self.__gameObj = gameObj
        self.__spellcardLock = Lock()

        self.playerSpells = []
        
    def getImage(self):
        """acquires the composited image of the player with effects.
        returns tuple of (image, yoff)

        Raises:
            NotImplementedError: needs to be overridden by subclasses
        """
        image, self.animationFrame = self.character.getFrame(self.animationNumber, self.animationFrame,\
            flip = self.facingDirection == abstractPlayer.LEFT)
        return image
        
    
    def getHealth(self):
        return self.health
    
    def run(self):
        """run on a separate thread for affecting change to the model.

        Raises:
            NotImplementedError: needs to be overridden by subclasses
        """
        raise NotImplementedError("need to implement this method")
    
    def setCharacter(self, character):
        self.character = character(self.__gameObj)
        
    def lockSpellCards(self):
        self.__spellcardLock.acquire()
        
    def unlockSpellCards(self):
        self.__spellcardLock.release()
        
    def getSpellCards(self):
        return self.playerSpells