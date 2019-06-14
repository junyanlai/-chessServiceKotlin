package com.shine.controller.poker.maj.old;

import java.io.Serializable;

public class IServerUserItem implements Serializable {

	private static final long serialVersionUID = 1L;

	public IServerUserItem() {
		Init();
	}

	public int wChairID; // 椅子位置
	public int cbUserStatus; // 用户状态
	public int isAndroidUser;// 属性信息

	public int dwFaceID; // 头像索引
	public int cbVipLevel; // 会员等级
	public int cbGender; // 用户性别
	public int cbOnline; // 是否在线
	public int dwUserID; // 用户 I D
	public int dwGroupID; // 社团索引
	public int lUserScore; // 用户分数
	public String szNickName = ""; // 用户昵称
	public String szIP;
	public int lUserCoin; // 用户金币
	public int heartTime; // 用户心跳时间

	void SetUserStatus(int bUserStatus) {
		cbUserStatus = bUserStatus;
	}

	public void Init() {
		dwUserID = 0;
		dwGroupID = 0;
		lUserScore = 0;
		dwFaceID = 0;
		cbVipLevel = 0;
		cbGender = 0;
		cbOnline = 0;

		wChairID = 0;
		cbUserStatus = 0;
		isAndroidUser = 0;
		szNickName = "";
		szIP = "";
		lUserCoin = 0;

		heartTime = 0;
	}

	int GetUserScore() {
		return lUserScore;
	}

	public String GetUserInfo() {
		return String.format("%d#%s", dwUserID, szNickName);
	}

	@Override
	public String toString() {
		return "IServerUserItem{" +
				"wChairID椅子位置=" + wChairID +
				", cbUserStatus用户状态=" + cbUserStatus +
				", isAndroidUser属性信息=" + isAndroidUser +
				", dwFaceID=头像索引" + dwFaceID +
				", cbVipLevel会员等级=" + cbVipLevel +
				", cbGender用户性别=" + cbGender +
				", cbOnline是否在线=" + cbOnline +
				", dwUserID=用户 I D" + dwUserID +
				", dwGroupID= 社团索引" + dwGroupID +
				", lUserScore=用户分数" + lUserScore +
				", szNickName=用户昵称'" + szNickName + '\'' +
				", szIP='" + szIP + '\'' +
				", lUserCoin用户金币=" + lUserCoin +
				", heartTime=用户心跳时间" + heartTime +
				'}';
	}
}
