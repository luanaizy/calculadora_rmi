import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class CalculadoraCliente {

    public static void main(String[] args) {

        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 2001); //obtem referencia ao Registry RMI rodando na porta 2001
            ICalculadora calc = (ICalculadora) reg.lookup("calculadora"); //procura o objeto remoto registrado com nome "calculadora"

            Scanner sc = new Scanner(System.in); //scanner para ler entradas do usuario
            ExpressionHandlerRMI handler = new ExpressionHandlerRMI(calc); //irá decompor a expressao e chamar servidor via rmi 

            while (true) { //loop para interface do cliente

                System.out.println("\nDigite a expressão (ou 'sair'):");
                String expr = sc.nextLine();

                if (expr.equalsIgnoreCase("sair")) //para o loop caso usuario deseje
                    break;

                System.out.println("Modo:"); //pergunta modo de calculo desejado
                System.out.println("1 - Cliente decompõe expressão (RMI operações básicas)");
                System.out.println("2 - Servidor calcula tudo (RMI expressão completa)");
                String op = sc.nextLine();

                double resultado = 0;

                if (op.equals("1")) { //modo 1: cliente decompoe expressao
                    resultado = handler.calcularExpressao(expr);
                } else { //modo 2: servidor calcula expressao
                    resultado = calc.calcularExpressao(expr);
                }
                
                //mostra resultado
                System.out.println("Resultado = " + resultado);
            }

        } catch (Exception e) { //captura possivel excecao
            e.printStackTrace();
        }
    }
}
