from players.abstractPlayer import abstractPlayer

class networkPlayer(abstractPlayer):
    def __init__(self, gameObj, networkConn, isServer) -> None:
        super().__init__(gameObj)
        self.__conn = networkConn
        self.__isServer = isServer
        
    def isServer(self):
        return self.__isServer
    
    # To be run on a separate thread
    def getNetInput(self):
        while True:
            data = self.__conn.recv(1024).decode()
            isDown = int(data[0]) == 0
            if isDown:
                self.holdKey(int(data[1:]))
            else:
                self.releaseKey(int(data[1:]))
        
    def getConn(self):
        return self.__conn