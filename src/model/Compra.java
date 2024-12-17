package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Compra {
    private int id;
    private double valor;
    private Date data;

    public Compra(double valor) {
        this.valor = valor;
        this.data = new Date();  // Define a data da compra como a data atual
    }

    public int getId() { 
        return id; 
    }

    public double getValor() { 
        return valor; 
    }

    public Date getData() { 
        return data; 
    }

    // Salva a compra no banco de dados e associa ao cartão de fidelidade
    public void salvarNoBanco(int cartaoFidelidadeId) throws SQLException {
        String sql = "INSERT INTO Compras (valor, data, cartaoFidelidade_id) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, valor);
            stmt.setTimestamp(2, new Timestamp(data.getTime()));
            stmt.setInt(3, cartaoFidelidadeId);
            int rowsAffected = stmt.executeUpdate();

            // Verifica se a inserção foi bem-sucedida
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getInt(1);
                    }
                }
            }
        }

        // Atualiza os pontos no cartão de fidelidade, apenas se o valor for positivo
        if (valor > 0) {
            CartaoFidelidade cartao = CartaoFidelidade.buscarPorClienteId(cartaoFidelidadeId);
            if (cartao != null) {
                cartao.adicionarPontos(valor, null);
                cartao.atualizarPontosNoBanco();
            }
        }
    }

    // Retorna todas as compras associadas a um cartão de fidelidade específico
    public static List<Compra> getComprasPorCliente(int cartaoFidelidadeId) throws SQLException {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM Compras WHERE cartaoFidelidade_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartaoFidelidadeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Compra compra = new Compra(rs.getDouble("valor"));
                    compra.id = rs.getInt("id");
                    compra.data = rs.getTimestamp("data");
                    compras.add(compra);
                }
            }
        }
        return compras;
    }
}
