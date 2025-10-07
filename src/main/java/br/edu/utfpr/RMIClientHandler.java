package br.edu.utfpr;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RMIClientHandler {
    private final ObjectCRUD db;
    public Scanner sc = new Scanner(System.in);

    public RMIClientHandler(ObjectCRUD db) {
        this.db = db;
    }

    public void handle() {
        String opcao = "";
        while (!"6".equals(opcao)) {
            System.out.println("\nEscolha uma opção:");
            System.out.println("[1] Inserir novo veículo");
            System.out.println("[2] Buscar veículo por ID");
            System.out.println("[3] Listar Todos os Veículos");
            System.out.println("[4] Remover veículo");
            System.out.println("[5] Atualizar informações de um veículo");
            System.out.println("[6] Sair");
            System.out.print("Digite o número da opção desejada: ");
            opcao = sc.nextLine();

            switch (opcao) {
                case "1" -> handleInsert();
                case "2" -> handleRead();
                case "3" -> handleListAll();
                case "4" -> handleRemove();
                case "5" -> handleUpdate();
                case "6" -> System.out.println("Encerrando...");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void handleInsert(){
        try{
            System.out.println("Insira as informacoes do veiculo:");
            System.out.print("Marca: ");
            String marca = sc.nextLine();
            System.out.print("Modelo: ");
            String modelo = sc.nextLine();
            int ano = readInt("Ano: ");
            System.out.print("Cambio: ");
            String cambio = sc.nextLine();
            System.out.print("Tipo de carroceria: ");
            String tipo = sc.nextLine();

            Carro carro = new Carro(marca, modelo, ano, cambio, tipo);
            db.insert(carro);
        } catch (RemoteException e){
            System.err.println(e.getMessage());
        }
    }

    private void handleRead(){
        try{
            int id = readInt("Digite o id do veículo: ");
            DB.CarroComId carroComId = db.read(id);
            if (carroComId == null) {
                System.out.printf("Carro com id %d nao encontrado", id);
                return;
            }
            Carro c = carroComId.carro();
            System.out.printf("ID: %d | Marca: %s | Modelo: %s | Ano: %d | Cambio: %s | Tipo: %s%n",
                    carroComId.id(), c.marca(), c.modelo(),
                    c.ano(), c.cambio(), c.tipo());
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
    }

    private void handleListAll(){
            listAllCars();
    }

    private void handleRemove(){
        listAllCars();
        try{
            int id = readInt("Digite o id do veiculo que deseja remover: ");
            if(db.delete(id)){
                System.out.printf("Carro com id %d removido", id);
            } else {
                System.out.printf("Carro com id %d nao encontrado", id);
            }
        } catch (RemoteException e){
            System.err.println(e.getMessage());
        }
    }

    private void handleUpdate(){
        listAllCars();
        try{
            int id = readInt("Digite o id do veiculo que deseja atualizar: ");
            while (true) {
                System.out.println("\nQual atributo você deseja alterar?");
                System.out.println("[1] Marca | [2] Modelo | [3] Ano | [4] Câmbio | [5] Tipo | [0] Voltar");
                int campo = readInt("Digite o número da sua opção: ");

                if (campo == 0) break;
                if (campo < 1 || campo > 5) {
                    System.out.println("Opção inválida.");
                    continue;
                }

                System.out.print("Digite o novo valor para este atributo: ");
                String novoValor = sc.nextLine();

                // Se o campo for 'ano', garanta que o valor seja um inteiro válido
                if (campo == 3) {
                    // força leitura de inteiro e então converte para string para update consistente
                    int novoAno = readInt("Confirme o novo ano (apenas números): ");
                    novoValor = String.valueOf(novoAno);
                }

                if (db.update(id, campoDb(campo), novoValor)) {
                    System.out.println("Atributo atualizado com sucesso!");
                } else {
                    System.out.println("Falha ao atualizar. Verifique os dados e o ID.");
                }
            }
        } catch (RemoteException e){
            System.err.println(e.getMessage());
        }
    }

    private void listAllCars(){
        try{
            List<DB.CarroComId> lista = db.listAll();
            System.out.println("\n--- Lista de Veículos Cadastrados ---");
            for (DB.CarroComId carroComId: lista) {
                Carro c = carroComId.carro();
                System.out.printf("ID: %d | Marca: %s | Modelo: %s | Ano: %d | Cambio: %s | Tipo: %s%n",
                        carroComId.id(), c.marca(), c.modelo(), c.ano(), c.cambio(), c.tipo());
            }
        } catch (RemoteException e){
            System.err.println(e.getMessage());
        }
    }

    private String campoDb(int atributoCode) {
        return Map.of(
                1, "marca",
                2, "modelo",
                3, "ano",
                4, "cambio",
                5, "tipo"
        ).get(atributoCode);
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            if (input == null) {
                System.out.println("Entrada inválida. Tente novamente.");
                continue;
            }
            input = input.trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }
}