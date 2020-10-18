package message;

import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;

public class TestMain {

    public static void main(String[] args0) throws Exception {
        YamlLoader loader = new YamlLoader();
        DCOP dcop = loader.loadDCOP("./yaml/graph_coloring_basic.yaml");

        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.setGui(true);
        IExternalAccess platform = Starter.createPlatform(config).get();

        // -- Adding a new agent to the platform with some input arguments.
        IComponentManagementService cms = SServiceProvider
                .getService(platform, IComponentManagementService.class).get();

        // See -- src/test/java/PrototypeDemo -- same function (sort of)

        //CreationInfo ci = new CreationInfo(
        //        SUtil.createHashMap(new String[]{"platform", "dcop"}, new Object[]{platform, dcop}));
        //cms.createComponent("Host","message.HostAgent.class", ci);
    }
}
