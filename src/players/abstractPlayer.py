from threading import RLock
from effects.knockBack import knockback
from physicalBody.abstractPhysicalBody import abstractPhysicalBody
from characters.abstractCharacter import abstractCharacter
from actions.abstractSpellAction import abstractSpellAction
from pygame import Rect

from physicalBody.attackProjectile import meleeProjectile, rangedProjectile

class abstractPlayer(abstractPhysicalBody):
    POSSESSOR = 0
    SLAVE = 1
    
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
    SWAP = 8
    
    FORWARD = 9
    BACK = 10
    
    actionNames = [
        "Right",
        "Left",
        "Up",
        "Down",
        "Dash",
        "Melee",
        "Magic",
        "Special",
        "Swap"
    ]
    
    NO_AIR_DASH = 0
    FIRST_RIGHT_AIR_DASH = 1
    FIRST_LEFT_AIR_DASH = 2
    HAS_TURNED_LEFT = 3
    HAS_TURNED_RIGHT = 4
    FALLING = 5
    
    
    #will have static list of projectile classes
    
    def __init__(self, gameObj) -> None:
        abstractPhysicalBody.__init__(self, (0,0), (0,0), None)
        self.hitbox = [Rect(0,0,30,60)]
        
        self.animationNumber = abstractCharacter.IDLE
        self.animationFrame = 0
        self.timeSinceLastFrame = 0
        self.characters = [None, None] # character classes for move set and skills
        self.curChar = 0
        
        self.health = 1000
        self.mana = 1000
        self.swapVal = 1000
        self.dashing = False
        self.blocking = False
        self.effects = []
        self.StunTime = 0
        
        self.turnedInAir = abstractPlayer.NO_AIR_DASH
        
        self.facingDirection = abstractPlayer.RIGHT_DIR
        self.characterIndex = None
        
        self.heldKeys = []
        self.ignoreKeys = set()
        
        self.__gameObj = gameObj
        self.__spellcardLock = RLock()
        self.__effectsLock = RLock()

        self.playerSpells = []
        self.actionMappings = [None, None]
        
        self.meleeCounter = 0
        self.lastMeleeMade = 0 # time of last base melee attack
        
    def getGameObj(self):
        return self.__gameObj
    
    def intro(self):
        self.characters[0].setIntroAnim(self.characters[1])
    
    def outro(self, wasWin):
        self.characters[0].setOutroAnim(wasWin)
        
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
        for effect in self.effects:
            effect.decrementTime(timeDec)
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
    
    def getActionMapping(self):
        return self.actionMappings[self.curChar]
    
    def getCurChar(self):
        return self.curChar
    
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
        
        image, self.animationNumber, self.animationFrame, width = self.getCharacter().getFrame(\
            self.animationNumber, self.animationFrame,\
            flip = self.facingDirection == abstractPlayer.LEFT_DIR)
        return image, self.getCharacter().baseWidth, width
        
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
        
    def incrementSwapVal(self, timeDiff):
        self.swapVal = min(self.getCharacter().incrementSwapVal(self.swapVal, timeDiff), 
            abstractPlayer.MAX_SWAP_VAL)
        
    def decrementSwapVal(self, val):
        self.swapVal = max(self.getCharacter().decrementSwapVal(self.swapVal, val), 0)
    
    def setCharacter(self, character, i, charsIndex):
        self.characters[charsIndex] = character(self.__gameObj)
        self.characterIndex = i
        
        self.actionMappings[charsIndex] = {
            str(sorted([abstractPlayer.FORWARD,abstractPlayer.MELEE])):self.characters[charsIndex].forwardMelee,
            str(sorted([abstractPlayer.BACK,abstractPlayer.MELEE])):self.characters[charsIndex].backMelee,
            str(sorted([abstractPlayer.DOWN,abstractPlayer.MELEE])):self.characters[charsIndex].downMelee,
            str(sorted([abstractPlayer.UP,abstractPlayer.MELEE])):self.characters[charsIndex].upMelee,
            str([abstractPlayer.MELEE]):self.characters[charsIndex].melee,
            
            str(sorted([abstractPlayer.FORWARD,abstractPlayer.WEAK])):self.characters[charsIndex].forwardWeak,
            str(sorted([abstractPlayer.BACK,abstractPlayer.WEAK])):self.characters[charsIndex].backWeak,
            str(sorted([abstractPlayer.DOWN,abstractPlayer.WEAK])):self.characters[charsIndex].downWeak,
            str(sorted([abstractPlayer.UP,abstractPlayer.WEAK])):self.characters[charsIndex].upWeak,
            str([abstractPlayer.WEAK]):self.characters[charsIndex].weak,
            
            str(sorted([abstractPlayer.FORWARD,abstractPlayer.STRONG])):self.characters[charsIndex].forwardStrong,
            str(sorted([abstractPlayer.BACK,abstractPlayer.STRONG])):self.characters[charsIndex].backStrong,
            str(sorted([abstractPlayer.DOWN,abstractPlayer.STRONG])):self.characters[charsIndex].downStrong,
            str(sorted([abstractPlayer.UP,abstractPlayer.STRONG])):self.characters[charsIndex].upStrong,
            str([abstractPlayer.STRONG]):self.characters[charsIndex].strong
        }
        
    def getCharacter(self):
        return self.characters[self.curChar]
        
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
        return self.getCharacter().getGravity()
    
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
            
        if str(recent) not in self.getActionMapping():
            return
        
        self.ignoreKeys = self.ignoreKeys.union(set(recent).intersection({abstractPlayer.MELEE, abstractPlayer.WEAK, abstractPlayer.STRONG}))
        try:
            action = self.getActionMapping()[str(recent)]
            actionThread = abstractSpellAction(self, self.__gameObj, action)
            actionThread.start()
        except NotImplementedError:
            print("action not implemented")
    
    def handleHeldKeys(self):
        if self.isDashing():
            self.setYVelocity(0)
        if self.isStun():
            return
        
        ofInterestKeys = set(self.heldKeys).difference(self.ignoreKeys)
        
        if abstractPlayer.SWAP in ofInterestKeys:
            self.swap()
            self.ignoreKeys.add(abstractPlayer.SWAP)
            return
        
        self.handleVerticalMovement(ofInterestKeys)
        self.handleHorizontalMovement()
        self.handleActions()
        
    def handleVerticalMovement(self, ofInterestKeys):
        if abstractPlayer.UP in ofInterestKeys\
                and self.getYPosition() == self.__gameObj.HEIGHT // 2:
            self.setYVelocity(-self.getCharacter().getJump())
            self.setAnimation(abstractCharacter.UP_START_INIT)
            self.ignoreKeys.add(abstractPlayer.UP)
            
        elif abstractPlayer.DOWN in ofInterestKeys\
                and self.getYPosition() == self.__gameObj.HEIGHT // 2:
            self.setYVelocity(self.getCharacter().getJump())
            self.setAnimation(abstractCharacter.DOWN_START_INIT)
            self.ignoreKeys.add(abstractPlayer.DOWN)
            
        if self.getYPosition() != self.__gameObj.HEIGHT // 2 and self.animationNumber == abstractCharacter.IDLE:
            if self.getYPosition() - (self.__gameObj.HEIGHT // 2) < -100:
                self.setAnimation(abstractCharacter.DOWN_START_INIT)
            elif self.getYPosition() - (self.__gameObj.HEIGHT // 2) > 100:
                self.setAnimation(abstractCharacter.UP_START_INIT)
            

    def handleDashBlock(self):
        if (self.turnedInAir == abstractPlayer.HAS_TURNED_LEFT and abstractPlayer.RIGHT in self.heldKeys) or\
                (self.turnedInAir == abstractPlayer.HAS_TURNED_RIGHT and abstractPlayer.LEFT in self.heldKeys) or\
                (self.turnedInAir == abstractPlayer.FALLING):
            self.turnedInAir = abstractPlayer.FALLING
            self.dashing = False
            return
        
        if abstractPlayer.DASH not in self.heldKeys:
            self.blocking = False
            self.dashing = False
            return
        
        if abstractPlayer.RIGHT in self.heldKeys or abstractPlayer.LEFT in self.heldKeys:
            self.blocking = False
            self.dashing = True
        elif self.getYVelocity() == 0:
            self.blocking = True
            self.dashing = False
        
        #velocity and animation
        if abstractPlayer.RIGHT in self.heldKeys:
            if self.turnedInAir == abstractPlayer.NO_AIR_DASH:
                self.turnedInAir = abstractPlayer.FIRST_RIGHT_AIR_DASH
            if self.turnedInAir == abstractPlayer.FIRST_LEFT_AIR_DASH:
                self.turnedInAir = abstractPlayer.HAS_TURNED_RIGHT
            
            if not self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptDashForwardAnims:
                self.setAnimation(abstractCharacter.DASH_FORWARD_START)
                self.getCharacter().dashForward(self)
            elif self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptDashBackAnims:
                self.setAnimation(abstractCharacter.DASH_BACK_START)
                self.getCharacter().dashBack(self)
                
        elif abstractPlayer.LEFT in self.heldKeys:
            if self.turnedInAir == abstractPlayer.NO_AIR_DASH:
                self.turnedInAir = abstractPlayer.FIRST_LEFT_AIR_DASH
            if self.turnedInAir == abstractPlayer.FIRST_RIGHT_AIR_DASH:
                self.turnedInAir = abstractPlayer.HAS_TURNED_LEFT
            
            if self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptDashForwardAnims:
                self.setAnimation(abstractCharacter.DASH_FORWARD_START)
                self.getCharacter().dashForward(self)
            elif not self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptDashBackAnims:
                self.setAnimation(abstractCharacter.DASH_BACK_START)
                self.getCharacter().dashBack(self)
                
        else:
            if self.animationNumber in abstractCharacter.idleTransitions:
                self.setAnimation(abstractCharacter.idleTransitions[self.animationNumber])
            self.setXVelocity(0)

    def handleHorizontalMovement(self):
        self.handleDashBlock()
        if abs(self.getYPosition() - (self.__gameObj.HEIGHT // 2)) > 20:
            return
        
        if abstractPlayer.RIGHT in self.heldKeys:
            self.setXVelocity(200)
            if not self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptForwardAnims:
                self.setAnimation(abstractCharacter.FORWARD_START)
            elif self.isFacingLeft() and self.animationNumber in abstractCharacter.interruptBackAnims:
                self.setAnimation(abstractCharacter.BACK_START)
                
        elif abstractPlayer.LEFT in self.heldKeys:
            self.setXVelocity(-200)
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
        
    def swap(self):
        self.curChar = 1 if self.curChar == 0 else 0
        self.decrementSwapVal(500)