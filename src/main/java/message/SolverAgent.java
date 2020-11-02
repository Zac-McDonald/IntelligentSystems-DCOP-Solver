package message;

import dcopsolver.algorithm.AdoptSolver;
import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.Constraint;
import dcopsolver.dcop.DCOP;
import dcopsolver.dcop.Variable;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.*;

import java.util.*;
import java.util.HashMap;
import java.util.stream.Collectors;

@Arguments({@Argument(name="dcop", clazz= DCOP.class),
        @Argument(name="assignedVariableName", clazz= String.class),
        @Argument(name="dfsTree", clazz= DFSTree.class)})
public class SolverAgent extends MessageAgent {
    @AgentArgument
    DCOP dcop;

    @AgentArgument
    String assignedVariableName;
    Variable assignedVariable;

    @AgentArgument
    DFSTree dfsTree;
    List<Variable> parentsChecked;
    List<Variable> psuedosChecked;
    ArrayList<Variable> variablesChecked = new ArrayList<Variable>();

    AdoptSolver solver;

    //agents var map
    private HashMap<String, IComponentIdentifier> variableMap = new HashMap<>();
    // TODO: pendingVariables and variablesChecked do similar things, maybe combine them?
    private ArrayList<IComponentIdentifier> pendingVariables = new ArrayList<>();
    private Boolean startSolving = false;
    private Boolean sentReadyMessage = false;

    @AgentCreated
    public void created () {
        super.created();

        assignedVariable = dcop.getVariables().get(assignedVariableName);
        variablesChecked.addAll(dcop.getVariables().values());
        parentsChecked = dfsTree.GetAllParents(assignedVariable);
        psuedosChecked = dfsTree.GetParents(assignedVariable, true);

        //List<Variable> children = dfsTree.GetAllChildren(assignedVariable);

        // Filter constraints to constraints on our variable and its parents
        List<Constraint> constraints = dcop.getConstraints().values().stream().filter(c -> {
            return c.getVariables().contains(assignedVariable);// && !c.getVariables().stream().anyMatch(children::contains);
        }).collect(Collectors.toList());

        solver = new AdoptSolver(assignedVariable, dfsTree, constraints, this);
    }

    @AgentKilled
    public void killed () {
        super.killed();

        // For debugging if agents were unreachable -- saves trying to find individual messages
        List<String> a = new ArrayList<>(variableMap.keySet());
        List<String> b = parentsChecked.stream().map(Variable::getName).collect(Collectors.toList());
        System.out.println(assignedVariableName + ": " + a + " -> " + b);
    }

    @Override
    public void body (IInternalAccess agent) {
        //

        while (true) {
            super.body(agent);

            if (startSolving) {
                //System.out.println(assignedVariableName + " -> starting agent");
                //addressBookCompleted = true;
                solver.start();
                startSolving = false;
            } else {
                if(!sentReadyMessage && variablesChecked.isEmpty()) {
                    for (IComponentIdentifier host : hosts) {
                        sendMessage(new Data("Start.solverReady",assignedVariable, getId()),host);
                        //sentReadyMessage = true;
                    }
                }

                for (IComponentIdentifier other : solvers) {
                    if (!variableMap.containsValue(other) && !pendingVariables.contains(other)) {
                        sendMessage(new Data("DCOP.askVariable", null, getId()), other);
                        pendingVariables.add(other);
                    }
                }
            }

/*
            if (parentsChecked.size() > 0) {
                // For each parent we haven't already messaged
                List<Variable> vars = dfsTree.GetAllParents(assignedVariable);
                for (Variable v: vars) {
                    // If we know the parent variables agent
                    if (addressBook.containsKey(variableMap.get(v))) {
                        Data content = new Data("Debug.neighbours", "parent", getId());
                        sendMessage(content, variableMap.get(v));

                        // Mark parent as sent to
                        parentsChecked.remove(v);
                    }
                }
            }

            if (psuedosChecked.size() > 0) {
                // For each parent we haven't already messaged
                List<Variable> vars = dfsTree.GetParents(assignedVariable, true);
                for (Variable v: vars) {
                    // If we know the parent variables agent
                    if (addressBook.containsKey(variableMap.get(v))) {
                        Data content = new Data("Debug.neighbours", "pseudo-parent", getId());
                        sendMessage(content, variableMap.get(v));

                        // Mark parent as sent to
                        psuedosChecked.remove(v);
                    }
                }
            }
*/
        }
    }

    public void sendMessage (Data content, String target) {
        content.source = getId();
        sendMessage(content, variableMap.get(target));
    }

    @Override
    protected void sendMessage (Data content, IComponentIdentifier id) {
        super.sendMessage(content, id);

        // TODO: See receiveMessage notes, similar idea for sending, allows us to wrap up messages at
        //       higher levels. Think the packet structure, as Data can take Data as its value.
    }

    @Override
    protected Data receiveMessage (Data content, String[] typeTree) {
        content = super.receiveMessage(content, typeTree);

        // TODO: When we receive a message, we can sort it by type and deal with it here
        //       When we implement the algorithms, I changed my mind from extending, that would be gross
        //       Instead we can just pass specific message types to it
        //       Return value is the message that still needs to be handled

        // If a message remains to be processed
        if (content != null) {
            if (typeTree.length == 2) {
                switch (typeTree[0]) {
                    case "Discover":
                        if (typeTree[1].equals("askType")) {
                            Data response = new Data("Discover.tellType", "Solver", getId());
                            sendMessage(response, content.source);
                        }
                        break;
                    case "DCOP":
                        switch (typeTree[1]) {
                            case "askVariable":
                                Data response = new Data("DCOP.tellVariable", assignedVariableName, getId());
                                sendMessage(response, content.source);
                                break;
                            case "tellVariable":
                                variableMap.put(dcop.getVariables().get((String) content.value).getName(), content.source);
                                variablesChecked.remove(dcop.getVariables().get((String) content.value));
                                pendingVariables.remove(content.source);
                                break;
                            case "startSolving":
                                //System.out.println(assignedVariableName + " -> set startSolving");
                                startSolving = true;
                                sentReadyMessage = true;
                                break;
                            case "endSolving":
                                    // Delete agents
                                    agent.killComponent();
                                break;
                        }
                        break;
                    case "Adopt":
                        if (typeTree[1].equals("askInfo")) {
                            Data replyMsg = new Data("Adopt.tellInfo", solver.getInfo(), getId());
                            sendMessage(replyMsg, content.source);
                        }
                        break;
                }
            }
        }

        solver.handleMessage(content, typeTree);

        return content;
    }
}
