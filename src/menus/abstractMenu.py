from typing import List
from menus.options.abstractOption import abstractOption

class abstractMenu:
    def __init__(self, name) -> None:
        self.__name = name
        
    def getName(self):
        return self.__name
    
    def getOptions(self) -> List[abstractOption]:
        return []