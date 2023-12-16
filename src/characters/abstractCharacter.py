import pygame

class abstractCharacter:
    slowDown = 5
    #animation order:
    # 0 idle 
    # 1 turn
    # 2 forward - start 
    # 3 forward - end 
    # 4 forward - loop
    # 5 back - start 
    # 6 back - end 
    # 7 back - loop
    # 8 dash forward - start 
    # 9 dash forward - end 
    # 10 dash forward - loop
    # 11 dash back - start 
    # 12 dash back - end 
    # 13 dash back - loop
    
    # 14 down - start
    # 15 down - loop
    # 16 down - end
    # 17 up - start
    # 18 up - loop
    # 19 up - end
    
    # 20 melee back
    # 21 melee close
    # 22 melee far
    # 23 third melee
    # 24 melee air
    # 25 melee up
    # 26 melee forward
    # 27 melee fourth
    # 28 melee down
    # 29 melee dash
    
    # 30 grab
    # 31 grab miss
    # 32 grab hit
    
    # 33 weak
    # 34 weak up
    # 35 weak down
    # 36 weak forward
    # 37 weak charged
    # 38 weak dash
    
    # 39 strong
    # 40 strong up
    # 41 strong forward
    # 42 strong down
    # 43 strong back
    
    def __init__(self, imagePath, gameObj) -> None:
        self.name = ""
        self.__gameObj = gameObj
        self.spritesheet = pygame.image.load(imagePath).convert_alpha()
        self.frames = [] # list of lists of tuples (((x,y),(x,y)),yoff) 
    
    def getName(self):
        return self.name
    
    def getFrame(self, animationIndex, frameIndex, flip = False):
        actualIndex = frameIndex // abstractCharacter.slowDown
        ((x1,y1),(x2,y2)) = self.frames[animationIndex][0][actualIndex]
        xoff,yoff = self.frames[animationIndex][1]
        crop = pygame.Rect(x1,y1,x2-x1,y2-y1)
        frame = self.spritesheet.subsurface(crop)
        
        if flip:
            frame = pygame.transform.flip(frame)
        
        return (frame, (frameIndex + 1) % (len(self.frames[animationIndex][0]) * abstractCharacter.slowDown))
    
    def setFrames(self, animations):
        self.frames = animations