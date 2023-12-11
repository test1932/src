from src.physicalBody.abstractPhysicalBody import abstractPhysicalBody

class abstractProjectile(abstractPhysicalBody):
    def __init__(self, p, v) -> None:
        super().__init__(p, v)
        self.effect = None
        self.damage = 0
        self.priority = 0 # 'weight' of projectile