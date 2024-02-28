from characters.abstractCharacter import abstractCharacter
from actions.abstractSpellAction import abstractSpellAction

class yukari(abstractCharacter):
    # class startDash(abstractSpellAction):
    #     def __init__(self, player, gameObj) -> None:
    #         super().__init__(player, gameObj)
            
    #     def run(self):
    #         pass # on start flip dashing
        
    # class endDash(abstractSpellAction):
    #     def __init__(self, player, gameObj) -> None:
    #         super().__init__(player, gameObj)
        
    #     def run(self):
    #         pass # flip back dashing
        
    # class weakAction(abstractSpellAction):
    #     def __init__(self, player, gameObj) -> None:
    #         super().__init__(player, gameObj)
    
    isSetup = False
    projectiles = []
    
    @classmethod
    def loadProjectileImgs(cls):
        pass # TODO
    
    def __init__(self, gameObj) -> None:
        super().__init__("assets/images/sheets/yukari.png", gameObj, 600, 135, 52)
        self.framePoints = [
            ([((1 + i * 257, 968), (256 + i * 257, 1143)) for i in range(11)], (0,0)), # idle
            ([((1 + (11 + i) * 257, 968), (256 + (11 + i) * 257, 1143)) for i in range(3)], (0,0)), # turn
            
            ([((1 + i * 257, 1156), (256 + i * 257, 1331)) for i in range(2)], (-7,0)), #forward start
            ([((1 + (7 + i) * 257, 1156), (256 + (7 + i) * 257, 1331)) for i in range(2)], (-7,0)), #forward end
            ([((1 + (2 + i) * 257, 1156), (256 + (2 + i) * 257, 1331)) for i in range(5)], (-7,0)), #forward loop
            
            ([((1 + i * 257, 1344), (256 + i * 257, 1519)) for i in range(2)], (10,0)), #back start
            ([((1 + (7 + i) * 257, 1344), (256 + (7 + i) * 257, 1519)) for i in range(2)], (10,0)), #back end
            ([((1 + (2 + i) * 257, 1344), (256 + (2 + i) * 257, 1519)) for i in range(5)], (10,0)), #back loop
            
            ([((1 + i * 289, 1532),(288 + i * 289, 1707)) for i in range(4)], (-8,0)), # dash forward start
            ([((1 + (9 + i) * 289, 1532),(288 + (9 + i) * 289, 1707)) for i in range(3)], (-8,0)), # dash forward end
            ([((1 + (4 + i) * 289, 1532),(288 + (4 + i) * 289, 1707)) for i in range(5)], (-8,0)), # dash forward loop
            
            ([((1 + i * 273, 1720),(272 + i * 273, 1895)) for i in range(3)], (0,0)), # dash back start
            ([((1 + (8 + i) * 273, 1720),(272 + (8 + i) * 273, 1895)) for i in range(3)], (0,0)), # dash back end
            ([((1 + (3 + i) * 273, 1720),(272 + (3 + i) * 273, 1895)) for i in range(5)], (0,0)), # dash back loop
            
            ([((1 + i * 273, 1908),(272 + i * 273, 2131)) for i in range(3)], (0,42)), # down start init
            ([((1 + (8 + i) * 273, 1908),(272 + (8 + i) * 273, 2131)) for i in [0]], (0,42)), # down start post
            ([((1 + (3 + i) * 273, 1908),(272 + (3 + i) * 273, 2131)) for i in range(5)], (0,42)), # down loop
            ([((1 + (8 + i) * 273, 1908),(272 + (8 + i) * 273, 2131)) for i in range(1)], (0,42)), # down end init
            ([((1 + i * 273, 1908),(272 + i * 273, 2131)) for i in [2,1,0]], (0,42)), # down end post
            
            ([((1 + (8 + i) * 273, 2133),(272 + (8 + i) * 273, 2356)) for i in [0]], (0,42)), # up start init
            ([((1 + i * 273, 2133),(272 + i * 273, 2356)) for i in range(3)], (0,42)), # up start post
            ([((1 + (3 + i) * 273, 2133),(272 + (3 + i) * 273, 2356)) for i in range(5)], (0,42)), # up loop
            ([((1 + i * 273, 2133),(272 + i * 273, 2356)) for i in [2,1,0]], (0,42)), # up send init
            ([((1 + (8 + i) * 273, 2133),(272 + (8 + i) * 273, 2356)) for i in range(1)], (0,42)) # up end post
            
            
            
            # TODO the rest
        ]
        
        self.setFrames(self.framePoints)
        if not yukari.isSetup:
            yukari.isSetup = True
            yukari.loadProjectileImgs()
            
    # actions
    def forwardMelee(self, spellaction):
        raise NotImplementedError("stub action")
    
    def backMelee(self, spellaction):
        raise NotImplementedError("stub action")
    
    def downMelee(self, spellaction):
        raise NotImplementedError("stub action")
    
    def upMelee(self, spellaction):
        raise NotImplementedError("stub action")
    
    def melee(self, spellaction):
        raise NotImplementedError("stub action")
    
    def forwardWeak(self, spellaction):
        raise NotImplementedError("stub action")
    
    def backWeak(self, spellaction):
        raise NotImplementedError("stub action")
    
    def downWeak(self, spellaction):
        raise NotImplementedError("stub action")
    
    def upWeak(self, spellaction):
        raise NotImplementedError("stub action")
    
    def weak(self, spellaction):
        print("TODO")
    
    def forwardStrong(self, spellaction):
        raise NotImplementedError("stub action")
    
    def backStrong(self, spellaction):
        raise NotImplementedError("stub action")
    
    def downStrong(self, spellaction):
        raise NotImplementedError("stub action")
    
    def upStrong(self, spellaction):
        raise NotImplementedError("stub action")
    
    def strong(self, spellaction):
        raise NotImplementedError("stub action")