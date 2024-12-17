package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=VanessaDocesDB;user=sistema_fidelidade;password=gb967865;encrypt=false;trustServerCertificate=true";
    private static final String USER = "sistema_fidelidade";  // Substitua com o usuário correto
    private static final String PASSWORD = "gb967865";  // Substitua pela senha do usuário, se necessário

    // Método para obter a conexão com o banco de dados
    public static Connection getConnection() throws SQLException {
        try {
            // Carrega o driver JDBC do SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do SQL Server não encontrado. Certifique-se de que o driver está no classpath.");
            e.printStackTrace();
            throw new SQLException("Erro ao carregar o driver JDBC", e);
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao SQL Server: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Método para executar uma consulta SQL e exibir os resultados
    public static void executeQuery(String query) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("Cliente: " + rs.getString("nome_cliente"));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao executar consulta no banco de dados.");
            e.printStackTrace();
        }
    }
}
