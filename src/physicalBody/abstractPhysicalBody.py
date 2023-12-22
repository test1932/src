from typing import List
import pygame

class abstractPhysicalBody:
    def __init__(self, p, v) -> None:
        self.gravityApplies = True
        self.position = p
        self.velocity = v
        self.hitbox = pygame.Rect(0,0,10,10) # pygame Rect object
    
    def collides(self, other):
        return other.hitbox.colliderect(self.hitbox)
    
    def setXPosition(self, x):
        self.hitbox.x += x - self.hitbox.x
        self.position = (x, self.position[1])
        
    def setYPosition(self, y):
        self.hitbox.y += y - self.hitbox.y
        self.position = (self.position[0], y)
        
    def getPosition(self):
        return self.position
    
    def setPosition(self,p):
        self.position = p
    
    def setXVelocity(self, x):
        self.velocity = (x, self.velocity[1])
        
    def setYVelocity(self, y):
        self.velocity = (self.velocity[0], y)
        
    def getXVelocity(self):
        return self.velocity[0]
    
    def getYVelocity(self):
        return self.velocity[1]
    
    def getXPosition(self):
        return self.position[0]
    
    def getYPosition(self):
        return self.position[1]
        
    def getHitbox(self):
        return self.hitbox