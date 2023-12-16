from menus.abstractMenu import abstractMenu

from menus.options.exitOption import exitOption
from menus.options.networkOption import networkOption
from menus.options.localOption import localOption

class mainMenu(abstractMenu):
    def __init__(self, gameObj) -> None:
        super().__init__("Main", None, "assets/images/backgrounds/tempBackground.jpg",\
            gameObj)
        options = [
                localOption(self.getGameObj(), self),
                networkOption(self.getGameObj(), self),
                exitOption(self)
            ]
        self.setOptions(options)