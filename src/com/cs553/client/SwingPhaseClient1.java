package com.cs553.client;

import com.cs553.implementation.Constants;
import com.cs553.implementation.Message;
import com.cs553.implementation.PhaseOperationInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.Naming;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Muthiah on 11/17/2016.
 *
 * This is the client application. This is the face of each server that we initiate.
 * Interface for composing the message to be sent and also choosing the processes to which the message are to be sent
 * are handled from this class.
 *
 * This class binds to thr server using the URL that the corresponding server listens to. This client will connect to
 * Server 1 and hence will use the URL "rmi://localhost:9100/testserver1"
 *
 * Java swing is used in this class to open up a separate window for this client from where message can be sent to other
 * clients and the incoming messages are also displayed.
 *
 * For the purpose of testing there, there is a thread embedded with this class which triggers message to all other three
 * processes in regular intervals. This helps in testing the Three Phase Protocol when there are a lot of message exchanges
 * between processes
 */
public class SwingPhaseClient1
{
    private static final int clientNumber = 1;
    private static int callNumber = 1;
    private static Timer threadTimer;
    private static boolean threadStatus = true;

    /**
     * This is a thread class for displaying the incoming messages from other process.
     * The messages are displayed only when the have a final time stamp and their delivery status is set to True. And they
     * are also displayed based on the time stamp of the client
     *
     * This thread is started from the main class when this client is initiated and this thread keeps checking for processed
     * messages in the client that is waiting to be displayed
     */
    static class DisplayTasker extends TimerTask
    {
        PhaseOperationInterface phaseOpr;
        JTextArea jArea;
        public DisplayTasker(PhaseOperationInterface phaseOpr, JTextArea jArea)
        {
            this.phaseOpr = phaseOpr;
            this.jArea = jArea;
        }
        public void run()
        {
            try
            {
                String temp = phaseOpr.DisplayMessage();
                if(temp != null)
                {
                    jArea.append(temp);
                    jArea.append("\n");
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception occurred in thread execution in Client 1");
                System.out.println(e);
            }
        }
    }

    /**
     * This is another thread class which is used to acknowledge the incoming messages from other processes in the network
     * Thread implementation for receiving messages makes the process non blocking whem receiving messages
     *
     * The thread is started by the main process when this client is initialized. Once started this thread keeps checking
     * in regular intervals for incoming messages that is waiting to be acknowledged with a revised time stamp based on the
     * current state of the client
     */
    static class ReceiveMessage extends TimerTask
    {
        PhaseOperationInterface phaseOpr;
        public ReceiveMessage(PhaseOperationInterface phaseOpr)
        {
            this.phaseOpr = phaseOpr;
        }

        public void run()
        {
            try
            {
                phaseOpr.ReviseTimeStamp();
            }
            catch (Exception e)
            {
                System.out.println("Exception occurred in thread to process incoming messages");
                System.out.println(e);
            }
        }
    }

    /**
     * This is a thread implementation for the sole purpose of testing the Three Phase Communication in the local setup
     *
     * This thread can both be invoked and stopped from the swing interface for this client. When started this thread starts
     * sending message to all other clients in regular intervals until the thread is stopped. This gives us a way for testing
     * the three phase operation when there are a lot of messages between different process
     *
     * @param msg - The message to be sent
     * @param dest - The destination processes to which this message has to be sent
     * @param phaseOpr - The phase operation interface with which the other processes can be contacted
     */
    public static void SendMessage(JTextField msg, JTextField dest, PhaseOperationInterface phaseOpr)
    {
        int choice = Integer.parseInt(dest.getText());
        String message = msg.getText();

        Message newMsg = new Message(message, clientNumber, 0, false);
        try
        {
            phaseOpr.SendMessage(newMsg, choice, clientNumber);
            System.out.println("Your message has been sent");
        }
        catch (Exception e)
        {
            System.out.println("Exception in client 1 message thread");
        }
    }

    /**
     * This is another thread implementation for sending message from this client to other specified clients in the
     * network. Handling the send operation using thread makes the process non blockoing and helps in better communication
     * when there are many processes exchanging messages with each other
     *
     * @param phaseOpr - Phase Operation Interface with which the sendMessage method can be invoked
     */
    public static void ThreadSend(PhaseOperationInterface phaseOpr)
    {
        String message = "MessageFrom_1_" + String.valueOf(callNumber++);

        Message newMsg = new Message(message, clientNumber, 0, false);
        try
        {
            phaseOpr.SendMessage(newMsg, 5, clientNumber);
            System.out.println("Your message has been sent");
        }
        catch (Exception e)
        {
            System.out.println("Exception in client 1 ThreadSend Function");
        }
    }

    /**
     * The main class implementation for this client.
     * This initializes the client and binds it to the corresponding server.
     * All the thread definitions found above are invoked from this main class.
     * This also builds up the Swing interface for communication with the client
     * @param args
     */
    public static void main(String[] args)
    {
        //Creating the Swing Window for the client
        JFrame jFrame = new JFrame();
        JButton sendMessage = new JButton("Send Message");
        sendMessage.setPreferredSize(new Dimension(50, 50));
        JButton threadSend = new JButton("Trigger Thread/Stop Thread");
        JTextField clientMsg = new JTextField();
        JTextField destId = new JTextField();
        JTextArea receivedMessages = new JTextArea();
        JScrollPane scrollArea = new JScrollPane(receivedMessages);

        jFrame.setTitle("Process 1");
        jFrame.setLayout(new GridLayout(4, 2));
        jFrame.add(new Label("Enter the Message to be sent: "));
        jFrame.add(clientMsg);
        jFrame.add(new Label("Enter Process number or enter 5 to broadcast to all process: "));
        jFrame.add(destId);
        jFrame.add(threadSend);
        //jFrame.add(new Label("Click on the button to send message: "));
        jFrame.add(sendMessage);
        jFrame.add(new Label("Messages received from other processes: "));
        jFrame.add(scrollArea);

        jFrame.setSize(800, 500);
        jFrame.setVisible(true);

        //Variables needed for the implemetation of the client
        boolean loopFlag = true;
        String userMsg = null;
        int tempChoice = 0;
        int choice = 0;
        int clientNumber = 1;
        try
        {
            //Binding with the server1
            PhaseOperationInterface phaseOperation = (PhaseOperationInterface)Naming.lookup(Constants.node1);
            System.out.println("Connected the test server 1");

            //Starting the send message thread
            sendMessage.addActionListener((ActionEvent event) ->
            {
                SendMessage(clientMsg, destId, phaseOperation);
            });
            Timer timer = new Timer();
            timer.schedule(new SwingPhaseClient1.DisplayTasker(phaseOperation, receivedMessages), 0, 100);
            timer.schedule(new SwingPhaseClient1.ReceiveMessage(phaseOperation), 0, 100);


            // This thread is to keep firing message to all other clients, it fires in every second
            // This thread is started and stopped inside the action event of a jframe button
            threadSend.addActionListener((ActionEvent e) ->
            {
                if(threadStatus)
                {
                    TimerTask hourlyTask = new TimerTask () {
                        @Override
                        public void run () {
                            ThreadSend(phaseOperation);
                        }
                    };

                    threadTimer = new Timer();
                    threadTimer.schedule(hourlyTask, 0, 1000);
                    threadStatus = false;
                }
                else
                {
                    threadTimer.cancel();
                    threadStatus = true;
                }
            });

        }
        catch (Exception e)
        {
            System.out.println(e);
            System.out.println("Exception occurred in Client 1");
        }
    }
}
