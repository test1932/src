package listeners;

import game.model.Controller;

public class ClientListener extends Thread {
    private Integer port;
    private String ipAddress;
    private Controller cont;

    public ClientListener(Controller cont, String ipAddress, Integer port) {
        this.port = port;
        this.ipAddress = ipAddress;
        this.cont = cont;
    }

    @Override
    public void run() {
        //TODO
    }
}