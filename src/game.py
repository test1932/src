import socket
import pygame
from menus.mainMenu import mainMenu
from menus.options.textField import textField
from menus.characterMenu import characterMenu
from players.humanPlayer import humanPlayer
from players.networkPlayer import networkPlayer

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
        self.currentBattle = None
        
        #game state
        self.players = [humanPlayer(self), None]
        
        #graphics
        self.bgRect = pygame.Surface((700,500))
        self.bgRect.set_alpha(128)
        self.bgRect.fill((255,255,255))
        
    def setCurrentMenu(self, newMenu):
        self.currentMenu = newMenu
        
    def setState(self, newState):
        assert newState in [game.GAME, game.MENU]
        self.state = newState
        
    def setOpponent(self, opponent):
        self.players[1] = opponent
        
    def setBattle(self, battle):
        self.currentBattle = battle
        
    def getPlayers(self):
        return self.players
        
    def displayMenu(self):
        self.screen.blit(self.currentMenu.getBackground(),(0,0))
        x = self.WIDTH // 8
        y = self.HEIGHT // 8
        self.screen.blit(self.bgRect, (x - 10,y - 20))
        self.screen.blit(self.titleFont.render(self.currentMenu.getName(),False,(0,0,0)), (x,y))
        for i,option in enumerate(self.currentMenu.getOptions()):
            yInc = (i + 1) * 30 + self.HEIGHT // 8
            self.screen.blit(self.baseFont.render(option.getName(), False, (0,0,0)),(x, y + yInc))
            
            if type(self.currentMenu) == characterMenu:
                if i == self.currentMenu.selectors[0]:
                    pygame.draw.line(self.screen, (255,0,0),\
                                    (x, y + yInc + 25), \
                                    (x + self.WIDTH // 16, y + yInc + 25), 3)
                if i == self.currentMenu.selectors[1]:
                    pygame.draw.line(self.screen, (0,0,255), \
                                    (x + self.WIDTH // 16, y + yInc + 25), \
                                    (x + self.WIDTH // 8, y + yInc + 25), 3)
            elif i == self.currentMenu.getPos():
                pygame.draw.line(self.screen, (255,0,0), (x, y + yInc + 25), (x + self.WIDTH // 8, y + yInc + 25), 3)
            if type(option) == textField:
                text = option.getShowText() if self.currentMenu.getFocus() else option.getText()
                self.screen.blit(self.baseFont.render(text, False, (0,0,0)), (x + self.WIDTH // 2, y + yInc))
                
    def displayGame(self):
        if type(self.players[1]) == networkPlayer:
            self.displayNetwork()
            return
        self.screen.blit(self.currentBattle.getBackground(),(0,0))
        self.displayPlayers()
        self.displayProjectiles()
        #display health bars and stuff
        
    def displayProjectiles(self):
        for player in self.players:
            player.lockSpellCards()
            for spellcard in player.getSpellCards():
                spellcard.lockProjectiles()
                self.displaySpellCard(spellcard)
                spellcard.unlockProjectiles()
            player.unlockSpellCards()
            
    def displaySpellCard(self, spellCard):
        for projectile in spellCard.getProjectiles:
            pos = projectile.getPosition()
            self.screen.blit(projectile.getImage(), pos)
        
    def displayPlayers(self):
        for player in self.players:
            pos = player.getPosition()
            self.screen.blit(player.getImage(),(pos[0], pos[1]))
        
    def displayNetwork(self):
        pass # TODO multicast shit
        
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
    
    def otherPlayer(self, player):
        if player == self.players[0]:
            return self.players[1]
        return self.players[0]
    
    def runGame(self):
        clock = pygame.time.Clock()
        while True:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    pygame.quit()
                    exit()
                if self.state == game.MENU:
                    self.handleMenuInput(event)
                elif self.state == game.GAME:
                    self.handleGameInput(event)
                    
            self.screen.fill((255,255,255))
            
            if self.state == game.MENU:
                self.displayMenu()
            elif self.state == game.GAME:
                self.currentBattle.updatebattle()
                self.displayGame()
            
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