package net.samagames.hydroangeas.client.remote;

import net.samagames.hydroangeas.Hydroangeas;

import javax.management.*;
import javax.management.relation.MBeanServerNotificationFilter;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 01/08/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class RemoteControl {

    private HashMap<String, RemoteService> services = new HashMap<>();

    private JMXConnector jmxConnector;
    private MBeanServerConnection mBeanServer;
    private RemoteListener remoteListener;

    private boolean isConnected = false;

    public RemoteControl(String host, int port)
    {
        remoteListener = new RemoteListener(this);
        new Thread(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {} //Wait for container start
            //Try to connect
            while (isConnected)
            {
                try {
                    Thread.sleep(1000);
                    JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
                    jmxConnector = JMXConnectorFactory.connect(url);
                    mBeanServer = jmxConnector.getMBeanServerConnection();
                    MBeanServerNotificationFilter filter = new MBeanServerNotificationFilter();
                    filter.enableAllObjectNames();
                    mBeanServer.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, remoteListener, filter, null);
                    isConnected = true;
                } catch (IOException | InstanceNotFoundException | InterruptedException e) {
                    Hydroangeas.getLogger().info("Cannot connect to " + host + ":" + port);
                }
            }
            loadAllService(); //First load, listener will handle after that
        }).start();
    }

    //Get all registered services
    private void loadAllService()
    {
        try {
            Set<ObjectInstance> objectInstances = mBeanServer.queryMBeans(null, null);
            for (ObjectInstance object : objectInstances)
            {
                addService(object.getObjectName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean addService(ObjectName name)
    {
        try {
            MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(name);
            RemoteService remoteService = new RemoteService(name, mBeanInfo);
            services.put(remoteService.getName(), remoteService);
        } catch (InstanceNotFoundException | ReflectionException | IntrospectionException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    boolean removeService(ObjectName name)
    {
        Iterator<RemoteService> values = services.values().iterator();
        while (values.hasNext())
        {
            RemoteService next = values.next();
            if (next.getObjectName().equals(name))
            {
                values.remove();
                break;
            }
        }
        return true;
    }

    public void removeService(String name)
    {
        services.remove(name);
    }

    public RemoteService getService(String name)
    {
        return services.get(name);
    }

    public Collection<RemoteService> getServices()
    {
        return services.values();
    }

    //TODO create custom exception
    public Object invokeService(RemoteService remoteService, String operation, Object[] args, String[] signatures)
            throws ReflectionException, MBeanException, InstanceNotFoundException, IOException {
        return mBeanServer.invoke(remoteService.getObjectName(), operation, args, signatures);
    }

    public boolean disconnect()
    {
        try {
            jmxConnector.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
