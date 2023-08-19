package game.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import actions.AbstractSpellAction;
import game.Game;
import game.Game.GameState;
import menu.pause.MenuPause;

import bodies.AbstractPhysicalBody;
import bodies.characters.AbstractCharacter;
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
    }

    /**
     * separates menus from game.
     */
    private void handleGameState() {
        switch (cont.game.gameState) {
            case Playing:
                runBattle();
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
    private void runBattle() {
        progressMotion();
        handlePause();
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
        handleSpellActions();
    }

    private void handleSpellActions() {
        for (AbstractPlayer player : cont.bat.players) {
            if (!(player instanceof HumanPlayer)) continue;
            if (player.character.getTimeout() <= 0) {
                interpretKeyPresses();
                System.out.println(cont.recentlyRel);
            }
            else {
                player.character.decrementTimeout(timeDiff);
            }
        }
    }

    private void interpretKeyPresses() {
        for (AbstractPlayer player : cont.bat.players) {
            if (!(player instanceof HumanPlayer)) continue;
            Combo[] playerKeys = interpretPlayerKeyPresses((HumanPlayer)player);//?
            AbstractSpellAction spellAction = toSpellAction(playerKeys, player);
            if (spellAction != null) spellAction.start();
        }
    }

    private AbstractSpellAction toSpellAction(Combo[] combo, AbstractPlayer p) {
        for (Pair<Combo[], AbstractSpellAction> comboPair : p.character.comboMapping) {
            if (combo.equals(comboPair.fst)) return comboPair.snd;
        }
        return null;
    }

    private Combo[] interpretPlayerKeyPresses(HumanPlayer p) {
        return cont.recentlyRel.stream().map(x -> keyToCombo(pairToKeys(x, p), 
            p.getFacingDirection())).filter(x -> x != null).toList().toArray(new Combo[0]);
    }

    private Keys pairToKeys(Pair<Integer, Long> keycode, HumanPlayer p) {
        return p.getInputAction(keycode.fst);
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
     * updates the velocity of all physical bodies.
     */
    private void updateVelocity() {
        for (AbstractPhysicalBody body : cont.bat.bodies) {
            Double[] oldV = body.getVelocity();
            Double[] newV = oldV;
            if (body.gravityApplies) newV[1] += 0.002 * timeDiff;
            for (int i = 0; i < 2; i++) {
                newV[i] = cont.bat.outOfBounds(i, body.hitbox, newV[i])? 0.0: newV[i]; // TODO collision with other player
            }
            body.setVelocity(newV);
        }
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
        for (AbstractPlayer player : cont.bat.players) {
            if (!(player instanceof HumanPlayer)) continue;
            Double[] oldV = player.getVelocity();
            double x = getVelocityMag(((HumanPlayer)player).getKeyCode(Keys.Left), 
                                        ((HumanPlayer)player).getKeyCode(Keys.Right));
            double y = oldV[1];
            if (cont.isKeyHeld(((HumanPlayer)player).getKeyCode(Keys.Up)) && 
                (player.getVelocity()[1] == 0)) {
                y -= 1;
            }
            player.setVel(new Double[]{x, y});
        }
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
        for (AbstractPlayer player : cont.bat.players) {
            Integer[] oldpos = player.getPosition();
            Integer[] newPos = new Integer[]{oldpos[0], oldpos[1]};
            for (int i = 0; i < 2; i++) {
                newPos[i] += (int)((double)timeDiff.intValue() * player.getVelocity()[i]);
            }
            cont.bat.changePlayerPos(pIndex++, newPos);
        }
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