package evaluator;

import tokenizert.*;
import tokenizert.Enums.TokenType;

import javax.naming.LimitExceededException;
import java.util.*;
import java.util.stream.Collectors;

public class TokenEvaluator implements IDiceEvaluator{
    private final int _operationsLimit = 20;
    private int operationCounter = 0;

    private final TokenCollection rpnTokens;
    private final ITokenizer tokenizer;
    private IEvaluationLogger logger;
    private Random _random;
    private Long seedForRandom;

    public TokenEvaluator(
            ITokenizer tokenizer,
            IEvaluationLogger logger){
        this(tokenizer);
        this.setLogger(logger);
    }

    public TokenEvaluator (ITokenizer tokenizer) {
        this.tokenizer = tokenizer;
        TokenCollection tokens = this.tokenizer.generateTokens();
        this.expressionModificatorTokens = tokens.stream()
                .filter(x -> x instanceof ExpressionModificatorToken)
                .map(x -> (ExpressionModificatorToken)x)
                .collect(Collectors.toList());
        this.rpnTokens = convertToPostfixNotation(tokens);
    }

    public List<ExpressionModificatorToken> expressionModificatorTokens;

    public void setLogger(IEvaluationLogger logger){
        this.logger = logger;
    }

    private Stack<IToken> evaluationStack;

    public TokenCollection getTokens() {
        return this.rpnTokens;
    }

    public Stack<IToken> getEvaluationStack(){
        return this.evaluationStack;
    }

    public IEvaluationLogger getLogger(){
        return this.logger;
    }

    public Random getRandom(){
        if (this._random == null){
            this._random = this.seedForRandom == null
                    ? new Random()
                    : new Random(seedForRandom);
        }
        return this._random;
    }

    public void setSeed(Long seed){
        if (this._random == null){
            this.seedForRandom = seed;
            return;
        }
        if (seed == null) {
            this._random = new Random();
        } else {
            this._random.setSeed(seed);
        }
        this.seedForRandom = seed;
    }

    public double evaluate() throws LimitExceededException {
        this.expressionModificatorTokens.forEach(x -> x.applyExpressionMod(this));
        this.evaluationStack = new Stack<>();
        for (IToken token : this.rpnTokens){
            TokenType tokenType = token.getTokenType();
            if (tokenType == TokenType.Operand){
                evaluationStack.push(token);
                continue;
            }
            if (tokenType == TokenType.Dice
                    || tokenType == TokenType.DiceModificator
                    || tokenType == TokenType.MathOperator){
                IOperator operatorToken = (IOperator)token;
                operatorToken.doOperation(this);
                this.incrementOperationCounter();
                continue;
            }
        }
        return ((OperandToken)evaluationStack.pop()).getNumber();
    }

    /**
     * Преобразует выражение из инфиксной записи в постфиксную. См. обратная польская запись.
     */
    private static TokenCollection convertToPostfixNotation(TokenCollection tokens){
        TokenCollection result = new TokenCollection();
        Stack<IToken> stack = new Stack<>();
        for(int i = 0; i < tokens.size(); i++){
            IToken token = tokens.get(i);
            switch (token.getTokenType()){
                case Operand:
                    // Если операнд относится к модификатору дайса, приписываем его к модификатору.
                    if (!stack.isEmpty() && stack.peek() instanceof DiceModificatorToken){
                        DiceModificatorToken diceModToken = (DiceModificatorToken)stack.peek();
                        diceModToken.parameter = (int)((OperandToken)token).getNumber();
                        break;
                    }
                    result.add(token);
                    break;
                case Dice:
                    // Решаем, есть ли у дайса левый операнд.
                    CommonDiceToken diceToken = (CommonDiceToken) token;
                    if (i == 0 || tokens.get(i - 1).getTokenType() != TokenType.Operand){
                        diceToken.isSingleDie = true;
                    }
                case DiceModificator:
                case MathOperator:
                    // Согласно алгоритму, если приоритет первого оператора в стэке больше или равен приоритету
                    // текущего оператора, освобождаем стэк до тех пор, пока не встретим оператор меньшего приоритета.
                    IOperator operatorToken = (IOperator) token;
                    while (!stack.isEmpty()){
                        IToken stackToken = stack.peek();
                        if (stackToken instanceof IOperator
                            && ((IOperator)stackToken).getPriority() >= operatorToken.getPriority()){
                            result.add(stack.pop());
                            continue;
                        }
                        break;
                    }
                    stack.push(operatorToken);
                    break;
                case OpenParenthesis:
                    stack.push(token);
                    break;
                case CloseParenthesis:
                    // Освобождаем стэк до тех пор, пока не встретим открывающую скобку.
                    while (!(stack.peek() instanceof OpenParenthesisToken)){
                        result.add(stack.pop());
                    }
                    // Уничтожаем открывающую скобку.
                    stack.pop();
                    break;
                default:
                    break;
            }
        }
        while (!stack.isEmpty()){
            result.add(stack.pop());
        }
        return result;
    }

    public DiceEvaluationResult evaluateDice(CommonDiceToken diceToken, OperandToken leftOperand, OperandToken rightOperand) throws LimitExceededException {
        return evaluateDice(diceToken, leftOperand == null ? 1 : (int) leftOperand.getNumber(), rightOperand == null ? 1 : (int) rightOperand.getNumber(), diceToken.modificators);
    }

    private DiceEvaluationResult evaluateDice(
            CommonDiceToken diceToken,
            int diceToRoll,
            int edges,
            List<CommonDiceToken.DiceModificator> modificators) throws LimitExceededException {
        DiceEvaluationResult rollResult = diceToken.rollDice(this.getRandom(), diceToRoll, edges);
        List<CommonDiceToken.DiceModificator> orderedModificators = modificators.stream().sorted((x, y) -> y.getPriority() - x.getPriority()).collect(Collectors.toList());
        this.incrementOperationCounter();
        for (CommonDiceToken.DiceModificator mod : orderedModificators){
            switch (mod.getType()){
                case KeepHigh:
                    List<Integer> diceRolledOrdered = rollResult.diceRolled.getPrimaryGroup().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                    rollResult.sum = diceRolledOrdered.subList(0, mod.param == null ? 1 : mod.param).stream().mapToInt(a -> a).sum();
                    break;
                case KeepLow:
                    diceRolledOrdered = rollResult.diceRolled.getPrimaryGroup().stream().sorted().collect(Collectors.toList());
                    rollResult.sum = diceRolledOrdered.subList(0, mod.param == null ? 1 : mod.param).stream().mapToInt(a -> a).sum();
                    break;
                case RerollKeepHigh:
                    int count = 1;
                    while (count < (mod.param == null ? 2 : mod.param)){
                        count++;
                        DiceEvaluationResult newRoll = evaluateDice(diceToken, diceToRoll, edges, modificators.stream().filter(x -> x.getPriority() > mod.getPriority()).collect(Collectors.toList()));
                        rollResult.diceRolled.addGroup(newRoll.diceRolled);
                        if (newRoll.sum > rollResult.sum){
                            rollResult.sum = newRoll.sum;
                            rollResult.diceRolled.primaryGroupIndex = rollResult.diceRolled.size() - 1;
                        }
                    }
                    break;
                case RerollKeepLow:
                    count = 1;
                    while (count < (mod.param == null ? 2 : mod.param)){
                        count++;
                        DiceEvaluationResult newRoll = evaluateDice(diceToken, diceToRoll, edges, modificators.stream().filter(x -> x.getPriority() > mod.getPriority()).collect(Collectors.toList()));
                        rollResult.diceRolled.addGroup(newRoll.diceRolled);
                        if (newRoll.sum < rollResult.sum){
                            rollResult.sum = newRoll.sum;
                            rollResult.diceRolled.primaryGroupIndex = rollResult.diceRolled.size() - 1;
                        }
                    }
                    break;
                case MoreThan:
                    rollResult.sum = (int) rollResult.diceRolled.getPrimaryGroup().stream().filter(x -> x >= (mod.param == null ? 1 : mod.param)).count();
                    break;
                case LessThan:
                    rollResult.sum = (int) rollResult.diceRolled.getPrimaryGroup().stream().filter(x -> x < (mod.param == null ? 1 : mod.param)).count();
                    break;
                case Explosive:
                    if (edges <= 1) {
                        break;
                    }
                    int dicesToExplode = (int) rollResult.diceRolled.getPrimaryGroup().stream().filter(x -> x == edges).count();
                    while (dicesToExplode > 0){
                        DiceEvaluationResult newRoll = diceToken.rollDice(this.getRandom(), dicesToExplode, edges);
                        dicesToExplode = (int) newRoll.diceRolled.getPrimaryGroup().stream().filter(x -> x == edges).count();
                        rollResult.diceRolled.getPrimaryGroup().addAll(newRoll.diceRolled.getPrimaryGroup());
                        rollResult.sum += newRoll.sum;
                    }
                    break;
                //TODO: разобраться с иммутабельностью Integer.
                case MaxPossible:
                    rollResult.diceRolled.getPrimaryGroup().forEach(x -> x = edges);
                    rollResult.sum = diceToRoll * edges;
                    break;
                case MinPossible:
                    rollResult.diceRolled.getPrimaryGroup().forEach(x -> x = 1);
                    rollResult.sum = edges;
                    break;
            }

            rollResult.expression += CommonDiceToken.getDiceModificatorDescription(mod.getType()) + (mod.param == null ? "" : mod.param);
        }

        return rollResult;
    }

    private void incrementOperationCounter() throws LimitExceededException {
        if (this.operationCounter >= this._operationsLimit){
            throw new LimitExceededException("Operations limit has been reached");
        }
        this.operationCounter++;
    }
}
