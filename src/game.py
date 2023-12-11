import socket
import pygame
from menus.mainMenu import mainMenu

class game:
    MENU = 0
    GAME = 1
    
    def __init__(self, width = 1000, height = 600):
        self.WIDTH = width
        self.HEIGHT = height
        self.titleFont = pygame.font.Font(pygame.font.get_default_font(), 60)
        self.baseFont = pygame.font.Font(pygame.font.get_default_font(), 30)
        self.screen = pygame.display.set_mode([self.WIDTH,self.HEIGHT])
        self.state = game.MENU
        self.currentMenu = mainMenu()
        self.currentSelection = 0
        
    def displayMenu(self):
        x = self.WIDTH // 8
        y = self.HEIGHT // 8
        self.screen.blit(self.titleFont.render(self.currentMenu.getName(),False,(0,0,0)), (x,y))
        for i,option in enumerate(self.currentMenu.getOptions()):
            yInc = (i + 1) * 50 + self.HEIGHT // 8
            self.screen.blit(self.baseFont.render(option.getName(), False, (0,0,0)),(x, y + yInc))
            if i == self.currentSelection:
                pygame.draw.line(self.screen, (255,0,0), (x, y + yInc + 35), (x + self.WIDTH // 8, y + yInc + 35), 3)
                
    def handleMenuInput(self,event):
        if event.type == pygame.KEYUP:
            if event.key == pygame.K_UP:
                self.currentSelection = (self.currentSelection - 1) % len(self.currentMenu.getOptions)
            elif event.key == pygame.K_DOWN:
                self.currentSelection = (self.currentSelection + 1) % len(self.currentMenu.getOptions)
            elif event.key == pygame.K_RETURN:
                self.currentMenu.getOptions()[self.currentSelection].handler()

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