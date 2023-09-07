package bodies.characters;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import actions.AbstractSpellAction;
import actions.AbstractSpellcard;
import bodies.AbstractPhysicalBody;
import effects.ExtremeKnockback;
import effects.IEffect;
import effects.Knockback;

public abstract class AbstractPlayer extends AbstractPhysicalBody {
    private static final int LEFT_X = 120;
    private static final int RIGHT_X = 800;
    public static final int MAX_HEALTH = 1000;
    public static final int MAX_MANA = 10000;
    public static final int MAX_CARD_PROGRESS = 10000;
    public static final int MANA_SEGMENTS = 5;

    protected int health = MAX_HEALTH;
    protected AbstractSpellcard[] hand = new AbstractSpellcard[5];
    private int cardCount = 0;

    private Boolean facingLeft;
    private boolean isStunned = false;
    private boolean isInvulnerable = false;
    private List<IEffect> effects = new ArrayList<IEffect>();
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

    public void removeEffect(IEffect e) {
        this.effects.remove(e);
    }

    public void setVel(Double[] velocity) {
        //TODO Something was here
        setVelocity(velocity);
    }

    public Boolean getFacingDirection() {
        return facingLeft;
    }

    //getters and setters
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (this.isInvulnerable) return;
        this.health = health;
    }

    public Rectangle getImage() {
        return image;
    }

    public void setImage(Rectangle image) {
        this.image = image;
    }

    public void applyNewEffect(IEffect effect) {
        if (this.isInvulnerable) return;
        effect.applyEffect(this);
        this.effects.add(effect);
    }

    public void removeKnockback() {
        effects = effects.stream()
            .filter(e -> !(e instanceof Knockback || e instanceof ExtremeKnockback))
            .collect(Collectors.toList());
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
        decrementCardProgress(-increment);
        if (cardCount < (curCardProgress / (MAX_CARD_PROGRESS / 5))) {
            int maxIndex = character.deck.length - 1;
            int rindex = Math.min(maxIndex, (int)(Math.random() * 20));
            hand[(curCardProgress / (MAX_CARD_PROGRESS / 5)) - 1] = character.deck[rindex];
            System.out.println("added card");
            cardCount++;
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
        this.isStunned = false;
        this.isInvulnerable = false;
    }

    public AbstractSpellcard getHeldCard() {
        return hand[0];
    }

    public void nextCard() {
        if (curCardProgress < (MAX_CARD_PROGRESS / 5)) return;
        AbstractSpellcard oldHead = hand[0];
        for (int i = 0; i < hand.length - 1; i++) {
            hand[i] = hand[i + 1];
        }
        hand[(curCardProgress / (MAX_CARD_PROGRESS / 5)) - 1] = oldHead;
    }

    public void playCard() {
        if (hand[0] == null) throw new RuntimeException("no card?");
        hand[0].activateSpellCard();
        decrementCardProgress(AbstractPlayer.MAX_CARD_PROGRESS / 5);
        cardCount--;
    }

    public void enactEffects(Long timeDiff) {
        // System.out.println(effects);
        for (int i = 0; i < effects.size(); i++) {
            if (i >= effects.size()) continue;
            effects.get(i).reduceTime(timeDiff);
        }
    }

    public boolean isStunned() {
        return isStunned;
    }

    public void setStunned(boolean isStunned) {
        this.isStunned = isStunned;
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public void setInvulnerable(boolean isInvulnerable) {
        this.isInvulnerable = isInvulnerable;
    }
}