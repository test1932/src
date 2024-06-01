import time
import pygame
from physicalBody.attackProjectile import meleeProjectile
from players.abstractPlayer import abstractPlayer

class battle:
    def __init__(self, gameObj, backgroundPath, nextBattle, maxLength = 180) -> None:
        self.__gameObj = gameObj
        self.lastUpdateTime = None # time of last frame
        self.timeIncrement = None # time since last frame
        self.background = pygame.transform.scale(pygame.image.load(backgroundPath), (gameObj.WIDTH, gameObj.HEIGHT))
        self.nextBattle = nextBattle
        
        self.timeRemaining = maxLength
        self.winner = None
        
    def getRemainingTime(self):
        return self.timeRemaining
        
    def setLastUpdateTime(self):
        self.lastUpdateTime = time.time()
        
    def getBackground(self):
        return self.background
    
    def getNextBattle(self):
        return self.nextBattle
        
    def updatebattle(self):
        if self.lastUpdateTime == None:
            self.lastUpdateTime = time.time()
            return
        self.timeIncrement = time.time() - self.lastUpdateTime
        self.lastUpdateTime = time.time()
        
        self.timeRemaining -= self.timeIncrement
        
        self.handlePlayerCollision()
        self.handleWallCollision()
        self.updatePlayerPositions()
        self.updateProjectilePositions()
        self.projectileCollision()
        self.checkPlayerFlip()
        self.applyGravity()
        
        for player in self.__gameObj.getPlayers():
            player.applyEffects(self.timeIncrement)
            if not player.isBlocking():
                player.incrementMana(100 * self.timeIncrement)
            else:
                player.decrementMana(100 * self.timeIncrement)
        
        if self.checkForWinner():
            pass # do winner behaviour
                
    def checkPlayerFlip(self):
        players = self.__gameObj.getPlayers()
        [p1,p2] = players
        midPlayer1 = (p1.getMinXhitbox()[0] + (x:=p1.getMaxXhitbox())[0] + x[1]) / 2
        midPlayer2 = (p2.getMinXhitbox()[0] + (x:=p2.getMaxXhitbox())[0] + x[1]) / 2
        
        pdirs = [abstractPlayer.RIGHT_DIR,  abstractPlayer.LEFT_DIR]
        if midPlayer1 > midPlayer2:
            pdirs[0], pdirs[1] = pdirs[1], pdirs[0]
        
        for i in range(len([p1,p2])):
            if players[i].getFacingDirection() != pdirs[i] and not players[i].isStun():
                players[i].flipFacingDirection()
        
    def handleWallCollision(self):
        for player in self.__gameObj.getPlayers():
            if player.getMinXhitbox()[0] <= 50:
                player.setXVelocity(max(player.getXVelocity(), 0))
            elif (y:= player.getMaxXhitbox())[0] + y[1] >= self.__gameObj.WIDTH - 50:
                player.setXVelocity(min(player.getXVelocity(), 0))
        
    def checkForWinner(self):
        players = self.__gameObj.getPlayers()
        if self.timeRemaining <= 0:
            self.winner = max(players, key = lambda x: x.health)
            return True
        for player in players:
            if player.getHealth() <= 0:
                self.winner = self.__gameObj.otherPlayer(player)
                return True
        return False
                
        
    def projectileCollision(self):
        for player in self.__gameObj.getPlayers():
            player.lockSpellCards()
            for spellAction in player.getSpellCards():
                spellAction.lockProjectiles()
                self.collideProjectilesOfSpellcard(spellAction, self.__gameObj.otherPlayer(player))
                spellAction.unlockProjectiles()
            player.unlockSpellCards()
        
    def collideProjectilesOfSpellcard(self, spellaction, player):
        for projectile in spellaction.getProjectiles():
            if player.collides(projectile):
                player.hit(projectile)
                # projectile.applyEffect()
            self.collideWithOtherProjectiles(projectile, player)
            
    def collideWithOtherProjectiles(self, projectile, otherPlayer):
        otherPlayer.lockSpellCards()
        for spellAction in otherPlayer.getSpellCards():
            spellAction.lockProjectiles()
            self.projectileCollisionWithSpellcard(spellAction, projectile)
            spellAction.unlockProjectiles()
        otherPlayer.unlockSpellCards()
        
    def projectileCollisionWithSpellcard(self, spellcard, projectile):
        for secondProjectile in spellcard.getProjectiles():
            if not secondProjectile.collides(projectile) \
                    or type(projectile) == meleeProjectile or type(secondProjectile) == meleeProjectile:
                continue
            secondProjectile.collideProjectile(projectile)
            projectile.collideProjectile(secondProjectile)
    
    def applyGravity(self):
        for player in self.__gameObj.getPlayers():
            if player.getYPosition() == self.__gameObj.HEIGHT // 2:
                continue
            # if player is about to meet the middle
            if (player.getYPosition() < self.__gameObj.HEIGHT // 2 and player.getYPosition() + self.timeIncrement * \
                    (player.getYVelocity() + 0.3) >= self.__gameObj.HEIGHT // 2) or \
                    (player.getYPosition() > self.__gameObj.HEIGHT // 2 and player.getYPosition() + \
                    self.timeIncrement * (player.getYVelocity() - 0.3) <= self.__gameObj.HEIGHT // 2):
                player.setYPosition(self.__gameObj.HEIGHT // 2)
                player.setYVelocity(0)
                continue
            if player.isStun():
                player.setYVelocity(20 if player.getYPosition() < self.__gameObj.HEIGHT // 2 else -20)
            else:
                increment = player.getGravity()
                if player.getYPosition() > self.__gameObj.HEIGHT // 2:
                    increment = -increment
                player.setYVelocity(player.getYVelocity() + increment)
            
    def updatePlayerPositions(self):
        for player in self.__gameObj.getPlayers():
            player.setXPosition(player.getXPosition() + self.timeIncrement * player.getXVelocity())
            player.setYPosition(player.getYPosition() + self.timeIncrement * player.getYVelocity())
            
    def handlePlayerCollision(self):
        players = self.__gameObj.getPlayers()
        if players[0].collides(players[1]):
            dir = -5 if (players[1].getPosition()[0] - players[0].getPosition()[0]) > 0 else 5
            players[0].setXPosition(players[0].getXPosition() + dir)
            players[1].setXPosition(players[1].getXPosition() - dir)
    
    def updateProjectilePositions(self):
        for player in self.__gameObj.getPlayers():
            player.lockSpellCards()
            for spellAction in player.getSpellCards():
                spellAction.lockProjectiles()
                self.updateProjectilePositionsOfSpellcard(spellAction)
                spellAction.unlockProjectiles()
            player.unlockSpellCards()
        
    def updateProjectilePositionsOfSpellcard(self, spellAction):
        for projectile in spellAction.getProjectiles():
            projectile.setXPosition(projectile.getXPosition() + self.timeIncrement * projectile.getXVelocity())
            projectile.setYPosition(projectile.getYPosition() + self.timeIncrement * projectile.getYVelocity())
            
    def setTime(self, time):
        self.timeRemaining = time