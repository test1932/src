from typing import List

class abstractPhysicalBody:
    def __init__(self, p, v) -> None:
        self.gravityApplies = True
        self.position = p
        self.velocity = v
        self.hitboxes = None # pygame Rect objects
    
    def collides(self, other):
        return other.hitbox.collideList(self.hitboxes) == -1