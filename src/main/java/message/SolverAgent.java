package message;

import dcopsolver.dcop.DCOP;
import dcopsolver.dcop.Variable;
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
    public void onCreated () {
        assignedVariable = dcop.getVariables().get(assignedVariableName);
    }

    @Override
    public void body (IInternalAccess agent) {
        while (true) {
            super.body(agent);
        }
    }
}
