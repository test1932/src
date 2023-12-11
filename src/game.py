import socket
import pygame
from menus.mainMenu import mainMenu
from menus.options.textField import textField

class game:
    MENU = 0
    GAME = 1
    
    def __init__(self, width = 1000, height = 600):
        self.WIDTH = width
        self.HEIGHT = height
        self.titleFont = pygame.font.Font(pygame.font.get_default_font(), 40)
        self.baseFont = pygame.font.Font(pygame.font.get_default_font(), 20)
        self.screen = pygame.display.set_mode([self.WIDTH,self.HEIGHT])
        self.state = game.MENU
        self.currentMenu = mainMenu(self)
        
        #graphics
        self.bgRect = pygame.Surface((700,500))
        self.bgRect.set_alpha(128)
        self.bgRect.fill((255,255,255))
        
    def setCurrentMenu(self, newMenu):
        self.currentMenu = newMenu
        
    def displayMenu(self):
        self.screen.blit(self.currentMenu.getBackground(),(0,0))
        x = self.WIDTH // 8
        y = self.HEIGHT // 8
        self.screen.blit(self.bgRect, (x - 10,y - 20))
        self.screen.blit(self.titleFont.render(self.currentMenu.getName(),False,(0,0,0)), (x,y))
        for i,option in enumerate(self.currentMenu.getOptions()):
            yInc = (i + 1) * 30 + self.HEIGHT // 8
            self.screen.blit(self.baseFont.render(option.getName(), False, (0,0,0)),(x, y + yInc))
            if i == self.currentMenu.getPos():
                pygame.draw.line(self.screen, (255,0,0), (x, y + yInc + 25), (x + self.WIDTH // 8, y + yInc + 25), 3)
            if type(option) == textField:
                text = option.getShowText() if self.currentMenu.getFocus() else option.getText()
                self.screen.blit(self.baseFont.render(text, False, (0,0,0)), (x + self.WIDTH // 2, y + yInc))
                
    def handleMenuInput(self,event):
        if event.type == pygame.KEYUP:
            if event.key == pygame.K_UP:
                self.currentMenu.decrementCursor()
            elif event.key == pygame.K_DOWN:
                self.currentMenu.incrementCursor()
            elif event.key == pygame.K_RETURN:
                self.currentMenu.runHandler()
            elif event.key == pygame.K_RIGHT:
                self.currentMenu.incrementOptionCursor()
            elif event.key == pygame.K_LEFT:
                self.currentMenu.decrementOptionCursor()
            elif event.key == pygame.K_BACKSPACE:
                self.currentMenu.backSpace()
            else:
                self.currentMenu.putKey(event.unicode)

    def handleGameInput(self,event):
        pass
    
    def runGame(self):
        clock = pygame.time.Clock()
        while True:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    pygame.quit()
                if self.state == game.MENU:
                    self.handleMenuInput(event)
                elif self.state == game.GAME:
                    self.handleGameInput()
                    
            self.screen.fill((255,255,255))
            
            if self.state == game.MENU:
                self.displayMenu()
            
            pygame.display.flip()
            clock.tick(60)
        

def main():
    pygame.init()

    # sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    # sock.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, 5)

    # sock.sendto(b"hello", ('224.1.1.1',5007))
    

    baseGame = game()
    baseGame.runGame()
        
if __name__ == '__main__':
    main()