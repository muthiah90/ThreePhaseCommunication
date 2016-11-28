package com.cs553.server;

import com.cs553.implementation.Constants;
import com.cs553.implementation.PhaseOperation;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Created by Muthiah on 11/17/2016.
 *
 * *This class corresponds to the first server that is initialized from the PhaseOperation
 * This class runs on the port 9200 and can be connected to by hitting the following URL
 * "rmi://localhost:9200/testserver2"
 */
public class TestServer2
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
            LocateRegistry.createRegistry(Constants.portNode2);
            Naming.rebind(Constants.node2, phaseOperation);
            System.out.println("Connection successful to server 2");
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }
}
