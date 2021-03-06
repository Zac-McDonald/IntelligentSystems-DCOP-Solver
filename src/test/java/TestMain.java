import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;

public class TestMain {

    public static void main (String[] args) {
        // Setup JadeX platform
        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.setGui(true);

        config.setNetworkName("Swinburne");
        config.setNetworkPass("SwinPass");

        config.setAwareness(true);
        IExternalAccess platform = Starter.createPlatform(config).get();
        IComponentManagementService cms = SServiceProvider
                .getService(platform, IComponentManagementService.class).get();

        // Create HostAgent
        CreationInfo ci = new CreationInfo(
                SUtil.createHashMap(new String[]{ "platform" }, new Object[]{ platform }));

        //cms.createComponent("Host","message.HostAgent.class", ci);
        cms.createComponent("CLI", "message.CLIAgent.class", ci);

    }
}
