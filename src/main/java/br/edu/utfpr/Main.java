package br.edu.utfpr;

import java.rmi.RemoteException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws RemoteException{
        DB db = new DB();
        RMIServer server = new RMIServer(db);
        RMIClient client = new RMIClient();
        Scanner sc = new Scanner(System.in);
        System.out.println("Selecione tipo de inicializacao: S - Servidor, C - Cliente");
        String tipo = sc.nextLine().toUpperCase().trim();
        switch (tipo){
            case "C" -> client.start();
            case "S"  -> server.start();
            default -> System.out.println("opcao invalida");
        }
    }
}