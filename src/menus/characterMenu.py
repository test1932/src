from menus.abstractMenu import abstractMenu
from menus.options.backOption import backOption
from menus.options.abstractOption import abstractOption
from players.networkPlayer import networkPlayer
from players.abstractPlayer import abstractPlayer

from characters.koishi import koishi
from characters.miko import miko
from characters.yukari import yukari
from characters.byakuren import byakuren

import threading

class characterMenu(abstractMenu):
    characters = [koishi, miko, yukari, byakuren, yukari, yukari]
    
    class characterOption(abstractOption):
        def __init__(self, gameObj, owner, character) -> None:
            super().__init__(character.__name__, owner)
            self.character = character
            self.__gameObj = gameObj
            
        def getIcon(self):
            return self.character.icon
            
        def handler(self):
            selector = self.getOwner().currentSelector
            if self.getOwner().isSelected(self.character):
                return
            
            # local
            if type(self.getOwner().getGameObj().getPlayers()[1]) != networkPlayer:
                self.getOwner().getGameObj().getPlayers()[selector].setCharacter(\
                    self.character, self.getOwner().selectors[selector], 
                    0 if self.getOwner().selectorStage == abstractPlayer.POSSESSOR else 1)
                
                if self.getOwner().currentSelector == 0:
                    self.getOwner().getGameObj().getPlayers()[0].setXPosition((2 * self.getOwner().getGameObj().WIDTH) // 5)
                    self.getOwner().getGameObj().getPlayers()[0].setYPosition(self.getOwner().getGameObj().HEIGHT / 2)
                
                else:
                    self.getOwner().getGameObj().getPlayers()[1].setXPosition((3 * self.getOwner().getGameObj().WIDTH) // 5)
                    self.getOwner().getGameObj().getPlayers()[1].setYPosition(self.getOwner().getGameObj().HEIGHT / 2)
                    self.getOwner().getGameObj().getPlayers()[1].setFacingDirection(0)
            # I am server 
            elif self.getOwner().getGameObj().getPlayers()[1].isServer():
                self.getOwner().getGameObj().getPlayers()[0].setXPosition((2 * self.getOwner().getGameObj().WIDTH) // 5)
                self.getOwner().getGameObj().getPlayers()[0].setYPosition(self.getOwner().getGameObj().HEIGHT / 2)
                self.getOwner().getGameObj().getPlayers()[0].setCharacter(self.character, self.getOwner().selectors[selector])
            # I am client
            else:    
                self.getOwner().getGameObj().getPlayers()[0].setCharacter(self.character, self.getOwner().selectors[selector])
            
            if self.getOwner().conn != None:
                # print(f"about to send {'-{0}'.format(self.getOwner().selectors[selector])}")
                self.getOwner().conn.sendall("-{0}".format(self.getOwner().selectors[selector]).encode(encoding = "utf-8"))
            
            if self.getOwner().selectorStage == abstractPlayer.POSSESSOR:
                self.getOwner().selectorStage = abstractPlayer.SLAVE
            
            elif self.getOwner().getCurSelector() == 0:
                self.getOwner().changeSelector()
                self.getOwner().selectorStage = abstractPlayer.POSSESSOR
            else:
                self.getOwner().getGameObj().setupPlayers()
                # print("switching to game, I am client")
                self.getOwner().getGameObj().setState(1)
            
    def __init__(self, previousMenu, gameObj, firstPickIndex) -> None:
        super().__init__("Character", previousMenu, None,
                        gameObj)
        self.firstSelector = firstPickIndex
        self.currentSelector = 0
        self.selectorStage = abstractPlayer.POSSESSOR
        
        # characters
        options = [characterMenu.characterOption(gameObj, self, char) \
            for char in characterMenu.characters]
        self.selectors = [0,0]
        self.setOptions(options)
        self.conn = None
        if type(self.getGameObj().getPlayers()[1]) == networkPlayer:
            self.conn = self.getGameObj().getPlayers()[1].getConn()
            
    def isSelected(self, char):
        if char == type(self.getGameObj().players[self.currentSelector].characters[0]):
            return True
        return False
            
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
            # print(f"recv {strRecv}")
            if strRecv[:1] == "-":
                choice = int(strRecv[1:])
                condition = False
                self.getGameObj().getPlayers()[1].setCharacter(self.getOptions()[choice].character, choice)
                self.getGameObj().getPlayers()[1].setXPosition((3 * self.getGameObj().WIDTH) // 5)
                self.getGameObj().getPlayers()[1].setYPosition(self.getGameObj().HEIGHT / 2)
                self.getGameObj().getPlayers()[1].setFacingDirection(0)
            else:
                self.selectors[self.currentSelector] = int(strRecv)
            pass
        if self.firstSelector == 0:
            self.getGameObj().setupPlayers()
            # print("switching to game, I am server")
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
    
    def incrementCursor(self, increment = 1):
        self.increment(increment * 3)
    
    #override
    def increment(self, increment):
        if self.getFocus():
            return
        
        row = self.selectors[self.currentSelector] // 3
        column = self.selectors[self.currentSelector] % 3
        
        if abs(increment) == 1:
            column = (column + increment) % 3
        else:
            row = (row + increment // 3) % (len(self.getOptions()) // 3)
        
        self.selectors[self.currentSelector] = row * 3 + column
        if type(self.getGameObj().getPlayers()[1]) == networkPlayer:
            self.conn.sendall("{0}".format(self.selectors[self.currentSelector]).encode(encoding = "utf-8"))
        self.setPos(self.selectors[self.currentSelector])
        
    def incrementOptionCursor(self):
        self.increment(1)
        
    def decrementOptionCursor(self):
        self.increment(-1)