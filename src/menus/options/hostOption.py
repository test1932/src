from menus.options.abstractOption import abstractOption

class hostOption(abstractOption):
    def __init__(self, gameObj, parent) -> None:
        super().__init__("Host", parent)
        self.__gameObj = gameObj
    
    def handler(self):
        pass
        # wait for a user to connect and set the opponent to a network client
    
        # TODO
        # put game into waiting state
        # start thread to await user cancellation or a connection
            # starts a thread to await a connection