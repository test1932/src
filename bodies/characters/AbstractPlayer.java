package bodies.characters;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import bodies.AbstractPhysicalBody;
import effects.IEffect;

public abstract class AbstractPlayer extends AbstractPhysicalBody {
    private final int LEFT_X = 120;
    private final int RIGHT_X = 700;
    public final int MAX_HEALTH = 1000;

    protected Double speedMultiplier = 1.0;
    protected int health = MAX_HEALTH;
    private Boolean facingLeft;
    private List<IEffect> effects = new LinkedList<IEffect>();
    private boolean isLeft;

    public AbstractCharacter character;

    public AbstractPlayer (Boolean isLeft, AbstractCharacter character) {
        int x;
        this.isLeft = isLeft;
        x = isLeft? LEFT_X:RIGHT_X;
        setPosition(new Integer[]{x,300});

        this.hitbox = new Rectangle(x, 200, 20, 50);
        this.image = this.hitbox;
        this.character = character;
        this.facingLeft = !isLeft;
        gravityApplies = true;
    }

    public void setVel(Double[] velocity) {
        //Something was here TODO
        setVelocity(velocity);
    }

    public Boolean getFacingDirection() {
        return facingLeft;
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

    public boolean isLeft() {
        return isLeft;
    }

    public void setFacingLeft(Boolean facingLeft) {
        this.facingLeft = facingLeft;
    }
}