package net.samagames.hydroangeas.client.remote;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
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

    public RemoteControl(String host, int port)
    {
        try {
            JMXServiceURL url =
                    new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
            jmxConnector = JMXConnectorFactory.connect(url);

            mBeanServer = jmxConnector.getMBeanServerConnection();
            Set<ObjectInstance> objectInstances = mBeanServer.queryMBeans(null, null);
            for (ObjectInstance object : objectInstances)
            {
                try {
                    MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(object.getObjectName());
                    RemoteService remoteService = new RemoteService(object.getObjectName(), mBeanInfo);
                    services.put(mBeanInfo.getClassName(), remoteService);
                } catch (InstanceNotFoundException
                        | ReflectionException
                        | IntrospectionException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RemoteService getService(String name)
    {
        return services.get(name);
    }

    public Collection<RemoteService> getServices()
    {
        return services.values();
    }

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

}
