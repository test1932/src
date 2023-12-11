from menus.options.exitOption import exitOption
from menus.options.networkOption import networkOption
from menus.abstractMenu import abstractMenu

class mainMenu(abstractMenu):
    def __init__(self, gameObj) -> None:
        super().__init__("Main", None, "assets/images/backgrounds/tempBackground.jpg",\
            gameObj.WIDTH, gameObj.HEIGHT)
        self.__gameObj = gameObj
        options = [
                networkOption(self.__gameObj, self),
                exitOption(self)
            ]
        self.setOptions(options)