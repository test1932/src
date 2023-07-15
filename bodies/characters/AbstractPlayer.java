package bodies.characters;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import bodies.AbstractPhysicalBody;
import effects.IEffect;

public abstract class AbstractPlayer extends AbstractPhysicalBody {
    private Double speedMultiplier = 1.0;
    private int health;
    private Rectangle image;
    private Boolean facingRight;
    private List<IEffect> effects = new LinkedList<IEffect>();

    private final int LEFT_X = 120;
    private final int RIGHT_X = 700;

    public AbstractPlayer (Boolean isLeft) {
        int x;
        x = isLeft? LEFT_X:RIGHT_X;
        setPosition(new Integer[]{x,500});
        this.image = new Rectangle(x, 200, 20, 50);
        this.hitbox = this.image;
    }

    public void setPos(Integer[] newPos) {
        setPosition(newPos);
        image.x = newPos[0];
        image.y = newPos[1];
    }

    public void setVel(Double[] velocity) {
        facingRight = velocity[0] > 0;
        setVelocity(velocity);
    }

    public Boolean getFacingDirection() {
        return facingRight;
    }

    //getters and setters
    public Double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(Double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Rectangle getImage() {
        return image;
    }

    public void setImage(Rectangle image) {
        this.image = image;
    }

    public void applyNewEffect(IEffect effect) {
        effect.applyEffect(this);
        this.effects.add(effect);
    }

    public void countDownEffects(Long timeDiff) {
        effects = effects.stream()
            .map(e -> reduceCount(e, timeDiff))
            .filter(e -> !e.effectIsOver())
            .toList();
    }

    private IEffect reduceCount(IEffect e, Long timeDiff) {
        e.reduceTime(timeDiff);
        return e;
    }
}