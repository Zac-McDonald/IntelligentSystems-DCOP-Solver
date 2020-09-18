package nQueensSolver;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;

/**
 * @author Charles Harold
 */
public class BootPlatform {
    public static void main(String[] args) {
        PlatformConfiguration conf = PlatformConfiguration.getMinimalRelayAwareness();
        conf.addComponent(QueensAgent.class);
        conf.setGui(true);
        IExternalAccess platform = Starter.createPlatform(conf).get();
        // -- Adding a new agent to the platform with some input arguments.
        IComponentManagementService cms = SServiceProvider
                .getService(platform, IComponentManagementService.class).get();
        buildManyAskAgents(cms);
    }
    /**
     * Assumes that QueenAgent is deployed and starts 10 question agents.
     * @param cms The Component Management Service to use.
     */
    private static void buildManyAskAgents (IComponentManagementService cms) {
        for (int i = 0; i < 10; i++) {
            CreationInfo ci = new CreationInfo(
                    SUtil.createHashMap(new String[]{"nAsk"}, new Integer[]{i}));
            cms.createComponent("SolverN" + i,"jadex.tutorials.week5.solver.AskAgent.class", ci);
        }
    }
}
