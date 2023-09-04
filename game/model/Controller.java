// package
package game.model;

// standard library imports
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import bodies.characters.AbstractPlayer;
import bodies.characters.HumanPlayer;
// local imports
import game.Game;
import game.model.scenario.AbstractScenario;
import game.model.scenario.Battle;

/**
 * @author: ~Yukarin~ <3
 * Class used for running game loop and processing changes.
 */
public class Controller {

    // constants
    private final long RECENT_TIMEOUT = 500l;

    // mutexes and references
    public ReentrantLock recMutex = new ReentrantLock();
    public ReentrantLock holdMutex = new ReentrantLock();
    public ReentrantLock ignoreKeysMutex = new ReentrantLock();
    private updater gameLoop;
    private AbstractScenario scenario;

    public AbstractPlayer[] players 
        = {new HumanPlayer(true), 
           new HumanPlayer(false)};

    public Game game;

    // key press data structures
    public LinkedList<Integer> heldKeys = new LinkedList<Integer>();
    public List<Pair<Integer, Long>> recentlyRel
        = Collections.synchronizedList(new LinkedList<Pair<Integer, Long>>());
    public Set<Integer> keysToIgnore
        = new HashSet<Integer>();

    /**
     * class representing link between game loop updating the model
     * and the key listener for getting input.
     * Constructor method.
     */
    public Controller () {
        //Pass
    }

    public void activateController(Game game) {
        this.game = game;
        gameLoop = new updater(this, recMutex, holdMutex, game);
        gameLoop.start();
    }

    /**
     * returns whether a particular keycode is currently being held down on
     * the keyboard by the user.
     * @param keyid code of the key.
     * @return whether the key is currently being held.
     */
    public Boolean isKeyHeld(Integer keyid) {
        return this.heldKeys.contains(keyid);
    }

    public AbstractPlayer otherPlayer(AbstractPlayer player) {
        return players[0] == player ? players[1] : players[0];
    }

    /**
     * adds a key to the list of keys being held.
     * @param keyid key code to add to the list.
     */
    public void holdKey(Integer keyid) {
        if (!isKeyHeld(keyid)) {
            holdMutex.lock();
            this.heldKeys.add(keyid);
            holdMutex.unlock();
        }
    }

    public void unholdKey(Integer keyid) {
        holdMutex.lock();
        this.heldKeys.remove(keyid);
        holdMutex.unlock();
    }

    public void ignoreKey(Integer keyid) {
        ignoreKeysMutex.lock();
        this.keysToIgnore.add(keyid);
        ignoreKeysMutex.unlock();
    }

    public void stopIgnoringKey(Integer keyid) {
        ignoreKeysMutex.lock();
        keysToIgnore.remove(keyid);
        ignoreKeysMutex.unlock();
    }

    public boolean isIgnored(Integer keyid) {
        return keysToIgnore.contains(keyid);
    }

    /**
     * remove keycode from the list of hed keys and add it to the list of
     * recently held keys.
     * @param keyid keycode to remove from the list.
     */
    public void releaseKey(Integer keyid) {
        holdMutex.lock();
        this.heldKeys.remove(keyid);
        holdMutex.unlock();
        recMutex.lock();
        this.recentlyRel.add(new Pair<Integer,Long>(keyid, RECENT_TIMEOUT));

        ignoreKeysMutex.lock();
        this.keysToIgnore.remove(keyid);
        ignoreKeysMutex.unlock();

        recMutex.unlock();
    }

    public AbstractScenario getScenario() {
        return scenario;
    }

    public void setScenario(AbstractScenario scenario) {
        this.scenario = scenario;
    }

    public Battle getBattle() {
        return this.scenario.getBattle();
    }
}
