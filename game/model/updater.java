package game.model;

import java.awt.Shape;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import actions.AbstractSpellAction;
import actions.ISpellActionFactory;
import game.Game;
import game.Game.GameState;
import game.model.scenario.AbstractScenario;
import game.model.scenario.AbstractScenario.ScenarioState;
import menu.pause.MenuPause;

import bodies.characters.AbstractPlayer;
import bodies.characters.HumanPlayer;
import bodies.characters.AbstractCharacter;
import bodies.characters.AbstractCharacter.Combo;
import bodies.characters.HumanPlayer.Keys;
import bodies.projectiles.AbstractProjectile;
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

    private final boolean DEBUG = false;

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
                if (DEBUG) System.out.println(cont.heldKeys);
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
            case SETUP:
                handleScenarioSetup();
                break;
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

    /**
     * resets player positions and starts the scenario.
     */
    private void handleScenarioSetup() {
        for (AbstractPlayer player : cont.players) {
            player.resetPosition();
            player.reset();
        }
        cont.getScenario().setup();
    }

    /**
     * applied gravity to players outside of battle.
     */
    private void outOfBattleMotion() {
        cont.getBattle().bodiesLock.lock();
        for (AbstractPlayer player : cont.players) {
            player.setVel(new Double[]{0d, player.getVelocity()[1]});
        }
        cont.getBattle().bodiesLock.unlock();
        updateVelocity();
        updatePositions();
    }
    
    /**
     * methods run as part of the main game loop for battles.
     */
    private void runBattle() {
        try {
            progressMotion();
            handlePause();
            progressBattle();
            checkIfBattleOver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * handles user progressing post battle dialogue.
     */
    private void handlePostDialogue() {
        if (keyPressTimout <= 0 && cont.isKeyHeld(90)) {
            // z
            keyPressDialogue();
        }
        else {
            keyPressTimout -= timeDiff;
        }
    }

    /**
     * progresses to next post battle dialogue and moves to next scenario if complete.
     */
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

    /**
     * progresses to next pre battle dialogue if user provides input.
     */
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

    /**
     * makes call to current scenario to update whether the battle is over.
     */
    private void checkIfBattleOver() {
        cont.getScenario().checkIfBattleOver();
    }

    /**
     * increments mana, handles spellcard input, and handles user action input.
     */
    private void progressBattle() {
        handleSpellCards();
        handlePlayerActions();
        incrementMana();
    }

    /**
     * checks if player can perform spellcard actions and runs those actions.
     */
    private void handleSpellCards() {
        cont.getBattle().bodiesLock.lock();
        for (AbstractPlayer player : cont.players) {
            if (!(player instanceof HumanPlayer)) continue;
            if (player.getCharacter().getSpellTimeout() <= 0) {
                HumanPlayer human = (HumanPlayer) player;
                handleHumanSpellCards(human);
            }
            else {
                player.getCharacter().decrementSpellTimeout(timeDiff);
            }
        }
        cont.getBattle().bodiesLock.unlock();
    }

    /**
     * changes/plays current spell card for HumanPlayer as appropriate.
     * @param human HumanPlayer to change/play spellcards for.
     */
    private void handleHumanSpellCards(HumanPlayer human) {
        if(cont.heldKeys.contains(human.getKeyCode(Keys.NextCard))) {
            human.nextCard();
            human.getCharacter().resetSpellTimeout(200l);
        }
        else if (cont.heldKeys.contains(human.getKeyCode(Keys.PlayCard)) 
                && human.getCurCardProgress() >= AbstractPlayer.MAX_CARD_PROGRESS / 5) {
            human.getCharacter().resetSpellTimeout(human.getHeldCard().timeout);
            human.playCard();
            keyPressTimout = KEY_PRESS_TIMEOUT;
        }
    }

    /**
     * increments mana for each player.
     */
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

    /**
     * runs the effects of status effects for each player.
     */
    private void applyStatusEffects() {
        cont.getBattle().bodiesLock.lock();
        for (AbstractPlayer player : cont.players) {
            player.enactEffects(timeDiff);
        }
        cont.getBattle().bodiesLock.unlock();
    }

    /**
     * updates facing directions of players based on the other player's position.
     */
    private void checkFacingDirections() {
        cont.getBattle().bodiesLock.lock();
        AbstractPlayer player1 = cont.players[0];
        AbstractPlayer player2 = cont.players[1];
        player1.setFacingLeft(player1.hitbox.x > player2.hitbox.x);
        player2.setFacingLeft(player2.hitbox.x > player1.hitbox.x);
        cont.getBattle().bodiesLock.unlock();
    }

    /**
     * checks for collision between a Shape and AbstractPlayer.
     * @param isX whether collision is in x.
     * @param s shape AbstractPlayer is checking collision with.
     * @param p AbstractPlayer to check collision.
     * @return whether collision is occuring.
     */
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

    /**
     * performs actions based on user input for each character.
     */
    private void handlePlayerActions() {
        cont.getBattle().bodiesLock.lock();
        for (AbstractPlayer player : cont.players) {
            if (!(player instanceof HumanPlayer)) continue;
            if (player.getCharacter().getTimeout() <= 0 && !player.isStunned()) {
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

    /**
     * checks for collisions between spell action projectiles and players.
     */
    private void checkForCollisions() {
        for (AbstractPlayer player : cont.players) {
            player.spellActionLock.lock();
            for (int i = 0; i < player.spellActions.size(); i++) {
                checkSpellActionForCollisions(player.spellActions.get(i));
            }
            player.spellActionLock.unlock();
        }
    }

    /**
     * makes call to handle collision between spellaction and player.
     */
    private void checkSpellActionForCollisions(AbstractSpellAction spellAction) {
        spellAction.projectileLock.lock();
        spellAction.collision(cont.otherPlayer(spellAction.getOwner()));
        spellAction.projectileLock.unlock();
    }

    /**
     * starts spell action threads for a given player based on user input.
     * @param player player to handle starting spellactions for.
     */
    private void interpretKeyPresses(HumanPlayer player) {
        Combo[] playerKeys = interpretPlayerKeyPresses(player);
        AbstractSpellAction spellAction = toSpellAction(playerKeys, player);
        if (spellAction != null && player.getCurMana() >= AbstractPlayer.MAX_MANA / AbstractPlayer.MANA_SEGMENTS) {
            player.getCharacter().resetTimeout(spellAction.getCoolDown());
            if (spellAction.isCostsMana()) {
                player.decrementCurMana((int)((double)AbstractPlayer.MAX_MANA / (double)AbstractPlayer.MANA_SEGMENTS));
            }
            player.incrementCardProgress(100);
            spellAction.start();
        }
    }

    /**
     * returns spell action corresponding to combo of keypresses, null if not recognised.
     * @param combo sequence of keypresses.
     * @param p player to check for spell action.
     * @return spell action corresponding to combo (if applicable).
     */
    private AbstractSpellAction toSpellAction(Combo[] combo, HumanPlayer p) {
        for (Pair<Combo[], ISpellActionFactory> comboPair : p.getCharacter().comboMapping) {
            if (Arrays.equals(combo,comboPair.fst)) {
                cont.ignoreKey(p.getKeyCode(AbstractCharacter.keyOfCombo(combo[combo.length - 1], p.getFacingDirection())));
                return comboPair.snd.newSpell();
            }
        }
        return null;
    }

    /**
     * 
     * @param p
     * @return
     */
    private Combo[] interpretPlayerKeyPresses(HumanPlayer p) {
        cont.holdMutex.lock();
        Combo[] res =  cont.heldKeys.stream()
            .filter(x -> !cont.isIgnored(x))
            .map(x -> AbstractCharacter.keyToCombo(intToKeys(x, p), p.getFacingDirection()))
            .filter(x -> x != null).toList()
            .toArray(new Combo[0]);
        cont.holdMutex.unlock();
        return res;
    }

    // private Keys pairToKeys(Pair<Integer, Long> keycode, HumanPlayer p) {
    //     return p.getInputAction(keycode.fst);
    // }

    private Keys intToKeys(Integer keycode, HumanPlayer p) {
        return p.getInputAction(keycode);
    }

    /**
     * updates the velocity of bodies.
     */
    private void updateVelocity() {
        cont.getBattle().bodiesLock.lock();
        for (AbstractPlayer player : cont.players) {
            Double[] oldV = player.getVelocity();
            Double[] newV = oldV;
            int reversed = player.isGravityReversed() ? -1 : 1;
            if (player.gravityApplies) newV[1] += 0.003 * reversed * timeDiff; // gravity

            applyStatusEffects(); // knockback, slow down, etc...
            player.setVelocity(newV);
            cont.getBattle().playerOutOfBounds(player);
        }
        cont.getBattle().bodiesLock.unlock();
        updateSpellActionsProjectilesVelocity();
        updateFriction();
    }

    private void updateFriction() {
        updatePlayerFriction();
    }

    private void updatePlayerFriction() {
        cont.getBattle().bodiesLock.lock();
        for (AbstractPlayer p : cont.players) {
            if(p.frictionApplies && cont.getBattle().collidesYBounds(p.hitbox)) p.applyFriction();
            updateProjectileFriction(p);
        }
        cont.getBattle().bodiesLock.unlock();
    }

    private void updateProjectileFriction(AbstractPlayer p) {
        for (int i = 0; i < p.spellActions.size(); i++) {
            ArrayList<AbstractProjectile> projectiles = p.spellActions.get(i).getProjectiles();
            updateSpellProjectileFriction(p, projectiles);
        }
    }

    private void updateSpellProjectileFriction(AbstractPlayer p, ArrayList<AbstractProjectile> projectiles) {
        for (int j = 0; j < projectiles.size(); j++) {
            if (projectiles.get(j).frictionApplies 
                    && cont.getBattle().collidesYBounds(projectiles.get(j).hitbox)) {
                projectiles.get(j).applyFriction();
            }
        }
    }

    /**
     * progresses spell actions.
     */
    public void updateSpellActionsProjectilesVelocity() {
        for (AbstractPlayer player : cont.players) {
            player.spellActionLock.lock();
            for (AbstractSpellAction spellAction : player.spellActions) {
                updateProjectilesVelocity(spellAction);
            }
            player.spellActionLock.unlock();   
        }
    }

    /**
     * progresses spell action.
     * @param spellAction spell action to progress.
     */
    private void updateProjectilesVelocity(AbstractSpellAction spellAction) {
        spellAction.projectileLock.lock();
        for (int i = 0; i < spellAction.getProjectiles().size(); i++) {
            Double[] oldV = spellAction.getProjectiles().get(i).getVelocity();
            Double[] newV = oldV;
            if (spellAction.getProjectiles().get(i).gravityApplies) newV[1] += 0.0015 * timeDiff;
            cont.getBattle().projectileWallCollision(spellAction.getProjectiles().get(i));
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
            double x = !player.isStunned() ? getVelocityMag(human.getKeyCode(Keys.Left), human.getKeyCode(Keys.Right)) : oldV[0];
            double y = oldV[1];
            y = handleJump(human, y);
            player.setVel(new Double[]{x, y});
            if (playerColliding(true, cont.players[(i++ + 1) % 2].hitbox, player)) {
                player.setVel(new Double[]{0d, y});
            };
        }
        cont.getBattle().bodiesLock.unlock();
    }

    /**
     * handles jumping input for a HumanPlayer.
     * @param human HumanPlayer to handle jumping for.
     * @param y old y velocity value.
     * @return new y velocity value.
     */
    private double handleJump(HumanPlayer human, double y) {
        int key = human.isGravityReversed() ? human.getKeyCode(Keys.Down) : human.getKeyCode(Keys.Up);
        int change = human.isGravityReversed() ? 1 : -1;

        if (cont.isKeyHeld(key) 
                && (human.getVelocity()[1] == 0) 
                && (cont.getBattle().isOnGround(human.hitbox, human.isGravityReversed()))
                && !human.isStunned()) {
            return y + (1.3 * change);
        }
        return y;
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
        updateSpellActionProjectilePositions();
        checkFacingDirections();
    }

    /**
     * updates projectile positions for all spell actions.
     */
    private void updateSpellActionProjectilePositions() {
        for (AbstractPlayer player : cont.players) {
            player.spellActionLock.lock();
            for (AbstractSpellAction spellAction : player.spellActions) {
                updateProjectilePositions(spellAction);
            }
            player.spellActionLock.unlock();   
        }
    }

    /**
     * updates projectile positions for a specific spell action.
     * @param spellAction spell action to update projectile positions of.
     */
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