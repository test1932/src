from threading import RLock, Thread
import pygame

class abstractSpellAction(Thread):    
    def __init__(self, player, gameObj, function) -> None:
        super().__init__()
        self.__projectiles = []
        self.__projectileLock = RLock()
        self.player = player
        self.gameObj = gameObj
        self.function = function
        
    def lockProjectiles(self):
        self.__projectileLock.acquire()
        
    def add(self, projectile):
        self.__projectileLock.acquire()
        self.__projectiles.append(projectile)
        self.__projectileLock.release()
        
    def end(self):
        self.player.removeSpellaction(self)
        
    def registerSpellaction(self):
        self.player.addSpellaction(self)
        
    def unregisterSpellaction(self):
        if self in self.player.playerSpells:
            self.player.removeSpellaction(self)
        
    def unlockProjectiles(self):
        self.__projectileLock.release()
        
    def run(self):
        self.registerSpellaction()
        self.function(self, self.player)
        self.unregisterSpellaction()
    
    def getProjectiles(self):
        return self.__projectiles
    
    def remove(self, projectile):
        self.lockProjectiles()
        if projectile in self.__projectiles:
            self.__projectiles.remove(projectile)
        self.unlockProjectiles()
            
    def removeAll(self):
        self.lockProjectiles()
        self.__projectiles = []
        self.unlockProjectiles()
        
    def toString(self):
        self.lockProjectiles()
        output = []
        for projectile in self.__projectiles:
            x,y = projectile.getXPosition(), projectile.getYPosition()
            frameNo = projectile.getFrameNo()
            ID = type(projectile).ID
            output.append(f'{ID},{frameNo},{x},{y}')
        self.unlockProjectiles()
        return ":".join(output)