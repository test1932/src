from characters.abstractCharacter import abstractCharacter
from actions.abstractSpellAction import abstractSpellAction
from characters.parseSpriteSheet import *
from physicalBody.attackProjectile import meleeProjectile, rangedProjectile
import time
from pygame import Rect

class yukari(abstractCharacter):
    isSetup = False
    projectiles = []
    
    MELEE_BACK = 24
    MELEE_CLOSE = 24
    MELEE_FORWARD = 25
    MELEE_DASH_FORWARD = 26
    MELEE_UP_START = 27
    MELEE_UP_LOOP = 28
    MELEE_UP_END = 29
    MELEE_DOWN = 30
    
    projectileImages = []
    
    @classmethod
    def loadProjectileImgs(cls):
        pass # TODO
    
    def __init__(self, gameObj) -> None:
        super().__init__("assets/images/sheets/yukari.png", gameObj, 600, 135, 52)
        parts = parseSpriteSheet(self.imagePath)
        
        self.framePoints = [
            (parts[5][:11], (0,0)), # idle
            (parts[5][11:14], (0,0)), # turn
            
            (parts[6][:2], (-7,0)), #forward start
            (parts[6][7:9], (-7,0)), #forward end
            (parts[6][2:7], (-7,0)), #forward loop
            
            (parts[7][:2], (10,0)), #back start
            (parts[7][7:9], (10,0)), #back end
            (parts[7][2:7], (10,0)), #back loop
            
            (parts[8][:4], (-8,0)), # dash forward start
            (parts[8][9:12], (-8,0)), # dash forward end
            (parts[8][4:9], (-8,0)), # dash forward loop
            
            (parts[9][:3], (0,0)), # dash back start
            (parts[9][8:11], (0,0)), # dash back end
            (parts[9][3:8], (0,0)), # dash back loop
            
            (parts[10][:3], (0,42)), # down start init
            (parts[10][8:9], (0,42)), # down start post
            (parts[10][3:8], (0,42)), # down loop
            (parts[10][8:9], (0,42)), # down end init
            (parts[10][2:0:-1], (0,42)), # down end post
            
            (parts[11][8:9], (0,42)), # up start init
            (parts[11][:3], (0,42)), # up start post
            (parts[11][3:8], (0,42)), # up loop
            (parts[11][2:0:-1], (0,42)), # up send init
            (parts[11][8:9], (0,42)), # up end post
            
            #character specific:
            
            (parts[12], (0,12)), # back or close attack
            (parts[13], (40,-8)), # close AAA or far A
            (parts[14], (0,0)), # dash A or air A
            (parts[15][:4], (0,42)), # up A start
            (parts[15][4:5], (0,42)), # up A loop
            (parts[15][5:], (0,42)), # up A end
            (parts[17][:6], (0,0)) # melee down
        ]

        self.characterTransitions = {
            yukari.MELEE_BACK: abstractCharacter.IDLE,
            yukari.MELEE_FORWARD: abstractCharacter.IDLE,
            yukari.MELEE_UP_START: yukari.MELEE_UP_LOOP,
            yukari.MELEE_UP_END: abstractCharacter.IDLE,
            yukari.MELEE_DASH_FORWARD: abstractCharacter.IDLE,
            yukari.MELEE_DOWN: abstractCharacter.IDLE
        }
        
        self.setFrames(self.framePoints, self.framePoints[0][0][0][1][0] - self.framePoints[0][0][0][0][0])
        if not yukari.isSetup:
            yukari.isSetup = True
            yukari.loadProjectileImgs()
            
    # actions
    def forwardMelee(self, spellaction, player, isCombo = False):
        #check if in air
        player.setAnimation(yukari.MELEE_FORWARD)
        player.setXVelocity(0)
        player.setYVelocity(0)
        player.setCooldown(0.5)
    
    def backMelee(self, spellaction, player):
        self.melee(spellaction, player, definitelyClose = True)
    
    def downMelee(self, spellaction, player):
        player.setAnimation(yukari.MELEE_DOWN)
        player.setXVelocity(0)
        player.setYVelocity(0)
        player.setCooldown(0.5)
    
    def upMelee(self, spellaction, player):
        # TODO apply stun effect
        player.setAnimation(yukari.MELEE_UP_START)
        player.setXVelocity(0)
        player.setYVelocity(0)
        time.sleep(1)
        player.setAnimation(yukari.MELEE_UP_END)
        player.setCooldown(0.5)
    
    def melee(self, spellaction, player, definitelyClose = False):
        otherPlayer = player.getOpponent()
        player.setXVelocity(0)
        player.setYVelocity(0)
        if player.distanceTo(otherPlayer) > 200 and not definitelyClose:
            self.forwardMelee(spellaction, player)
        
        else:
            if time.time() - player.lastMeleeMade > 1:
                player.meleeCounter = 0
            player.lastMeleeMade = time.time()
            
            if player.meleeCounter == 4:
                pass
            elif player.meleeCounter == 3:
                pass
            elif player.meleeCounter == 2:
                self.forwardMelee(spellaction, player, True)
            else:
                self.__baseMelee(spellaction, player)
            
            player.meleeCounter = (player.meleeCounter + 1) % 5
    
    def __baseMelee(self, spellaction, player):
        player.setAnimation(yukari.MELEE_CLOSE)
        player.setCooldown(0.5)
        
        hitBoxes = [Rect(0,0,30,50), Rect(0,0,70,70), Rect(0,0,50,30)]
        
        y = player.getYPosition()
        if not player.isFacingLeft():
            xOff = 0
        else:
            xOff = player.getHitbox().width
        pos = [player.getXPosition() + xOff, y]
        
        x1Off = -(40 + 30) if player.isFacingLeft() else 40
        meleeProjectile((pos[0] + x1Off, pos[1] - 50),(0,0),spellaction, player, 20, hitBoxes[0], hitBoxes[0])
        time.sleep(0.3)
        x2Off = -(50 + 70) if player.isFacingLeft() else 50
        meleeProjectile((pos[0] + x2Off, pos[1] - 30),(0,0),spellaction, player, 20, hitBoxes[1], hitBoxes[1])
        time.sleep(1)
        x3Off = -(90 + 50) if player.isFacingLeft() else 90
        meleeProjectile((pos[0] + x3Off, pos[1] + 10),(0,0),spellaction, player, 20, hitBoxes[2], hitBoxes[2])
        time.sleep(0.3)
    
    def forwardWeak(self, spellaction, player):
        raise NotImplementedError("stub action")
    
    def backWeak(self, spellaction, player):
        raise NotImplementedError("stub action")
    
    def downWeak(self, spellaction, player):
        raise NotImplementedError("stub action")
    
    def upWeak(self, spellaction, player):
        raise NotImplementedError("stub action")
    
    def weak(self, spellaction, player):
        print("TODO")
    
    def forwardStrong(self, spellaction, player):
        raise NotImplementedError("stub action")
    
    def backStrong(self, spellaction, player):
        raise NotImplementedError("stub action")
    
    def downStrong(self, spellaction, player):
        raise NotImplementedError("stub action")
    
    def upStrong(self, spellaction, player):
        raise NotImplementedError("stub action")
    
    def strong(self, spellaction, player):
        raise NotImplementedError("stub action")