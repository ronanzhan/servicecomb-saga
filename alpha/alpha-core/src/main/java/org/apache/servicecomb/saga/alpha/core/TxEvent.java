package org.apache.servicecomb.saga.alpha.core;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.Date;

public class TxEvent {
    public static final long MAX_TIMESTAMP = 253402214400000L; // 9999-12-31 00:00:00

    private Long id;
    private Date createTime;
    private Date modifyTime;
    private String serviceName;
    private String instanceId;
    private String globalTxId;
    private String localTxId;
    private String parentTxId;
    private String type;
    private String compensationMethod;
    private Date expiryTime;
    private String retryMethod;
    private int retries;
    private byte[] payloads;

    public TxEvent() {
    }

    public TxEvent(TxEvent txEvent) {
        id(txEvent.getId())
                .serviceName(txEvent.getServiceName())
                .instanceId(txEvent.getInstanceId())
                .createTime(txEvent.getCreateTime())
                .globalTxId(txEvent.getGlobalTxId())
                .localTxId(txEvent.getLocalTxId())
                .parentTxId(txEvent.getParentTxId())
                .type(txEvent.getType())
                .compensationMethod(txEvent.getCompensationMethod())
                .expiryTime(txEvent.getExpiryTime())
                .retryMethod(txEvent.getRetryMethod())
                .retries(txEvent.getRetries())
                .payloads(txEvent.getPayloads())
                ;
    }

    public TxEvent id(long id) {
        this.id = id;
        return this;
    }

    public TxEvent serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public TxEvent instanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public TxEvent createTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public TxEvent modifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
        return this;
    }

    public TxEvent globalTxId(String globalTxId) {
        this.globalTxId = globalTxId;
        return this;
    }

    public TxEvent localTxId(String localTxId) {
        this.localTxId = localTxId;
        return this;
    }

    public TxEvent parentTxId(String parentTxId) {
        this.parentTxId = parentTxId;
        return this;
    }

    public TxEvent type(String type) {
        this.type = type;
        return this;
    }

    public TxEvent compensationMethod(String compensationMethod) {
        this.compensationMethod = compensationMethod;
        return this;
    }

    public TxEvent expiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
        return this;
    }

    public TxEvent retryMethod(String retryMethod) {
        this.retryMethod = retryMethod;
        return this;
    }

    public TxEvent retries(int retries) {
        this.retries = retries;
        return this;
    }

    public TxEvent payloads(byte[] payloads) {
        this.payloads = payloads;
        return this;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getGlobalTxId() {
        return globalTxId;
    }

    public void setGlobalTxId(String globalTxId) {
        this.globalTxId = globalTxId;
    }

    public String getLocalTxId() {
        return localTxId;
    }

    public void setLocalTxId(String localTxId) {
        this.localTxId = localTxId;
    }

    public String getParentTxId() {
        return parentTxId;
    }

    public void setParentTxId(String parentTxId) {
        this.parentTxId = parentTxId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompensationMethod() {
        return compensationMethod;
    }

    public void setCompensationMethod(String compensationMethod) {
        this.compensationMethod = compensationMethod;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getRetryMethod() {
        return retryMethod;
    }

    public void setRetryMethod(String retryMethod) {
        this.retryMethod = retryMethod;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public byte[] getPayloads() {
        return payloads;
    }

    public void setPayloads(byte[] payloads) {
        this.payloads = payloads;
    }



    @Override
    public String toString() {
        return "TxEvent{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", globalTxId='" + globalTxId + '\'' +
                ", localTxId='" + localTxId + '\'' +
                ", parentTxId='" + parentTxId + '\'' +
                ", type=" + type +
                ", compensationMethod='" + compensationMethod + '\'' +
                ", expiryTime=" + expiryTime +
                ", retryMethod='" + retryMethod + '\'' +
                ", retries=" + retries +
                '}';
    }
}
