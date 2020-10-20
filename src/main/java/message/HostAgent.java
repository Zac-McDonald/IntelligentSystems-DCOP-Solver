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
    private HashMap<IComponentIdentifier, Boolean> hostsReady;
    private DCOP dcop;
    private DFSTree tree;
    private String state = "start";

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
    }

    public void negotiateHostNodes(){
        for (IComponentIdentifier host : hosts){
            hostsReady.put(host,false);
        }

        //sort a list of all the hosts
        List<IComponentIdentifier> sorted = hosts;
        sorted = sorted.stream().sorted().collect(Collectors.toList());
        //add each of the host node lists to a hashmap of these lists.
        for (int i = 0; i < sorted.size(); i++) {
            hostNodeMap.put(sorted.get(i), tree.gethD().getHostNodes().get(i));
        }

        //make sure everyone's list matches before we start
        for (IComponentIdentifier other : hosts){
            sendMessage(new Data("Negotiate.askHostNodes", hostNodeMap, getId()), other);
        }

    }

    //TODO host launches agents only for its nodes
    protected void startDcopAgents () {
        IComponentManagementService cms = SServiceProvider
                .getService(platform, IComponentManagementService.class).get();

        dcop.getVariables().keySet().forEach(name -> {
            CreationInfo ci = new CreationInfo(
                    SUtil.createHashMap(new String[] { "dcop", "assignedVariableName", "dfsTree" }, new Object[] { dcop, name, tree })
            );
            cms.createComponent("Agent:" + name, "message.SolverAgent.class", ci);
        });
    }

    @Override
    public void body (IInternalAccess agent) {
        while (true) {
            super.body(agent);
            if (state == "start"){
                //TODO understand how many hosts are on the system.
                //how to make sure that this is up to date by the time the solving starts?
                Integer numberOfHosts = hosts.size();
                //TODO create a DFSTree for that many hosts.
                tree = new DFSTree(dcop.getVariables(), dcop.getConstraints(), numberOfHosts);
                /*TODO message to determine ownership for different hostnodes
                    - for each of the hosts, send a message asking for one of the hostNodes
                    - tiebreaking by using each host's component ID*/
                state = "stop";
                negotiateHostNodes();
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
                }
            }
        }
        return content;
    }
}
