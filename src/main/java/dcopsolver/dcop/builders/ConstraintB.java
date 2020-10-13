package dcopsolver.dcop.builders;

import dcopsolver.dcop.Constraint;
import dcopsolver.dcop.FunctionConstraint;
import dcopsolver.dcop.JavascriptEngine;
import dcopsolver.dcop.Variable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;

//constraint builder
public class ConstraintB {
    String name;
    String type;
    String expression;
    String source;

    public ConstraintB(){}

    //converts builder to actual constraint
    public Constraint build(String dcopDir, HashMap<String, Variable> v) throws Exception {
        Constraint c = null;

        //for functional constraint
        if (this.type.equals("functional")) {
            ArrayList<Variable> variables = new ArrayList<>();

            // Searches for variables in expression -- uses Regex to match complete Javascript tokens
            for (Map.Entry<String, Variable> variable: v.entrySet()) {
                // Use the precompiled Regex to get a Set of matched Strings
                Set<String> stringTokens = new HashSet<String>();
                Matcher matcher = JavascriptEngine.tokenPattern.matcher(expression);
                while (matcher.find()) {
                    stringTokens.add(matcher.group());
                }

                if (stringTokens.contains(variable.getKey())) {
                    variables.add(variable.getValue());
                }
            }

            if (source == null) {
                source = "";
            } else {
                // Parse source file as a string
                try {
                    StringBuilder sb = new StringBuilder();
                    Files.readAllLines(Paths.get(dcopDir, source)).forEach(sb::append);
                    source = sb.toString();
                } catch (IOException e) {
                    throw new FileNotFoundException("Could not find source file: " + source);
                }
            }

            c = new FunctionConstraint(this.name, variables, this.expression, this.source);
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

    public void setSource(String source) {
        this.source = source;
    }
}
