class abstractPlayer:
    LEFT = 0
    RIGHT = 1
    
    def __init__(self) -> None:
        self.animationNumber = 0
        self.animationFrame = 0
        self.timeSinceLastFrame = 0
        self.character = None # character class for move set and skills
        self.health = 0
        self.facingDirection = abstractPlayer.RIGHT
        
        self.playerBody = None # physical body object representing the player
        self.playerSpells = []
        
    def getImage(self):
        """acquires the composited image of the player with effects.

        Raises:
            NotImplementedError: needs to be overridden by subclasses
        """
        raise NotImplementedError("need to implement this method")