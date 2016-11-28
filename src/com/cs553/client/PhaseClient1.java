package com.cs553.client;

import com.cs553.implementation.Constants;
import com.cs553.implementation.Message;
import com.cs553.implementation.PhaseOperationInterface;

import javax.swing.*;
import java.rmi.Naming;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Muthiah on 11/17/2016.
 */
public class PhaseClient1
{
    private static final int clientNumber = 1;

    static class DisplayTasker extends TimerTask
    {
        PhaseOperationInterface phaseOpr;
        public DisplayTasker(PhaseOperationInterface phaseOpr)
        {
            this.phaseOpr = phaseOpr;
        }
        public void run()
        {
            try
            {
                String temp = phaseOpr.DisplayMessage();
                if(temp != null)
                {
                    System.out.println(temp);
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception occurred in thread execution in Client 1");
                System.out.println(e);
            }
        }
    }

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
    public static void main(String[] args)
    {
        //Actual Client Implementation
        boolean loopFlag = true;
        String userMsg = null;
        int choice = 0;
        int clientNumber = 1;
        try
        {
            PhaseOperationInterface phaseOperation = (PhaseOperationInterface)Naming.lookup(Constants.node1);
            System.out.println("Connected the test server 1");

            Timer timer = new Timer();
            timer.schedule(new PhaseClient1.DisplayTasker(phaseOperation), 0, 100);
            timer.schedule(new PhaseClient1.ReceiveMessage(phaseOperation), 0, 100);

            Scanner input = new Scanner(System.in);

            while(loopFlag)
            {
                System.out.println("Please enter the message to be sent (minimum 3 characters)");
                userMsg = input.nextLine();
                Message newMsg = new Message(userMsg, clientNumber, 0, false);

                System.out.println("Please choose the destination to send to: ");
                System.out.println("Node 2 : 2");
                System.out.println("Node 3 : 3");
                System.out.println("Node 4 : 4");
                System.out.println("All Nodes : 5");
                System.out.println("Exit: 0");
                System.out.println("Please enter the choice number: ");
                String tempChoice = input.nextLine();
                if(tempChoice != null)
                {
                    choice = Integer.parseInt(tempChoice);
                    if (choice == 0)
                    {
                        loopFlag = false;
                        break;
                    }
                }

                if (choice != 0)
                {
                    phaseOperation.SendMessage(newMsg, choice, clientNumber);
                    System.out.println("Your message has been sent");
                }

            }
            //answer = phaseOperation.TestMethod(value);
            //System.out.println(answer);

            //phaseOperation.SendMessage(msg);
            //System.out.println("The message has been sent");
        }
        catch (Exception e)
        {
            System.out.println(e);
            System.out.println("Exception occurred in Client 1");
        }
    }
}
