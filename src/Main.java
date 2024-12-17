import controller.ClienteController;
import view.MainFrame;

public class Main {
    public static void main(String[] args) {
        // Criando uma instância do controlador
        ClienteController clienteController = new ClienteController();
        
        // Iniciando a interface gráfica (MainFrame)
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame(clienteController);
        });
    }
}
