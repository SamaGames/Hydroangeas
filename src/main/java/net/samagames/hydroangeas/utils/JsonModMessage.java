package net.samagames.hydroangeas.utils;

public class JsonModMessage
{
    protected String sender;
    protected ChatColor senderPrefix;
    protected String message;

    public JsonModMessage(String sender, ChatColor senderPrefix, String message)
    {
        this.sender = sender;
        this.senderPrefix = senderPrefix;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public ChatColor getSenderPrefix() {
        return senderPrefix;
    }

    public void setSenderPrefix(ChatColor senderPrefix) {
        this.senderPrefix = senderPrefix;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}