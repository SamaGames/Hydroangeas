package net.samagames.hydroangeas.client.remote;

import net.samagames.hydroangeas.Hydroangeas;

import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 28/08/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
class RemoteListener implements NotificationListener {

    private RemoteControl remoteControl;

    RemoteListener(RemoteControl remoteControl)
    {
        this.remoteControl = remoteControl;
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        MBeanServerNotification mbs = (MBeanServerNotification) notification;
        if(MBeanServerNotification.REGISTRATION_NOTIFICATION.equals(mbs.getType())) {
            remoteControl.addService(mbs.getMBeanName());
            Hydroangeas.getLogger().info("New service: " + mbs.getMBeanName().getCanonicalName());
        } else if(MBeanServerNotification.UNREGISTRATION_NOTIFICATION.equals(mbs.getType())) {
            remoteControl.removeService(mbs.getMBeanName());
        }
    }
}
