To run (cmd)
    
    set PATH_TO_FX="path\to\javafx-sdk-20\lib"

    javac --module-path %PATH_TO_FX% --add-modules javafx.controls game/Game.java

    java --module-path %PATH_TO_FX% --add-modules javafx.controls game/Game
