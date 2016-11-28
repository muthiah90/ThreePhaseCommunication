package com.cs553.server;

import com.cs553.implementation.Constants;
import com.cs553.implementation.PhaseOperation;
import com.cs553.implementation.PhaseOperationInterface;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.TimerTask;

/**
 * Created by Muthiah on 11/17/2016.
 *
 * This class corresponds to the first server that is initialized from the PhaseOperation
 * This class runs on the port 9100 and can be connected to by hitting the following URL
 * "rmi://localhost:9100/testserver1"
 */
public class TestServer1
{
    /**
     * Main method to start the server by binding with the foresaid port
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            //This cteps tries to bind with the specified address and once done starts listening for incoming connection
            //from the binded port
            PhaseOperation phaseOperation = new PhaseOperation();
            LocateRegistry.createRegistry(Constants.portNode1);
            Naming.rebind(Constants.node1, phaseOperation);
            System.out.println("Connection successful to server 1");

            //Timer timer = new Timer();
            //timer.schedule(new TestServer1.ReceiveMessage(phaseOperation), 0, 100);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }
}
