package br.edu.utfpr;

import java.rmi.Naming;

public class RMIClient {
    public void start(){
        try{
            ObjectCRUD db = (ObjectCRUD) Naming.lookup("rmi://localhost:1099/eduardogabriela");
            RMIClientHandler handler = new RMIClientHandler(db);
            handler.handle();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
