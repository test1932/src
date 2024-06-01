from effects.abstractEffect import abstractEffect
from characters.abstractCharacter import abstractCharacter

class knockback(abstractEffect):
    # if in Stun, will slide by default
    def __init__(self, player, duration, initVelocity) -> None:
        super().__init__(player, duration)
        self.__duration = duration
        self.__initVelocity = initVelocity
        
    def startEffect(self):
        self.player.setStun(self.__duration)
        self.player.setXVelocity(self.__initVelocity[0])
        self.player.setYVelocity(self.__initVelocity[1])
        
    def applyEffect(self):
        return super().applyEffect()