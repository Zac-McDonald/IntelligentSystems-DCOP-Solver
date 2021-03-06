
package message;

import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.AgentCreated;

import java.util.*;

public class CLIAgent extends MessageAgent {
    private Scanner in;
    private String input;
    private String dcopPath;

    @AgentCreated
    public void created () {
        super.created();

        in = new Scanner(System.in);
        input = "Null";

        //dcopPath = "./yaml/graph_coloring_basic.yaml";
        //dcopPath = "./yaml/graph_coloring_10vars.yaml";
        dcopPath = "./yaml/graph_coloring_6vars.yaml";
        //dcopPath = "./yaml/graph_coloring_basic2.yaml";
    }

    @Override
    public void body (IInternalAccess agent) {
        System.out.println("Enter CMD>");

        while (true) {
            super.body(agent);

            // TODO: Totally doesn't work -- really would like async input though
            if (in.hasNextLine())
                input = in.nextLine();

            String[] inputParams = input.split(" ");

            if (inputParams.length > 0) {
                if (inputParams[0].equals("start")) {
                    if (inputParams.length == 2) {
                        dcopPath = inputParams[1];
                    }

                    DCOP dcop = loadDCOP(dcopPath);
                    if (dcop != null) {
                        //just send the start message to the first host the CLI Agent is aware of
                        System.out.print("Start message sent to: " + hosts.get(0));

                        Data startMsg = new Data("Start.firstHost", dcop, getId());
                        sendMessage(startMsg, hosts.get(0));
                    }
                } else if (inputParams[0].equals("list")) {
                    System.out.println("Known agents:");
                    for (IComponentIdentifier id : addressBook.keySet()) {
                        System.out.println("\t" + id + ",");
                    }
                }
            }
        }
    }

    public DCOP loadDCOP (String dcopFile) {
        // Load DCOP from YAML
        try {
            YamlLoader loader = new YamlLoader();
            DCOP dcop = loader.loadDCOP(dcopFile);
            System.out.println("Successfully loaded DCOP ("+ dcopFile + ")");
            return dcop;
        } catch (Exception e) {
            System.out.println("Error loading DCOP ("+ dcopFile + "): " + e.toString());
            e.printStackTrace();
        }
        return null;
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

