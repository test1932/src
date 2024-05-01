from menus.abstractMenu import abstractMenu
from menus.options.textField import textField
from menus.options.backOption import backOption
from model.battle import battle

from menus.options.abstractOption import abstractOption
from menus.characterMenu import characterMenu
from players.networkPlayer import networkPlayer
import socket
import struct

class networkMenu(abstractMenu):
    IP_IS_AT = 0
    PORT_IS_AT = 1
    MULTICAST_IS_AT = 2
    MULTICAST_PORT_IS_AT = 3
    
    class spectateOption(abstractOption):
        def __init__(self, gameObj, owner) -> None:
            super().__init__("Spectate", owner)
            self.__gameObj = gameObj
            
        def handler(self):
            multicastIP = self.getOwner().getOptions()[networkMenu.MULTICAST_IS_AT].getText()
            multicastPortNo = int(self.getOwner().getOptions()[networkMenu.MULTICAST_PORT_IS_AT].getText())
            
            sockGrp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
            sockGrp.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            sockGrp.bind(('',multicastPortNo))
            mreq = struct.pack("=4sl", socket.inet_aton(multicastIP), socket.INADDR_ANY)
            sockGrp.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
            self.__gameObj.setMulticastConnListen(sockGrp, multicastIP, multicastPortNo)
            
            self.__gameObj.setOpponent(networkPlayer(self.__gameObj, None, False))
            
            self.__gameObj.setBattle(battle(self.__gameObj, "assets/images/backgrounds/backbackground.jpg",None))
            self.__gameObj.setState(1)

    class connectOption(abstractOption):
        def __init__(self, gameObj, parent) -> None:
            super().__init__("Connect", parent)
            self.__gameObj = gameObj
            
        def handler(self):
            ip = self.getOwner().getOptions()[networkMenu.IP_IS_AT].getText()
            portNo = int(self.getOwner().getOptions()[networkMenu.PORT_IS_AT].getText())
            multicastPortNo = int(self.getOwner().getOptions()[networkMenu.MULTICAST_PORT_IS_AT].getText())
            sock = socket.socket(socket.AF_INET)
            sock.connect((ip, portNo))
            self.__gameObj.setOpponent(networkPlayer(self.__gameObj, sock, False))
            
            ipGroup = sock.recv(10240).decode()
            sockGrp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
            sockGrp.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            sockGrp.bind(('',multicastPortNo))
            mreq = struct.pack("=4sl", socket.inet_aton(ipGroup), socket.INADDR_ANY)
            sockGrp.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
            self.__gameObj.setMulticastConnListen(sockGrp, ipGroup, multicastPortNo)
            
            self.__gameObj.setBattle(battle(self.__gameObj, "assets/images/backgrounds/backbackground.jpg",None))
            characterSelectionMenu = characterMenu(self.getOwner(), self.__gameObj, 1)
            
            self.__gameObj.setCurrentMenu(characterSelectionMenu)
            characterSelectionMenu.runSelection()
            
            #TODO
            # put game into waiting state
            # start thread to await user cancellation or a connection
                # start a thread to connect

    class hostOption(abstractOption):
        def __init__(self, gameObj, parent) -> None:
            super().__init__("Host", parent)
            self.__gameObj = gameObj
        
        def handler(self):
            portNo = int(self.getOwner().getOptions()[networkMenu.PORT_IS_AT].getText())
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.bind(('0.0.0.0', portNo))
            sock.listen()
            conn, addr = sock.accept()
            self.__gameObj.setOpponent(networkPlayer(self.__gameObj, conn, True))
            
            multicastIP = self.getOwner().getOptions()[networkMenu.MULTICAST_IS_AT].getText()
            multicastPortNo = int(self.getOwner().getOptions()[networkMenu.MULTICAST_PORT_IS_AT].getText())
            
            sockGrp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
            sockGrp.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, 5)
            self.__gameObj.setMulticastConnHost(sockGrp, multicastIP, multicastPortNo)
            conn.sendall(multicastIP.encode(encoding = "utf-8"))
            
            characterSelectionMenu = characterMenu(self.getOwner(), self.__gameObj, 0)
            self.__gameObj.setBattle(battle(self.__gameObj, "assets/images/backgrounds/backbackground.jpg",None))
            self.__gameObj.setCurrentMenu(characterSelectionMenu)
            characterSelectionMenu.runSelection()
        
            # TODO
            # put game into waiting state
            # handle invalid port
            # start thread to await user cancellation or a connection
                # starts a thread to await a connection
    
    def __init__(self, previousMenu, gameObj) -> None:
        super().__init__("Network", previousMenu, "assets/images/backgrounds/config.jpg",\
            gameObj)
        options = [
                textField("IP address", "127.0.0.1", self),
                textField("Port", "5000", self),
                textField("Multicast Address", "224.1.1.1", self),
                textField("Multicast Port", "5007", self),
                networkMenu.hostOption(self.getGameObj(), self),
                networkMenu.connectOption(self.getGameObj(), self),
                networkMenu.spectateOption(self.getGameObj(), self),
                backOption(self.getGameObj(), previousMenu, self)
            ]
        self.setOptions(options)