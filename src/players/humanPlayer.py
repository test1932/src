from players.abstractPlayer import abstractPlayer
import pygame

class humanPlayer(abstractPlayer):
    mapping1 = {
            pygame.K_RIGHT: abstractPlayer.RIGHT,
            pygame.K_LEFT: abstractPlayer.LEFT,
            pygame.K_UP: abstractPlayer.UP,
            pygame.K_DOWN: abstractPlayer.DOWN,
            pygame.K_s: abstractPlayer.DASH,
            pygame.K_z: abstractPlayer.MELEE,
            pygame.K_x: abstractPlayer.WEAK,
            pygame.K_c: abstractPlayer.STRONG
        }
    
    mapping2 = {
            pygame.K_d: abstractPlayer.RIGHT,
            pygame.K_a: abstractPlayer.LEFT,
            pygame.K_w: abstractPlayer.UP,
            pygame.K_s: abstractPlayer.DOWN,
            pygame.K_v: abstractPlayer.DASH,
            pygame.K_z: abstractPlayer.MELEE,
            pygame.K_x: abstractPlayer.WEAK,
            pygame.K_c: abstractPlayer.STRONG
        }
    
    def __init__(self, gameObj, index) -> None:
        super().__init__(gameObj)
        self.mapping = dict()
        self.mapping = humanPlayer.mapping1 if index == 0 else humanPlayer.mapping2
        
    def getMapping(self):
        return self.mapping