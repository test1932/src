from characters.abstractCharacter import abstractCharacter

class miko(abstractCharacter):
    def __init__(self,gameObj) -> None:
        super().__init__("assets/images/sheets/miko.png",gameObj)