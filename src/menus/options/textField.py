from menus.options.abstractOption import abstractOption

class textField(abstractOption):
    def __init__(self, name, initText, owner) -> None:
        super().__init__(name, owner)
        self.__text = initText
        self.__pos = len(initText)
        self.__owner = owner
        
    def deleteChar(self):
        if self.__pos == 0:
            return
        self.__text = self.__text[:self.__pos - 1] + self.__text[self.__pos:]
        self.__pos -= 1
    
    def putChar(self, char):
        self.__text = [*self.__text[:self.__pos], char, *self.__text[self.__pos:]]
        self.__pos += 1
    
    def incrementPos(self):
        self.__pos += 1 % (len(self.__text) + 1)
        
    def decrementPos(self):
        self.__pos -= 1 % (len(self.__text) + 1)
        
    def handler(self):
        self.__owner.toggleFocus()
        
    def getText(self):
        return self.__text
    
    def getShowText(self):
        return "".join([*self.__text[:self.__pos],'|',*self.__text[self.__pos:]])