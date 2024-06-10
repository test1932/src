from menus.options.abstractOption import abstractOption
from players.abstractPlayer import abstractPlayer
import pygame

class keyBindingOption(abstractOption):
    def __init__(self, keyName, player, owner, currentKey, action) -> None:
        super().__init__(abstractPlayer.actionNames[keyName], owner)
        self.__player = player
        self.currentKey = currentKey
        self.action = action
        
    def getCurrentKey(self):
        return pygame.key.name(self.currentKey)
    
    def handler(self):
        self.getOwner().toggleFocus()
        
    def putChar(self, key):
        del self.__player.getMapping()[self.currentKey]
        self.currentKey = key
        self.__player.getMapping()[self.currentKey] = self.action
        self.getOwner().toggleFocus()