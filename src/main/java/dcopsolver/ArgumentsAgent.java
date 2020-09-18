package dcopsolver;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.*;

/**
 * An agent that starts up and prints its input arguments.
 */
@Agent
@Arguments(@Argument(name="myName", description = "Name of this agent", clazz=String.class, defaultvalue = "\"Hugo\""))
public class ArgumentsAgent {
    /**
     * Variable that is associated with the deceleration above by its name.
     */
    @AgentArgument
    protected String myName;

    /**
     * Main body that is run after component is started.
     */
    @AgentBody
    public void body () {
        System.out.println("Hello World this is " + myName);
    }

    /**
     * A main function to start the platform, the first agent is started with
     * the default arguments. Afterwards a second agent is started with
     * specific input arguments.
     * @param args The main arguments are not used.
     */

    public static void main (String [] args) {
        PlatformConfiguration conf = PlatformConfiguration.getDefault();
        //sets up debug features on the platform
        conf.setDebugFutures(true);

        //add and agent with default args
        conf.addComponent(ArgumentsAgent.class);

        //gui please
        conf.setGui(true);
        
        //get return a future from the platform...
        IExternalAccess platform = Starter.createPlatform(conf).get();

        // -- Adding a new agent to the platform with some input arguments.
        IComponentManagementService cms = SServiceProvider
                .getService(platform, IComponentManagementService.class).get();
        CreationInfo ci = new CreationInfo(
                SUtil.createHashMap(new String[]{"myName"}, new Object[]{"Bobby"}));
        cms.createComponent("NotBobby","dcopsolver.ArgumentsAgent.class", ci);
    }
}
