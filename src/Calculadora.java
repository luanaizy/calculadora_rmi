import java.rmi.RemoteException;

public class Calculadora implements ICalculadora {

    public double soma(double a, double b) throws RemoteException {
    	//soma os dois operadores
        return a + b;
    }

    public double subtrai(double a, double b) throws RemoteException {
    	//subtrai os dois operadores
        return a - b;
    }

    public double multiplica(double a, double b) throws RemoteException {
    	//multiplica os dois operadores
        return a * b;
    }

    public double divide(double a, double b) throws RemoteException {
    	//divide os dois operadores
        return a / b;
    }

    public double calcularExpressao(String expr) throws RemoteException {
    	//calcula expressao
        ExpressionCalculatorRMI calc = new ExpressionCalculatorRMI();
        return calc.calcular(expr);
    }
}
