from menus.abstractMenu import abstractMenu
from menus.options.backOption import backOption
from menus.options.abstractOption import abstractOption
from players.networkPlayer import networkPlayer

from characters.koishi import koishi
from characters.miko import miko
from characters.yukari import yukari

import threading
import time

class characterMenu(abstractMenu):
    class characterOption(abstractOption):
        def __init__(self, gameObj, owner, character) -> None:
            super().__init__(character.__name__, owner)
            self.character = character
            self.__gameObj = gameObj
            
        def handler(self):
            self.getOwner().getGameObj().getPlayers()[self.getOwner().currentSelector].setCharacter(self.character)
            
            if type(self.getGameObj().getPlayers()[1]) != networkPlayer:
                if self.getOwner().currentSelector == 0:
                    pass # put on left, set facing right
                else:
                    pass # put on right, set facing left
            
            if self.getOwner().getCurSelector() == 0:
                self.getOwner().changeSelector()
            else:
                self.getOwner().getGameObj().setState(1)
            if self.getOwner().conn != None:
                self.getOwner().conn.sendall("-{0}".format(self.getOwner().currentSelector).encode(encoding = "utf-8"))
            
    def __init__(self, previousMenu, gameObj, firstPickIndex) -> None:
        super().__init__("Character", previousMenu, "assets/images/backgrounds/character.jpg",
                        gameObj)
        self.firstSelector = firstPickIndex
        self.currentSelector = 0
        # characters
        options = [characterMenu.characterOption(gameObj, self, char) for char in [
                koishi, miko, yukari
            ]
        ]
        self.selectors = [0,0]
        self.setOptions(options)
        self.conn = None
        if type(self.getGameObj().getPlayers()[1]) == networkPlayer:
            self.conn = self.getGameObj().getPlayers()[1].getConn()
            
    def runSelection(self):
        if self.firstSelector == 1:
            assert type(self.getGameObj().getPlayers()[1]) == networkPlayer
            networkChoice = threading.Thread(target=self.networkSelection)
            networkChoice.start()
        
    def networkSelection(self):
        self.toggleFocus() # block player from picking
        condition = True
        while condition:
            strRecv = self.conn.recv(1024).decode("utf-8")
            if strRecv[:1] == "-":
                choice = int(strRecv[1:])
                condition = False
                self.getGameObj().getPlayers()[1].setCharacter(self.getOptions()[choice].character)
            else:
                self.selectors[self.currentSelector] = int(strRecv)
            pass
        if self.firstSelector == 0:
            self.getGameObj().setState(1)
        else:
            # let local choose
            self.currentSelector = 1
        self.toggleFocus() # allow player to pick
        
    def getCurSelector(self):
        return self.currentSelector
    
    def changeSelector(self):
        self.currentSelector = int(not self.currentSelector)
        if type(self.getGameObj().getPlayers()[1]) == networkPlayer:
            networkChoice = threading.Thread(target=self.networkSelection)
            networkChoice.start()
        
    #override
    def incrementCursor(self, increment = 1):
        if self.getFocus():
            return
        self.selectors[self.currentSelector] = \
            (self.selectors[self.currentSelector] + increment) % len(self.getOptions())
        if type(self.getGameObj().getPlayers()[1]) == networkPlayer:
            self.conn.sendall("{0}".format(self.selectors[self.currentSelector]).encode(encoding = "utf-8"))
        self.setPos(self.selectors[self.currentSelector])