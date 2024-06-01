import threading
import pygame
import time
import json

from menus.mainMenu import mainMenu
from menus.options.textField import textField
from menus.characterMenu import characterMenu
from menus.options.keyBindingOption import keyBindingOption
from menus.pauseMenu import pauseMenu

from players.humanPlayer import humanPlayer
from players.networkPlayer import networkPlayer
from players.abstractPlayer import abstractPlayer

from graphics.progressBar import progressBar

DEBUG = True
# DEBUG = False

class game:
    MENU = 0
    GAME = 1
    
    def __init__(self, width = 1000, height = 600):
        self.i = 0 # debugging
        
        self.WIDTH = width
        self.HEIGHT = height
        self.titleFont = pygame.font.Font(pygame.font.get_default_font(), 40)
        self.baseFont = pygame.font.Font(pygame.font.get_default_font(), 20)
        self.screen = pygame.display.set_mode([self.WIDTH,self.HEIGHT])
        self.state = game.MENU
        self.displayRange = [[0, 0], [self.WIDTH, self.HEIGHT]]
        
        #game state
        self.players = [humanPlayer(self, 0), None]
        self.opponentHuman = humanPlayer(self, 1)
        
        self.mainMenu = mainMenu(self)
        self.pauseMenu = pauseMenu(None, self)
        self.currentMenu = self.mainMenu
        self.currentBattle = None
        
        self.multicastString = None
        self.multicastConnListen = None
        self.multicastConnHost = None
        self.multicastIP = None
        self.multicastPort = None
        self.gameDisplay = False
        
        #graphics
        self.bgRect = pygame.Surface((700,500))
        self.bgRect.set_alpha(128)
        self.bgRect.fill((255,255,255))
        
        imagePathMana = "assets/images/other/mana.png"
        maxMana = abstractPlayer.MAX_MANA
        maxhealth = abstractPlayer.MAX_HEALTH
        maxSwap = abstractPlayer.MAX_SWAP_VAL
        self.manabars = [
            progressBar(maxMana, maxMana, 50, 530, 300, 60, imagePathMana, True, 5), 
            progressBar(maxMana, maxMana, 650, 530, 300, 60, imagePathMana, True, 5)
            ]
        self.healthbars = [
            progressBar(maxhealth, maxhealth, 50, 30, 350, 25, None),
            progressBar(maxhealth, maxhealth, 600, 30, 350, 25, None)
        ]
        self.swapbars = [
            progressBar(maxSwap, maxSwap, 50, 60, 100, 10, None),
            progressBar(maxSwap, maxSwap, 850, 60, 100, 10, None)
        ]
        
    def setupPlayers(self):
        for player in self.players:
            if type(player) == networkPlayer and player.isServer():
                thread = threading.Thread(target = player.getNetInput)
                thread.setDaemon(True)
                thread.start()
                
    def displayImage(self, image, coords):
        if image == None:
            if DEBUG:
                print("None image")
        elif isinstance(image, pygame.Rect):
            # debugging images
            if DEBUG:
                pygame.draw.rect(self.screen, (255,0,0), (*coords, image[2], image[3]), 2)
        else:
            factor = self.WIDTH / (self.displayRange[1][0] - self.displayRange[0][0])
            (x,y) = coords
            width = factor * image.get_width()
            height = factor * image.get_height()
            newX = (x - self.displayRange[0][0]) * factor
            newY = (y - self.displayRange[0][1]) * factor
            newImage = pygame.transform.scale(image, (width, height))
            self.screen.blit(newImage, (newX, newY))
        
    def displayBackground(self, image):
        x1 = self.displayRange[0][0]
        y1 = self.displayRange[0][1]
        width = self.displayRange[1][0] - x1
        height = self.displayRange[1][1] - y1
        crop = pygame.Rect(x1, y1, width, height)
        frame = pygame.transform.scale(image.subsurface(crop), (self.WIDTH, self.HEIGHT))
        self.screen.blit(frame, (0,0))
                
    def setHumanOpponent(self):
        self.players[1] = self.opponentHuman
        
    def getHumanOpponent(self):
        return self.opponentHuman
        
    def setMulticastConnListen(self, conn, ip, port):
        self.multicastConnListen = conn
        self.multicastIP = ip
        self.multicastPort = port
        
    def setMulticastConnHost(self, conn, ip, port):
        self.multicastConnHost = conn
        self.multicastIP = ip
        self.multicastPort = port
        
    def setCurrentMenu(self, newMenu):
        self.currentMenu = newMenu
        
    def setState(self, newState):
        assert newState in [game.GAME, game.MENU]
        if newState == game.GAME:
            self.currentBattle.setLastUpdateTime()
        self.state = newState
        
    def startGameDisplay(self):
        self.gameDisplay = True
        displayThread = threading.Thread(target = self.threadMulticastListen)
        displayThread.start()
        
    def setOpponent(self, opponent):
        self.players[1] = opponent
        
    def setBattle(self, battle):
        self.currentBattle = battle
        
    def getPlayers(self):
        return self.players
    
    def drawHighlights(self, i, x, y, yInc):
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
            colour = (100,255,100) if self.currentMenu.getFocus() else (255,0,0)
            pygame.draw.line(self.screen, colour, (x, y + yInc + 25), (x + self.WIDTH // 8, y + yInc + 25), 3)
                
        
    def displayMenu(self):
        self.screen.blit(self.currentMenu.getBackground(),(0,0))
        x = self.WIDTH // 8
        y = self.HEIGHT // 8
        self.screen.blit(self.bgRect, (x - 10,y - 20))
        self.screen.blit(self.titleFont.render(self.currentMenu.getName(),False,(0,0,0)), (x,y))
        for i,option in enumerate(self.currentMenu.getOptions()):
            yInc = (i + 1) * 30 + self.HEIGHT // 8
            self.screen.blit(self.baseFont.render(option.getName(), False, (0,0,0)),(x, y + yInc))
            
            self.drawHighlights(i, x, y, yInc)
            
            if type(option) == textField:
                text = option.getShowText() if self.currentMenu.getFocus() else option.getText()
                self.screen.blit(self.baseFont.render(text, False, (0,0,0)), (x + self.WIDTH // 2, y + yInc))
            elif type(option) == keyBindingOption:
                text = option.getCurrentKey()
                self.screen.blit(self.baseFont.render(text, False, (0,0,0)), (x + self.WIDTH // 2, y + yInc))
    
    # wtf
    def setZoom(self):
        [p1, p2] = self.players
        [mid1, mid2] = [p.getMiddleHitbox() for p in self.players]
        
        xDiff = abs(mid1[0] - mid2[0]) + 400
        yDiff = abs(mid1[1] - mid2[1]) + 400
        
        x1 = max(min(p1.getMinXhitbox()[0], p2.getMinXhitbox()[0]), 0)
        x2 = min(max((x:=p1.getMaxXhitbox())[0] + x[1], (y:=p2.getMaxXhitbox())[0] + y[1]), self.WIDTH)
        y1 = max(min(p1.getMinYhitbox()[0], p2.getMinYhitbox()[0]), 0)
        y2 = min(max((x:=p1.getMaxYhitbox())[0] + x[1], (y:=p2.getMaxYhitbox())[0] + y[1]), self.HEIGHT)
        
        midX = (x2 + x1) / 2
        midY = (y2 + y1) / 2
        
        proportionX = xDiff / self.WIDTH
        proportionY = yDiff / self.HEIGHT
            
        fromX, toX, fromY, toY = 0, self.WIDTH, 0, self.HEIGHT
        
        if proportionX < 1/3 and (proportionX > proportionY or proportionY < 1/3):
            proportion = 1/3
        elif proportionX > proportionY:
            proportion = proportionX
        else:
            proportion = proportionY
        
        multiplier = (proportion / 2)
        fromX = midX - self.WIDTH * multiplier
        toX = midX + self.WIDTH * multiplier
        fromY = midY - self.HEIGHT * multiplier
        toY = midY + self.HEIGHT * multiplier
            
        if toX > self.WIDTH:
            fromX -= toX - self.WIDTH
            toX = self.WIDTH
        elif fromX < 0:
            toX += -fromX
            fromX = 0
            
        if toY > self.HEIGHT:
            fromY -= toY - self.HEIGHT
            toY = self.HEIGHT
        elif fromY < 0:
            toY += -fromY
            fromY = 0
        
        self.displayRange = [[max(fromX,0), max(fromY,0)], [min(toX,self.WIDTH), min(toY,self.HEIGHT)]]
    
    def displayGame(self):
        # print(self.i % 1000)
        # self.i += 1
        self.setZoom()
        if self.multicastConnListen == None:
            self.displayBackground(self.currentBattle.getBackground())
        if type(self.players[1]) == networkPlayer:
            if self.multicastConnListen != None:
                self.displayNetwork()
                return
            else:
                self.sendNetworkDisplay()
        self.displayPlayers()
        self.displayProjectiles()
        if DEBUG:
            for player in self.players:
                for box in player.getHitbox():
                    pygame.draw.rect(self.screen, (255,255,255), box)
        self.displayBattleOverlay()

    def displayBattleOverlay(self):
        self.displayTimeRemaining()
        for i in range(2):
            self.healthbars[i].getImage(self.screen)
            self.manabars[i].getImage(self.screen)
            self.swapbars[i].getImage(self.screen)
    
    def displayTimeRemaining(self):
        x = (self.WIDTH // 2) - 15
        y = self.HEIGHT // 10 - 10
        text = str(int(round(self.currentBattle.getRemainingTime(), 0)))
        # replace with animation
        pygame.draw.circle(self.screen, (255,255,255), (self.WIDTH // 2, self.HEIGHT // 10), self.WIDTH // 30)
        self.screen.blit(self.baseFont.render(text, False, (0,0,0)), (x,y))
        
    def displayProjectiles(self):
        for player in self.players:
            player.lockSpellCards()
            for spellcard in player.getSpellCards():
                spellcard.lockProjectiles()
                self.displaySpellCard(spellcard)
                spellcard.unlockProjectiles()
            player.unlockSpellCards()
            
    def displaySpellCard(self, spellCard):
        for projectile in spellCard.getProjectiles():
            pos = projectile.getPosition()
            self.displayImage(projectile.getImage(), pos)
        
    def displayPlayers(self):
        for player in self.players:
            pos = player.getPosition()
            baseX = player.getCharacter().baseX
            baseY = player.getCharacter().baseY
            
            image, baseWidth, width = player.getImage()
            
            xOff = player.getCharacter().getXoffset() if not player.isFacingLeft() else\
                -player.getCharacter().getXoffset() + (width - baseWidth)
            yOff = player.getCharacter().getYoffset()
            
            self.displayImage(image,(pos[0] - baseX - xOff, pos[1] - baseY - yOff))
            
    def sendNetworkDisplay(self):
        gameState = {
            "time": self.currentBattle.getRemainingTime(),
            "bounds": self.displayRange,
            "players": [
                {
                    "x": p.getXPosition(),
                    "y": p.getYPosition(),
                    "character": p.getCharacterIndex(),
                    "health": p.getHealth(),
                    "mana": p.getMana(),
                    "swap": p.getSwap(),
                    "projectiles": [
                        {
                            "x": projectile.getXPosition(),
                            "y": projectile.getYPosition(),
                            "type": type(projectile).ID,
                            "frame": projectile.getFrameNo(),
                            "direction": projectile.getDirection()
                        }
                        for spellcard in p.getSpellCards() for projectile in spellcard.getProjectiles()
                    ],
                    "animation": p.getAnimationNo(),
                    "frame": p.getFrameNo(),
                    "direction": p.getFacingDirection()
                }
                for p in self.players
            ] 
        }
        self.multicastConnHost.sendto(json.dumps(gameState).encode(encoding = "utf-8"), 
                                      (self.multicastIP, self.multicastPort))
        
    def threadMulticastListen(self):
        while True:
            self.multicastString = self.multicastConnListen.recv(1024).decode()
            time.sleep(0.01)
        
    def displayNetwork(self):
        if not self.gameDisplay:
            self.startGameDisplay()
        if self.multicastString == None:
            return
        
        data = json.loads(self.multicastString)
        
        self.currentBattle.setTime(data['time'])
        self.displayRange = data['bounds']
        self.displayBackground(self.currentBattle.getBackground())

        #character images
        for i,player in enumerate(data['players']):
            self.players[i].setHealth(player['health'])
            self.players[i].setMana(player['mana'])
            self.players[i].setSwapVal(player['swap'])
            
            if self.players[i].getCharacterIndex() == None:
                self.players[i].setCharacter(characterMenu.characters[player['character']], player['character'])
            
            self.players[i].setPosition((player['x'],player['y'])) 
            
            self.players[i].setFacingDirection(abstractPlayer.LEFT if player['direction'] else abstractPlayer.RIGHT)
            self.displayCharacter(self.players[i], player['animation'], player['frame'])
        
        #projectile images
        for i,player in enumerate(data['players']):
            self.displayNetworkProjectiles(player['projectiles'], self.players[i])
            
        self.displayBattleOverlay()
            
    def displayNetworkProjectiles(self, projectiles, player):
        for projectile in projectiles:
            if projectiles['type'] == -1:
                continue
            projectile = type(player).projectiles[projectile['type']]
            self.displayImage(projectile.frames[projectile['frame']], projectile['x'], projectile['y'])
            
    def displayCharacter(self, player, animationNo, frameNo):
        pos = player.getPosition()
        baseX = player.getCharacter().baseX
        baseY = player.getCharacter().baseY
        
        player.setAnimationFrame(animationNo, frameNo)
        
        image, baseWidth, width = player.getImage(isNetwork = True)
            
        xOff = player.getCharacter().getXoffset() if not player.isFacingLeft() else\
            -player.getCharacter().getXoffset() + (width - baseWidth)
        yOff = player.getCharacter().getYoffset()

        self.displayImage(image,(pos[0] - baseX - xOff, pos[1] - baseY - yOff))
        
        
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
                self.currentMenu.putKey(event)

    def sendKeyPress(self, key, isUp):
        if type(self.players[1]) == networkPlayer and not self.players[1].isServer() and \
                self.players[1].getConn() != None:
            self.players[1].getConn().sendall("{0}{1}".format(isUp,key).encode(encoding = "utf-8"))

    def handleGameInput(self,event):
        if event.type == pygame.KEYUP and event.key == pygame.K_ESCAPE:
            self.setState(game.MENU)
            self.setCurrentMenu(self.pauseMenu)
            return
        for player in self.players:
            if not type(player) == humanPlayer:
                continue
            if event.type == pygame.KEYDOWN and event.key in player.mapping:
                player.heldKeys.append(player.mapping[event.key])
                self.sendKeyPress(player.mapping[event.key], 0)
            if event.type == pygame.KEYUP and event.key in player.mapping:
                player.heldKeys.remove(player.mapping[event.key])
                if player.mapping[event.key] in player.ignoreKeys:
                    player.ignoreKeys.remove(player.mapping[event.key])
                self.sendKeyPress(player.mapping[event.key], 1)
    
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
                # print(self.players[0].getHealth(), self.players[1].getHealth())
                if type(self.players[1]) != networkPlayer or self.players[1].isServer():
                    for i,player in enumerate(self.players):
                        player.handleHeldKeys()
                        self.manabars[i].setValue(self.players[i].getMana())
                        self.healthbars[i].setValue(self.players[i].getHealth())
                        self.swapbars[i].setValue(self.players[i].getSwap())
                    self.currentBattle.updatebattle()
                self.displayGame()
            
            pygame.display.flip()
            clock.tick(60)
    

def main():
    pygame.init()

    baseGame = game()
    baseGame.runGame()
        
if __name__ == '__main__':
    main()