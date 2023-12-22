from characters.abstractCharacter import abstractCharacter

class yukari(abstractCharacter):
    isSetup = False
    projectiles = []
    
    @classmethod
    def loadProjectileImgs(cls):
        pass # TODO
    
    def __init__(self, gameObj) -> None:
        super().__init__("assets/images/sheets/yukari.png", gameObj, 500)
        self.framePoints = [
            ([((1 + i * 257, 968), (256 + i * 257, 1143)) for i in range(11)], (135,52)), # idle
            ([((1 + (11 + i) * 257, 968), (256 + (11 + i) * 257, 1143)) for i in range(3)], (135,52)), # turn
            
            ([((1 + i * 257, 1156), (256 + i * 257, 1331)) for i in range(2)], (135,52)), #forward start
            ([((1 + (7 + i) * 257, 1156), (256 + (7 + i) * 257, 1331)) for i in range(2)], (135,52)), #forward end
            ([((1 + (2 + i) * 257, 1156), (256 + (2 + i) * 257, 1331)) for i in range(5)], (135,52)), #forward loop
            
            ([((1 + i * 257, 1344), (256 + i * 257, 1519)) for i in range(2)], (149,52)), #back start
            ([((1 + (7 + i) * 257, 1344), (256 + (7 + i) * 257, 1519)) for i in range(2)], (149,52)), #back end
            ([((1 + (2 + i) * 257, 1344), (256 + (2 + i) * 257, 1519)) for i in range(5)], (149,52)), #back loop
            
            ([((1 + i * 289, 1532),(288 + i * 289, 1707)) for i in range(4)], (127,52)), # dash forward start
            ([((1 + (9 + i) * 289, 1532),(288 + (9 + i) * 289, 1707)) for i in range(3)], (127,52)), # dash forward end
            ([((1 + (4 + i) * 289, 1532),(288 + (4 + i) * 289, 1707)) for i in range(5)], (127,52)), # dash forward loop
            
            ([((1 + i * 273, 1720),(272 + i * 273, 1895)) for i in range(3)], (135,52)), # dash back start
            ([((1 + (8 + i) * 273, 1720),(272 + (8 + i) * 273, 1895)) for i in range(3)], (135,52)), # dash back end
            ([((1 + (3 + i) * 273, 1720),(272 + (3 + i) * 273, 1895)) for i in range(5)], (135,52)), # dash back loop
            
            ([((1 + i * 273, 1908),(272 + i * 273, 2131)) for i in range(3)], (135,94)), # down start init
            ([((1 + (8 + i) * 273, 1908),(272 + (8 + i) * 273, 2131)) for i in [0]], (135,94)), # down start post
            ([((1 + (3 + i) * 273, 1908),(272 + (3 + i) * 273, 2131)) for i in range(5)], (135,94)), # down loop
            ([((1 + (8 + i) * 273, 1908),(272 + (8 + i) * 273, 2131)) for i in range(1)], (135,94)), # down end init
            ([((1 + i * 273, 1908),(272 + i * 273, 2131)) for i in [2,1,0]], (135,94)), # down end post
            
            ([((1 + (8 + i) * 273, 2133),(272 + (8 + i) * 273, 2356)) for i in [0]], (135,94)), # up start init
            ([((1 + i * 273, 2133),(272 + i * 273, 2356)) for i in range(3)], (135,94)), # up start post
            ([((1 + (3 + i) * 273, 2133),(272 + (3 + i) * 273, 2356)) for i in range(5)], (135,94)), # up loop
            ([((1 + i * 273, 2133),(272 + i * 273, 2356)) for i in [2,1,0]], (135,94)), # up send init
            ([((1 + (8 + i) * 273, 2133),(272 + (8 + i) * 273, 2356)) for i in range(1)], (135,94)) # up end post
            
            
            
            # TODO the rest
        ]
        
        self.setFrames(self.framePoints)
        if not yukari.isSetup:
            yukari.isSetup = True
            yukari.loadProjectileImgs()