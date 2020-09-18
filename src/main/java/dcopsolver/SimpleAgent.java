package dcopsolver;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class SimpleAgent {
    /**
     * Body of agent that prints Hello World.
     */
    @AgentBody
    public void body () {
        System.out.println("Hello World");
    }
}
