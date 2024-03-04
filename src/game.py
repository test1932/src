import threading
import pygame

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
        self.displayRange = [[0, self.WIDTH], [0, self.HEIGHT]]
        
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
        
    def setMulticastConnListen(self, conn):
        self.multicastConnListen = conn
        
    def setMulticastConnHost(self, conn):
        self.multicastConnHost = conn
        
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
    
    def setZoom(self):
        [p1, p2] = self.players
        xDiff = abs(p1.getHitbox().x - p2.getHitbox().x) + max(p1.getHitbox().width, p2.getHitbox().width)
        yDiff = abs(p1.getYPosition() - p2.getYPosition())
        x1 = max(min(p1.getHitbox().x, p2.getHitbox().x), 0)
        x2 = min(max(p1.getHitbox().x + p1.getHitbox().width, p2.getHitbox().x + p2.getHitbox().width), self.WIDTH)
        y1 = max(min(p1.getHitbox().y, p2.getHitbox().y), 0)
        y2 = min(max(p1.getHitbox().y + p1.getHitbox().height, p2.getHitbox().y + p2.getHitbox().height), self.HEIGHT)
            
        if (x2 - x1) < self.WIDTH / 2:
            d = self.WIDTH / 2 - (x2 - x1)
            x1 -= d / 2
            x2 += d / 2
            xDiff = self.WIDTH / 2

        buffer = 200 if xDiff + 200 <= self.WIDTH else self.WIDTH - xDiff
        xDiff += buffer
        x1 -= buffer // 2
        x2 += buffer // 2
            
        if xDiff / self.WIDTH > yDiff / self.HEIGHT:
            yDiff = ((self.HEIGHT * xDiff) / self.WIDTH) - (y2 - y1) # amount needing to be split
            y1 -= yDiff / 2
            y2 += yDiff / 2
        else:
            xDiff = ((self.WIDTH * yDiff) / self.HEIGHT) - (x2 - x1) # amount needing to be split
            x1 -= xDiff / 2
            x2 += xDiff / 2
            
        if x2 > self.WIDTH:
            x1 -= x2 - self.WIDTH
            x2 = self.WIDTH
        elif x1 < 0:
            x2 += -x1
            x1 = 0
            
        if y2 > self.HEIGHT:
            y1 -= y2 - self.HEIGHT
            y2 = self.HEIGHT
        elif y1 < 0:
            y2 += -y1
            y1 = 0
            
        self.displayRange = ((x1,y1),(x2,y2))
    
    def displayGame(self):
        # print(self.i % 1000)
        # self.i += 1
        self.setZoom()
        if self.multicastConnListen == None:
            self.setZoom()
            self.displayBackground(self.currentBattle.getBackground())
        if type(self.players[1]) == networkPlayer:
            if self.multicastConnListen != None:
                self.displayNetwork()
                return
            else:
                self.sendNetworkDisplay()
        self.displayPlayers()
        self.displayProjectiles()
        for player in self.players:
            pygame.draw.rect(self.screen, (255,255,255), player.getHitbox())
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
        displayRangeStr = f'{self.displayRange[0][0]},{self.displayRange[0][1]},{self.displayRange[1][0]},{self.displayRange[1][1]},'
        displayRangeStr = displayRangeStr + f'{self.currentBattle.getRemainingTime()}/'
        toSend = []
        for p in self.players:
            character = f'{p.getCharacterIndex()},{p.getAnimationNo()},{p.getFrameNo()},{p.getXPosition()},{p.getYPosition()},{p.getFacingDirection()},'
            character = character + f'{p.getHealth()},{p.getMana()},{p.getSwap()};'
            p.lockSpellCards()
            spellcards = ",".join([spellcards.append(spellcard.toString()) for spellcard in p.getSpellCards()])
            p.unlockSpellCards()
            toSend.append(character + spellcards)
        self.multicastConnHost.sendto((displayRangeStr + ("|".join(toSend))).encode(encoding = "utf-8"), ('224.1.1.1', 5007))
        
    def threadMulticastListen(self):
        while True:
            self.multicastString = self.multicastConnListen.recv(1024).decode()
        
    def displayNetwork(self):
        if not self.gameDisplay:
            self.startGameDisplay()
        if self.multicastString == None:
            return
        
        data = self.multicastString
        [meta, playersStr] = data.split('/')
        [x1, y1, x2, y2, time] = meta.split(',')
        self.currentBattle.setTime(float(time))
        self.displayRange = ((float(x1),float(y1)),(float(x2),float(y2)))
        self.displayBackground(self.currentBattle.getBackground())
        
        players = playersStr.split('|')
        for i,player in enumerate(players):
            projectileSplit = player.split(';')
            fields = projectileSplit[0].split(',')
            characterIndex, animationNo, frameNo, x, y, facingLeft, health, mana, swap = \
                int(fields[0]), \
                int(fields[1]), \
                int(fields[2]), \
                float(fields[3]), \
                float(fields[4]), \
                int(fields[5]) == abstractPlayer.LEFT,\
                float(fields[6]),\
                float(fields[7]),\
                float(fields[8])
                
            self.players[i].setHealth(health)
            self.players[i].setMana(mana)
            self.players[i].setSwapVal(swap)
            
            projectiles = projectileSplit[1].split(':')
            if self.players[i].getCharacterIndex() == None:
                self.players[i].setCharacter(characterMenu.characters[characterIndex], characterIndex)
            
            self.players[i].setPosition((x,y)) 
            
            self.players[i].setFacingDirection(abstractPlayer.LEFT if facingLeft else abstractPlayer.RIGHT)
            self.displayCharacter(self.players[i], animationNo, frameNo)
            self.displayNetworkProjectiles(projectiles, self.players[i])
        self.displayBattleOverlay()
            
    def displayNetworkProjectiles(self, projectiles, player):
        for projectile in projectiles:
            if projectile == '':
                continue
            data = projectile.split(',')
            ID, frameNo, x, y = data[0], data[1], data[2], data[3]
            projectile = type(player).projectiles[ID]
            self.displayImage(projectile.frames[frameNo], x, y)
            
    def displayCharacter(self, player, animationNo, frameNo):
        pos = player.getPosition()
        baseX = player.getCharacter().baseX
        baseY = player.getCharacter().baseY
        
        player.setAnimationFrame(animationNo, frameNo)
        image = player.getImage(isNetwork = True)
        
        xOff = player.getCharacter().getXoffset() if not player.isFacingLeft() else -player.getCharacter().getXoffset()
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