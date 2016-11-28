package com.cs553.implementation;

import java.io.Serializable;

/**
 * Created by Muthiah on 11/17/2016.
 *
 * This is the class containing all the message attributes.
 * An instance of this class is created for every message that is to be passed in the network
 */
public class Message implements Serializable
{
    //Holds the actual message to be sent
    private String msg;

    //Variable to hold the message tag that is unique for every message
    private String tag;

    //Variable to hold the ID of the message sender
    private int senderId;

    //Variable to hold the message priority
    private int priority;

    //Flag to indicate the deliverable state of the message
    private boolean deliverable;

    /**
     * Default constructor for the class
     */
    public Message()
    {

    }

    /**
     * Parameterized constructor
     * @param msg - The actual message to be sent
     * @param senderId - The ID of the sender
     * @param priority - The time stamp of the message being sent
     * @param deliverable = The deliverable status of the message
     */
    public Message(String msg, int senderId, int priority, boolean deliverable)
    {
        this.msg = msg;
        this.senderId = senderId;
        this.priority = priority;
        this.deliverable = deliverable;
    }

    /**
     * Getter for message
     * @return - Message stored in the object
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Setter for message
     * @param msg - Actual message to be stored
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Getter for message tag
     * @return - The unique message tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Setter of the mesasage tag
     * @param tag - Unique tag for the message
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Getter for the Sender ID
     * @return - The sender ID of the message
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * Setter for the Sender ID
     * @param senderId - sender ID of the message
     */
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    /**
     * Getter for the Deliverable flag
     * @return - Returns the deliverable status of the message
     */
    public boolean isDeliverable() {
        return deliverable;
    }

    /**
     * Setter for the deliverable flag
     * @param deliverable - Boolean deliverable status of the message
     */
    public void setDeliverable(boolean deliverable) {
        this.deliverable = deliverable;
    }

    /**
     * Getter for the time stamp of the message
     * @return - Returns the time stamp of the message
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Setter for the time stamp of the message
     * @param priority - Returns the current time stamp of the message
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
