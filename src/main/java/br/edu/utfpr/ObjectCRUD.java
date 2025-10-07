package br.edu.utfpr;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ObjectCRUD extends Remote {
    void insert(Carro carro) throws RemoteException;

    DB.CarroComId read(int id) throws RemoteException;

    List<DB.CarroComId> listAll() throws RemoteException;

    boolean delete(int id) throws RemoteException;

    boolean update(int id, String campo, String novoValor) throws RemoteException;
}
