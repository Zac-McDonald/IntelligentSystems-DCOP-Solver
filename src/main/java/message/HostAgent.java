package message;

import dcopsolver.computations_graph.DFSNode;
import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.DCOP;
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

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;



@Arguments(@Argument(name="platform", clazz= IExternalAccess.class))
public class HostAgent extends MessageAgent {
    @AgentArgument
    private IExternalAccess platform;

    @AgentCreated
    public void created () {
        super.created();
        dcop = loadDCOP("./yaml/graph_coloring_basic.yaml");
    }

    private HashMap<IComponentIdentifier, List<DFSNode>> hostNodeMap;
    private DCOP dcop;
    private DFSTree tree;
    private String state = "dormant";

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
        return dcop;
    }

    public void startOtherHosts(){
        state = "solving";
        //create the DFSTree
        tree = new DFSTree(dcop.getVariables(), dcop.getConstraints(), hosts.size());
        //make a hashmap of each host and assign nodes
        for (int i = 0; i < hosts.size(); i++) {
            hostNodeMap.put(hosts.get(i), tree.gethD().getHostNodes().get(i));
        }
        //send out the map to the other hosts
        for (IComponentIdentifier other : hosts){
            sendMessage(new Data("Start.tellHostNodes", hostNodeMap, getId()), other);
        }
        //send out the tree to the other hosts
        for (IComponentIdentifier other : hosts){
            sendMessage(new Data("Start.tellDFSTree", tree, getId()), other);
        }
    }

    //TODO host launches agents only for its nodes
    protected void startDcopAgents () {
        IComponentManagementService cms = SServiceProvider
                .getService(platform, IComponentManagementService.class).get();

        for (DFSNode node : hostNodeMap.get(agent.getComponentIdentifier())){
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
            /*System.out.println(state);
            if (state.equals("show")){
                System.out.println("Hosts on network"+hosts);
            }
            if (state.equals("start")){
                startOtherHosts();
                startDcopAgents();
            }
            if (state != "solving"){
                Scanner in = new Scanner(System.in);
                if (in.hasNextLine()){
                    state =in.nextLine();
                }
            }*/
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
                        if (typeTree[1].equals("tellHostNodes")){
                            if (content.source != agent.getComponentIdentifier()){ //dont call self
                                hostNodeMap = (HashMap<IComponentIdentifier, List<DFSNode>>) content.value;
                                if (tree != null){
                                    state.equals("solving");
                                    startDcopAgents();
                                }
                            }
                        }
                        if (typeTree[1].equals("tellDFSTree")){
                            if (content.source != agent.getComponentIdentifier()){ //dont call self
                                tree = (DFSTree) content.value;
                                if (hostNodeMap != null){
                                    state.equals("solving");
                                    startDcopAgents();
                                }
                            }
                        }
                }
            }
        }
        return content;
    }
}
