from players.abstractPlayer import abstractPlayer

class networkPlayer(abstractPlayer):
    def __init__(self, gameObj, networkConn) -> None:
        super().__init__(gameObj)
        self.__conn = networkConn
    
    def listenForInput(self):
        data = self.__conn.recv(1024)
        #TODO something with this
        
    def getConn(self):
        return self.__conn