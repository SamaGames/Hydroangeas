package net.samagames.hydroangeas.client.remote;

import net.samagames.hydroangeas.Hydroangeas;

import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;

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
