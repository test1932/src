from menus.abstractMenu import abstractMenu
from menus.options.keyBindingOption import keyBindingOption
from menus.options.backOption import backOption

class configMenu(abstractMenu):
    def __init__(self, playerIndex, player, previousMenu, gameObj) -> None:
        super().__init__(f'Config player {playerIndex + 1}', previousMenu, "assets/images/backgrounds/config.jpg", \
            gameObj)
        options = [
            keyBindingOption(player.getMapping()[key], player, self, key, player.getMapping()[key]) \
                for key in player.getMapping()
        ]
        options.append(backOption(self.getGameObj(), previousMenu, self))
        self.setOptions(options)