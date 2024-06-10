from menus.options.abstractOption import abstractOption
from menus.characterMenu import characterMenu
from players.humanPlayer import humanPlayer
from model.battle import battle

class storyOption(abstractOption):
    def __init__(self, gameObj, parentMenu) -> None:
        super().__init__("STORY", parentMenu)
        self.__gameObj = gameObj
        
    def handler(self):
        self.__gameObj.setHumanOpponent()
        characterSelectionMenu = characterMenu(self.getOwner(), self.__gameObj, 0)
        
        self.__gameObj.setCurrentMenu(characterSelectionMenu)
        self.__gameObj.setBattle(battle(self.__gameObj, "assets/images/backgrounds/Simple Backgrounds/Hakurei Shrine (A).png",None))
        characterSelectionMenu.runSelection()