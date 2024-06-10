from menus.abstractMenu import abstractMenu

from menus.options.exitOption import exitOption
from menus.options.musicOption import musicOption
from menus.options.networkOption import networkOption
from menus.options.localOption import localOption
from menus.options.practiceOption import practiceOption
from menus.options.replayOption import replayOption
from menus.options.settingsOption import settingsOption
from menus.options.storyOption import storyOption
from menus.options.aiOption import aiOption

class mainMenu(abstractMenu):
    def __init__(self, gameObj) -> None:
        super().__init__("", None, "assets/images/backgrounds/tempBackground.jpg",\
            gameObj)
        options = [
                storyOption(self.getGameObj(), self),
                aiOption(self.getGameObj(), self),
                localOption(self.getGameObj(), self),
                networkOption(self.getGameObj(), self),
                practiceOption(self.getGameObj(), self),
                replayOption(self.getGameObj(), self),
                musicOption(self.getGameObj(), self),
                settingsOption(self.getGameObj(), self),
                exitOption(self)
            ]
        self.setOptions(options)