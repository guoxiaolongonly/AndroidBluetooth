package com.standard.bluetoothdemo.bean;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/4/21 13:18
 */

public class BaseMessageInfo {
    public static final int TYPE_SEND = 1;
    public static final int TYPE_RECEIVE = 2;
    private String messageTime;
    private String messageContent;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;
    private int allPack;
    private int currentPack;
    private int messageType;

    public static int getTypeSend() {
        return TYPE_SEND;
    }

    public static int getTypeReceive() {
        return TYPE_RECEIVE;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void setAllPack(int allPack) {
        this.allPack = allPack;
    }

    public void setCurrentPack(int currentPack) {
        this.currentPack = currentPack;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public int getAllPack() {
        return allPack;
    }

    public int getCurrentPack() {
        return currentPack;
    }

    public int getMessageType() {
        return messageType;
    }


}
