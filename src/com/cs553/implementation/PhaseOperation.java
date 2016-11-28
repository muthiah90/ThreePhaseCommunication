package com.cs553.implementation;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by Muthiah on 11/17/2016.
 *
 * This is the implementation of the PhaseOperationInterface
 * Since a client will send and receive messages, this implementation will contain definition for all activities
 * from sending to receiving messages
 *
 * All variable declarations are specific to the process and not to the instance of the process that's created during
 * message passing, which is why all the variables are declared static.
 */
public class PhaseOperation extends UnicastRemoteObject implements PhaseOperationInterface
{
    //Variable to hold the current timestamp of the process
    private static int priorityNumber = 0;

    //This will track the number of processes that has to reply for message that has been sent
    private static int replyCount = 0;

    //This will contain the max Priority based on the response from all the receivers of a certain message
    private static int maxPriority = 0;

    //Count of number of process that are yet to reply after a message signal being sent to them
    private static int toReplyCount = 0;

    //Holds the unique message tag that is generated for every message that is sent from a process
    private static String messageTag = null;

    //Received message Queue. Contains all the received messages
    private static List<Message> messageList = Collections.synchronizedList(new ArrayList<Message>());

    //Message map, that links the message tags to the corresponding messages
    private static Map<String, Message> messageMap = Collections.synchronizedMap(new HashMap<String, Message>());

    //This map holds the messages based on the priority assigned to the messages
    private static Map<Integer, String> priorityMap = Collections.synchronizedMap(new HashMap<Integer, String>());

    //List containing the ID's of all the senders
    private static List<Integer> senderList = Collections.synchronizedList(new ArrayList<Integer>());

    //In message Queue to hold all the incoming messages for the process
    private static List<Message> inMessageQueue = Collections.synchronizedList(new ArrayList<Message>());

    /**
     * Default constrcutor
     * @throws RemoteException
     */
    public PhaseOperation() throws RemoteException
    {
        super();
    }

    /**
     * Method to create the message tag based on the message that is to be sent
     * @param msg
     */
    private void createMessageTag(String msg)
    {
        String finalString = "";
        Random rand = new Random();

        //This takes the first character and the third character
        //It also appends a random number to the generated string
        finalString = finalString + msg.charAt(0);
        finalString = finalString + msg.charAt(msg.length() - 2);
        finalString = finalString + String.valueOf(rand.nextInt(9999));

        //The final tag is set in the static variable to be used for message passing
        messageTag = finalString;

        System.out.println("Final String: " + finalString);
    }

    /**
     * This ia an over ridden method from the PhaseOperationInterface
     * This is invoked from the client when it intends to send message to other clients
     *
     * The messages are sent to other processes based on the destination number. For sending it to a single process the
     * number 1 to 4 is used. And number 5 is used to flood messages to all other processes in the network
     * @param msg - Message to be sent
     * @param dest - Processes to which the message has to be sent
     * @param self - The self process ID
     * @return - Returns a string stating the status of the send activity
     * @throws RemoteException
     */
    @Override
    public String SendMessage(Message msg, int dest, int self) throws RemoteException
    {
        createMessageTag(msg.getMsg());

        //This is to increase the current timestamp before sending the message
        priorityNumber++;
        msg.setPriority(priorityNumber);
        msg.setTag(messageTag);

        //This is for sending message to all other processes in the network
        if(dest > 4)
        {
            toReplyCount = 3;
            for(int i = 1 ; i < 5; i++)
            {
                if (i != self)
                {
                    try
                    {
                        PhaseOperationInterface phaseOperation = (PhaseOperationInterface)Naming.lookup(Constants.connectionMap.get(i));
                        phaseOperation.ReceiveMessage(msg);
                        senderList.add(i);
                        System.out.println("Message sent to client " + i);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Exception occurred in Send Message");
                        System.out.println(e);
                    }
                }
            }
        }

        //This is to send the message to a single process corresponding to the number given in the destination parameter
        else
        {
            try
            {
                toReplyCount = 1;
                senderList.add(dest);
                PhaseOperationInterface phaseOperation = (PhaseOperationInterface)Naming.lookup(Constants.connectionMap.get(dest));
                phaseOperation.ReceiveMessage(msg);
                System.out.println("Message sent to client");
            }
            catch (Exception e)
            {
                System.out.println("Exception occurred in Send Message");
                System.out.println(e);
            }
        }
        return "Message sent";
    }

    /**
     * This is overridden method from the PhaseOperationInterface
     *
     * This method is used for receiving the incoming message from other processes in the network
     * When a message comes in from other process, this method receives the message and puts it in the inMessageQueue
     * which will then be processed by the process threads
     * @param msg - Message to be processed
     * @throws RemoteException
     */
    @Override
    public void ReceiveMessage(Message msg) throws RemoteException
    {
        System.out.println("Receive Message");
        inMessageQueue.add(msg);
        messageMap.put(msg.getTag(), msg);
        //ReviseTimeStamp(msg);
    }

    /**
     * This is overridden method from the PhaseOperationInterface
     *
     * This method is used to display the processed message on screen based on their final time stamp.
     * A in house thread keeps running and invokes this method to check for available messages to be displayed
     * @return - Returns the message to be displayed
     * @throws RemoteException
     */
    @Override
    public String DisplayMessage() throws RemoteException
    {
        if(messageList.size() > 0)
        {
            Message temp = messageList.get(0);
            messageList.remove(0);
            return temp.getMsg();
        }

        return null;
    }

    /**
     * This is also an overridden method from the PhaseOperationInterface
     *
     * This method is used to send the Revised Time Stamp to the process from which the message was received.
     * There is a in house thread which invokes this message based on the messages from the inMessageQueue. The messages
     * are processed based on their arrival order and the revised time stamp is sent to the process which had sent the
     * message to this process
     * @throws RemoteException
     */
    @Override
    public void ReviseTimeStamp() throws RemoteException
    {
        //System.out.println("Revise Time Stamp");
        //This is to check if there is Message waiting to be processed in the message Queue
        if (inMessageQueue.size() > 0)
        {
            //Once a message is processed, it is removed from the message Queue
            Message msg = inMessageQueue.get(0);
            inMessageQueue.remove(0);

            //This is increment the current time stamp so that the new message can be accommodated
            priorityNumber++;
            int calcPriority = priorityNumber;

            //Checking the timestamp of the received message and possible time stamp of the process and generating the
            //response accordingly
            if(msg.getPriority() > priorityNumber)
            {
                calcPriority = msg.getPriority();
                priorityNumber = calcPriority;
            }
            priorityMap.put(priorityNumber, msg.getTag());

            //The response is then sent to the process that had sent the message in the first place
            try
            {
                PhaseOperationInterface phaseOperation = (PhaseOperationInterface)Naming.lookup(Constants.connectionMap.get(msg.getSenderId()));
                phaseOperation.ReceiveRevisedTimeStamp(calcPriority);
            }
            catch (Exception e)
            {
                System.out.println("Exception occurred in Server in ReviseTimeStamp method");
                System.out.println(e);
            }
        }
    }

    /**
     * This is also an overridden method from the PhaseOperationInterface
     *
     * This method is used to receive the revised time stamp.
     * This method is typically invoked by all the processes which had received from this process. This method gathers all
     * the revised time stamps from all the processes that it had sent its message to. Based on the received time stamps,
     * the biggest is selected and it is then sent to all the processes again thereby completing the message exchange process
     * from the senders end
     * @param time - The revised time stamp
     * @throws RemoteException
     */
    @Override
    public void ReceiveRevisedTimeStamp(int time) throws RemoteException
    {
        System.out.println("Receive Revised Time Stamp");
        //The replyCount is incremented each time when this method is invoked. This helps to keep track of the remaining
        //process that has to reply
        replyCount++;

        //This checks for the maximum time that is recived this far
        maxPriority = maxPriority > time ? maxPriority : time;

        //If the reply count matches the actual number of process that the message was sent to, then it means that all the
        //process had replied and we will have to send the final time stamp to them.
        if (replyCount == toReplyCount)
        {
            //This helps in sending the final timestamp to all the process to which this particular message was intended
            //to when it was initially sent
            for (int i = 0 ; i < senderList.size(); i++)
            {
                //Invoking the receive final time stamp  method in each process with the final time stamp
                try
                {
                    PhaseOperationInterface phaseOperation = (PhaseOperationInterface)Naming.lookup(Constants.connectionMap.get(senderList.get(i)));
                    phaseOperation.ReceiveFinalTimeStamp(maxPriority, messageTag);
                }
                catch (Exception e)
                {
                    System.out.println("Exception occurred in Server in ReceiveRevisedTimeStamp method");
                    System.out.println(e);
                }
            }

            //All the counters are reset here so that it can take up the next message sending and receiving process
            messageTag = null;
            toReplyCount = 0;
            replyCount = 0;
            senderList.clear();
            priorityNumber = maxPriority;
        }
    }

    /**
     * This is again another overridden method from the PhaseOperationInterface
     *
     * This method is to receive the final timestamp that is to be associated with the message. This method is invoked by
     * the sender of the message.
     *
     * The message is retrieved from the queue based on the unique tag of the message and then the time stamp of the message
     * is changed to the received time stamp and then the delievrable status of the message is also set to true so that
     * it can be processed for displaying it on the client window
     *
     * @param time - The final time stamp that is to be appended with the message
     * @param tag - The unique tag for the message, that is used for identifying the message
     * @throws RemoteException
     */
    @Override
    public void ReceiveFinalTimeStamp(int time, String tag) throws RemoteException
    {
        //Receiving the message and setting the deliverable status to true
        System.out.println("Receive Final Time Stamp");
        Message msg = messageMap.get(tag);
        msg.setDeliverable(true);

        //This block finds the message with the unique message and replaces the messages tag associated with the tag with
        //the new message that is received
        for(int key: priorityMap.keySet())
        {
            String temp = priorityMap.get(key);
            if (temp == msg.getTag())
            {
                priorityMap.remove(key);
                priorityMap.put(time, tag);
                break;
            }
        }

        priorityNumber = time;

        //This call will order the message in the delivery queue based on the final time stamp received for that message
        DeliverableMessage();
    }

    /**
     * This method is to order the deliverable message queue based on the time stamp and the deliverable status of the
     * message.
     *
     * This method checks for both the time stamp of the message and also the delivery status flag in each messahe and
     * orders the messages based on these two parameters
     *
     * This method doesnt take any arguments and does not return any value
     */
    private void DeliverableMessage()
    {
        System.out.println("Deliverable Message");
        ArrayList<Integer> tempList = new ArrayList<Integer>();
        for(int i: priorityMap.keySet())
        {
            tempList.add(i);
        }

        for (int key: tempList)
        {
            Message tempMsg = messageMap.get(priorityMap.get(key));
            if (tempMsg.isDeliverable())
            {
                messageList.add(tempMsg);
                messageMap.remove(priorityMap.get(key));
                priorityMap.remove(key);
            }
        }
    }
}
