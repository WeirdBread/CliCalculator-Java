package evaluator;

import java.util.ArrayList;
import java.util.List;

public class EvaluationLogger implements IEvaluationLogger {
    public List<String> operationsResults = new ArrayList<>();

    public void logEvaluationResult(String result) {
        this.operationsResults.add(result);
    }

    @Override
    public String toString() {
        return "Evaluation sequence: " + String.join(" | ", this.operationsResults);
    }
}
