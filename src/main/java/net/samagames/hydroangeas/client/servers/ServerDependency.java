package net.samagames.hydroangeas.client.servers;

public class ServerDependency
{
    private final String name;
    private final String version;

    public ServerDependency(String name, String version)
    {
        this.name = name;
        this.version = version;
    }

    public String getName()
    {
        return this.name;
    }

    public String getVersion()
    {
        return this.version;
    }
}
