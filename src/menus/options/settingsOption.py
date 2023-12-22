from menus.options.abstractOption import abstractOption
from menus.settingsMenu import settingsMenu

class settingsOption(abstractOption):
    def __init__(self, gameObj, owner) -> None:
        super().__init__("Settings", owner)
        self.settingsMenu = settingsMenu(owner, gameObj)
        self.__gameObj = gameObj
        
    def handler(self):
        self.__gameObj.setCurrentMenu(self.settingsMenu)