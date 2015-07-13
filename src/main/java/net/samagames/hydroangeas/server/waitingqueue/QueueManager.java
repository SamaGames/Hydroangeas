package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.server.HydroangeasServer;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 12/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class QueueManager {

    public List<Queue> queues = new ArrayList<>();
    private HydroangeasServer instance;

    public QueueManager(HydroangeasServer instance)
    {

        this.instance = instance;
    }

    public Queue getQueueByName(String name)
    {
        for(Queue queue : queues)
        {
            if(queue.getName().equals(name))
            {
                return queue;
            }
        }
        return null;
    }

}
