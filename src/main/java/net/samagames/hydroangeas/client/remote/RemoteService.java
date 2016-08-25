package net.samagames.hydroangeas.client.remote;

import javax.management.MBeanInfo;
import javax.management.ObjectName;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 05/08/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class RemoteService {

    private ObjectName objectName;
    private MBeanInfo mBeanInfo;

    public RemoteService(ObjectName objectName, MBeanInfo mBeanInfo)
    {
        this.objectName = objectName;
        this.mBeanInfo = mBeanInfo;
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
    }
}
