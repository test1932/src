class abstractEffect:
    def __init__(self, player, duration) -> None:
        self.player = player
        self.timeRemaining = duration
        
    def decrementTime(self, timeDec):
        self.timeRemaining -= timeDec
    
    def getRemainingTime(self):
        return self.timeRemaining
    
    def applyEffect(self):
        pass # setup
    
    def removeEffect(self):
        pass # cleanup