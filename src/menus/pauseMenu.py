from menus.abstractMenu import abstractMenu
from menus.options.abstractOption import abstractOption
from menus.options.settingsOption import settingsOption

class pauseMenu(abstractMenu):
    class resumeOption(abstractOption):
        def __init__(self, owner, gameObj) -> None:
            super().__init__("Resume", owner)
            self.__gameObj = gameObj
        
        def handler(self):
            self.__gameObj.setState(1)
    
    class mainMenuOption(abstractOption):
        def __init__(self, owner, gameObj) -> None:
            super().__init__("Exit", owner)
            self.__gameObj = gameObj
        
        def handler(self):
            self.__gameObj.setCurrentMenu(self.__gameObj.mainMenu)
    
    def __init__(self, previousMenu, gameObj) -> None:
        super().__init__("Pause", previousMenu, "assets/images/backgrounds/tempPause.png", gameObj)
        options = [
            pauseMenu.resumeOption(self, gameObj),
            settingsOption(gameObj, self),
            pauseMenu.mainMenuOption(self, gameObj)
        ]
        self.setOptions(options)