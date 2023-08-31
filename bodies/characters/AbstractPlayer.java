package bodies.characters;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import actions.AbstractSpellAction;
import actions.AbstractSpellcard;
import bodies.AbstractPhysicalBody;
import effects.IEffect;

public abstract class AbstractPlayer extends AbstractPhysicalBody {
    private static final int LEFT_X = 120;
    private static final int RIGHT_X = 800;
    public static final int MAX_HEALTH = 1000;
    public static final int MAX_MANA = 10000;
    public static final int MAX_CARD_PROGRESS = 10000;
    public static final int MANA_SEGMENTS = 5;

    protected Double speedMultiplier = 1.0;
    protected int health = MAX_HEALTH;
    protected AbstractSpellcard[] hand = new AbstractSpellcard[5];

    private Boolean facingLeft;
    private List<IEffect> effects = new LinkedList<IEffect>();
    public ArrayList<AbstractSpellAction> spellActions = new ArrayList<AbstractSpellAction>();

    private boolean isLeft;
    private int curMana = MAX_MANA;
    private int curCardProgress = 0;
    private boolean isManaBlocked = false;

    private ReentrantLock manaLock = new ReentrantLock();
    public ReentrantLock spellActionLock = new ReentrantLock();

    protected AbstractCharacter character;

    public AbstractPlayer (Boolean isLeft) {
        int x;
        this.isLeft = isLeft;
        x = isLeft? LEFT_X:RIGHT_X;
        setPosition(new Integer[]{x,0});

        this.hitbox = new Rectangle(x, 0, 30, 80);
        this.image = this.hitbox;
        this.facingLeft = !isLeft;
        gravityApplies = true;
    }

    public void resetPosition() {
        int x;
        x = isLeft? LEFT_X:RIGHT_X;
        setPosition(new Integer[]{x,0});
    }

    public int getCurCardProgress() {
        return curCardProgress;
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

    public int getCurMana() {
        return curMana;
    }

    public void setCurMana(int curMana) {
        manaLock.lock();
        this.curMana = curMana;
        manaLock.unlock();
    }

    public void decrementCurMana(int decrement) {
        manaLock.lock();
        this.curMana = Math.max(Math.min(curMana - decrement, MAX_MANA), 0);
        manaLock.unlock();
    }

    public void incrementCurMana(int increment) {
        decrementCurMana(-increment);
    }

    public void decrementCardProgress(int decrement) {
        manaLock.lock();
        this.curCardProgress = Math.max(Math.min(curCardProgress - decrement, MAX_CARD_PROGRESS), 0);
        manaLock.unlock();
    }

    public void incrementCardProgress(int increment) {
        int temp = curCardProgress;
        decrementCardProgress(-increment);
        if (!((temp / (MAX_CARD_PROGRESS / 5)) - (curCardProgress / (MAX_CARD_PROGRESS / 5)) == 0)) {
            int maxIndex = character.deck.length - 1;
            int rindex = Math.min(maxIndex, (int)(Math.random() * 20));
            hand[(curCardProgress / (MAX_CARD_PROGRESS / 5)) - 1] = character.deck[rindex];
        }
    }

    public boolean isManaBlocked() {
        return isManaBlocked;
    }

    public void setManaBlocked(boolean isManaBlocked) {
        manaLock.lock();
        this.isManaBlocked = isManaBlocked;
        manaLock.unlock();
    }

    public AbstractSpellcard[] getHand() {
        return hand;
    }

    public void setHand(AbstractSpellcard[] hand) {
        this.hand = hand;
    }

    public AbstractCharacter getCharacter() {
        return character;
    }

    public void setCharacter(AbstractCharacter character) {
        this.character = character;
    }

    public void reset() {
        this.health = MAX_HEALTH;
        this.curMana = MAX_MANA;
        this.curCardProgress = 0;
    }

    public AbstractSpellcard getHeldCard() {
        return hand[0];
    }

    public void nextCard() {
        if (curCardProgress < (MAX_CARD_PROGRESS / 5)) return;
        System.out.println("next card");
        AbstractSpellcard oldHead = hand[0];
        for (int i = 0; i < hand.length - 1; i++) {
            hand[i] = hand[i + 1];
        }
        System.out.println(curCardProgress);
        hand[(curCardProgress / (MAX_CARD_PROGRESS / 5)) - 1] = oldHead;
    }

    public void playCard() {
        System.out.println("playing card");
        if (hand[0] == null) return;
        hand[0].activateSpellCard();
    }
}