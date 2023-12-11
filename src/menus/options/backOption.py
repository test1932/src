from menus.options.abstractOption import abstractOption

class backOption(abstractOption):
    def __init__(self, gameObj, parentMenu, owner) -> None:
        super().__init__("Back", owner)
        self.__gameObj = gameObj
        self.__parentMenu = parentMenu
        
    def handler(self):
        self.__gameObj.setCurrentMenu(self.__parentMenu)