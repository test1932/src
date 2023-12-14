from menus.options.abstractOption import abstractOption
from menus.characterMenu import characterMenu
from players.networkPlayer import networkPlayer
import socket

class connectOption(abstractOption):
    def __init__(self, gameObj, parent) -> None:
        super().__init__("Connect", parent)
        self.__gameObj = gameObj
        
    def handler(self):
        ip = self.getOwner().getOptions()[0].getText()
        portNo = int(self.getOwner().getOptions()[1].getText())
        sock = socket.socket(socket.AF_INET)
        sock.connect((ip, portNo))
        
        self.__gameObj.setOpponent(networkPlayer(self.__gameObj,sock))
        
        characterSelectionMenu = characterMenu(self.getOwner(), self.__gameObj, 1)
        
        self.__gameObj.setCurrentMenu(characterSelectionMenu)
        characterSelectionMenu.runSelection()
        
        # TODO change menu to character select, wait for opponent to select first
        
        #TODO
        # put game into waiting state
        # start thread to await user cancellation or a connection
            # start a thread to connect