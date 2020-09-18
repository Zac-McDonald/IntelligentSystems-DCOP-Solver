package chat.testing.chatv2;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import chat.testing.IChatService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

/**
 * A Graphical UI that each agent can start on its local machine.
 * It should allow you to send group chat messages to anyone else connected
 * to your JadeX Platform.
 */
public class ChatGuiD2 extends JFrame {
    JTextArea received;
    JTextField message;

    /**
     * Class constructor.
     * @param agent Reference to the component that is using this GUI.
     */
    public ChatGuiD2(final IExternalAccess agent) {
        super(agent.getComponentIdentifier().getName());
        getContentPane().setLayout(new BorderLayout());
        JButton send = new JButton("Send");
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String text = message.getText();
                message.setText(""); // -- clear the text
                agent.scheduleStep(new IComponentStep<Void>() {
                    public IFuture<Void> execute(IInternalAccess ia) {
                        IFuture<Collection<IChatService>> chatservices = ia
                                .getComponentFeature(IRequiredServicesFeature.class)
                                .getRequiredServices("chatservices");
                        chatservices.addResultListener(new DefaultResultListener<Collection<IChatService>>() {
                            public void resultAvailable(Collection<IChatService> result) {
                                for (IChatService cs : result) {
                                    cs.message(agent.getComponentIdentifier().getName(), text);
                                }
                            }
                        });
                        return IFuture.DONE;
                    }
                });
            }
        });
        send.setPreferredSize(new Dimension(200, 25));
        message = new JTextField();
        message.setPreferredSize(new Dimension(500, 25));
        received = new JTextArea();
        received.setPreferredSize(new Dimension(700, 300));
        getContentPane().add(received, BorderLayout.PAGE_START);
        getContentPane().add(message, BorderLayout.LINE_START);
        getContentPane().add(send, BorderLayout.LINE_END);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                agent.killComponent();
            }
        });
        // -- Display the window
        pack();
        setVisible(true);
    }

    /**
     * Simple function for adding a message to the screen.
     * @param msg The message to display.
     */
    public void addMessage(String msg) {
        received.append(msg + "\n");
    }

}
