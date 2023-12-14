from typing import List
from menus.options.abstractOption import abstractOption
from menus.options.textField import textField
import pygame

class abstractMenu:
    def __init__(self, name, previousMenu, bgPath, gameObj, options = []) -> None:
        self.__gameObj = gameObj
        self.__name = name
        self.previousMenu = previousMenu
        self.__focus = False
        self.__options = options
        self.__pos = 0
        self.background = pygame.transform.scale(pygame.image.load(bgPath), (gameObj.WIDTH, gameObj.HEIGHT))
    
    def getGameObj(self):
        return self.__gameObj
        
    def getName(self):
        return self.__name
    
    def getOptions(self) -> List[abstractOption]:
        return []
    
    def getFocus(self):
        return self.__focus
    
    def toggleFocus(self):
        self.__focus = not self.__focus
        
    def incrementCursor(self, increment = 1):
        if not self.__focus:
            self.__pos = (self.__pos + increment) % len(self.__options)
    
    def decrementCursor(self, decrement = 1):
        self.incrementCursor(increment = -decrement)
        
    def getPos(self):
        return self.__pos
    
    def setOptions(self, options):
        self.__options = options
        
    def getOptions(self):
        return self.__options
    
    def runHandler(self):
        self.__options[self.__pos].handler()
        
    def incrementOptionCursor(self):
        if not self.__focus:
            return
        if type(self.__options[self.__pos]) == textField:
            self.__options[self.__pos].incrementPos()
            
    def decrementOptionCursor(self):
        if not self.__focus:
            return
        if type(self.__options[self.__pos]) == textField:
            self.__options[self.__pos].decrementPos()
            
    def backSpace(self):
        if not self.__focus:
            return
        if type(self.__options[self.__pos]) == textField:
            self.__options[self.__pos].deleteChar()
            
    def putKey(self, char):
        if not self.__focus:
            return
        if type(self.__options[self.__pos]) == textField:
            self.__options[self.__pos].putChar(char)
            
    def getBackground(self):
        return self.background