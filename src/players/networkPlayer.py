from players.abstractPlayer import abstractPlayer

class networkPlayer(abstractPlayer):
    def __init__(self, gameObj, networkConn, isServer) -> None:
        super().__init__(gameObj)
        self.__conn = networkConn
        self.__isServer = isServer
    
    def run(self):
        while True:
            data = self.__conn.recv(1024)
        #TODO something with this
        
    def getConn(self):
        return self.__conn