package br.edu.utfpr;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB extends UnicastRemoteObject implements ObjectCRUD{
    private static final String URL = "jdbc:sqlite:teste.db";

    public DB() throws RemoteException {
        initializeDB();
    }

    public void initializeDB() throws RemoteException {
        String sql = "CREATE TABLE IF NOT EXISTS carros(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "marca TEXT NOT NULL," +
                "modelo TEXT NOT NULL," +
                "ano INTEGER NOT NULL," +
                "cambio TEXT NOT NULL," +
                "tipo TEXT NOT NULL)";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    public void insert(Carro carro) throws RemoteException  {
        String sql = "INSERT INTO carros(marca, modelo, ano, cambio, tipo) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, carro.marca());
            pstmt.setString(2, carro.modelo());
            pstmt.setInt(3, carro.ano());
            pstmt.setString(4, carro.cambio());
            pstmt.setString(5, carro.tipo());

            int affectedRows = pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            System.out.printf("Veículo inserido com o id: %s%n", rs.getInt(1));
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar carro: " + e.getMessage());
        }
    }

    public CarroComId read(int id)  throws RemoteException {
        String sql = "SELECT * FROM carros WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Carro carro = new Carro(
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("ano"),
                        rs.getString("cambio"),
                        rs.getString("tipo")
                );
                return (new CarroComId(rs.getInt("id"), carro));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar carro: " + e.getMessage());
        }
        return null;
    }

    public List<CarroComId> listAll()  throws RemoteException {
        List<CarroComId> lista = new ArrayList<>();
        String sql = "SELECT * FROM carros";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Carro carro = new Carro(
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("ano"),
                        rs.getString("cambio"),
                        rs.getString("tipo")
                );
                lista.add(new CarroComId(rs.getInt("id"), carro));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os carros: " + e.getMessage());
        }
        return lista;
    }

    public boolean delete(int id)  throws RemoteException {
        String sql = "DELETE FROM carros WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.printf("DB: Successfully deleted item with id: %s%n", id);
                return true;
            } else {
                System.out.printf("DB Warning: Delete did not affect any rows for item id: %s%n", id);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao deletar carro: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int id, String campo, String novoValor) throws RemoteException  {
        // Validação para evitar SQL Injection no nome do campo
        if (!List.of("marca", "modelo", "ano", "cambio", "tipo").contains(campo)) {
            System.err.println("ERRO: Tentativa de atualizar um campo inválido: " + campo);
            return false;
        }

        String sql = String.format("UPDATE carros SET %s = ? WHERE id = ?", campo);
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Se o campo for 'ano', converte para Inteiro
            if (campo.equals("ano")) {
                pstmt.setInt(1, Integer.parseInt(novoValor));
            } else {
                pstmt.setString(1, novoValor);
            }
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Erro ao atualizar carro: " + e.getMessage());
            return false;
        }
    }

    // Classe auxiliar para retornar o carro com seu ID
    public record CarroComId(int id, Carro carro) implements Serializable{}
}
