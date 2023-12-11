class abstractOption:
    def __init__(self, name) -> None:
        self.__name = name
    
    def handler(self):
        """Action to perform when clicked
        """
        pass
    
    def getName(self):
        return self.__name