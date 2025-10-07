package br.edu.utfpr;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
    private final DB db;

    public RMIServer(DB db) {
        this.db = db;
    }
    public void start() {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi://localhost/db", db);
            System.out.println("Servidor Pronto");
            while (true) {}
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
