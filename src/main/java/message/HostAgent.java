package message;

import dcopsolver.computations_graph.DFSNode;
import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.DCOP;
import dcopsolver.dcop.Variable;
import fileInput.YamlLoader;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

@Arguments(@Argument(name="platform", clazz= IExternalAccess.class))
public class HostAgent extends MessageAgent {
    @AgentArgument
    private IExternalAccess platform;

    private HashMap<IComponentIdentifier, List<DFSNode>> hostNodeMap = new HashMap<IComponentIdentifier, List<DFSNode>>();
    private DCOP dcop;
    private DFSTree tree;
    private ArrayList<Variable> solversChecked;

    @AgentCreated
    public void created () {
        super.created();
    }

    public void startOtherHosts() {
        //create the DFSTree
        try {
            tree = new DFSTree(dcop.getVariables(), dcop.getConstraints(), hosts.size());
            tree.OutputGraph();
        } catch (Exception e) {
            System.out.println("Failed To Create DFS Tree - Host Size: " + hosts.size());
            return;
        }

        //make a hashmap of each host and assign nodes
        for (int i = 0; i < hosts.size(); i++) {
            hostNodeMap.put(hosts.get(i), tree.gethD().getHostNodes().get(i));
        }

        //pack up the host nodes hashmap and the DFS Tree into a container
        ArrayList<Object> pair = new ArrayList<Object>();
        pair.add(dcop);
        pair.add(hostNodeMap);
        pair.add(tree);

        //send out the container to the other hosts
        for (IComponentIdentifier other : hosts) {
            //don't message itself
            if (!other.equals(getId())) {
                sendMessage(new Data("Start.tellHostNodes", pair, getId()), other);
            }
        }
    }

    protected void startDcopAgents () {
        IComponentManagementService cms = SServiceProvider
                .getService(platform, IComponentManagementService.class).get();

        for (DFSNode node : hostNodeMap.get(getId())){
            String name = node.getName();
            CreationInfo ci = new CreationInfo(
                    SUtil.createHashMap(new String[] { "dcop", "assignedVariableName", "dfsTree" }, new Object[] { dcop, name, tree })
            );
            cms.createComponent("Agent:" + name, "message.SolverAgent.class", ci);
        }
    }

    @Override
    public void body (IInternalAccess agent) {
        while (true) {
            super.body(agent);
        }
    }

    @Override
    protected void sendMessage (Data content, IComponentIdentifier id) {
        super.sendMessage(content, id);
    }

    @Override
    protected Data receiveMessage (Data content, String[] typeTree) {
        content = super.receiveMessage(content, typeTree);

        // If a message remains to be processed
        if (content != null) {
            if (typeTree.length == 2) {
                switch (typeTree[0]) {
                    case "Discover":
                        if (typeTree[1].equals("askType")) {
                            Data response = new Data("Discover.tellType", "Host", getId());
                            sendMessage(response, content.source);
                        }
                        break;
                    case "Start":
                        if (typeTree[1].equals("tellHostNodes")) {
                            //don't receive messages from self
                            if (!content.source.equals(getId())) {
                                ArrayList<Object> pair = (ArrayList<Object>) content.value;
                                dcop = (DCOP) pair.get(0);
                                hostNodeMap = (HashMap<IComponentIdentifier, List<DFSNode>>) pair.get(1);
                                tree = (DFSTree) pair.get(2);
                                startDcopAgents();
                            }
                        } else if (typeTree[1].equals("firstHost")) {
                            System.out.println(getId() + " is the Starter Host");

                            // Extract DCOP
                            dcop = (DCOP)content.getValue();

                            // Start Agents
                            startOtherHosts();
                            startDcopAgents();

                            //initialise the solvers check list.
                            solversChecked = new ArrayList<Variable>();

                            //add all of our variables to it
                            solversChecked.addAll(dcop.getVariables().values());
                        } else if (typeTree[1].equals("solverReady")) {
                            System.out.println(content.source + "is ready");

                            //check that the solver check list is initialised (indicating if this is the root host)
                            if (solversChecked != null) {
                                System.out.println(getId() + " Is the root host");

                                //remove the variable name from the list
                                solversChecked.remove((Variable)content.value);

                                //if the list is done send out the message to start solving
                                if (solversChecked.size() == 0) {
                                    for (IComponentIdentifier solver : solvers) {
                                        System.out.println("all agents ready!");
                                        sendMessage(new Data("DCOP.startSolving", null, getId()),solver);
                                    }
                                }
                            }
                        }
                        break;
                }
            }
        }
        return content;
    }



}
