from menus.abstractMenu import abstractMenu
from menus.options.backOption import backOption
from menus.options.configOption import configOption

class settingsMenu(abstractMenu):
    def __init__(self, previousMenu, gameObj) -> None:
        super().__init__("Settings", previousMenu, "assets/images/backgrounds/settings.png", gameObj)
        options = [
            configOption(0, gameObj.getPlayers()[0], self, gameObj),
            configOption(1, gameObj.getHumanOpponent(), self, gameObj),
            backOption(self.getGameObj(), previousMenu, self)
        ]
        self.setOptions(options)