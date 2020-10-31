package message;

import GUI.GUI;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Arguments(@Argument(name="platform", clazz= IExternalAccess.class))
public class GUIAgent extends MessageAgent {
    @AgentArgument
    private IExternalAccess platform;
    private GUI gui;
    private DCOP dcop;
    private DFSTree tree;
    private HashMap<IComponentIdentifier, List<DFSNode>> hostNodeMap;

    long dcopDelay = 500;
    long nextDcop;

    @AgentCreated
    public void created () {
        super.created();

        YamlLoader loader = new YamlLoader();
        //DCOP dcop = loader.loadDCOP("./yaml/graph_coloring_basic.yaml");
        try {
            dcop = loader.loadDCOP("./yaml/graph_coloring_10vars.yaml");
        }
        catch(Exception e) {}
        tree = new DFSTree(dcop.getVariables(), dcop.getConstraints(), 3);

    }


    @Override
    public void body (IInternalAccess agent) {


        while (true) {
            super.body(agent);

            long currentTime = System.currentTimeMillis();
            if (currentTime > nextDcop) {
                nextDcop = currentTime + dcopDelay;

                for (IComponentIdentifier host: hosts){
                    sendMessage(new Data("GUI.askDCOP", null, getId()), host);
                }
            }

            if ((tree != null) && (gui == null)){
                gui = new GUI(tree, agent.getExternalAccess());
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
                            Data response = new Data("Discover.tellType", "GUI", getId());
                            sendMessage(response, content.source);
                        }
                        break;
                    case "GUI":
                        if (typeTree[1].equals("tellDCOP")) {
                            ArrayList<Object> triple = (ArrayList<Object>) content.value;
                            dcop = (DCOP) triple.get(0);
                            hostNodeMap = (HashMap<IComponentIdentifier, List<DFSNode>>) triple.get(1);
                            tree = (DFSTree) triple.get(2);
                        }
                        break;
                }
            }
        }
        return content;
    }
}
