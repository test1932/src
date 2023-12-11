from menus.options.abstractOption import abstractOption

class exitOption(abstractOption):
    def __init__(self) -> None:
        super().__init__("Exit")
        
    def handler(self):
        exit()