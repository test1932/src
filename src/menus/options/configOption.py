from menus.options.abstractOption import abstractOption
from menus.configMenu import configMenu

class configOption(abstractOption):
    def __init__(self, playerIndex, player, owner, gameObj) -> None:
        super().__init__(f'Config player {playerIndex + 1}', owner)
        self.__player = player
        self.__gameObj = gameObj
        self.configMenu = configMenu(playerIndex, player, owner, gameObj)
        
    def handler(self):
        self.__gameObj.setCurrentMenu(self.configMenu)