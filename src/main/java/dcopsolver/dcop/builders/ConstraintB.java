package dcopsolver.dcop.builders;

import dcopsolver.dcop.Constraint;
import dcopsolver.dcop.FunctionConstraint;
import dcopsolver.dcop.Variable;
import dcopsolver.jadex.JavascriptAgent;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

//constraint builder
public class ConstraintB {
    String name;
    String type;
    String expression;

    public ConstraintB(){}

    //converts builder to actual constraint
    public Constraint build(HashMap<String, Variable> v) throws Exception {
        Constraint c = null;

        //for functional constraint
        if (this.type.equals("functional")) {
            ArrayList<Variable> variables = new ArrayList<>();

            // Searches for variables in expression -- uses Regex to match complete Javascript tokens
            for (Map.Entry<String, Variable> variable: v.entrySet()) {
                // Use the precompiled Regex to get a Set of matched Strings
                Set<String> stringTokens = new HashSet<String>();
                Matcher matcher = JavascriptAgent.tokenPattern.matcher(expression);
                while (matcher.find()) {
                    stringTokens.add(matcher.group());
                }

                if (stringTokens.contains(variable.getKey())) {
                    variables.add(variable.getValue());
                }
            }

            c = new FunctionConstraint(this.name, variables, this.expression);
        }

        //if constraint can't be found, throw exception
        if (c == null)
            throw new Exception("Constraint type not found: " + type);
        else
            return c;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) { this.type = type; }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
