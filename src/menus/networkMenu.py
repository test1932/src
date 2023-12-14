from menus.abstractMenu import abstractMenu
from menus.options.textField import textField
from menus.options.backOption import backOption
from menus.options.hostOption import hostOption
from menus.options.connectOption import connectOption

class networkMenu(abstractMenu):
    IP_IS_AT = 0
    PORT_IS_AT = 1
    
    def __init__(self, previousMenu, gameObj) -> None:
        super().__init__("Network", previousMenu, "assets/images/backgrounds/config.jpg",\
            gameObj)
        options = [
                textField("IP address", "127.0.0.1", self),
                textField("Port", "5000", self),
                hostOption(self.getGameObj(), self),
                connectOption(self.getGameObj(), self),
                backOption(self.getGameObj(), previousMenu, self)
            ]
        self.setOptions(options)