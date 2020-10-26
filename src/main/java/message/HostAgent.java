package message;

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

@Arguments(@Argument(name="platform", clazz= IExternalAccess.class))
public class HostAgent extends MessageAgent {
    @AgentArgument
    private IExternalAccess platform;

    @AgentCreated
    public void created () {
        super.created();

        //initiateDcop("./yaml/graph_coloring_basic.yaml");
        initiateDcop("./yaml/graph_coloring_10vars.yaml");
    }

    public void initiateDcop (String dcopFile) {
        // Load DCOP from YAML
        try {
            YamlLoader loader = new YamlLoader();
            DCOP dcop = loader.loadYAML(dcopFile);

            DFSTree tree = new DFSTree(dcop.getVariables(), dcop.getConstraints(), 1);
            tree.OutputGraph();

            startDcopAgents(dcop, tree);
            System.out.println("Successfully loaded DCOP ("+ dcopFile + ")");

        } catch (Exception e) {
            System.out.println("Error loading DCOP ("+ dcopFile + "): " + e.toString());
            e.printStackTrace();
        }
    }

    protected void startDcopAgents (DCOP dcop, DFSTree tree) {
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
        //

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
                }
            }
        }
        return content;
    }
}
