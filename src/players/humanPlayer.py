from players.abstractPlayer import abstractPlayer

class humanPlayer(abstractPlayer):
    def __init__(self, gameObj) -> None:
        super().__init__(gameObj)
        self.inputSource = None
        
    def changeInputSOurce(self, newInputSource):
        # change between keyboard / controller
        self.inputSource = newInputSource
        
    def listenForInput(self):
        pass # TODO listen to keyboard/controller