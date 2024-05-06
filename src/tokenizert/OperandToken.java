package tokenizert;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class OperandToken implements IToken{
    public OperandToken(double d) {
        this._number = d;
        this._symbol = String.valueOf(d);
    }

    private final double _number;
    public double getNumber(){
        return this._number;
    }

    public Enums.TokenType getTokenType(){
        return Enums.TokenType.Operand;
    }

    private final String _symbol;
    public String getSymbol(){
        return this._symbol;
    }

    @Override
    public String toString(){
        DecimalFormat format = new DecimalFormat("0.#####", new DecimalFormatSymbols(Locale.US));
        return format.format(this.getNumber());
    }
}
