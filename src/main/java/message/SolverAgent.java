package message;

import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.DCOP;
import dcopsolver.dcop.Variable;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

import java.util.*;
import java.util.HashMap;
import java.util.Map;

@Arguments({@Argument(name="dcop", clazz= DCOP.class),
        @Argument(name="assignedVariableName", clazz= String.class),
        @Argument(name="dfsTree", clazz= DFSTree.class)})
public class SolverAgent extends MessageAgent {
    @AgentArgument
    DCOP dcop;

    @AgentArgument
    String assignedVariableName;
    public Variable assignedVariable;

    @AgentArgument
    DFSTree dfsTree;
    List<Variable> parentsChecked;

    @AgentCreated
    public void created () {
        assignedVariable = dcop.getVariables().get(assignedVariableName);
        parentsChecked = dfsTree.GetAllParents(assignedVariable);
    }

    //agents var map
    private HashMap<Variable,IComponentIdentifier> variableMap = new HashMap<>();

    @Override
    public void body (IInternalAccess agent) {
        //

        while (true) {
            super.body(agent);

            //add the other solvers and their variables to a map
            for (IComponentIdentifier solver:solvers) {
                if (!variableMap.containsValue(solver)){
                    Map<String,Object> args = addressBook.get(solver).getAgent().getComponentFeature(IArgumentsResultsFeature.class).getArguments();
                    if(args.get("dcop").hashCode() == dcop.hashCode()){
                            variableMap.put(dcop.getVariables().get(args.get("assignedVariableName")),solver);
                    }
                }
            }


            if (parentsChecked.size()>0){
                List<Variable> vars = dfsTree.GetAllParents(assignedVariable);
                for (Variable v: vars) {
                    Data content = new Data("Debug.neighbours", "parent", getId());
                    if (addressBook.containsKey(variableMap.get(v))){
                        sendMessage(content, variableMap.get(v));
                        parentsChecked.remove(v);
                    }
                }
            }

        }

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
                }
            }
        }

        return content;
    }
}
