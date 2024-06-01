from threading import RLock
from effects.knockBack import knockback
from physicalBody.abstractPhysicalBody import abstractPhysicalBody
from characters.abstractCharacter import abstractCharacter
from actions.abstractSpellAction import abstractSpellAction
from pygame import Rect

from physicalBody.attackProjectile import meleeProjectile, rangedProjectile

class abstractPlayer(abstractPhysicalBody):
    LEFT_DIR = 0
    RIGHT_DIR = 1
    
    MAX_HEALTH = 1000
    MAX_MANA = 1000
    MAX_SWAP_VAL = 1000
    
    RIGHT = 0
    LEFT = 1
    UP = 2
    DOWN = 3
    DASH = 4
    MELEE = 5
    WEAK = 6
    STRONG = 7
    
    FORWARD = 8
    BACK = 9
    
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
        abstractPhysicalBody.__init__(self, (0,0), (0,0), None)
        self.hitbox = [Rect(0,0,30,60)]
        
        self.animationNumber = abstractCharacter.IDLE
        self.animationFrame = 0
        self.timeSinceLastFrame = 0
        self.character = None # character class for move set and skills
        
        self.health = 1000
        self.mana = 1000
        self.swapVal = 1000
        self.dashing = False
        self.blocking = False
        self.effects = []
        self.StunTime = 0
        
        self.facingDirection = abstractPlayer.RIGHT_DIR
        self.characterIndex = None
        
        self.heldKeys = []
        self.ignoreKeys = set()
        
        self.__gameObj = gameObj
        self.__spellcardLock = RLock()
        self.__effectsLock = RLock()

        self.playerSpells = []
        self.actionMapping = None
        
        self.meleeCounter = 0
        self.lastMeleeMade = 0 # time of last base melee attack
        
    # override from abstract physical body
    def collides(self, other):
        if isinstance(other, abstractPlayer):
            for rect_box in self.hitbox:
                if rect_box.collidelist(other.getHitbox()) != -1:
                    return True
            return False
        # other is just a rect
        return other.getHitbox().collidelist(self.hitbox) != -1
    
    def setXPosition(self, x):
        for box in self.hitbox:
            box.x += x - box.x
        self.position = (x, self.position[1])
        
    def setYPosition(self, y):
        for box in self.hitbox:
            box.y += y - box.y
        self.position = (self.position[0], y)
        
    def getBoxXPositions(self):
        return [(b.x, b.width) for b in self.hitbox]
    
    def getBoxYPositions(self):
        return [(b.y, b.height) for b in self.hitbox]
    
    def getMaxXhitbox(self):
        return max(self.getBoxXPositions(), key = lambda x:x[0]+x[1])
    
    def getMinXhitbox(self):
        return min(self.getBoxXPositions(), key = lambda x:x[0])
    
    def getMaxYhitbox(self):
        return max(self.getBoxYPositions(), key = lambda x:x[0]+x[1])
    
    def getMinYhitbox(self):
        return min(self.getBoxYPositions(), key = lambda x:x[0])
    
    def getMiddleHitbox(self):
        x = self.getMaxXhitbox()
        y = self.getMaxYhitbox()
        return [
            (self.getMinXhitbox()[0] + x[0] + x[1]) / 2,
            (self.getMinYhitbox()[0] + y[0] + y[1]) / 2
        ]
    
    def distanceTo(self, other):
        if isinstance(other, abstractPlayer):
            return abs(self.getMiddleHitbox()[0] - other.getMiddleHitbox()[0])
        pointX2 = other.hitbox.x + (other.hitbox.width / 2)
        return abs(self.getMiddleHitbox[0] - pointX2)
    
    
    
    ###########################################################
    ## Rest of class                                         ##
    ###########################################################
        
    #for debugging
    def spellcardsAreLocked(self):
        return self.__spellcardLock.locked()
        
    def lockEffects(self):
        self.__effectsLock.acquire()
        
    def unlockEffects(self):
        self.__effectsLock.release()
        
    def addEffect(self, newEffect):
        self.lockEffects()
        self.effects.append(newEffect)
        newEffect.startEffect()
        self.unlockEffects()
        
    def applyEffects(self, timeDec):
        self.lockEffects()
        if self.StunTime > 0:
            self.StunTime -= timeDec
        map(lambda x: x.decrementTime(timeDec), self.effects)
        i = 0
        while i < len(self.effects):
            if self.effects[i].getRemainingTime() < 0:
                self.effects[i].removeEffect()
                del self.effects[i]
            else:
                self.effects[i].applyEffect()
                i += 1
        self.unlockEffects()
        
    def isStun(self):
        return self.StunTime > 0
    
    def isDashing(self):
        return self.dashing
    
    def isBlocking(self):
        return self.blocking
    
    def addSpellaction(self, spellaction):
        self.lockSpellCards()
        self.playerSpells.append(spellaction)
        self.unlockSpellCards()
        
    def removeSpellaction(self, spellaction):
        self.lockSpellCards()
        if spellaction in self.playerSpells:
            self.playerSpells.remove(spellaction)
        self.unlockSpellCards()
    
    def holdKey(self, key):
        if not key in self.heldKeys:
            self.heldKeys.append(key)
            
    def releaseKey(self, key):
        self.heldKeys.remove(key)
    
    def setStun(self, value):
        self.StunTime = value
        
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
        if self.animationNumber == abstractCharacter.UP_LOOP and self.getYVelocity() >= 0 and\
                self.animationNumber <= 23:
            if self.getYPosition() < self.__gameObj.HEIGHT // 2:
                self.setAnimation(abstractCharacter.UP_END_INIT)
            else:
                self.setAnimation(abstractCharacter.UP_END_POST)
        elif self.animationNumber == abstractCharacter.DOWN_LOOP and self.getYVelocity() <= 0 and\
                self.animationNumber <= 23:
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
        
        image, self.animationNumber, self.animationFrame, width = self.character.getFrame(\
            self.animationNumber, self.animationFrame,\
            flip = self.facingDirection == abstractPlayer.LEFT_DIR)
        return image, self.character.baseWidth, width
        
    def getOpponent(self):
        return self.__gameObj.otherPlayer(self)
    
    def getHealth(self):
        return self.health
    
    def getMana(self):
        return self.mana
    
    def getSwap(self):
        return self.swapVal
    
    def incrementHealth(self, val):
        assert val >= 0
        self.health = min(abstractPlayer.MAX_HEALTH, self.health + val)
        
    def decrementHealth(self, val):
        assert val >= 0
        self.health = max(0, self.health - val)
        
    def setHealth(self, val):
        assert val >= 0
        self.health = val
        
    def setMana(self, val):
        assert val >= 0
        self.mana = val
        
    def setSwapVal(self, val):
        assert val >= 0
        self.swapVal = val
        
    def incrementMana(self, val):
        assert val >= 0
        self.mana = min(abstractPlayer.MAX_MANA, self.mana + val)
        
    def decrementMana(self, val):
        assert val >= 0
        self.mana = max(0, self.mana - val)
        
    def incrementSwapVal(self, val):
        assert val >= 0
        self.swapVal = min(abstractPlayer.MAX_SWAP_VAL, self.swapVal + val)
        
    def decrementSwapVal(self, val):
        assert val >= 0
        self.swapVal = max(0, self.swapVal - val)
    
    def setCharacter(self, character, i):
        self.character = character(self.__gameObj)
        self.characterIndex = i
        
        self.actionMapping = {
            str(sorted([abstractPlayer.FORWARD,abstractPlayer.MELEE])):self.character.forwardMelee,
            str(sorted([abstractPlayer.BACK,abstractPlayer.MELEE])):self.character.backMelee,
            str(sorted([abstractPlayer.DOWN,abstractPlayer.MELEE])):self.character.downMelee,
            str(sorted([abstractPlayer.UP,abstractPlayer.MELEE])):self.character.upMelee,
            str([abstractPlayer.MELEE]):self.character.melee,
            
            str(sorted([abstractPlayer.FORWARD,abstractPlayer.WEAK])):self.character.forwardWeak,
            str(sorted([abstractPlayer.BACK,abstractPlayer.WEAK])):self.character.backWeak,
            str(sorted([abstractPlayer.DOWN,abstractPlayer.WEAK])):self.character.downWeak,
            str(sorted([abstractPlayer.UP,abstractPlayer.WEAK])):self.character.upWeak,
            str([abstractPlayer.WEAK]):self.character.weak,
            
            str(sorted([abstractPlayer.FORWARD,abstractPlayer.STRONG])):self.character.forwardStrong,
            str(sorted([abstractPlayer.BACK,abstractPlayer.STRONG])):self.character.backStrong,
            str(sorted([abstractPlayer.DOWN,abstractPlayer.STRONG])):self.character.downStrong,
            str(sorted([abstractPlayer.UP,abstractPlayer.STRONG])):self.character.upStrong,
            str([abstractPlayer.STRONG]):self.character.strong
        }
        
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
    
    def isInAir(self):
        raise NotImplementedError("a")
    
    def __replaceDirections(self, recent):
        for i in range(len(recent)):
            if (recent[i] == abstractPlayer.RIGHT and self.isFacingLeft()) or\
                    (recent[i] == abstractPlayer.LEFT and not self.isFacingLeft()):
                recent[i] = abstractPlayer.BACK
            elif (recent[i] == abstractPlayer.RIGHT and not self.isFacingLeft()) or\
                    (recent[i] == abstractPlayer.LEFT and self.isFacingLeft()):
                recent[i] = abstractPlayer.FORWARD
    
    def handleActions(self):
        recent = list(filter(lambda x: x not in self.ignoreKeys.difference({abstractPlayer.UP, abstractPlayer.DOWN}),\
                            self.heldKeys))[-3:]
        self.__replaceDirections(recent)
        recent.sort()
            
        if str(recent) not in self.actionMapping:
            return
        
        self.ignoreKeys = self.ignoreKeys.union(set(recent).intersection({abstractPlayer.MELEE, abstractPlayer.WEAK, abstractPlayer.STRONG}))
        try:
            action = self.actionMapping[str(recent)]
            actionThread = abstractSpellAction(self, self.__gameObj, action)
            actionThread.start()
        except NotImplementedError:
            print("action not implemented")
    
    def handleHeldKeys(self):
        if self.isDashing():
            self.setYVelocity(0)
        if self.isStun():
            return
        
        if abstractPlayer.UP in set(self.heldKeys).difference(self.ignoreKeys)\
                and self.getYPosition() == self.__gameObj.HEIGHT // 2:
            self.setYVelocity(-self.character.getJump())
            self.setAnimation(abstractCharacter.UP_START_INIT)
            self.ignoreKeys.add(abstractPlayer.UP)
            
        elif abstractPlayer.DOWN in set(self.heldKeys).difference(self.ignoreKeys)\
                and self.getYPosition() == self.__gameObj.HEIGHT // 2:
            self.setYVelocity(self.character.getJump())
            self.setAnimation(abstractCharacter.DOWN_START_INIT)
            self.ignoreKeys.add(abstractPlayer.DOWN)
        
        self.handleHorizontalMovement()
        self.handleActions()

    def handleDashBlock(self):
        if abstractPlayer.DASH not in self.heldKeys:
            self.blocking = False
            self.dashing = False
            return False
        
        if abstractPlayer.RIGHT in self.heldKeys or abstractPlayer.LEFT in self.heldKeys:
            self.blocking = False
            self.dashing = True
        elif self.getYVelocity() == 0:
            self.blocking = True
            self.dashing = False
            
        if abstractPlayer.RIGHT in self.heldKeys:
            self.setXVelocity(400)
            if not self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptDashForwardAnims:
                self.setAnimation(abstractCharacter.DASH_FORWARD_START)
            elif self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptDashBackAnims:
                self.setAnimation(abstractCharacter.DASH_BACK_START)
                
        elif abstractPlayer.LEFT in self.heldKeys:
            self.setXVelocity(-400)
            if self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptDashForwardAnims:
                self.setAnimation(abstractCharacter.DASH_FORWARD_START)
            elif not self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptDashBackAnims:
                self.setAnimation(abstractCharacter.DASH_BACK_START)
                
        else:
            if self.animationNumber in abstractCharacter.idleTransitions:
                self.setAnimation(abstractCharacter.idleTransitions[self.animationNumber])
            self.setXVelocity(0)
        return True

    def handleHorizontalMovement(self):
        if self.handleDashBlock():
            return
        if abs(self.getYPosition() - (self.__gameObj.HEIGHT // 2)) > 20:
            return
        
        if abstractPlayer.RIGHT in self.heldKeys:
            self.setXVelocity(300)
            if not self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptForwardAnims:
                self.setAnimation(abstractCharacter.FORWARD_START)
            elif self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptBackAnims:
                self.setAnimation(abstractCharacter.BACK_START)
                
        elif abstractPlayer.LEFT in self.heldKeys:
            self.setXVelocity(-300)
            if self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptForwardAnims:
                self.setAnimation(abstractCharacter.FORWARD_START)
            elif not self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptBackAnims:
                self.setAnimation(abstractCharacter.BACK_START)
                
        else:
            if self.animationNumber in abstractCharacter.idleTransitions:
                self.setAnimation(abstractCharacter.idleTransitions[self.animationNumber])
            self.setXVelocity(0)
            
    def magicBlock(self):
        self.setAnimation(abstractCharacter.MAGIC_GUARD)
        #knockback
        self.addEffect(knockback(self, 0.1, (100,0) if self.isFacingLeft() else (-100,0)))
    
    def meleeBlock(self):
        self.setAnimation(abstractCharacter.GUARD)
        #knockback, check direction
        self.addEffect(knockback(self, 0.1, (100,0) if self.isFacingLeft() else (-100,0)))
        
    def hit(self, projectile):
        if isinstance(projectile, meleeProjectile):
            if self.blocking:
                projectile.destroy()
                return
        elif isinstance(projectile, rangedProjectile):
            if self.dashing:
                return
            if self.blocking:
                projectile.destroy()
                return
        
        #check if low or high
        if self.getYPosition() - projectile.getYPosition() > 0:
            self.setAnimation(abstractCharacter.HIT_HIGH)
        else:
            self.setAnimation(abstractCharacter.HIT_LOW)
        self.clearSpells()
        projectile.applyEffect()
    
    def clearSpells(self):
        self.lockSpellCards()
        self.playerSpells = []
        self.unlockSpellCards()