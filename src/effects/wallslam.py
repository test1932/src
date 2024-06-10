from effects.abstractEffect import abstractEffect
from characters.abstractCharacter import abstractCharacter

class wallslam(abstractEffect):
    LAUNCH = 0
    REBOUND = 1
    
    # if in Stun, will slide by default
    def __init__(self, player, duration, initVelocity) -> None:
        super().__init__(player, 20)
        self.__initVelocity = initVelocity
        self.__state = wallslam.LAUNCH
        
    def startEffect(self):
        self.player.setStun(20)
        self.player.setXVelocity(self.__initVelocity[0])
        self.player.setYVelocity(self.__initVelocity[1])
        self.player.setAnimation(abstractCharacter.HIT_LOW)
        
    def applyEffect(self):
        if self.__state == self.LAUNCH:
            leftWall = self.player.getMinXhitbox()[0] <= 50
            rightWall = (y:= self.player.getMaxXhitbox())[0] + y[1] >= self.player.getGameObj().WIDTH - 50
            if leftWall or rightWall:
                self.player.setAnimation(abstractCharacter.WALL_BOUNCE_END)
                self.player.setXVelocity(100 if leftWall else -100)
                self.player.setYVelocity(self.player.getYVelocity() / 2)
                self.__state = wallslam.REBOUND
        else:
            if self.player.getYPosition() == self.player.getGameObj().HEIGHT // 2:
                self.player.setStun(0)
                self.removeEffect()
    
    def removeEffect(self):
        super().removeEffect()
        self.player.setXVelocity(0)
        self.player.setYVelocity(0)