package message;

import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;
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
        initiateDcop("./yaml/graph_coloring_basic.yaml");
    }

    public void initiateDcop (String dcopFile) {
        // Load DCOP from YAML
        try {
            YamlLoader loader = new YamlLoader();
            DCOP dcop = loader.loadYAML(dcopFile);

            startDcopAgents(dcop);
            System.out.println("Successfully loaded DCOP ("+ dcopFile + ")");
        } catch (Exception e) {
            System.out.println("Error loading DCOP ("+ dcopFile + "): " + e.toString());
            e.printStackTrace();
        }
    }

    protected void startDcopAgents (DCOP dcop) {
        IComponentManagementService cms = SServiceProvider
                .getService(platform, IComponentManagementService.class).get();

        dcop.getVariables().keySet().forEach(name -> {
            CreationInfo ci = new CreationInfo(
                    SUtil.createHashMap(new String[] { "dcop", "variable" }, new Object[] { dcop, name })
            );
            cms.createComponent("Agent: " + name, "message.SolverAgent.class", ci);
        });
    }

    @Override
    public void body (IInternalAccess agent) {
        System.out.println(agent.getComponentIdentifier().hashCode());

        //

        while (true) {
            super.body(agent);
        }
    }

    // Send message
    //@Override
    protected void sendMessage () {
        //
    }

    // Receive message
    //@Override
    protected void onReceiveMessage () {
        //
    }
}
