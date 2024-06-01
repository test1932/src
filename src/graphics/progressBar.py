import pygame

class progressBar:
    def __init__(self, maxVal, initVal, x, y, width, height, imagePath = None, segmented = False, noSegments = 5) -> None:
        self.currentVal = initVal
        self.maxVal = maxVal
        self.x = x
        self.y = y
        self.width = width
        self.height = height
        self.segmented = segmented
        self.noSegments = noSegments
        self.image = pygame.image.load(imagePath).convert_alpha() if imagePath != None else None
        
    def displaySegmentedNoImage(self, graphic, noFill, remaining):
        pygame.draw.rect(graphic, (255,255,255), (self.x,self.y,self.width, self.height))
        for i in range(noFill):
            x = i * self.width / self.noSegments
            pygame.draw.rect(graphic, (255,0,0), (self.x + x, self.x + 1 ,self.width // self.noSegments, self.height))
        width = remaining / (self.width // self.noSegments)
        x = noFill * self.width / self.noSegments
        pygame.draw.rect(graphic, (255,0,0),(self.x + x, self.y, width, self.height))
    
    def displayNonSegmentedNoImage(self, graphic):
        pygame.draw.rect(graphic, (255,255,255), (self.x,self.y,self.width, self.height))
        pygame.draw.rect(graphic, (255, 0, 0), (2 + self.x ,2 + self.y, (self.currentVal * self.width / self.maxVal) - 4, self.height - 4))
        
    def displaySegmentedImage(self, graphic, noFill, remaining, image):
        scaled = pygame.transform.scale(image, (self.width // self.noSegments, self.height))
        for i in range(noFill):
            x = i * self.width / self.noSegments
            pygame.draw.circle(graphic, (0,255,0), (self.x + ((i + 0.5) * self.width / self.noSegments),\
                self.y + 0.5 * self.height), self.height / 2)
            graphic.blit(scaled, (self.x + x, self.y))
        width = remaining * self.width / self.maxVal # fraction of total
        x = noFill * self.width / self.noSegments
        crop = pygame.Rect(0,0,width,self.height)
        graphic.blit(scaled.subsurface(crop),(self.x + x, self.y))
        
    def displayNonSegmentedImage(self, graphic, image):
        scaled = pygame.transform.scale(image, (self.width, self.height))
        background = pygame.Rect(0,0,self.width, self.height)
        graphic.blit(background, (self.x, self.y))
        crop = pygame.Rect(0, 0, (self.width * self.currentVal / self.maxVal) - 2, self.height)
        graphic.blit(scaled.subsurface(crop), (self.x + 2, self.y + 2))
        
    def getImage(self, screen):
        
        if self.segmented:
            noFill = int(self.currentVal // (self.maxVal // self.noSegments))
            remaining = self.currentVal % (self.maxVal // self.noSegments) # units of resource
        if self.image == None:
            if self.segmented:
                self.displaySegmentedNoImage(screen, noFill, remaining)
            else:
                self.displayNonSegmentedNoImage(screen)
        else:
            if self.segmented:
                self.displaySegmentedImage(screen, noFill, remaining, self.image)
            else:
                self.displayNonSegmentedImage(screen, self.image)
        # return graphic
    
    def increment(self, value):
        assert value >= 0
        self.currentVal = min(self.currentVal + value, self.maxVal)
        
    def decrement(self, value):
        assert value <= 0
        self.currentVal = max(self.currentVal - value, self.maxVal )
        
    def setValue(self, value):
        self.currentVal = value