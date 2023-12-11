from menus.options.abstractOption import abstractOption
from menus.networkMenu import networkMenu

class networkOption(abstractOption):
    def __init__(self, gameObj, parentMenu) -> None:
        super().__init__("Network Play", parentMenu)
        self.__gameObj = gameObj
        self.__networkMenu = networkMenu(parentMenu, gameObj)
        
    def handler(self):
        self.__gameObj.setCurrentMenu(self.__networkMenu)