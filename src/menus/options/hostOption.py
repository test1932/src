from menus.options.abstractOption import abstractOption
from menus.characterMenu import characterMenu
from players.networkPlayer import networkPlayer
import socket

class hostOption(abstractOption):
    def __init__(self, gameObj, parent) -> None:
        super().__init__("Host", parent)
        self.__gameObj = gameObj
    
    def handler(self):
        portNo = int(self.getOwner().getOptions()[1].getText())
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.bind(('0.0.0.0', portNo))
        sock.listen()
        conn, addr = sock.accept()
        self.__gameObj.setOpponent(networkPlayer(self.__gameObj, conn))
        
        characterSelectionMenu = characterMenu(self.getOwner(), self.__gameObj, 0)
        
        self.__gameObj.setCurrentMenu(characterSelectionMenu)
        characterSelectionMenu.runSelection()
        
        # TODO change menu to character select, select first
    
        # TODO
        # put game into waiting state
        # handle invalid port
        # start thread to await user cancellation or a connection
            # starts a thread to await a connection