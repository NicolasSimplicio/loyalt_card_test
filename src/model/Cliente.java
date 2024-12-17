package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private int id;
    private String nome;
    private String email;
    private CartaoFidelidade cartaoFidelidade;

    public Cliente(String nome, String email) {
        if (nome == null || email == null || nome.trim().isEmpty() || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e email do cliente não podem ser vazios.");
        }
        this.nome = nome;
        this.email = email;
        this.cartaoFidelidade = new CartaoFidelidade(); // Cria o cartão de fidelidade ao criar o cliente
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public CartaoFidelidade getCartaoFidelidade() {
        return cartaoFidelidade;
    }

    // Método para salvar o cliente no banco de dados
    public void salvarNoBanco() throws SQLException {
        String sql = "INSERT INTO Clientes (nome, email) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getInt(1); // Recupera o ID gerado para o cliente
                        this.cartaoFidelidade.salvarNoBanco(this.id); // Salva o cartão de fidelidade no banco com o ID do cliente
                    }
                }
            } else {
                throw new SQLException("Erro ao salvar cliente no banco de dados. Nenhuma linha foi afetada.");
            }
        }
    }

    // Método estático para buscar um cliente pelo email
    public static Cliente buscarPorEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email do cliente não pode ser vazio.");
        }

        String sql = "SELECT * FROM Clientes WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente(rs.getString("nome"), rs.getString("email"));
                    cliente.id = rs.getInt("id");
                    // Recupera o cartão de fidelidade associado ao cliente
                    cliente.cartaoFidelidade = CartaoFidelidade.buscarPorClienteId(cliente.id);
                    return cliente;
                }
            }
        }
        return null; // Retorna null caso o cliente não seja encontrado
    }

    // Método para listar todos os clientes
    public static void listarTodosClientes() throws SQLException {
        String sql = "SELECT c.id, c.nome, c.email, cf.pontos FROM Clientes c " +
                     "INNER JOIN CartoesFidelidade cf ON c.id = cf.cliente_id";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("ID | Nome | Email | Pontos");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %d%n", 
                                  rs.getInt("id"), 
                                  rs.getString("nome"), 
                                  rs.getString("email"), 
                                  rs.getInt("pontos"));
            }
        }
    }
    // Método estático para buscar todos os clientes do banco de dados
    public static List<Cliente> buscarTodos() throws SQLException {
        // Lista para armazenar todos os clientes
        List<Cliente> clientes = new ArrayList<>();

        // SQL para buscar todos os clientes
        String sql = "SELECT * FROM Clientes";

        // Estabelece a conexão com o banco de dados
        try (Connection conn = Database.getConnection();  // Supondo que você tenha um método estático para conectar
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Percorre os resultados da consulta
            while (rs.next()) {
                // Cria um objeto Cliente a partir dos dados recuperados
                Cliente cliente = new Cliente(rs.getString("nome"), rs.getString("email"));
                cliente.id = rs.getInt("id"); // Preenche o ID
                cliente.cartaoFidelidade = CartaoFidelidade.buscarPorClienteId(cliente.id); // Busca o cartão de fidelidade

                // Adiciona o cliente à lista
                clientes.add(cliente);
            }
        }

        return clientes; // Retorna a lista de clientes
    }
}
