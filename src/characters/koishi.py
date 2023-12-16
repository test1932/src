from characters.abstractCharacter import abstractCharacter

class koishi(abstractCharacter):
    def __init__(self,gameObj) -> None:
        super().__init__("assets/images/sheets/koishi.png",gameObj)