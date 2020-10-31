package message;

import dcopsolver.algorithm.InfoMessage;
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

    private HashMap<IComponentIdentifier, List<DFSNode>> hostNodeMap = new HashMap<>();
    private DCOP dcop;
    private DFSTree tree;
    private ArrayList<Variable> solversChecked;

    private Boolean shownSolution = false;
    private HashMap<String, InfoMessage> solverInfo;

    long nextCheckResultDelay = 1000;
    long nextCheckResult;

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
        ArrayList<Object> pair = new ArrayList<>();
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

        shownSolution = false;
        solverInfo = new HashMap<>();

        for (DFSNode node : hostNodeMap.get(getId())){
            String name = node.getName();
            solverInfo.put(name, null);
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

            //if the list is done send out the message to start solving
            if (solversChecked != null && solversChecked.isEmpty()) {
                System.out.println("all agents ready!");

                for (IComponentIdentifier solver : solvers) {
                    sendMessage(new Data("DCOP.startSolving", null, getId()), solver);
                }

                solversChecked = null;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime > nextCheckResult) {
                nextCheckResult = currentTime + nextCheckResultDelay;

                // Loop through solvers - ask for solution
                for (IComponentIdentifier solver : solvers) {
                    Data infoReq = new Data("Adopt.askInfo", null, getId());
                    sendMessage(infoReq, solver);
                }

                // Check if solvers have finished
                if (!shownSolution && solverInfo != null) {
                    boolean solved = true;
                    for (String variable : solverInfo.keySet()) {
                        // Check that all have terminated
                        if (solverInfo.get(variable) == null || !solverInfo.get(variable).getTerminated()) {
                            solved = false;
                            break;
                        }
                    }

                    // Print result
                    if (solved) {
                        StringBuilder sb = new StringBuilder("Solved dcop:\n");
                        for (String variable : solverInfo.keySet()) {
                            InfoMessage info = solverInfo.get(variable);
                            sb.append(info.getName()).append("=").append(info.getValue()).append(", cosing ").append(info.getCost());
                        }
                        System.out.println(sb.toString());
                        shownSolution = true;
                    }
                }
            }
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
                        switch (typeTree[1]) {
                            case "tellHostNodes":
                                //don't receive messages from self
                                if (!content.source.equals(getId())) {
                                    ArrayList<Object> pair = (ArrayList<Object>) content.value;
                                    dcop = (DCOP) pair.get(0);
                                    hostNodeMap = (HashMap<IComponentIdentifier, List<DFSNode>>) pair.get(1);
                                    tree = (DFSTree) pair.get(2);
                                    startDcopAgents();
                                }
                                break;
                            case "firstHost":
                                System.out.println(getId() + " is the Starter Host");

                                // Extract DCOP
                                dcop = (DCOP) content.getValue();

                                // Start Agents
                                startOtherHosts();
                                startDcopAgents();

                                //initialise the solvers check list.
                                solversChecked = new ArrayList<>();

                                //add all of our variables to it
                                solversChecked.addAll(dcop.getVariables().values());
                                break;
                            case "solverReady":
                                //System.out.println(content.source + "is ready");

                                //check that the solver check list is initialised (indicating if this is the root host)
                                if (solversChecked != null) {
                                    //System.out.println(getId() + " Is the root host");

                                    //remove the variable name from the list
                                    solversChecked.remove((Variable) content.value);
                                }
                                break;
                        }
                        break;
                    case "Adopt":
                        if (typeTree[1].equals("tellInfo")) {
                            InfoMessage response = (InfoMessage)content.value;
                            solverInfo.put(response.getName(), response);
                        }
                        break;
                    case "GUI":
                        if (typeTree[1].equals("askDCOP")) {
                            ArrayList<Object> triple = new ArrayList<>();
                            triple.add(dcop);
                            triple.add(hostNodeMap);
                            triple.add(tree);
                            Data response = new Data("GUI.tellDCOP", triple, getId());
                            sendMessage(response, content.source);
                        }
                        break;
                }
            }
        }
        return content;
    }



}
