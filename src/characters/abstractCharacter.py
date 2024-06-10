import pygame
from characters.parseSpriteSheet import *

class abstractCharacter:
    DEFAULT_IMAGE_PATH = "assets/images/characters"
    
    @classmethod
    def pathTo(cls, subclass, fileName):
        return f"{cls.DEFAULT_IMAGE_PATH}/{subclass.__name__}/{fileName}"
    
    icon = None
    spritesheet = None
    portraits = None
    
    frames = []
    characterTransitions = None
    baseWidth = 0
    
    #animation values:
    IDLE = 0
    TURN = 1
    FORWARD_START = 2 
    FORWARD_END = 3 
    FORWARD_LOOP = 4
    BACK_START = 5
    BACK_END = 6 
    BACK_LOOP = 7
    
    DASH_FORWARD_START = 8
    DASH_FORWARD_END = 9
    DASH_FORWARD_LOOP = 10
    DASH_BACK_START = 11
    DASH_BACK_END = 12
    DASH_BACK_LOOP = 13
    
    DOWN_START_INIT = 14
    DOWN_START_POST = 15
    DOWN_LOOP = 16
    DOWN_END_INIT = 17
    DOWN_END_POST = 18
    UP_START_INIT = 19
    UP_START_POST = 20
    UP_LOOP = 21
    UP_END_INIT = 22
    UP_END_POST = 23
    
    HIT_HIGH = 24
    HIT_LOW = 25
    
    GUARD = 26
    MAGIC_GUARD = 27
    WALL_BOUNCE_END = 28
    
    autoTransitions = {
        FORWARD_START: FORWARD_LOOP,
        BACK_START: BACK_LOOP,
        DASH_BACK_START: DASH_BACK_LOOP,
        DASH_FORWARD_START: DASH_FORWARD_LOOP,
        
        UP_START_INIT: UP_LOOP,
        UP_START_POST: UP_LOOP,
        DOWN_START_INIT: DOWN_LOOP,
        DOWN_START_POST: DOWN_LOOP,
        DOWN_END_INIT: UP_START_POST,
        DOWN_END_POST: IDLE,
        UP_END_INIT: DOWN_START_POST,
        UP_END_POST: IDLE,
        
        TURN: IDLE,
        BACK_END: IDLE,
        FORWARD_END: IDLE,
        DASH_BACK_END: IDLE,
        DASH_FORWARD_END: IDLE,
        
        HIT_HIGH: IDLE,
        HIT_LOW: IDLE,
        GUARD: IDLE,
        MAGIC_GUARD: IDLE,
        WALL_BOUNCE_END: IDLE
    }
    
    idleTransitions = {
        FORWARD_LOOP: FORWARD_END,
        FORWARD_START: FORWARD_END,
        BACK_LOOP: BACK_END,
        BACK_START: BACK_END,
        DASH_FORWARD_LOOP: DASH_FORWARD_END,
        DASH_FORWARD_START: DASH_FORWARD_END,
        DASH_BACK_LOOP: DASH_BACK_END,
        DASH_BACK_START: DASH_BACK_END
    }
    
    interruptDashForwardAnims = [
        BACK_START,
        BACK_END,
        BACK_LOOP,
        IDLE,
        
        UP_END_INIT,
        UP_END_POST,
        UP_LOOP,
        UP_START_INIT,
        UP_START_POST,
        
        DOWN_END_INIT,
        DOWN_END_POST,
        DOWN_LOOP,
        DOWN_START_INIT,
        DOWN_START_POST,
        
        DASH_BACK_END,
        DASH_BACK_LOOP,
        DASH_BACK_START,
        
        FORWARD_START,
        FORWARD_END,
        FORWARD_LOOP
    ]
        
    interruptDashBackAnims = [
        BACK_START,
        BACK_END,
        BACK_LOOP,
        IDLE,
        
        UP_END_INIT,
        UP_END_POST,
        UP_LOOP,
        UP_START_INIT,
        UP_START_POST,
        
        DOWN_END_INIT,
        DOWN_END_POST,
        DOWN_LOOP,
        DOWN_START_INIT,
        DOWN_START_POST,
        
        DASH_FORWARD_END,
        DASH_FORWARD_LOOP,
        DASH_FORWARD_START,
        
        FORWARD_START,
        FORWARD_END,
        FORWARD_LOOP
    ]
    
    interruptBackAnims = [
        FORWARD_START,
        FORWARD_END,
        FORWARD_LOOP,
        IDLE
    ]
    
    interruptForwardAnims = [
        BACK_START,
        BACK_END,
        BACK_LOOP,
        IDLE
    ]
    
    @classmethod
    def setupPortaits(cls):
        parts = parseSpriteSheet(cls.portraitPath)
        cls.portraitPoints = [item for row in parts for item in row]
    
    @classmethod
    def setup(cls):
        cls.setup = True
        # needs defined in subclasses
    
    def __init__(self, gameObj, gravityWeight, baseX, baseY) -> None:
        self.name = ""
        self.baseX = baseX
        self.baseY = baseY
        self.__gameObj = gameObj
        self.animationRate = 5 # frames each image is shown for
        
        self.currentXOffset = None
        self.currentYOffset = None
        self.gravityWeight = gravityWeight # eg 300
        
    def setIntroAnim(self, partnerClass):
        pass # for subclasses
    
    def setOutroAnim(self, wasWin):
        pass # for subclasses
        
    def getJump(self):
        return self.gravityWeight
    
    def getGravity(self):
        return self.gravityWeight // 50
    
    def getName(self):
        return self.name
    
    def getFrame(self, animationIndex, frameIndex, flip = False):
        actualIndex = frameIndex // self.animationRate
        ((x1,y1),(x2,y2)) = self.__class__.frames[animationIndex][0][actualIndex]
        xoff,yoff = self.__class__.frames[animationIndex][1]
        
        crop = pygame.Rect(x1,y1,x2-x1,y2-y1)
        frame = self.__class__.spritesheet.subsurface(crop)
        
        self.currentXOffset = xoff
        self.currentYOffset = yoff
        
        if animationIndex == abstractCharacter.TURN:
            flip = not flip
        
        if flip:
            frame = pygame.transform.flip(frame, True, False)
            
        if animationIndex in abstractCharacter.autoTransitions and \
                frameIndex == (len(self.__class__.frames[animationIndex][0]) * self.animationRate) - 1:
            animationIndex = abstractCharacter.autoTransitions[animationIndex]
            frameIndex = 0
            
        elif animationIndex in self.__class__.characterTransitions and \
                frameIndex == (len(self.__class__.frames[animationIndex][0]) * self.animationRate) - 1:
            animationIndex = self.__class__.characterTransitions[animationIndex]
            frameIndex = 0
            
        else:
            frameIndex = (frameIndex + 1) % (len(self.__class__.frames[animationIndex][0]) * self.animationRate)
        
        self.animationRate = self.__class__.frames[animationIndex][2]
        return (frame, animationIndex, frameIndex, x2 - x1)
    
    @classmethod
    def setFrames(cls, animations, baseWidth):
        cls.frames = animations
        cls.baseWidth = baseWidth
        
    def getXoffset(self):
        return self.currentXOffset
    
    def getYoffset(self):
        return self.currentYOffset
    
    def decrementSwapVal(self, swapval, val):
        return swapval - val
    
    def incrementSwapVal(self, swapval, timeDiff):
        return swapval + 20 * timeDiff
    
    
    
    
    
    # action stubs
    def dashForward(self, player):
        pass
    
    def dashBack(self, player):
        pass
    
    def forwardMelee(self, player):
        raise NotImplementedError("stub action")
    
    def backMelee(self, player):
        raise NotImplementedError("stub action")
    
    def downMelee(self, player):
        raise NotImplementedError("stub action")
    
    def upMelee(self, player):
        raise NotImplementedError("stub action")
    
    def melee(self, player):
        raise NotImplementedError("stub action")
    
    def forwardWeak(self, player):
        raise NotImplementedError("stub action")
    
    def backWeak(self, player):
        raise NotImplementedError("stub action")
    
    def downWeak(self, player):
        raise NotImplementedError("stub action")
    
    def upWeak(self, player):
        raise NotImplementedError("stub action")
    
    def weak(self, player):
        raise NotImplementedError("stub action")
    
    def forwardStrong(self, player):
        raise NotImplementedError("stub action")
    
    def backStrong(self, player):
        raise NotImplementedError("stub action")
    
    def downStrong(self, player):
        raise NotImplementedError("stub action")
    
    def upStrong(self, player):
        raise NotImplementedError("stub action")
    
    def strong(self, player):
        raise NotImplementedError("stub action")