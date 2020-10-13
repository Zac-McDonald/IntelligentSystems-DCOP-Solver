package message;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;

import java.util.HashMap;

public class TestMain {

    public static void main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.setGui(true);
        IExternalAccess platform = Starter.createPlatform(config).get();


        //make a hashmap
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("zac", 2);
        map.put("Gus", 3);

        // -- Adding a new agent to the platform with some input arguments.
        IComponentManagementService cms = SServiceProvider
                .getService(platform, IComponentManagementService.class).get();

        CreationInfo ci = new CreationInfo(
                SUtil.createHashMap(new String[]{"dcop"}, new Object[]{map}));
        cms.createComponent("Agent 1","message.MessageAgent.class", ci);

        ci = new CreationInfo(
                SUtil.createHashMap(new String[]{"dcop"}, new Object[]{map}));
        cms.createComponent("Agent 2","message.MessageAgent.class", ci);

        ci = new CreationInfo(
                SUtil.createHashMap(new String[]{"dcop"}, new Object[]{map}));
        cms.createComponent("Agent 3","message.MessageAgent.class", ci);
    }
}
