from effects.abstractEffect import abstractEffect

class knockback(abstractEffect):
    # if in cooldown, will slide by default
    def __init__(self, player, duration, initVelocity) -> None:
        super().__init__(player, duration)
        self.__duration = duration
        
    def applyEffect(self):
        self.player.setCooldown(self.__duration)
        
    