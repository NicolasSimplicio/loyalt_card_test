package controller;

import model.Cliente;
import model.Compra;
import view.MainFrame;
import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteController {
    private MainFrame view;

    public ClienteController() {
        // Construtor vazio para evitar referência circular
    }

    public void setMainFrame(MainFrame view) {
        this.view = view;
    }

    // Método para adicionar cliente ao banco e ao sistema
    public void adicionarCliente(String nome, String email) {
        try {
            Cliente cliente = new Cliente(nome, email);
            cliente.salvarNoBanco(); // Salva no banco de dados
            cliente.getCartaoFidelidade().salvarNoBanco(cliente.getId()); // Associa o cartão de fidelidade
            view.displayMessage("Cliente adicionado: " + nome + " (" + email + ")");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Erro ao adicionar cliente: " + e.getMessage());
        }
    }

    // Método para adicionar compra e atualizar os pontos do cliente
    public int adicionarCompra(String email, double valorCompra) {
        try {
            // Busca o cliente pelo email
            Cliente cliente = Cliente.buscarPorEmail(email);
            if (cliente != null) {
                // Cria a compra e associa ao cliente
                Compra compra = new Compra(valorCompra);
                compra.salvarNoBanco(cliente.getCartaoFidelidade().getId());
                
                // Atualiza os pontos do cartão fidelidade do cliente
                cliente.getCartaoFidelidade().adicionarPontos(valorCompra, view); // Chama o método que já faz o cálculo e atualização
                
                // Retorna os pontos atuais após a compra
                return cliente.getCartaoFidelidade().getPontos(); // Retorna os pontos atualizados
            } else {
                JOptionPane.showMessageDialog(view, "Cliente não encontrado.");
                return 0;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Erro ao registrar compra: " + e.getMessage());
            return 0;
        }
    }

   public Object[][] getClientes() throws SQLException {
    // Lista para armazenar os dados dos clientes
    List<Object[]> listaClientes = new ArrayList<>();

    // Buscar todos os clientes do banco
    List<Cliente> clientes = Cliente.buscarTodos();  // Método que você já tem na classe Cliente

    for (Cliente cliente : clientes) {
        // Para cada cliente, cria um array de objetos (nome, email e pontos)
        Object[] clienteData = new Object[3];
        clienteData[0] = cliente.getNome();   // Nome
        clienteData[1] = cliente.getEmail();  // Email
        clienteData[2] = cliente.getCartaoFidelidade().getPontos(); // Pontos

        // Adiciona o array de dados à lista
        listaClientes.add(clienteData);
    }

    // Converte a lista para um array de arrays (necessário para preencher a tabela)
    return listaClientes.toArray(new Object[0][]);
}

}