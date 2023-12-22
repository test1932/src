from threading import Lock
from physicalBody.abstractPhysicalBody import abstractPhysicalBody
from characters.abstractCharacter import abstractCharacter

class abstractPlayer(abstractPhysicalBody):
    LEFT_DIR = 0
    RIGHT_DIR = 1
    
    MAX_HEALTH = 1000
    MAX_MANA = 1000
    
    RIGHT = 0
    LEFT = 1
    UP = 2
    DOWN = 3
    DASH = 4
    MELEE = 5
    WEAK = 6
    STRONG = 7
    
    actionNames = [
        "Right",
        "Left",
        "Up",
        "Down",
        "Dash",
        "Melee",
        "Magic",
        "Special"
    ]
    
    #will have static list of projectile classes
    
    def __init__(self, gameObj) -> None:
        abstractPhysicalBody.__init__(self, (0,0), (0,0))
        self.animationNumber = abstractCharacter.IDLE
        self.animationFrame = 0
        self.timeSinceLastFrame = 0
        self.character = None # character class for move set and skills
        
        self.health = 1000
        self.mana = 1000
        self.dashing = False
        self.effects = []
        self.cooldownTime = 0
        
        self.facingDirection = abstractPlayer.RIGHT_DIR
        self.characterIndex = None
        
        self.heldKeys = []
        self.ignoreKeys = set()
        
        self.__gameObj = gameObj
        self.__spellcardLock = Lock()

        self.playerSpells = []
        
    def isCooldown(self):
        return self.cooldownTime > 0
    
    def holdKey(self, key):
        if not key in self.heldKeys:
            self.heldKeys.append(key)
            
    def releaseKey(self, key):
        self.heldKeys.remove(key)
    
    def setCooldown(self, value):
        self.cooldownTime = value
        
    def isFacingLeft(self):
        return self.facingDirection == abstractPlayer.LEFT_DIR
    
    def setAnimationFrame(self, animationIndex, frameIndex):
        self.animationFrame = frameIndex
        self.animationNumber = animationIndex
    
    def getCharacterIndex(self):
        return self.characterIndex
    
    def getAnimationNo(self):
        return self.animationNumber
    
    def getFrameNo(self):
        return self.animationFrame
    
    def handleUpDownAnimation(self):
        if self.animationNumber == abstractCharacter.UP_LOOP and self.getYVelocity() >= 0:
            if self.getYPosition() < self.__gameObj.HEIGHT // 2:
                self.setAnimation(abstractCharacter.UP_END_INIT)
            else:
                self.setAnimation(abstractCharacter.UP_END_POST)
        elif self.animationNumber == abstractCharacter.DOWN_LOOP and self.getYVelocity() <= 0:
            if self.getYPosition() > self.__gameObj.HEIGHT // 2:
                self.setAnimation(abstractCharacter.DOWN_END_INIT)
            else:
                self.setAnimation(abstractCharacter.DOWN_END_POST)
        
    def getImage(self, isNetwork = False):
        """acquires the composited image of the player with effects.
        returns tuple of (image, yoff)

        Raises:
            NotImplementedError: needs to be overridden by subclasses
        """
        if not isNetwork:
            self.handleUpDownAnimation()
        
        image, self.animationNumber, self.animationFrame = self.character.getFrame(\
            self.animationNumber, self.animationFrame,\
            flip = self.facingDirection == abstractPlayer.LEFT_DIR)
        return image
        
    
    def getHealth(self):
        return self.health
    
    def setCharacter(self, character, i):
        self.character = character(self.__gameObj)
        self.characterIndex = i
        
    def getCharacter(self):
        return self.character
        
    def lockSpellCards(self):
        self.__spellcardLock.acquire()
        
    def unlockSpellCards(self):
        self.__spellcardLock.release()
        
    def getSpellCards(self):
        return self.playerSpells
    
    def setFacingDirection(self, direction):
        self.facingDirection = direction
    
    def flipFacingDirection(self):
        #TODO something about turn animation
        self.setAnimation(abstractCharacter.TURN)
        self.facingDirection = abstractPlayer.LEFT_DIR if \
            self.facingDirection == abstractPlayer.RIGHT_DIR else abstractPlayer.RIGHT_DIR
            
    def getFacingDirection(self):
        return self.facingDirection
    
    def setAnimation(self, animationNo):
        self.animationFrame = 0
        self.animationNumber = animationNo
        
    def getGravity(self):
        return self.character.getGravity()
    
    def handleHeldKeys(self):
        if self.isCooldown():
            return
        
        if abstractPlayer.UP in self.heldKeys and self.getYPosition() == self.__gameObj.HEIGHT // 2:
            self.setYVelocity(-self.character.getJump())
            self.setAnimation(abstractCharacter.UP_START_INIT)
            
        elif abstractPlayer.DOWN in self.heldKeys and self.getYPosition() == self.__gameObj.HEIGHT // 2:
            self.setYVelocity(self.character.getJump())
            self.setAnimation(abstractCharacter.DOWN_START_INIT)
        
        self.handleHorizontalMovement()

    def handleHorizontalMovement(self):
        if abstractPlayer.RIGHT in self.heldKeys:
            self.setXVelocity(300)
            if not self.isFacingLeft() and self.animationNumber in [abstractCharacter.IDLE, abstractCharacter.BACK_START, abstractCharacter.BACK_LOOP, abstractCharacter.BACK_END]:
                self.setAnimation(abstractCharacter.FORWARD_START)
            elif self.isFacingLeft() and self.animationNumber in [abstractCharacter.IDLE, abstractCharacter.FORWARD_START, abstractCharacter.FORWARD_LOOP, abstractCharacter.FORWARD_END]:
                self.setAnimation(abstractCharacter.BACK_START)
        elif abstractPlayer.LEFT in self.heldKeys:
            self.setXVelocity(-300)
            if self.isFacingLeft() and self.animationNumber in [abstractCharacter.IDLE, abstractCharacter.BACK_START, abstractCharacter.BACK_LOOP, abstractCharacter.BACK_END]:
                self.setAnimation(abstractCharacter.FORWARD_START)
            elif not self.isFacingLeft() and self.animationNumber in [abstractCharacter.IDLE, abstractCharacter.FORWARD_START, abstractCharacter.FORWARD_LOOP, abstractCharacter.FORWARD_END]:
                self.setAnimation(abstractCharacter.BACK_START)
        else:
            if self.animationNumber in abstractCharacter.idleTransitions:
                self.setAnimation(abstractCharacter.idleTransitions[self.animationNumber])
                # print(f"animation is now {self.animationNumber}")
                
            self.setXVelocity(0)