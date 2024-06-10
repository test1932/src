from characters.abstractCharacter import abstractCharacter
from actions.abstractSpellAction import abstractSpellAction
from characters.parseSpriteSheet import *
from physicalBody.attackProjectile import meleeProjectile, rangedProjectile
import time
import pygame

class byakuren(abstractCharacter):
    isSetup = False
    sheetPath = "assets/images/characters/yukari/sheet.png"
    iconPath = "assets/images/characters/yukari/icon.png"
    portraitPath = "assets/images/characters/yukari/portraits.png"
    
    @classmethod
    def setup(cls):
        cls.isSetup = True
        cls.loadProjectileImgs()
        cls.setupFrames()
        
        cls.spritesheet = pygame.image.load(cls.sheetPath).convert_alpha()
        cls.icon = pygame.image.load(cls.iconPath).convert_alpha()
        cls.portraits = pygame.image.load(cls.portraitPath).convert_alpha()
    
    @classmethod
    def setupFrames(cls):
        parts = parseSpriteSheet(cls.sheetPath)
        
        cls.framePoints = [
            (parts[5][:11], (0,0), 5), # idle
            (parts[5][11:14], (0,0), 5), # turn
            
            (parts[6][:2], (-7,0), 5), #forward start
            (parts[6][7:9], (-7,0), 5), #forward end
            (parts[6][2:7], (-7,0), 5), #forward loop
            
            (parts[7][:2], (10,0), 5), #back start
            (parts[7][7:9], (10,0), 5), #back end
            (parts[7][2:7], (10,0), 5), #back loop
            
            (parts[8][:4], (-8,0), 5), # dash forward start
            (parts[8][9:12], (-8,0), 5), # dash forward end
            (parts[8][4:9], (-8,0), 5), # dash forward loop
            
            (parts[9][:3], (0,0), 5), # dash back start
            (parts[9][8:11], (0,0), 5), # dash back end
            (parts[9][3:8], (0,0), 5), # dash back loop
            
            (parts[10][:3], (0,42), 5), # down start init
            (parts[10][8:9], (0,42), 5), # down start post
            (parts[10][3:8], (0,42), 5), # down loop
            (parts[10][8:9], (0,42), 5), # down end init
            (parts[10][2:0:-1], (0,42), 5), # down end post
            
            (parts[11][8:9], (0,42), 5), # up start init
            (parts[11][:3], (0,42), 5), # up start post
            (parts[11][3:8], (0,42), 5), # up loop
            (parts[11][2:0:-1], (0,42), 5), # up send init
            (parts[11][8:9], (0,42), 5), # up end post
            
            (parts[43][7:], (20, 0), 5), # hit high
            (parts[44], (-25, -10), 5), # hit low
            
            (parts[42][:4], (13,-3), 8), # guard
            (parts[42][4:8], (0,-5), 5), # magic guard
            (parts[45][:5], (-20,16), 8), # wall bounce end
            
            #character specific:
            
            (parts[12], (0,12), 3), # back or close attack
            (parts[13], (40,-8), 4), # close AAA or far A
            (parts[14], (0,0), 5), # dash A or air A
            (parts[15][:4], (0,42), 5), # up A start
            (parts[15][4:5], (0,42), 5), # up A loop
            (parts[15][5:], (0,42), 5), # up A end
            (parts[17][:6], (0,0), 6) # melee down
        ]

        cls.characterTransitions = {
            cls.MELEE_BACK: abstractCharacter.IDLE,
            cls.MELEE_FORWARD: abstractCharacter.IDLE,
            cls.MELEE_UP_START: cls.MELEE_UP_LOOP,
            cls.MELEE_UP_END: abstractCharacter.IDLE,
            cls.MELEE_DASH_FORWARD: abstractCharacter.IDLE,
            cls.MELEE_DOWN: abstractCharacter.IDLE
        }
        
        cls.setFrames(cls.framePoints, cls.framePoints[0][0][0][1][0] - cls.framePoints[0][0][0][0][0])
    
    projectiles = [
        
    ]
    
    projectileImages = [
        
    ]
    
    MELEE_BACK = 29
    MELEE_CLOSE = 29
    MELEE_FORWARD = 30
    MELEE_DASH_FORWARD = 31
    MELEE_UP_START = 32
    MELEE_UP_LOOP = 33
    MELEE_UP_END = 34
    MELEE_DOWN = 35
    
    @classmethod
    def loadProjectileImgs(cls):
        pass # TODO
    
    def __init__(self, gameObj) -> None:
        super().__init__(gameObj, 600, 135, 52)
            
    #override
    def getFrame(self, animationIndex, frameIndex, flip = False):
        (frame, animationIndex, frameIndex, xdiff) = \
            super().getFrame(animationIndex, frameIndex, flip)
        frame.set_colorkey((0,0,0))#TODO
        return (frame, animationIndex, frameIndex, xdiff)
            
    # actions
    def forwardMelee(self, spellaction, player, isCombo = False):
        #check if in air
        player.setAnimation(byakuren.MELEE_FORWARD)
        player.setXVelocity(0)
        player.setYVelocity(0)
        player.setStun(0.1)
    
    def backMelee(self, spellaction, player):
        self.melee(spellaction, player, definitelyClose = True)
    
    def downMelee(self, spellaction, player):
        player.setAnimation(byakuren.MELEE_DOWN)
        player.setXVelocity(0)
        player.setYVelocity(0)
        player.setStun(0.1)
    
    def upMelee(self, spellaction, player):
        # TODO apply stun effect
        player.setAnimation(byakuren.MELEE_UP_START)
        player.setXVelocity(0)
        player.setYVelocity(0)
        time.sleep(1)
        player.setAnimation(byakuren.MELEE_UP_END)
        player.setStun(0.1)
    
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
        player.setAnimation(byakuren.MELEE_CLOSE)
        player.setStun(0.5)
        
        hitBoxes = [pygame.Rect(0,0,30,50), pygame.Rect(0,0,70,70), pygame.Rect(0,0,50,30)]
        
        y = player.getYPosition()
        xOff = 0
        # if not player.isFacingLeft():
        #     xOff = 0
        # else:
        #     xOff = player.getHitbox().width
        pos = [player.getXPosition() + xOff, y]
        
        x1Off = -(40 + 30) if player.isFacingLeft() else 40
        meleeProjectile((pos[0] + x1Off, pos[1] - 50),(0,0),spellaction, player, 20, hitBoxes[0], hitBoxes[0], player.isFacingLeft())
        time.sleep(0.3)
        x2Off = -(50 + 70) if player.isFacingLeft() else 50
        meleeProjectile((pos[0] + x2Off, pos[1] - 30),(0,0),spellaction, player, 20, hitBoxes[1], hitBoxes[1], player.isFacingLeft())
        time.sleep(0.1)
        x3Off = -(90 + 50) if player.isFacingLeft() else 90
        meleeProjectile((pos[0] + x3Off, pos[1] + 10),(0,0),spellaction, player, 20, hitBoxes[2], hitBoxes[2], player.isFacingLeft())
        time.sleep(0.1)
    
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