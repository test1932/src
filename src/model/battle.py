import time
import pygame
from physicalBody.attackProjectile import meleeProjectile
from players.abstractPlayer import abstractPlayer
from players.humanPlayer import humanPlayer

#TODO
# - intros
# - possible pre-battle dialogue
# - "3 2 1 Battle!"
# - "winner on damage and timeout"
# - take two functions - one for p1 win, one for p2 win

# dialogue:
# [(player_focus, c_1, sprite_1, c_2, sprite_2, dialogue)]

class battle:
    INTROS = 0
    PRE_BATTLE_DIALOGUE = 1
    STARTING = 2
    BATTLING = 3
    OUTROS = 4
    POST_BATTLE_DIALOGUE = 5
    DONE = 6
    
    def __init__(self, gameObj, backgroundPath, nextBattle, maxLength = 180, 
            preBattleDialogue = [], postBattleDialogue = []) -> None:
        self.preBattleDialogue = preBattleDialogue
        self.postBattleDialogue = postBattleDialogue
        self.curDialogueIndex = 0
        
        self.__gameObj = gameObj
        self.lastUpdateTime = None # time of last frame
        self.timeIncrement = None # time since last frame
        self.background = pygame.transform.scale(pygame.image.load(backgroundPath), (gameObj.WIDTH, gameObj.HEIGHT))
        self.nextBattle = nextBattle
        
        self.timeRemaining = maxLength
        self.introTimeRemaining = 3
        self.winner = None
        
        self.heldKeys = set()
        self.ignoreKeys = set()
        
        self.state = battle.INTROS
        
    def startIntros(self):
        for player in self.__gameObj.players:
            player.intro()
            
    def startOutros(self, winner):
        for player in self.__gameObj.players:
            player.outro(winner == player)
        
    def getRemainingTime(self):
        return self.timeRemaining
        
    def setLastUpdateTime(self):
        self.lastUpdateTime = time.time()
        
    def getBackground(self):
        return self.background
    
    def getNextBattle(self):
        return self.nextBattle
    
    def handleInput(self, event):
        if self.state == battle.BATTLING:
            self.handleBattleInput(event)
        else:
            if event.type == pygame.KEYDOWN:
                if event.key in self.heldKeys:
                    self.ignoreKeys.add(event.key)
                else:
                    self.heldKeys.add(event.key)
            elif event.type == pygame.KEYUP:
                if event.key in self.heldKeys:
                    self.heldKeys.remove(event.key)
                if event.key in self.ignoreKeys:
                    self.ignoreKeys.remove(event.key)
                
        
    def handleBattleInput(self, event):
        for player in self.__gameObj.players:
            if not type(player) == humanPlayer:
                continue
            
            if event.type == pygame.KEYDOWN and event.key in player.mapping:
                player.heldKeys.append(player.mapping[event.key])
                self.__gameObj.sendKeyPress(player.mapping[event.key], 0)
                
            if event.type == pygame.KEYUP and event.key in player.mapping:
                if player.mapping[event.key] in player.heldKeys:
                    player.heldKeys.remove(player.mapping[event.key])
                if player.mapping[event.key] in player.ignoreKeys:
                    player.ignoreKeys.remove(player.mapping[event.key])
                self.__gameObj.sendKeyPress(player.mapping[event.key], 1)
        
    def updatebattle(self):
        if self.lastUpdateTime == None:
            self.lastUpdateTime = time.time()
            return
        self.timeIncrement = time.time() - self.lastUpdateTime
        self.lastUpdateTime = time.time()
        
        if self.state == battle.INTROS:
            self.countdownTo(battle.PRE_BATTLE_DIALOGUE)
        elif self.state == battle.PRE_BATTLE_DIALOGUE:
            self.handleDialogue(battle.STARTING, self.preBattleDialogue)
        elif self.state == battle.STARTING:
            self.countdownTo(battle.BATTLING)
        elif self.state == battle.BATTLING:
            self.updateFighting()
        elif self.state == battle.OUTROS:
            self.countdownTo(battle.POST_BATTLE_DIALOGUE)
        elif self.state == battle.POST_BATTLE_DIALOGUE:
            self.handleDialogue(battle.DONE, self.postBattleDialogue)
            if self.state == battle.DONE:
                self.endBattle()
        
    def handleDialogue(self, nextState, dialogue):
        if self.curDialogueIndex == len(dialogue):
            self.state = nextState
            self.curDialogueIndex = 0
        
        ofInterestKeys = self.heldKeys.difference(self.ignoreKeys)
        if pygame.K_z in ofInterestKeys:
            self.curDialogueIndex += 1
    
    def endBattle(self):
        pass # TODO
        
    def countdownTo(self, nextState):
        self.introTimeRemaining -= self.timeIncrement
        if self.introTimeRemaining <= 0:
            self.state = nextState
            self.introTimeRemaining = 1
        
    def updateFighting(self):
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
            player.incrementSwapVal(self.timeIncrement)
            if not player.isBlocking():
                player.incrementMana(100 * self.timeIncrement)
            else:
                player.decrementMana(100 * self.timeIncrement)
                
        self.updatePlayerPositions()
        
        if self.checkForWinner():
            self.state = battle.OUTROS
                
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
                    or isinstance(projectile, meleeProjectile) or isinstance(secondProjectile, meleeProjectile):
                continue
            secondProjectile.collideProjectile(projectile)
            projectile.collideProjectile(secondProjectile)
    
    def applyGravity(self):
        for player in self.__gameObj.getPlayers():
            if player.getYPosition() == self.__gameObj.HEIGHT // 2:
                continue
            # if player is about to meet the middle
            
            middleAbove = player.getYPosition() < self.__gameObj.HEIGHT // 2 and\
                player.getYVelocity() > 0 and\
                (player.getYPosition() + self.timeIncrement * \
                player.getYVelocity() + 5 >= self.__gameObj.HEIGHT // 2)
            
            middleBelow = player.getYPosition() > self.__gameObj.HEIGHT // 2 and\
                player.getYVelocity() < 0 and\
                (player.getYPosition() + self.timeIncrement * \
                player.getYVelocity() - 5 <= self.__gameObj.HEIGHT // 2)
            
            if middleAbove or middleBelow:
                player.setYPosition(self.__gameObj.HEIGHT // 2)
                player.setYVelocity(0)
                player.turnedInAir = abstractPlayer.NO_AIR_DASH
                continue
            # if player.isStun():
            #     player.setYVelocity(20 if player.getYPosition() < self.__gameObj.HEIGHT // 2 else -20)
            else:
                increment = player.getGravity()
                if player.getYPosition() > self.__gameObj.HEIGHT // 2:
                    increment = -increment
                player.setYVelocity(player.getYVelocity() + increment)
            
    def updatePlayerPositions(self):
        # print([player.getXVelocity() for player in self.__gameObj.getPlayers()], [player.isStun() for player in self.__gameObj.getPlayers()])
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