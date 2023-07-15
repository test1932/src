package game;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import bodies.AbstractPhysicalBody;

public class Controller {
    private class updater extends Thread {
        private Controller cont;
        private Long lastTime = null;
        private Long timeDiff = Long.valueOf(0);
        private ReentrantLock mutex;

        public updater(Controller c, ReentrantLock mutex) {
            this.cont = c;
            this.mutex = mutex;
        }

        public void run() {
            lastTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - lastTime < Long.valueOf(100 / 12)) {
                    try {
                        sleep(2);
                    } catch (InterruptedException e) {
                        System.err.println("failed to sleep game loop");
                    }
                    continue;
                }
                timeDiff = System.currentTimeMillis() - lastTime;
                lastTime = System.currentTimeMillis();
                handleGameState();
            }
        }

        private void handleGameState() {
            switch (cont.game.gameState) {
                case Playing:
                    runBattle();
                    break;
                case Menu:
                    break;
            }
        }

        private void runBattle() {
            progressMotion();
        }

        private void progressMotion() {
            handleMovement();
            updateVelocity();
            updatePositions();
            removeOldRecent();
        }

        private void updateVelocity() {
            for (AbstractPhysicalBody body : cont.bat.bodies) {
                Double[] oldV = body.getVelocity();
                Double[] newV = oldV;
                newV[1] += 0.002 * timeDiff;
                for (int i = 0; i < 2; i++) {
                    newV[i] = cont.bat.outOfBounds(i, body.hitbox, newV[i])? 0.0: newV[i];
                }
                body.setVelocity(newV);
            }
        }

        private void removeOldRecent() {
            mutex.lock();
            cont.recentlyRel = Collections.synchronizedList(
                new LinkedList<Pair<Integer, Long>>(cont.recentlyRel.stream()
                .map(e -> reduceCount(e)).filter(e -> e.snd > Long.valueOf(0)).toList()));
            mutex.unlock();
        }

        private void handleMovement() {
            Double[] oldV = cont.bat.players[0].getVelocity();
            double x = getVelocityMag(37, 39);
            double y = oldV[1];
            if (cont.isKeyHeld(38) && (cont.bat.players[0].getVelocity()[1] == 0)) {
                y -= 1;
            }
            cont.bat.players[0].setVel(new Double[]{x, y});
        }

        private double getVelocityMag(int keyL, int keyR) {
            if (cont.isKeyHeld(keyL)) return -0.3;
            else if (cont.isKeyHeld(keyR)) return 0.3;
            return 0;
        }

        private void updatePositions() {
            Integer[] oldpos = cont.bat.players[0].getPosition();
            Integer[] newPos = new Integer[]{oldpos[0], oldpos[1]};
            for (int i = 0; i < 2; i++) {
                newPos[i] += (int)((double)timeDiff.intValue()
                    * cont.bat.players[0].getVelocity()[i]);
            }
            cont.bat.changePlayerPos(0, newPos);
        }

        private Pair<Integer, Long> reduceCount(Pair<Integer, Long> p) {
            p.snd -= timeDiff;
            return p;
        }
    }

    public class Pair <U,V> {
        public U fst;
        public V snd;

        public Pair (U val1, V val2) {
            fst = val1;
            snd = val2;
        }

        @Override
        public String toString() {
            return String.valueOf(snd);
        }
    }

    private final Long RECENT_TIMEOUT = Long.valueOf(1000);

    private ReentrantLock recMutex = new ReentrantLock();
    private ReentrantLock holdMutex = new ReentrantLock();
    private updater gameLoop;
    private Battle bat;
    public Game game;

    private LinkedList<Integer> heldKeys = new LinkedList<Integer>();
    private List<Pair<Integer, Long>> recentlyRel
        = Collections.synchronizedList(new LinkedList<Pair<Integer, Long>>());

    public Controller (Battle b, Game g) {
        this.bat = b;
        this.game = g;
        gameLoop = new updater(this, recMutex);
        gameLoop.start();
    }

    public Boolean isKeyHeld(Integer keyid) {
        return this.heldKeys.contains(keyid);
    }

    public void holdKey(Integer keyid) {
        if (!isKeyHeld(keyid)) {
            holdMutex.lock();
            this.heldKeys.add(keyid);
            holdMutex.unlock();
        }
    }

    public void releaseKey(Integer keyid) {
        holdMutex.lock();
        this.heldKeys.remove(keyid);
        holdMutex.unlock();
        recMutex.lock();
        this.recentlyRel.add(new Pair<Integer,Long>(keyid, RECENT_TIMEOUT));
        recMutex.unlock();
    }
}
