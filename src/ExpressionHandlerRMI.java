import java.util.Stack;

public class ExpressionHandlerRMI {

    private final ICalculadora calc; //interface remota usada para chamadas rmi

    public ExpressionHandlerRMI(ICalculadora calc) {
        this.calc = calc; //guarda stub remoto fornecido pelo cliente rmi
    }

    public double calcularExpressao(String expr) throws Exception {
        String postfix = infixParaPostfix(expr); //converte a expressao para notacao pos fixa
        return avaliarPostfix(postfix); //avalia a pos fixa chamando o servidor via rmi
    }

    private String infixParaPostfix(String infix) {

        infix = infix.replace(" ", ""); //remove espaços
        StringBuilder out = new StringBuilder(); //saida em notacao pos fixa
        StringBuilder number = new StringBuilder(); //buffer para numeros
        Stack<Character> stack = new Stack<>(); //pilha de operadores

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                number.append(c); //acumula numero (pode ter varios digitos)
            } 
            else {

                //coloca numero acumulado na saida
                if (number.length() > 0) {
                    out.append(number).append(' ');
                    number.setLength(0);
                }

                if (c == '(') {
                    stack.push(c); //empilha parenteses abrindo
                } 
                else if (c == ')') { //fecha parenteses
                    while (!stack.isEmpty() && stack.peek() != '(')
                        out.append(stack.pop()).append(' '); //vai colocando operadores

                    if (stack.isEmpty())
                        throw new IllegalArgumentException("parênteses desbalanceados");

                    stack.pop(); //remove '('
                } 
                else if (isOp(c)) { //se for operador
                    while (!stack.isEmpty() && isOp(stack.peek()) &&
                           prioridade(stack.peek()) >= prioridade(c)) //prioridade dos operadores
                        out.append(stack.pop()).append(' ');

                    stack.push(c); //empilha operador
                } 
                else {
                    throw new IllegalArgumentException("caractere inválido na expressão: " + c);
                }
            }
        }

        //coloca numero no final da expressao
        if (number.length() > 0) {
            out.append(number).append(' ');
        }

        //desempilha operadores restantes
        while (!stack.isEmpty()) {
            char op = stack.pop();
            if (op == '(')
                throw new IllegalArgumentException("parênteses desbalanceados");
            out.append(op).append(' ');
        }

        return out.toString().trim();
    }

    private double avaliarPostfix(String postfix) throws Exception {

        Stack<Double> stack = new Stack<>(); //pilha usada no calculo pos fixado
        String[] tokens = postfix.split("\\s+"); //separa caracteres

        for (String token : tokens) {

            if (token.isEmpty())
                continue;

            if (isNumber(token)) {
                stack.push(Double.parseDouble(token)); //se for numero, empilha
            }
            else if (token.length() == 1 && isOp(token.charAt(0))) {

                if (stack.size() < 2) //verifica se ha ao menos 2 operandos para operacao
                    throw new IllegalArgumentException("expressão inválida");

                double b = stack.pop(); //retira o segundo operando da pilha
                double a = stack.pop(); //retira o primeiro operando da pilha
                double r;

                char op = token.charAt(0);

                //ocorre a chamada remota rmi para realizar a operacao
                switch (op) {
                    case '+': r = calc.soma(a, b); break; 
                    case '-': r = calc.subtrai(a, b); break; 
                    case '*': r = calc.multiplica(a, b); break; 
                    case '/': r = calc.divide(a, b); break; 
                    default:
                        throw new IllegalArgumentException("operador inválido: " + op);
                }

                stack.push(r); //empilha resultado retornado do servidor rmi
            } 
            else {
                throw new IllegalArgumentException("token inválido no postfix: " + token);
            }
        }

        if (stack.size() != 1)
            throw new IllegalArgumentException("expressão inválida, sobrou item na pilha");

        return stack.pop(); //sobra um unico valor, retorna o resultado final
    }

    private boolean isOp(char c) { //verifica se é operador
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int prioridade(char op) { //prioridade dos operadores
        return (op == '+' || op == '-') ? 1 : 2;
    }

    private boolean isNumber(String s) { //verifica se é numero
        return s.matches("\\d+(\\.\\d+)?");
    }
}
