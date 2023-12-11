class abstractOption:
    def __init__(self, name, owner) -> None:
        self.__name = name
        self.__owner = owner
    
    def handler(self):
        """Action to perform when clicked
        """
        pass
    
    def getName(self):
        return self.__name
    
    def getOwner(self):
        return self.__owner