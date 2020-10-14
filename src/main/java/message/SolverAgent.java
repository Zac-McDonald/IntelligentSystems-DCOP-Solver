package message;

import dcopsolver.dcop.DCOP;
import dcopsolver.dcop.Variable;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

@Arguments({@Argument(name="dcop", clazz= DCOP.class), @Argument(name="assignedVariableName", clazz= String.class)})
public class SolverAgent extends MessageAgent {
    @AgentArgument
    DCOP dcop;

    @AgentArgument
    String assignedVariableName;
    Variable assignedVariable;

    @AgentCreated
    public void created () {
        assignedVariable = dcop.getVariables().get(assignedVariableName);
    }

    @Override
    public void body (IInternalAccess agent) {
        //

        while (true) {
            super.body(agent);
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
