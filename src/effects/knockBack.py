from effects.abstractEffect import abstractEffect
from characters.abstractCharacter import abstractCharacter

class knockback(abstractEffect):
    # if in cooldown, will slide by default
    def __init__(self, player, duration, initVelocity, isHigh = False) -> None:
        super().__init__(player, duration)
        self.__duration = duration
        self.__isHigh = isHigh
        self.__initVelocity = initVelocity
        
    def startEffect(self):
        self.player.setCooldown(self.__duration)
        self.player.setVelocity(self.__initVelocity)
        if self.__isHigh:
            self.player.setAnimation(abstractCharacter.HIT_HIGH)
        else:
            self.player.setAnimation(abstractCharacter.HIT_LOW)
        
    def applyEffect(self):
        return super().applyEffect()