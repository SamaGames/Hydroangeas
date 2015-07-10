package net.samagames.hydroangeas.utils;

public enum InstanceType
{
    CLIENT("Client"),
    SERVER("Server");

    private final String text;

    InstanceType(String text)
    {
        this.text = text;
    }

    public String toString()
    {
        return this.text;
    }
}