package net.samagames.hydroangeas.client.remote;

import javax.management.MBeanInfo;
import javax.management.ObjectName;

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
public class RemoteService {

    private ObjectName objectName;
    private MBeanInfo mBeanInfo;
    private String name;

    public RemoteService(ObjectName objectName, MBeanInfo mBeanInfo)
    {
        this.objectName = objectName;
        setmBeanInfo(mBeanInfo);
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public void setObjectName(ObjectName objectName) {
        this.objectName = objectName;
    }

    public MBeanInfo getmBeanInfo() {
        return mBeanInfo;
    }

    public void setmBeanInfo(MBeanInfo mBeanInfo) {
        this.mBeanInfo = mBeanInfo;
        String[] split = mBeanInfo.getClassName().split("\\.");
        name = split[split.length-1];
    }

    public String getName() {
        return name;
    }
}
