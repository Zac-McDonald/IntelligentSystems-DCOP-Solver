
package message;

import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.AgentCreated;

import java.util.*;

public class CLIAgent extends MessageAgent {

    private Scanner in;
    private String input;
    private Data startMsg;

    @AgentCreated
    public void created () {
        in = new Scanner(System.in);
        input = "Null";
        startMsg = new Data("Start.firstHost", null, agent.getComponentIdentifier());
    }

    @Override
    public void body (IInternalAccess agent) {
        System.out.print("Enter CMD>");
        while (true) {
            super.body(agent);
            input = in.nextLine();
            if (input == "1"){
                //just send the start message to the first host the CLI Agent is aware of
                System.out.print("Start message sent to: " + hosts.get(0));
                sendMessage(startMsg, hosts.get(0));
            }



        }
    }


}

