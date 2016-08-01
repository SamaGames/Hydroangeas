package net.samagames.hydroangeas.client.resources;

public class ServerDependency
{
    private String name;
    private String version;
    private String type;
    private String ext;

    public String getName()
    {
        return this.name;
    }

    public String getVersion()
    {
        return this.version;
    }


    public String getType()
    {
        if (type == null)
            return "plugin";
        return type;
    }

    public String getExt()
    {
        if (ext == null)
            return "jar";
        return ext;
    }

    public boolean isExtractable()
    {
        return ext != null && !ext.equals("jar");
    }
}
