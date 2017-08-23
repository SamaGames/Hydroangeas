package net.samagames.hydroangeas.common.protocol.network;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.util.UUID;

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
public class ServiceRequest extends AbstractPacket {

    private UUID reqId;

    private String target;

    private String name;

    private String operation;

    private Object[] arguments;

    private String[] signature;

    public ServiceRequest(UUID reqId, String target, String name, String operation, Object[] arguments) {
        this.reqId = reqId;
        this.target = target;
        this.name = name;
        this.operation = operation;
        this.arguments = arguments;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String[] getSignature() {
        return signature;
    }

    public void setSignature(String[] signature) {
        this.signature = signature;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public UUID getReqId() {
        return reqId;
    }

    public void setReqId(UUID reqId) {
        this.reqId = reqId;
    }
}
