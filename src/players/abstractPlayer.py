class abstractPlayer:
    LEFT = 0
    RIGHT = 1
    
    def __init__(self, gameObj) -> None:
        self.animationNumber = 0
        self.animationFrame = 0
        self.timeSinceLastFrame = 0
        self.character = None # character class for move set and skills
        self.health = 0
        self.facingDirection = abstractPlayer.RIGHT
        
        self.__gameObj = gameObj
        
        self.playerBody = None # physical body object representing the player
        self.playerSpells = []
        
    def getImage(self):
        """acquires the composited image of the player with effects.

        Raises:
            NotImplementedError: needs to be overridden by subclasses
        """
        raise NotImplementedError("need to implement this method")
    
    def listenForInput(self):
        """run on a separate thread for affecting change to the model.

        Raises:
            NotImplementedError: needs to be overridden by subclasses
        """
        raise NotImplementedError("need to implement this method")