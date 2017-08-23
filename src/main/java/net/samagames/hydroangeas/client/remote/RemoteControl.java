package net.samagames.hydroangeas.client.remote;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;

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

/*
 * This file is part of Hydroangeas.
 *
 * Hydroangeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hydroangeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hydroangeas.  If not, see <http://www.gnu.org/licenses/>.
 */
public class RemoteControl {

    private HashMap<String, RemoteService> services = new HashMap<>();

    private JMXConnector jmxConnector;
    private MBeanServerConnection mBeanServer;
    private RemoteListener remoteListener;

    private boolean isConnected = false;

    public RemoteControl(MinecraftServerC serverC, String host, int port)
    {
        remoteListener = new RemoteListener(this);
        new Thread(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {} //Wait for container start
            int i = 0;
            while (!isConnected)//Try to connect
            {
                if(i > 5)
                {
                    Hydroangeas.getLogger().info("Failed to connect at RMI shutdown: " + serverC.getServerName());
                    serverC.stopServer();
                    return;
                }

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
                i++;
            }
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {} //Wait for container start
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
        }
        return false;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
