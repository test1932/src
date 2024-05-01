import pygame

class abstractCharacter:
    slowDown = 5
    
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
    
    def __init__(self, imagePath, gameObj, gravityWeight, baseX, baseY) -> None:
        self.name = ""
        self.baseX = baseX
        self.baseY = baseY
        self.__gameObj = gameObj
        self.spritesheet = pygame.image.load(imagePath).convert_alpha()
        self.frames = [] # list of lists of tuples (((x,y),(x,y)),yoff) 
        self.currentXOffset = None
        self.currentYOffset = None
        self.gravityWeight = gravityWeight # eg 300
        self.imagePath = imagePath
        self.characterTransitions = None
        self.baseWidth = 0
        
    def getJump(self):
        return self.gravityWeight
    
    def getGravity(self):
        return self.gravityWeight // 50
    
    def getName(self):
        return self.name
    
    def getFrame(self, animationIndex, frameIndex, flip = False):
        actualIndex = frameIndex // abstractCharacter.slowDown
        ((x1,y1),(x2,y2)) = self.frames[animationIndex][0][actualIndex]
        xoff,yoff = self.frames[animationIndex][1]
        crop = pygame.Rect(x1,y1,x2-x1,y2-y1)
        frame = self.spritesheet.subsurface(crop)
        
        self.currentXOffset = xoff
        self.currentYOffset = yoff
        
        if animationIndex == abstractCharacter.TURN:
            flip = not flip
        
        if flip:
            frame = pygame.transform.flip(frame, True, False)
            
        if animationIndex in abstractCharacter.autoTransitions and \
                frameIndex == (len(self.frames[animationIndex][0]) * abstractCharacter.slowDown) - 1:
            animationIndex = abstractCharacter.autoTransitions[animationIndex]
            frameIndex = 0
            
        elif animationIndex in self.characterTransitions and \
                frameIndex == (len(self.frames[animationIndex][0]) * abstractCharacter.slowDown) - 1:
            animationIndex = self.characterTransitions[animationIndex]
            frameIndex = 0
            
        else:
            frameIndex = (frameIndex + 1) % (len(self.frames[animationIndex][0]) * abstractCharacter.slowDown)
        
        return (frame, animationIndex, frameIndex, x2 - x1)
    
    def setFrames(self, animations, baseWidth):
        self.frames = animations
        self.baseWidth = baseWidth
        
    def getXoffset(self):
        return self.currentXOffset
    
    def getYoffset(self):
        return self.currentYOffset
    
    # action stubs
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