package tokenizer;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TokenCollection extends ArrayList<IToken> {

    @Override
    public String toString(){
        return this.stream().map(Object::toString).collect(Collectors.joining(", "));
    }
}
