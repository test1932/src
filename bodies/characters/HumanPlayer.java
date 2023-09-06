package bodies.characters;

import java.util.HashMap;

public class HumanPlayer extends AbstractPlayer {
    public enum Keys {Left, Right, Up, Down, Melee, Weak, Strong, PlayCard, NextCard, Dash}
    public Keys[] keysArr = Keys.values();
    private HashMap<Keys, Integer> keyMapping = new HashMap<Keys, Integer>();
    private HashMap<Integer, Keys> inverseKeyMapping = new HashMap<Integer, Keys>();

    public HumanPlayer(Boolean isLeft) {
        super(isLeft);
        resetMapping();
    }

    private void initP1Mapping() {
        Integer[] codes = {37, 39, 38, 40, 90, 88, 67, 68, 83, 65};
        initMapping(codes);
    }

    private void initP2Mapping() {
        Integer[] codes = {65, 68, 87, 83, -1, -1, -1, -1, -1, -1}; //TODO keybindings
        initMapping(codes);
    }

    private void initMapping(Integer[] keycodes) {
        for (int i = 0; i < keysArr.length; i++) {
            keyMapping.put(keysArr[i], keycodes[i]);
            inverseKeyMapping.put(keycodes[i], keysArr[i]);
        }
    }

    public void resetMapping() {
        if (this.isLeft()) {
            initP1Mapping();
            return;
        }
        initP2Mapping();
    }

    public void updateKey(Keys val, Integer code) {
        keyMapping.put(val, code);
        inverseKeyMapping.put(code, val);
    }

    public Integer getKeyCode(Keys inputAction) {
        return keyMapping.get(inputAction);
    }

    public Keys getInputAction(Integer keycode) {
        return inverseKeyMapping.get(keycode);
    }
}
