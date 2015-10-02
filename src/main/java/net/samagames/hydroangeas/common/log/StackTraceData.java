package net.samagames.hydroangeas.common.log;

import net.samagames.restfull.LogLevel;
import net.samagames.restfull.RestAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class StackTraceData
{
    private List<String> data;
    public StackTraceData(String header)
    {
        this.data = new ArrayList<>();
        this.data.add(header);
    }

    public void addData(String trace)
    {
        this.data.add(trace);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        data.forEach((line) -> builder.append(line + "\n"));
        return builder.toString();
    }

    public void end(String clientID)
    {
        RestAPI.getInstance().log(LogLevel.ERROR, clientID, toString());
    }
}
