package lab6;

import java.util.ArrayList;

public class StoreServersMessage {
    private ArrayList<String> servers;

    public StoreServersMessage(ArrayList<String> servers) {
        this.servers = servers;
    }

    public ArrayList<String> getServers() {
        return servers;
    }
}

