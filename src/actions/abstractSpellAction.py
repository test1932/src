from threading import Lock, Thread
import pygame

class abstractSpellAction(Thread):
    def __init__(self, imagePath) -> None:
        self.__projectiles = []
        self.__projectileLock = Lock()
        self.image = pygame.image.load(imagePath)
        
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