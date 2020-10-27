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
    private Scanner in = new Scanner(System.in);
    private HashMap<IComponentIdentifier, List<DFSNode>> hostNodeMap;
    private DCOP dcop;
    private DFSTree tree;

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
        //create the DFSTree
        //TODO there may be more hosts than there are variable to divide up... what happens?
        System.out.println("debug 1");

        try{tree = new DFSTree(dcop.getVariables(), dcop.getConstraints(), hosts.size());}
        catch (Exception e){System.out.println("Failed To Create DFS Tree");}
        tree.PrintHosts();

        System.out.println("debug 2");
        //make a hashmap of each host and assign nodes
        for (int i = 0; i < hosts.size(); i++) {
            System.out.println(hosts.get(i).toString());
            System.out.println(tree.gethD().getHostNodes().get(i));
            hostNodeMap.put(hosts.get(i), tree.gethD().getHostNodes().get(i));
            System.out.println("debug 3." + i);
        }
        System.out.println("debug 4");
        System.out.println(hostNodeMap.toString());
        System.out.println(tree.toString());

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
        System.out.println("debug 6");
        System.out.println(agent.getComponentIdentifier() +" starting DCOP Agents");

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
                                    startDcopAgents();
                                }
                            }
                        }
                        if (typeTree[1].equals("tellDFSTree")){
                            if (content.source != agent.getComponentIdentifier()){ //dont call self
                                tree = (DFSTree) content.value;
                                if (hostNodeMap != null){
                                    startDcopAgents();
                                }
                            }
                        }
                        if (typeTree[1].equals("firstHost")){
                            System.out.print("Start Message Received\n");
                            startOtherHosts();
                            startDcopAgents();
                        }
                }
            }
        }
        return content;
    }
}
