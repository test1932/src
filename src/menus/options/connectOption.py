from menus.options.abstractOption import abstractOption

class connectOption(abstractOption):
    def __init__(self, gameObj, parent) -> None:
        super().__init__("Connect", parent)
        self.__gameObj = gameObj
        
    def handler(self):
        pass
        # connect to a network server
        
        #TODO
        # put game into waiting state
        # start thread to await user cancellation or a connection
            # start a thread to connect