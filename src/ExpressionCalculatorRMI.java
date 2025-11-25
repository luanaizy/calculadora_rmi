import java.util.Stack;

public class ExpressionCalculatorRMI {

    public double calcular(String expr) { 
        String postfix = toPostfix(expr); //converte a expressao para notacao pos fixa, que facilita o calculo
        return evalPostfix(postfix); //avalia o postfix no servidor rmi
    }

    private String toPostfix(String infix) {

        StringBuilder out = new StringBuilder(); //saida da expressao em notacao pos fixa
        Stack<Character> stack = new Stack<>(); //pilha de operadores
        StringBuilder number = new StringBuilder(); //buffer para montar numeros

        infix = infix.replace(" ", ""); //remove espaços

        for (int i = 0; i < infix.length(); i++) { //percorre caractere por caractere

            char c = infix.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                number.append(c); //acumula numero (pode ter varios digitos)
            } 
            else { //nao é numero

                //coloca numero acumulado na saída
                if (number.length() > 0) {
                    out.append(number).append(" ");
                    number.setLength(0);
                }

                if (c == '(') {
                    stack.push(c); //empilha parentese abrindo
                } 
                else if (c == ')') {
                    //desempilha ate o parentese abrindo
                    while (!stack.isEmpty() && stack.peek() != '(')
                        out.append(stack.pop()).append(" ");

                    stack.pop(); //remove '('
                } 
                else if (isOp(c)) { //se for operador
                    
                    //desempilha operadores com maior ou igual prioridade
                    while (!stack.isEmpty() && isOp(stack.peek()) &&
                           prioridade(stack.peek()) >= prioridade(c))
                        out.append(stack.pop()).append(" ");

                    stack.push(c); //empilha operador atual
                }
            }
        }

        //coloca ultimo numero na saída, se houver
        if (number.length() > 0) {
            out.append(number).append(" ");
        }

        //desempilha operadores restantes
        while (!stack.isEmpty()) {
            out.append(stack.pop()).append(" ");
        }

        return out.toString().trim(); //retorna em formato postfix
    }

    private double evalPostfix(String postfix) { //executa o calculo da expressao pos fixa

        Stack<Double> stack = new Stack<>(); //pilha usada para resolver operacoes

        for (String token : postfix.split("\\s+")) { //percorre token por token

            if (isNum(token)) {
                stack.push(Double.parseDouble(token)); //se for numero, empilha
            }
            else if (token.length() == 1 && isOp(token.charAt(0))) {

                double b = stack.pop(); //retira da pilha o segundo operando
                double a = stack.pop(); //retira da pilha o primeiro operando

                //operações ocorrem internamente no lado do servidor rmi
                stack.push( aplica(a,b,token.charAt(0)) ); //empilha resultado
            }
        }

        return stack.pop(); //retorna resultado final
    }

    private boolean isNum(String s) { //verifica se é numero
        return s.matches("\\d+(\\.\\d+)?");
    }

    private boolean isOp(char c) { //verifica se é operador
        return c=='+' || c=='-' || c=='*' || c=='/';
    }

    private int prioridade(char c) { //verifica prioridade dos operadores
        return (c=='+' || c=='-') ? 1 : 2;
    }

    private double aplica(double a, double b, char c) { //aplica operacao no servidor rmi
        switch(c){
            case '+': return a+b;
            case '-': return a-b;
            case '*': return a*b;
            case '/': return a/b;
        }
        return 0;
    }
}
