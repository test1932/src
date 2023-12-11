import time

class battle:
    def __init__(self, gameObj) -> None:
        self.__gameObj = gameObj
        self.lastUpdateTime = None # time of last frame
        self.timeIncrement = None # time since last frame
        
    def updatebattle(self):
        if self.lastUpDateTime == None:
            self.lastUpdateTime = time.time()
            return
        self.timeIncrement = time.time() - self.lastUpdateTime()
        #TODO
        # * update player velocities
        #   - handle collision with players
        # * update player positions
        # * update projectile velocities
        # * update projectile positions
        #   - handle collision with players and other projectiles