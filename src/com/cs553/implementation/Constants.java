package com.cs553.implementation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Muthiah on 11/18/2016.
 *
 * This class holds all the constants that is used across the project.
 * All the declarations here are static. They are done so to avoid class initialization at the place of usage.
 * If a new node is added to the system then it has to be registered in here for proper functioning of the system.
 */
public class Constants
{
    // Port numbers to be used while initiating the server
    // 1, 2, 3 and 4 corresponds to four different nodes used in this experiment
    public static final int portNode1 = 9100;
    public static final int portNode2 = 9200;
    public static final int portNode3 = 9300;
    public static final int portNode4 = 9400;

    //The URL in which the server will be listening for connections
    // 1, 2, 3 and 4 corresponds to four different nodes used in this experiment
    public static final String node1 = "rmi://localhost:9100/testserver1";
    public static final String node2 = "rmi://localhost:9200/testserver2";
    public static final String node3 = "rmi://localhost:9300/testserver3";
    public static final String node4 = "rmi://localhost:9400/testserver4";

    //This map contains the connection details for all four nodes in the experiment
    //It is initialized during the start of the experiment
    public static final Map<Integer, String> connectionMap;
    static
    {
        connectionMap = new HashMap<>();
        connectionMap.put(1, "rmi://localhost:9100/testserver1");
        connectionMap.put(2, "rmi://localhost:9200/testserver2");
        connectionMap.put(3, "rmi://localhost:9300/testserver3");
        connectionMap.put(4, "rmi://localhost:9400/testserver4");
    }
}
