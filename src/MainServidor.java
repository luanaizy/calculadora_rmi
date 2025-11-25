import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MainServidor {

    public static void main(String[] args) {

        try {
            System.out.println("Iniciando servidor RMI...");

            Calculadora obj = new Calculadora(); //cria objeto calculadora

            ICalculadora stub = (ICalculadora)
                UnicastRemoteObject.exportObject(obj, 0); //transforma o objeto em stub remoto

            Registry registry = LocateRegistry.createRegistry(2001); //cria registry rmi na porta 2001

            registry.rebind("calculadora", stub); //registra o stub com o nome publico

            System.out.println("Servidor pronto!");
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
