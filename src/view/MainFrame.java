package view;

import controller.ClienteController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JTextField nomeField;
    private JTextField emailField;
    private JTextArea infoArea;
    private JTextField valorCompraField;
    private JTextField clienteEmailField;
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private ClienteController clienteController;

    public MainFrame(ClienteController clienteController) {
        this.clienteController = clienteController;
        this.clienteController.setMainFrame(this); // Aqui você associa a 'MainFrame' ao 'ClienteController'
    
        setTitle("Vanessa Doces - Cartão Fidelidade");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Painel para "Adicionar Cliente" e "Adicionar Compra" um embaixo do outro
        JPanel painelAdicionar = new JPanel();
        painelAdicionar.setLayout(new BoxLayout(painelAdicionar, BoxLayout.Y_AXIS));  // Mudança para BoxLayout vertical

            // Painel de adicionar cliente
        JPanel adicionarClientePanel = new JPanel(new GridBagLayout());
        adicionarClientePanel.setBorder(BorderFactory.createTitledBorder("Adicionar Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Label ocupa toda a largura do campo
        gbc.anchor = GridBagConstraints.WEST; // Alinha à esquerda
        adicionarClientePanel.add(new JLabel("Nome:"), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 2; // Campo de texto ocupa a largura normal
        nomeField = new JTextField(20);
        adicionarClientePanel.add(nomeField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Label ocupa toda a largura do campo
        adicionarClientePanel.add(new JLabel("Email:"), gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 2; // Campo de texto ocupa a largura normal
        emailField = new JTextField(20);
        adicionarClientePanel.add(emailField, gbc);

        // Botão para adicionar cliente
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Botão ocupa toda a largura
        JButton adicionarClienteButton = new JButton("Adicionar Cliente");
        adicionarClientePanel.add(adicionarClienteButton, gbc);
        painelAdicionar.add(adicionarClientePanel);

        adicionarClienteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nome = nomeField.getText();
                String email = emailField.getText();
                try {
                    clienteController.adicionarCliente(nome, email);
                    atualizarTabela();
                    displayMessage("Cliente adicionado com sucesso!");
                    limparCamposCliente();
                } catch (Exception ex) {
                    displayMessage("Erro ao adicionar cliente: " + ex.getMessage());
                }
            }
        });

        // Painel de compras
        JPanel comprasPanel = new JPanel(new GridBagLayout());
        comprasPanel.setBorder(BorderFactory.createTitledBorder("Adicionar Compra"));

        // Email do cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Label ocupa toda a largura do campo
        comprasPanel.add(new JLabel("Email do Cliente:"), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 2; // Campo de texto ocupa a largura normal
        clienteEmailField = new JTextField(20);
        comprasPanel.add(clienteEmailField, gbc);

        // Valor da compra
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Label ocupa toda a largura do campo
        comprasPanel.add(new JLabel("Valor da Compra:"), gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 2; // Campo de texto ocupa a largura normal
        valorCompraField = new JTextField(20);
        comprasPanel.add(valorCompraField, gbc);

        // Botão para adicionar compra
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Botão ocupa toda a largura
        JButton adicionarCompraButton = new JButton("Adicionar Compra");
        comprasPanel.add(adicionarCompraButton, gbc);

        painelAdicionar.add(comprasPanel);

        adicionarCompraButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = clienteEmailField.getText();
                String valorCompraStr = valorCompraField.getText();
                try {
                    double valorCompra = Double.parseDouble(valorCompraStr);
                    int pontos = clienteController.adicionarCompra(email, valorCompra);
                    atualizarTabela();
                    displayMessage("Compra adicionada! Cliente " + email + " agora tem " + pontos + " pontos.");
                } catch (NumberFormatException ex) {
                    displayMessage("Erro: Valor de compra inválido.");
                } catch (Exception ex) {
                    displayMessage("Erro ao adicionar compra: " + ex.getMessage());
                }
            }
        });

        painelAdicionar.add(adicionarClientePanel);
        painelAdicionar.add(comprasPanel);

        // Área de exibição de informações
        infoArea = new JTextArea(5, 30);
        infoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoArea);
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informações"));
        infoPanel.add(scrollPane, BorderLayout.CENTER);

        JButton limparInfoButton = new JButton("Limpar Informações");
        limparInfoButton.addActionListener(e -> infoArea.setText(""));
        infoPanel.add(limparInfoButton, BorderLayout.SOUTH);

        // Tabela para exibir clientes
        JPanel tabelaPanel = new JPanel(new BorderLayout());
        tabelaPanel.setBorder(BorderFactory.createTitledBorder("Clientes e Pontos"));

        String[] colunas = {"Nome", "Email", "Pontos"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaClientes = new JTable(modeloTabela);

        JScrollPane tabelaScroll = new JScrollPane(tabelaClientes);
        tabelaPanel.add(tabelaScroll, BorderLayout.CENTER);

        JButton atualizarTabelaButton = new JButton("Atualizar Tabela");
        atualizarTabelaButton.addActionListener(e -> atualizarTabela());
        tabelaPanel.add(atualizarTabelaButton, BorderLayout.SOUTH);

        // Organizando a disposição
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelAdicionar, tabelaPanel);
        splitPane.setResizeWeight(0.6); // Deixa o lado esquerdo maior
        splitPane.setDividerLocation(0.6);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Informações no restante da tela
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        atualizarTabela();
        setVisible(true);
    }

    public void displayMessage(String message) {
        infoArea.append(message + "\n");
    }

    private void limparCamposCliente() {
        nomeField.setText("");
        emailField.setText("");
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0); // Limpa os dados existentes
        try {
            for (Object[] cliente : clienteController.getClientes()) {
                modeloTabela.addRow(cliente);
            }
        } catch (Exception e) {
            displayMessage("Erro ao atualizar tabela: " + e.getMessage());
        }
    }
}
