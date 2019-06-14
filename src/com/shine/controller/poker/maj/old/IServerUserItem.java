package com.shine.controller.poker.maj.old;

import java.io.Serializable;

public class IServerUserItem implements Serializable {

	private static final long serialVersionUID = 1L;

	public IServerUserItem() {
		Init();
	}

	public int wChairID; // ����λ��
	public int cbUserStatus; // �û�״̬
	public int isAndroidUser;// ������Ϣ

	public int dwFaceID; // ͷ������
	public int cbVipLevel; // ��Ա�ȼ�
	public int cbGender; // �û��Ա�
	public int cbOnline; // �Ƿ�����
	public int dwUserID; // �û� I D
	public int dwGroupID; // ��������
	public int lUserScore; // �û�����
	public String szNickName = ""; // �û��ǳ�
	public String szIP;
	public int lUserCoin; // �û����
	public int heartTime; // �û�����ʱ��

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
				"wChairID����λ��=" + wChairID +
				", cbUserStatus�û�״̬=" + cbUserStatus +
				", isAndroidUser������Ϣ=" + isAndroidUser +
				", dwFaceID=ͷ������" + dwFaceID +
				", cbVipLevel��Ա�ȼ�=" + cbVipLevel +
				", cbGender�û��Ա�=" + cbGender +
				", cbOnline�Ƿ�����=" + cbOnline +
				", dwUserID=�û� I D" + dwUserID +
				", dwGroupID= ��������" + dwGroupID +
				", lUserScore=�û�����" + lUserScore +
				", szNickName=�û��ǳ�'" + szNickName + '\'' +
				", szIP='" + szIP + '\'' +
				", lUserCoin�û����=" + lUserCoin +
				", heartTime=�û�����ʱ��" + heartTime +
				'}';
	}
}
