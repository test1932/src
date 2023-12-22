from threading import Lock, Thread
import pygame

class abstractSpellAction(Thread):    
    def __init__(self) -> None:
        self.__projectiles = []
        self.__projectileLock = Lock()
        
    def lockProjectiles(self):
        self.__projectileLock.acquire()
        
    def unlockProjectiles(self):
        self.__projectileLock.release()
        
    def getImage(self):
        return self.image
        
    def run(self):
        raise NotImplementedError("need to implement per subclass")
    
    def getProjectiles(self):
        return self.__projectiles
    
    def remove(self, projectile):
        self.__projectiles.remove(projectile)
        
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