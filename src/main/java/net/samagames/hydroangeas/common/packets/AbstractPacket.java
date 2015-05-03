package net.samagames.hydroangeas.common.packets;

import com.google.gson.Gson;
import net.samagames.hydroangeas.Hydroangeas;

public abstract class AbstractPacket
{
    private final Runnable callback;

    public AbstractPacket(Runnable callback)
    {
        this.callback = callback;
    }

    public AbstractPacket()
    {
        this.callback = null;
    }

    public abstract String getChannel();

    public void send()
    {
        Hydroangeas.getInstance().getRedisSubscriber().send(this);
    }

    public void callback()
    {
        try
        {
            if (this.callback != null)
                this.callback.run();
        }
        catch (Exception ignored) {}
    }

    public String getData()
    {
        return new Gson().toJson(this);
    }
}