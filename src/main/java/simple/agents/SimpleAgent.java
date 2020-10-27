package simple.agents;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

import java.util.Scanner;


@Agent
public class SimpleAgent {
    /**
     * Body of agent that prints Hello World.
     */
    @AgentBody
    public void body () {
        Scanner in = new Scanner(System.in);
        String name = in.nextLine();
        printName(name);
    }
    public void printName(String name){

        if (name.equals("matthew") ){
            System.out.println("hey matt");
        }else{
            System.out.println("no thanks");
        }
    }
}
