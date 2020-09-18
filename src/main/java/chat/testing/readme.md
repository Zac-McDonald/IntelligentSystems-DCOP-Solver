Much of the material for this week has come from the JadeX tutorial on Active Components here: https://download.actoron.com/docs/releases/latest/jadex-mkdocs/tutorials/ac/01%20Introduction/. 
Although the comments should make the code self-explanatory you may wish to read the original explanation if you have trouble.

## ChatV1
Run the `ChatD1Agent` `main()` function and use the JadeX GUI to deploy several agents. 
Look at the console output when deploying each agent. 
Also, take note of how the `ChatD1Agent` is providing the `IChatService`by using the `ChatServiceD1` class as an implementation.

## ChatV2
The `ChatGUID2` class creates a Javax Swing GUI for a user to interact with.
As with `chatv1` the agent uses the chat service class as an implantation of the `IChatService`, the `ChatServiceD2` class uses the `ChatGuiD2`.

The `ChatGUID2` `send` button sends a message to all components providing the `IChatService`.
Remember, those components don't need to be an agent and the `IChatService` does not need to be implemented with `ChatGuiD2`, for example JadeX supports integration with Android applications.
The `ChatGUID2.addMessage` function is used by the `ChatServiceD2` to place a new message on the screen. 
As all the program logic is separated out of the agent `ChatD2Agent` does not require any code. 
As in the week2 tutorial the main function that starts the JadeX platform in `ChatD2Agent` could be moved into another class.

You should look back at our earlier tutorials and figure out how to connect two remote JadeX platforms between yourself and another student. 
See if you can get a remote chat working between some number of students in your project.

### Challange One: 
Take a look at the original tutorial and see if you can follow along with some of their extensions. 
Can you make a secure version of this application?

## Solver
This module has been created by combining several of our earlier tutorials.
A QueensAgent supplies the IQueens service allowing AskAgents to ask for the solution to their N-Queens problem.

### Challange Two:
Can you adapt the QueensAgent so that it caches old results and does not recalculate a problem that it already has a solution for.
