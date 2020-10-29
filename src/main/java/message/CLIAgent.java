
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
            if (input.equals("start")) {
                //just send the start message to the first host the CLI Agent is aware of
                System.out.print("Start message sent to: " + hosts.get(0));
                sendMessage(startMsg, hosts.get(0));
            }
        }
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
                            Data response = new Data("Discover.tellType", "CLI", getId());
                            sendMessage(response, content.source);
                        }
                        break;
                }
            }
        }

        return content;
    }


}

