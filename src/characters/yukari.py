from characters.abstractCharacter import abstractCharacter

class yukari(abstractCharacter):
    def __init__(self, gameObj) -> None:
        super().__init__("assets/images/sheets/yukari.png", gameObj)
        self.framePoints = [
            ([((1 + i * 257, 968), (256 + i * 257, 1143)) for i in range(11)], (135,52)), # idle
            ([((1 + (11 + i) * 257, 968), (256 + (11 + i) * 257, 1143)) for i in range(3)], (135,52)), # turn
            
            ([((1 + i * 257, 1156), (256 + i * 257, 1331)) for i in range(2)], 0), #forward start
            ([((1 + (2 + i) * 257, 1156), (256 + (2 + i) * 257, 1331)) for i in range(5)], (135,52)), #forward loop
            ([((1 + (7 + i) * 257, 1156), (256 + (7 + i) * 257, 1331)) for i in range(2)], (135,52)), #forward end
            
            ([((1 + i * 257, 1344), (256 + i * 257, 1519)) for i in range(2)], 0), #back start
            ([((1 + (2 + i) * 257, 1344), (256 + (2 + i) * 257, 1519)) for i in range(5)], (135,52)), #back loop
            ([((1 + (7 + i) * 257, 1344), (256 + (7 + i) * 257, 1519)) for i in range(2)], (135,52)) #back end
            
            # TODO the rest
        ]
        self.setFrames(self.framePoints)