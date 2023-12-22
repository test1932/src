import time
import pygame

class battle:
    def __init__(self, gameObj, backgroundPath, nextBattle) -> None:
        self.__gameObj = gameObj
        self.lastUpdateTime = None # time of last frame
        self.timeIncrement = None # time since last frame
        self.background = pygame.transform.scale(pygame.image.load(backgroundPath), (gameObj.WIDTH, gameObj.HEIGHT))
        self.nextBattle = nextBattle
        self.winner = None
        
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
        
        self.handlePlayerCollision()
        self.handleWallCollision()
        self.updatePlayerPositions()
        self.updateProjectilePositions()
        self.projectileCollision()
        self.checkPlayerFlip()
        self.applyGravity()
        self.checkForWinner()
        
    def checkPlayerFlip(self):
        players = self.__gameObj.getPlayers()
        if (players[0].getHitbox().x > players[1].getHitbox().x) !=\
                players[0].isFacingLeft():
            if not players[0].isCooldown():
                self.__gameObj.getPlayers()[0].flipFacingDirection()
            if not players[1].isCooldown():
                self.__gameObj.getPlayers()[1].flipFacingDirection()
        
    def handleWallCollision(self):
        for player in self.__gameObj.getPlayers():
            if player.getHitbox().x <= 0:
                player.setXVelocity(max(player.getXVelocity(), 0))
            elif player.getHitbox().x + player.getHitbox().width >= self.__gameObj.WIDTH:
                player.setXVelocity(min(player.getXVelocity(), 0))
        
    def checkForWinner(self):
        for player in self.__gameObj.getPlayers():
            if player.getHealth() <= 0:
                self.winner = player
        
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
            if projectile.collides(player):
                projectile.effect()
            self.collideWithOtherProjectiles(projectile, self.__gameObj.otherPlayer(player))
            
    def collideWithOtherProjectiles(self, projectile, otherPlayer):
        otherPlayer.lockSpellCards()
        for spellAction in otherPlayer.getSpellCards():
            spellAction.lockProjectiles()
            self.projectileCollisionWithSpellcard(spellAction, projectile)
            spellAction.unlockProjectiles()
        otherPlayer.unlockSpellCards()
        
    def projectileCollisionWithSpellcard(self, spellcard, projectile):
        for secondProjectile in spellcard.getProjectiles():
            if not secondProjectile.collides(projectile):
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
                self.updateProjectilesOfSpellcard(spellAction)
                spellAction.unlockProjectiles()
            player.unlockSpellCards()
        
    def updateProjectilePositionsOfSpellcard(self, spellAction):
        for projectile in spellAction.getProjectiles():
            projectile.setXPosition(self.getXPosition() + self.timeIncrement * self.getXVelocity())
            projectile.setYPosition(self.getYPosition() + self.timeIncrement * self.getYVelocity())