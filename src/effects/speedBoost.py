from effects.abstractEffect import abstractEffect
from characters.abstractCharacter import abstractCharacter

class speedBoost(abstractEffect):
    def __init__(self, player, duration, value, direction) -> None:
        super().__init__(player, duration)
        self.value = value
        self.direction = direction
        
    def startEffect(self):
        pass
        
    def applyEffect(self):
        self.player.setXVelocity(self.direction * self.value)
        
class dashBoost(speedBoost):
    def __init__(self, player, value, direction) -> None:
        super().__init__(player, 180, value, direction)
        
    def applyEffect(self):
        super().applyEffect()
        if not self.player.isDashing():
            self.setDuration(0)