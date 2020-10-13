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
    protected Boolean receiveMessage (Data content) {
        return super.receiveMessage(content);

        // TODO: When we receive a message, we can sort it by type and deal with it here
        //       When we implement the algorithms, it'll probably be like ADOPTSolverAgent extending this class
        //       It would then have ADOPT specific message handling, but always pass messages down to super first
        //       Return value is "was this message handled", might be a better method
        //       Biggest downside of this is multiple levels can't handle the same message,
        //       although we can just not return on a case-by-case if we need to.
    }
}
