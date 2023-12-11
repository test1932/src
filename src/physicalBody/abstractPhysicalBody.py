from typing import List

class abstractPhysicalBody:
    def __init__(self) -> None:
        self.gravityApplies = True
        self.position = [0,0]
        self.velocity = [0,0]
        self.hitboxes = None # pygame Rect objects
    
    def collides(self, other):
        return other.hitbox.collideList(self.hitboxes) == -1