from menus.options.exitOption import exitOption
from menus.abstractMenu import abstractMenu

class mainMenu(abstractMenu):
    def __init__(self) -> None:
        super().__init__("Second Realm of Fallen Star")
        self.options = [
                exitOption()
            ]
        
    def getOptions(self):
        return self.options