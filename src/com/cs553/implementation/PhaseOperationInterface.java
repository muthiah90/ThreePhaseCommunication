package com.cs553.implementation;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Muthiah on 11/17/2016.
 *
 * Interface for handling the three phase communication protocol. This handles the operation of both the sender and the
 * receiver
 */
public interface PhaseOperationInterface extends Remote
{
    //This method is to send messages to other process
    public String SendMessage(Message msg, int dest, int self) throws RemoteException;

    //This method is to receive all the incoming messages
    public void ReceiveMessage(Message msg) throws RemoteException;

    //This method is to re arrange the queue based on the arrival of the final time stamps
    public String DisplayMessage() throws RemoteException;

    //This method is to calculate the next possible time stamp for the arrival message
    public void ReviseTimeStamp() throws RemoteException;

    //This method is to receive the revised time stamps for a message from all other messages
    public void ReceiveRevisedTimeStamp(int time) throws RemoteException;

    //This method is to receive the final time stamp and re arrange the queue based on the time stamp
    public void ReceiveFinalTimeStamp(int time, String tag) throws RemoteException;
}
