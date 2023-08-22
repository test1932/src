package game.model;

import java.awt.Shape;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import actions.AbstractSpellAction;
import actions.AbstractSpellActionFactory;
import game.Game;
import game.Game.GameState;
import game.model.scenario.AbstractScenario;
import menu.pause.MenuPause;

import bodies.AbstractPhysicalBody;
import bodies.characters.AbstractPlayer;
import bodies.characters.HumanPlayer;
import bodies.characters.AbstractCharacter.Combo;
import bodies.characters.HumanPlayer.Keys;
import menu.settings.config.KeyOption;

public class updater extends Thread {
    /* Time stuff, mutex for synchronising use of recently pressed keys,
        and reference to controller object. */
    private Controller cont;
    private Long lastTime = null;
    private Long timeDiff = Long.valueOf(0);
    private ReentrantLock mutexRec;
    private ReentrantLock mutexHeld;
    private Long keyPressTimout = Long.valueOf(0);
    private Game gameModel;

    // Constants.
    private final Long KEY_PRESS_TIMEOUT = 200l;
    private final int[] UP_DOWN_KEYCODES = {38,40};

    // Menu for pausing.
    public MenuPause pauseMenu;

    /**
     * Constructor method.
     * @param c Controller object for signalling and accessing the model.
     * @param mutex Mutex for synchronising removing elements from recently 
     * pressed list.
     */
    public updater(Controller c, ReentrantLock mutexRec, ReentrantLock mutexHeld, Game gameModel) {
        this.cont = c;
        this.mutexRec = mutexRec;
        this.mutexHeld = mutexHeld;
        this.gameModel = gameModel;
        pauseMenu = new MenuPause(gameModel, null);
    }

    /**
     * Overridden 'run' method used when running the thread.
     */
    @Override
    public void run() {
        lastTime = System.currentTimeMillis();
        while (true) {
            runGameLoop();
        }
    }

    /**
     * Single iteration of game loop.
     */
    private void runGameLoop() {
        if (System.currentTimeMillis() - lastTime < Long.valueOf(100 / 24)) {
            try {
                sleep(1);
            } catch (InterruptedException e) {
                System.err.println("failed to sleep game loop");
            }
            return;
        }
        timeDiff = System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();
        handleGameState();
        gameModel.notifyObservers();
    }

    /**
     * separates menus from game.
     */
    private void handleGameState() {
        switch (cont.game.gameState) {
            case Playing:
                runScenario();
                break;
            case Menu:
                runMenu();
                break;
        }
    }

    /**
     * handles menu input timeouts and inputs.
     */
    private void runMenu() {
        // System.out.println(keyPressTimout);
        if (keyPressTimout <= 0) {
            if (!setConfig()) handleArrows();
            handleSelection();
            if (gameModel.gameState != GameState.Menu) return;
        }
        else {
            keyPressTimout -= timeDiff;
        }
    }

    /**
     * configures key binding.
     */
    private boolean setConfig() {
        mutexHeld.lock();
        if (cont.game.getCurMenu().getSelected() instanceof KeyOption &&
                cont.heldKeys.size() != 0) {
            KeyOption option = (KeyOption)cont.game.getCurMenu().getSelected();
            if (cont.isKeyHeld(10)) {
                option.toggleSelected();
                keyPressTimout = KEY_PRESS_TIMEOUT;
            }
            else if (option.isSelected()) {
                option.setKeyID(cont.heldKeys.getLast());
                mutexHeld.unlock();
                keyPressTimout = KEY_PRESS_TIMEOUT;
                return true;
            }
        }
        mutexHeld.unlock();
        return false;
    }

    /**
     * handles selection of option.
     */
    private void handleSelection() {
        if (cont.isKeyHeld(90)) {
            keyPressTimout = KEY_PRESS_TIMEOUT;
            cont.game.getCurMenu().runSelected();
        }
    }

    /**
     * handles changing the highlighted option.
     */
    private void handleArrows() {
        for (int code : UP_DOWN_KEYCODES) {
            if (cont.isKeyHeld(code)) {
                keyPressTimout = KEY_PRESS_TIMEOUT;
                cont.game.getCurMenu().incrementSelection(code - 39);
                cont.game.notifyObservers();
            }
        }
    }

    /**
     * progresses the state of the battle
     */
    private void runScenario() {
        switch(cont.getScenario().getCurScenarioState()) {
            case BATTLE:
                runBattle();
                break;
            case PRE_BATTLE:
                outOfBattleMotion();
                handlePreDialogue();
                break;
            case POST_BATTLE:
                outOfBattleMotion();
                handlePostDialogue();
                break;
        }
    }

    private void outOfBattleMotion() {
        cont.getBattle().bodiesLock.lock();
        for (AbstractPlayer player : cont.players) {
            player.setVel(new Double[]{0d, player.getVelocity()[1]});
        }
        cont.getBattle().bodiesLock.unlock();
        updateVelocity();
        updatePositions();
    }
    
    
    private void runBattle() {
        progressMotion();
        handlePause();
        progressBattle();
        checkIfBattleOver();
    }

    private void handlePostDialogue() {
        if (keyPressTimout <= 0 && cont.isKeyHeld(90)) {
            // z
            keyPressDialogue();
        }
        else {
            keyPressTimout -= timeDiff;
        }
    }

    private void keyPressDialogue() {
        keyPressTimout = KEY_PRESS_TIMEOUT;
        if (cont.getScenario().nextDialogue()) {
            AbstractScenario nextScenario = cont.getScenario().getNextScenario();
            if (nextScenario == null) {
                gameModel.setCurMenu(gameModel.titleMenu);
                gameModel.gameState = GameState.Menu;
                gameModel.getCont().setScenario(null);
            }
            else {
                cont.setScenario(cont.getScenario().getNextScenario());
            }
        }
    }

    private void handlePreDialogue() {
        if (keyPressTimout <= 0) {
            // z
            if (cont.isKeyHeld(90)) {
                keyPressTimout = KEY_PRESS_TIMEOUT;
                cont.getScenario().nextDialogue();
            }
        }
        else {
            keyPressTimout -= timeDiff;
        }
    }

    private void checkIfBattleOver() {
        cont.getScenario().checkIfBattleOver();
        // if (cont.getScenario().getBattle().isOver()) System.out.println("battle is over");
    }

    private void progressBattle() {
        handleSpellActions();
        incrementMana();
    }

    private void incrementMana() {
        cont.getBattle().bodiesLock.lock();
        for (AbstractPlayer player : cont.players) {
            if (!player.isManaBlocked()) player.incrementCurMana(10);
        }
        cont.getBattle().bodiesLock.unlock();
    }

    /**
     * checks if the user has paused the battle
     */
    private void handlePause() {
        if (cont.isKeyHeld(32)) {
            cont.game.setCurMenu(pauseMenu);
            cont.game.gameState = GameState.Menu;
            cont.game.notifyObservers();
        }
    }

    /**
     * updates the kinetics of the objects in the battle.
     */
    private void progressMotion() {
        handleMovement();
        updateVelocity();
        updatePositions();
        removeOldRecent();
    }

    private void checkFacingDirections() {
        cont.getBattle().bodiesLock.lock();
        AbstractPlayer player1 = cont.players[0];
        AbstractPlayer player2 = cont.players[1];
        player1.setFacingLeft(player1.hitbox.x > player2.hitbox.x);
        player2.setFacingLeft(player2.hitbox.x > player1.hitbox.x);
        cont.getBattle().bodiesLock.unlock();
    }

    private void playerCollision() {
        cont.getBattle().bodiesLock.lock();
        if (!cont.players[0].collides(cont.players[1].hitbox)) {
             cont.getBattle().bodiesLock.unlock();
             return;
        }
        cont.getBattle().bodiesLock.unlock();
    }

    public Boolean playerColliding(boolean isX, Shape s, AbstractPlayer p) {
        cont.getBattle().bodiesLock.lock();
        if (p.getVelocity()[isX ? 0 : 1] == 0) return false;
        boolean retval;
        double dif;
        if (isX) {
            dif = s.getBounds2D().getCenterX() - p.hitbox.getBounds2D().getCenterX();
        }
        else {
            dif = s.getBounds2D().getCenterY() - p.hitbox.getBounds2D().getCenterY();
        }
        retval = (p.collides(s) && p.getVelocity()[isX ? 0 : 1] * dif > 0);
        cont.getBattle().bodiesLock.unlock();
        return retval;
    }

    private void handleSpellActions() {
        cont.getBattle().bodiesLock.lock();
        for (AbstractPlayer player : cont.players) {
            if (!(player instanceof HumanPlayer)) continue;
            if (player.getCharacter().getTimeout() <= 0) {
                interpretKeyPresses((HumanPlayer)player);
                // System.out.println(cont.recentlyRel);
            }
            else {
                player.getCharacter().decrementTimeout(timeDiff);
            }
        }
        cont.getBattle().bodiesLock.unlock();
        checkForCollisions();
    }

    private void checkForCollisions() {
        for (AbstractPlayer player : cont.players) {
            player.spellActionLock.lock();
            for (int i = 0; i < player.spellActions.size(); i++) {
                checkSpellActionForCollisions(player.spellActions.get(i));
            }
            player.spellActionLock.unlock();
        }
    }

    private void checkSpellActionForCollisions(AbstractSpellAction spellAction) {
        spellAction.projectileLock.lock();
        spellAction.collision(cont.otherPlayer(spellAction.getOwner()));
        spellAction.projectileLock.unlock();
    }

    private void interpretKeyPresses(HumanPlayer player) {
        Combo[] playerKeys = interpretPlayerKeyPresses(player);
        AbstractSpellAction spellAction = toSpellAction(playerKeys, player);
        if (spellAction != null && player.getCurMana() >= AbstractPlayer.MAX_MANA / AbstractPlayer.MANA_SEGMENTS) {
            player.getCharacter().resetTimeout(spellAction.getCoolDown());
            player.decrementCurMana((int)((double)AbstractPlayer.MAX_MANA / (double)AbstractPlayer.MANA_SEGMENTS));
            spellAction.start();
        }
    }

    private AbstractSpellAction toSpellAction(Combo[] combo, AbstractPlayer p) {
        for (Pair<Combo[], AbstractSpellActionFactory> comboPair : p.getCharacter().comboMapping) {
            if (Arrays.equals(combo,comboPair.fst)) return comboPair.snd.newSpell();
        }
        return null;
    }

    private Combo[] interpretPlayerKeyPresses(HumanPlayer p) {
        return cont.heldKeys.stream().map(x -> keyToCombo(intToKeys(x, p), 
            p.getFacingDirection())).filter(x -> x != null).toList().toArray(new Combo[0]);
    }

    private Keys pairToKeys(Pair<Integer, Long> keycode, HumanPlayer p) {
        return p.getInputAction(keycode.fst);
    }

    private Keys intToKeys(Integer keycode, HumanPlayer p) {
        return p.getInputAction(keycode);
    }

    private Combo keyToCombo(Keys key, boolean facingLeft) {
        if (key == null) return null;
        switch(key) {
            case Up:
                return Combo.Up;
            case Down:
                return Combo.Down;
            case Left:
                if (facingLeft) return Combo.Forward;
                else return Combo.Back;
            case Right:
                if (facingLeft) return Combo.Back;
                else return Combo.Forward;
            case Weak:
                return Combo.Weak;
            case Strong:
                return Combo.Strong;
            case Melee:
                return Combo.Melee;
            default:
                return null;
        }
    }

    /**
     * updates the velocity of bodies.
     */
    private void updateVelocity() {
        cont.getBattle().bodiesLock.lock();
        for (AbstractPhysicalBody body : cont.getBattle().bodies) {
            Double[] oldV = body.getVelocity();
            Double[] newV = oldV;
            if (body.gravityApplies) newV[1] += 0.003 * timeDiff;
            for (int i = 0; i < 2; i++) {
                newV[i] = cont.getBattle().outOfBounds(i, body.hitbox, newV[i])? 0.0: newV[i];
            }
            body.setVelocity(newV);
        }
        cont.getBattle().bodiesLock.unlock();
        updateSpellActionsProjectilesVelocity();
    }

    public void updateSpellActionsProjectilesVelocity() {
        for (AbstractPlayer player : cont.players) {
            player.spellActionLock.lock();
            for (AbstractSpellAction spellAction : player.spellActions) {
                updateProjectilesVelocity(spellAction);
            }
            player.spellActionLock.unlock();   
        }
    }

    private void updateProjectilesVelocity(AbstractSpellAction spellAction) {
        spellAction.projectileLock.lock();
        for (int i = 0; i < spellAction.getProjectiles().size(); i++) {
            Double[] oldV = spellAction.getProjectiles().get(i).getVelocity();
            Double[] newV = oldV;
            if (spellAction.getProjectiles().get(i).gravityApplies) newV[1] += 0.002 * timeDiff;
            for (int j = 0; j < 2; j++) {
                newV[j] = cont.getBattle().outOfBounds(j, spellAction.getProjectiles().get(i).hitbox, newV[j])? 0.0: newV[j];
            }
            spellAction.getProjectiles().get(i).setVelocity(newV);
        }
        spellAction.projectileLock.unlock();
    }

    /**
     * removes keys from recently pressed keys which have expired.
     */
    private void removeOldRecent() {
        mutexRec.lock();
        cont.recentlyRel = Collections.synchronizedList(
            new LinkedList<Pair<Integer, Long>>(cont.recentlyRel.stream()
            .map(e -> reduceCount(e)).filter(e -> e.snd > Long.valueOf(0)).toList()));
        mutexRec.unlock();
    }

    /**
     * handles user input for moving players.
     */
    private void handleMovement() {
        cont.getBattle().bodiesLock.lock();
        int i = 0;
        
        for (AbstractPlayer player : cont.players) {
            if (!(player instanceof HumanPlayer)) continue;
            Double[] oldV = player.getVelocity();
            HumanPlayer human = (HumanPlayer)player;
            double x = getVelocityMag(human.getKeyCode(Keys.Left), human.getKeyCode(Keys.Right));
            double y = oldV[1];
            if (cont.isKeyHeld(human.getKeyCode(Keys.Up)) && 
                (player.getVelocity()[1] == 0)) {
                y -= 1.3;
            }
            player.setVel(new Double[]{x, y});
            if (playerColliding(true, cont.players[(i++ + 1) % 2].hitbox, player)) {
                player.setVel(new Double[]{0d, y});
            };
        }
        cont.getBattle().bodiesLock.unlock();
    }

    /**
     * returns the velocity of the player based on keyboard inputs.
     * @param keyL keycode for left movement.
     * @param keyR keycode for right movement.
     * @return velocity of player.
     */
    private double getVelocityMag(int keyL, int keyR) {
        if (cont.isKeyHeld(keyL)) return -0.3;
        else if (cont.isKeyHeld(keyR)) return 0.3;
        return 0;
    }

    /**
     * updates the positions of the players based on their velocity and elapsed time.
     */
    private void updatePositions() {
        int pIndex = 0;
        cont.getBattle().bodiesLock.lock();
        for (AbstractPlayer player : cont.players) {
            Integer[] oldpos = player.getPosition();
            Integer[] newPos = new Integer[]{oldpos[0], oldpos[1]};
            for (int i = 0; i < 2; i++) {
                newPos[i] += (int)((double)timeDiff.intValue() * player.getVelocity()[i]);
            }
            cont.getBattle().changePlayerPos(pIndex++, newPos);
        }
        cont.getBattle().bodiesLock.unlock();
        playerCollision();
        updateSpellActionProjectilePositions();
        checkFacingDirections();
    }

    private void updateSpellActionProjectilePositions() {
        for (AbstractPlayer player : cont.players) {
            player.spellActionLock.lock();
            for (AbstractSpellAction spellAction : player.spellActions) {
                updateProjectilePositions(spellAction);
            }
            player.spellActionLock.unlock();   
        }
    }

    private void updateProjectilePositions(AbstractSpellAction spellAction) {
        spellAction.projectileLock.lock();
        for (int i = 0; i < spellAction.getProjectiles().size(); i++) {
            Integer[] oldpos = spellAction.getProjectiles().get(i).getPosition();
            Integer[] newPos = new Integer[]{oldpos[0], oldpos[1]};
            for (int j = 0; j < 2; j++) {
                newPos[j] += (int)((double)timeDiff.intValue() * spellAction.getProjectiles().get(i).getVelocity()[j]);
            }
            spellAction.getProjectiles().get(i).setPosition(newPos);
        }
        spellAction.projectileLock.unlock();
    }

    /**
     * reduces second element of a pair type by the time difference between frames.
     * @param p pair of time and keycode.
     * @return pair with decremented time.
     */
    private Pair<Integer, Long> reduceCount(Pair<Integer, Long> p) {
        p.snd -= timeDiff;
        return p;
    }
}