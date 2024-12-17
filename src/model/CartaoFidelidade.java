package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import view.MainFrame;

public class CartaoFidelidade {
    private int id;
    private int pontos;
    private String nomeCliente;

    public CartaoFidelidade() {
        this.pontos = 0;
    }

    public int getId() { 
        return id; 
    }

    public int getPontos() { 
        return pontos; 
    }

 // Método para adicionar pontos com base no valor da compra
    public void adicionarPontos(double valor, MainFrame mainFrame) {
        int novosPontos = (int) (valor * 0.1);  // Converte 10% do valor em pontos
        this.pontos += novosPontos;
        try {
            atualizarPontosNoBanco(); // Atualiza os pontos no banco após adicionar
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar pontos no banco: " + e.getMessage());
        }
        verificarMetaPontos(mainFrame);  // Verifica e exibe a recompensa
    }

    // Método para verificar metas de pontos e exibir mensagem de recompensa
    public void verificarMetaPontos(MainFrame mainFrame) {
        String recompensa = null;

        if (this.pontos >= 200) {
            recompensa = "Bombom no Pote";
            mainFrame.displayMessage(" O cliente atingiu 200 pontos. Recompensa: " + recompensa);
            mainFrame.displayMessage("Os pontos do cliente foram reiniciados.");
            this.pontos = 0; // Reinicia os pontos
        } else if (this.pontos >= 150) {
            recompensa = "Pão de Mel";
        } else if (this.pontos >= 100) {
            recompensa = "Trufa";
        }

        if (recompensa != null && this.pontos < 200) {
            mainFrame.displayMessage(" O cliente atingiu " + this.pontos + " pontos. Recompensa: " + recompensa);
        }

        try {
            atualizarPontosNoBanco(); // Atualiza o banco após reiniciar ou ajustar os pontos
        } catch (SQLException e) {
            mainFrame.displayMessage("Erro ao atualizar pontos no banco: " + e.getMessage());
        }
    }



    // Método para salvar o cartão no banco de dados associado a um cliente
    public void salvarNoBanco(int clienteId) throws SQLException {
        String sql = "INSERT INTO CartoesFidelidade (cliente_id, pontos) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, clienteId);
            stmt.setInt(2, pontos);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    // Método para atualizar o saldo de pontos no banco de dados
    public void atualizarPontosNoBanco() throws SQLException {
        String sql = "UPDATE CartoesFidelidade SET pontos = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pontos);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    // Método estático para buscar um cartão de fidelidade pelo ID do cliente
    public static CartaoFidelidade buscarPorClienteId(int clienteId) throws SQLException {
        String sql = "SELECT * FROM CartoesFidelidade WHERE cliente_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CartaoFidelidade cartao = new CartaoFidelidade(); // Inicializa sem nomeCliente
                    cartao.id = rs.getInt("id");
                    cartao.pontos = rs.getInt("pontos");
                    return cartao;
                }
            }
        }
        return null;
    }
}
