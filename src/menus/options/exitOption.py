from menus.options.abstractOption import abstractOption

class exitOption(abstractOption):
    def __init__(self, owner) -> None:
        super().__init__("Exit", owner)
        
    def handler(self):
        exit()