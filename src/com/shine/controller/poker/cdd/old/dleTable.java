package com.shine.controller.poker.cdd.old;

import com.shine.controller.poker.maj.old.IServerUserItem;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class dleTable{

	long needDealTime;
	int m_dwTableID;

	boolean m_bGameStarted;
	boolean m_bTableStarted;

	int m_byRound;
	public int m_vecRoundScore[] = new int[5];

	GameLogic m_GameLogic = new GameLogic();
	String m_curStage;
	int m_bTableCardArray[][] = new int[5][13];

	IServerUserItem m_pIUserItem[] = new IServerUserItem[5];
	int random(int iMin, int iMax){
		Random ra =new Random();
		return ra.nextInt(iMax-iMin) + iMin;
	}


	//time out
	int dotime = 0;//do times()


	int GetNextSeat(int point){
		if (point==3) return 0;
		else return (point + 1);
	}
	int GetFollowedType(){
		int index = 0;
		for (int i = 0; i < tableList.size(); i++) {
			int[] cache = tableList.get(i);
			if (cache.length==1 && cache[0]==0)	continue;
			else {index=i;break;}
		}
		return m_GameLogic.Type(tableList.get(index), tableList.get(index).length);
	}

	int[] GetFollowedCard(){
		int index = 0;
		for (int i = 0; i < tableList.size(); i++) {
			int[] cache = tableList.get(i);
			if (cache.length==1 && cache[0]==0)	continue;
			else if (cache.length==1 && cache[0]!=0) return cache;
			else {index=i;return tableList.get(i);}
		}
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$:index"+index);
		return tableList.get(index);
	}

	boolean m_bCardJokerIn;//jokerStatus
	boolean switchdone;
	int roundConunt=0;//round Cache
	public String m_commandCache[];//commamdCache
	int point;
	int[] switcount = new int[]{-1,-1,-1,-1};
	int firstcalluid;
	List<Integer> switcharr = new LinkedList<Integer>();
	List<int[]> switchsrc = new LinkedList<int[]>();
	{
		switchsrc.add(new int[]{0});
		switchsrc.add(new int[]{0});
		switchsrc.add(new int[]{0});
		switchsrc.add(new int[]{0});
	}
	LinkedList<int []> tableList=new LinkedList();//cardCache
	int getLastCount(){
		int result = 0;
		for (int i = 0; i < 4; i++)
			if (switcount[i]==-1)
				result += 1;
		return result;
	}//getLastCount
	int getFcallseat(){
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 13; j++)
				if (m_bTableCardArray[i][j]==0x03)
					return i;
		return 4;
	}//getFirstCallSeat


	public int FindSeat(int _Uid) {

		for (int bySeatID = 0; bySeatID < 4; bySeatID++)
			if (m_pIUserItem[bySeatID].dwUserID == _Uid)
				return bySeatID;

		return Constants.INVALID_CHAIR;
	}

	void addTableList(int[] arrs){
		int l = arrs.length;
		int[] brrs = new int[l];
		System.arraycopy(arrs,0,brrs,0,l);
		for (int i = 0; i < l; i++) 
			brrs[i]=arrs[i];
		tableList.addFirst(brrs);
	}

	public boolean SendData(int wChairID, String sData) {

		if (wChairID == Constants.INVALID_CHAIR) {
			for (short i = 0; i < m_pIUserItem.length; i++)
				if (m_pIUserItem[i].dwUserID > 0)
					if (m_pIUserItem[i].isAndroidUser == 0)
						//dthall.getInstance().onSend(m_pIUserItem[i].dwUserID, sData);

			return true;
		} else {
			if (wChairID >= m_pIUserItem.length)
				System.out.println();
				//dthall.getInstance().onSend(wChairID, sData);
			else if (m_pIUserItem[wChairID].dwUserID > 0) {
				if (m_pIUserItem[wChairID].isAndroidUser == 0)
					//dthall.getInstance().onSend(m_pIUserItem[wChairID].dwUserID, sData);

				return true;
			}
		}

		return false;
	}

	public String GetErrMsg(String type,String reason){
		/*SUBCMD_S_Err cmd = new SUBCMD_S_Err();
		cmd.type = type;
		cmd.reason = reason;
		return cmd.getJsonData();*/
		return null;
	}

	void FaPai() {

		//状态值改变
		m_bTableStarted = true;
		m_bGameStarted = true;

		//牌局状态改变
		m_curStage = "STAGE_FAPAI";

		//不知道哪里用的
		needDealTime = 8;

		if (m_byRound == 0) {
			m_byRound = 1;
		}//回合信息改变
		int cbTableCardArray[] = new int[52];
		//混乱牌堆
		m_GameLogic.RandCardList(cbTableCardArray, 52); // 计算数组的个数
		for (int i = 0; i < 52; i++) {
			int x = i / 13;
			int y = i % 13;
			m_bTableCardArray[x][y] = cbTableCardArray[i];
		}

		//m_bTableCardArray[0][0] = 0x4E;
		/*m_bTableCardArray[0] = new int[]{0x01, 0x02, 0x03, 0x4E, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D};
		m_bTableCardArray[1] = new int[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D,};
		m_bTableCardArray[2] = new int[]{0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D,};
		m_bTableCardArray[3] = new int[]{0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D,};*/
		//如果加入鬼牌
		int joker_value = 0x00;
		if (m_bCardJokerIn) {
			int random = random(0, 52);
			if (random==3) random += 1;
			joker_value = m_bTableCardArray[random / 13][random % 13];
			m_bTableCardArray[random / 13][random % 13] = 0x4E;

			//测试
			//joker_value = 0x04;
			//m_bTableCardArray[random / 13][random % 13] = 0x4E;

			System.out.println("鬼牌：" + joker_value + "坐标x,y=" + random / 13 + "," + random % 13);
		}
		//如果加入门牌
		int men_value = 0x00;
		if (true) {
			int random = random(0, 52);
			men_value = m_bTableCardArray[random / 13][random % 13];
			m_bTableCardArray[random / 13][random % 13] = 0x4E;

			System.out.println("门牌：" + men_value);
		}

		System.out.println("FAPAI : " + m_dwTableID);


		//传回消息
		/*SUBCMD_S_FaPai cmd = new SUBCMD_S_FaPai();
		cmd.haveJoker = m_bCardJokerIn ? 1 : 0;
		cmd.jokerValue = m_GameLogic.Replace(joker_value);
		cmd.menValue = m_GameLogic.Replace(men_value);*/

		//原为用户信息的
		for (int i = 0; i < 4; i++) {
			m_vecRoundScore[i] = 0;

			if (m_pIUserItem[i].dwUserID > 0 && (m_pIUserItem[i].cbUserStatus == Constants.US_READY || m_pIUserItem[i].cbUserStatus == Constants.US_PLAY)) {
				m_pIUserItem[i].cbUserStatus = Constants.US_PLAY;
				//cmd.vecIsReady[i] = 1;
			} else {
				m_pIUserItem[i].cbUserStatus = Constants.US_NULL;
				//cmd.vecIsReady[i] = 0;
			}
		}

		int seat = m_GameLogic.card3Seat(m_bTableCardArray);
		firstcalluid = m_pIUserItem[seat].dwUserID;
		point = seat;
		for (int i = 0; i < 4; i++) {
			if (m_pIUserItem[i].dwUserID > 0 && m_pIUserItem[i].cbUserStatus == Constants.US_PLAY) {
				for (int k = 0; k < 13; k++) {
					//cmd.byPai[k] = m_GameLogic.Replace(m_bTableCardArray[i][k]);//替换草花和方块replace方法
					//cmd.firstCallUser = m_pIUserItem[seat].dwUserID;
				}

				//发送牌组到每个用户手里
				//SendData(m_pIUserItem[i].dwUserID, cmd.getJsonData());
			}
			System.out.println("原牌堆:" + Arrays.toString(m_bTableCardArray[i]));
		}
	}

	void OnEventGameStart() {

		FaPai();
		for (int i = 0; i < 4; i++)
			if (m_pIUserItem[i].dwUserID > 0)
				m_pIUserItem[i].cbUserStatus = Constants.US_PLAY;

		m_bTableStarted = true;
		m_bGameStarted = true;
		TimeOut_SwitchPass();
	}

	void Handle_getStatus(int _Uid, String vecArg[]){

		int seat = FindSeat(_Uid);
		int[] lastcount = new int[]{
				m_GameLogic.getLastCount(m_bTableCardArray[0]),
				m_GameLogic.getLastCount(m_bTableCardArray[1]),
				m_GameLogic.getLastCount(m_bTableCardArray[2]),
				m_GameLogic.getLastCount(m_bTableCardArray[3])
		};

/*
		SUBCMD_S_Status cmd = new SUBCMD_S_Status();
		cmd.nextseat = GetNextSeat(point);
		cmd.vecCount = lastcount;
		System.arraycopy(m_bTableCardArray[seat],0,cmd.vecCard,0,13);

		for (int i = 0; i < 13; i++)
			cmd.vecCard[i] = m_GameLogic.Replace(cmd.vecCard[i]);

		//int size = tableList.size();
		if (tableList.size()>0)
			for (int[] m:tableList)
				cmd.tablecard.add(m);
		SendData(_Uid, cmd.getJsonData());
*/
	}

	void Handle_onReady(int _Uid, String vecArg[]) {

		if (m_bGameStarted == true)
			return;

		int bySeatID = FindSeat(_Uid);
		if (bySeatID < 4)
			m_pIUserItem[bySeatID].cbUserStatus = Constants.US_READY;

		int cntReady = 0;
		for (int i = 0; i < 4; i++)
			if (m_pIUserItem[i].cbUserStatus == Constants.US_READY)
				cntReady++;

		System.out.println("准备人数："+cntReady);
		cntReady = 0;

		for (int i = 0; i < 4; i++)
			if (m_pIUserItem[i].cbUserStatus == Constants.US_READY)
				cntReady++;

		//返回准备好的消息
/*
		SUBCMD_S_Ready cmd = new SUBCMD_S_Ready();
		cmd.m_lUid = _Uid;
		cmd.m_wUserChairID = bySeatID;
		cmd.m_dwTableID = m_dwTableID;
		SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());
*/

		if (cntReady == 4)
			OnEventGameStart();

	}


	void Handle_onCall(int _Uid, String vecArg[]){

		int seat = FindSeat(_Uid);
		//cardData
		String Pai[] = vecArg[1].split("\\,");
		System.out.println("##___firstcall____##:"+Pai[0]);
		int dwHandCardCount=Pai.length;
		int dwHandCard[] = new int[dwHandCardCount];
		for (int i = 0; i < dwHandCardCount; i++) {
			dwHandCard[i] = Integer.parseInt(Pai[i]);
			//System.out.println("######_mgcall");
			//System.out.println("######_mgcall_bf:"+dwHandCard[i]);
			dwHandCard[i] = m_GameLogic.Replace(dwHandCard[i]);//replace
			//System.out.println("######_mgcall_af:"+dwHandCard[i]);
		}

		//isDone
		if (m_GameLogic.isDone(m_commandCache)){
			if (switchdone) {//
				//have 0x03
				if (vecArg[0].equals("firstcall")) {//System.out.println(1);
					if (seat == point) {
						if (m_GameLogic.isFirstCall(dwHandCard) && tableList.size() == 0) {                                        //System.out.println(2);


							//handCard belongs to userCard
							if (m_GameLogic.GetCardOnLow(m_bTableCardArray[seat], dwHandCard)) {                                    //System.out.println(3);
								//getCardType
								if (m_GameLogic.Type(dwHandCard, dwHandCardCount) != 0) {                                    //System.out.println(4);
									//clear
									m_GameLogic.ClearCardSend(m_bTableCardArray[seat], dwHandCard);//System.out.println("type"+m_GameLogic.Type(dwHandCard,dwHandCardCount));
									point = seat;
									System.out.println("############### point 指向(had call)) seat="+point);
									//add to cache
									m_commandCache[seat] = "call";
									addTableList(dwHandCard);
									dotime++;//-

									//send to all
/*
									SUBCMD_S_Call cmd = new SUBCMD_S_Call();
									cmd.strPai = vecArg[1];
									cmd.seatId = _Uid;
									cmd.nextUser = m_pIUserItem[m_GameLogic.circle(seat)].dwUserID;
									SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());
*/
									TimeOut_Pass(seat);
									//if end
									if (m_GameLogic.isEnd(m_bTableCardArray[seat])) {
/*
										SUBCMD_S_End cmdEnd = new SUBCMD_S_End();
										cmdEnd.winnerseat = seat;
										//getScores
										SendData(CardTypeDzz.INVALID_CHAIR, cmdEnd.getJsonData());
										FinalCount(_Uid);
										End();
*/
									}
								}// else dthall.getInstance().onSend(_Uid, GetErrMsg("call", "wrong cardType"));
							}// else dthall.getInstance().onSend(_Uid, GetErrMsg("call", "illegal cards"));
						}// else dthall.getInstance().onSend(_Uid, GetErrMsg("call", "no smallest card"));
					}// else dthall.getInstance().onSend(_Uid, GetErrMsg("call", "not your turn"));
				}//no 0x03
				else {
					if (seat == GetNextSeat(point)) {
						//handCard belongs to userCard
						if (m_GameLogic.GetCardOnLow(m_bTableCardArray[seat], dwHandCard)) {                                        //System.out.println(3);

							//getCardType
							if (m_GameLogic.Type(dwHandCard, dwHandCardCount) != 0) {                                        //System.out.println(4);
								//clear
								m_GameLogic.ClearCardSend(m_bTableCardArray[seat], dwHandCard);
								point = seat;
								System.out.println("############### point 指向(had call)) seat="+point);
								//add to cache
								m_commandCache[seat] = "call";
								addTableList(dwHandCard);
								dotime++;//-
								//send to all
/*
								SUBCMD_S_Call cmd = new SUBCMD_S_Call();
								cmd.strPai = vecArg[1];
								cmd.seatId = _Uid;
								cmd.nextUser = m_pIUserItem[m_GameLogic.circle(seat)].dwUserID;
								SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());
*/
								TimeOut_Pass(seat);
								//if end
								if (m_GameLogic.isEnd(m_bTableCardArray[seat])) {
/*
									SUBCMD_S_End cmdEnd = new SUBCMD_S_End();
									cmdEnd.winnerseat = seat;
									//getScores
									SendData(CardTypeDzz.INVALID_CHAIR, cmdEnd.getJsonData());
									FinalCount(_Uid);
									End();
*/
								}
							}// else dthall.getInstance().onSend(_Uid, GetErrMsg("call", "wrong cardType"));
						}// else dthall.getInstance().onSend(_Uid, GetErrMsg("call", "illegal cards"));
					}// else dthall.getInstance().onSend(_Uid, GetErrMsg("call", "not your turn"));
				}
			}else SendData(_Uid,GetErrMsg("switchLog","please do switchLog"));
		}//else dthall.getInstance().onSend(_Uid, GetErrMsg("call","last round not done"));

	}

	void Handle_onFollow(int _Uid, String vecArg[]){

		int seat = FindSeat(_Uid);
		//cardData
		String Pai[] = vecArg[1].split("\\,");
		int dwHandCardCount=Pai.length;
		int dwHandCard[] = new int[dwHandCardCount];
		for (int i = 0; i < dwHandCardCount; i++) {
			dwHandCard[i] = Integer.parseInt(Pai[i]);
			//System.out.println("######_mgfall");
			//System.out.println("######_mgfall_bf:"+dwHandCard[i]);
			dwHandCard[i] = m_GameLogic.Replace(dwHandCard[i]);//replace
			//System.out.println("######_mgfall_af:"+dwHandCard[i]);
		}

		if (seat==GetNextSeat(point)) {
			if (switchdone){
		//handCard belongs to userCard
		if (m_GameLogic.GetCardOnLow( m_bTableCardArray[seat],dwHandCard)){

			//getCardType is follow
			if (m_GameLogic.Type(dwHandCard,dwHandCardCount) == GetFollowedType()){

				//heightValue
				//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$:GetFollowedCard_value  "+GetFollowedCard()[0]);
				//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$:tablelist  "+tableList.toString());
				/*for (int i = 0; i < tableList.size(); i++) {
					System.out.println(Arrays.toString(tableList.get(i)));
				}*/
				if (m_GameLogic.SLDone(GetFollowedCard(),dwHandCard)){

					//clear
					m_GameLogic.ClearCardSend(m_bTableCardArray[seat],dwHandCard);
					m_GameLogic.commandClean(m_commandCache);
					point=seat;
					//add to cache
					m_commandCache[seat] = "follow";
					addTableList(dwHandCard);
					dotime++;//-
					//send to all
/*
					SUBCMD_S_Follow cmd = new SUBCMD_S_Follow();
					cmd.strPai = vecArg[1];
					cmd.seatId = _Uid;
					cmd.nextUser=m_pIUserItem[m_GameLogic.circle(seat)].dwUserID;
					SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());
*/
					TimeOut_Pass(seat);
					//if end
					if (m_GameLogic.isEnd( m_bTableCardArray[seat])){
/*
						SUBCMD_S_End cmdEnd = new SUBCMD_S_End();
						cmdEnd.winnerseat = seat;
						//getScores
						SendData(CardTypeDzz.INVALID_CHAIR, cmdEnd.getJsonData());
						FinalCount(_Uid);
						End();
*/
					}
			}//else dthall.getInstance().onSend(_Uid, GetErrMsg("follow","noenough weight"));
		}//else dthall.getInstance().onSend(_Uid, GetErrMsg("follow","illegal card"));
		}//else dthall.getInstance().onSend(_Uid, GetErrMsg("follow","illegal cards"));
		}//else SendData(_Uid,GetErrMsg("switchLog","please do switchLog"));
		}//else dthall.getInstance().onSend(_Uid, GetErrMsg("follow","not your turn"));

	}

	void Handle_onBoom(int _Uid, String vecArg[]){

		int seat = FindSeat(_Uid);
		//cardData
		String Pai[] = vecArg[1].split("\\,");
		int dwHandCardCount=Pai.length;
		int dwHandCard[] = new int[dwHandCardCount];
		for (int i = 0; i < dwHandCardCount; i++) {
			dwHandCard[i] = Integer.parseInt(Pai[i]);
			dwHandCard[i] = m_GameLogic.Replace(dwHandCard[i]);//replace
		}

		if (seat==GetNextSeat(point)) {
			if (switchdone){
		//handCard belongs to userCard
		if (m_GameLogic.GetCardOnLow( m_bTableCardArray[seat],dwHandCard)){

			//getCardType
			if (m_GameLogic.Type(dwHandCard,dwHandCardCount) >= GetFollowedType() ){
				//hightValue
				if (	m_GameLogic.Type(dwHandCard,dwHandCardCount) > GetFollowedType()){

					point=seat;
					//clear
					m_GameLogic.ClearCardSend( m_bTableCardArray[seat],dwHandCard);
					m_GameLogic.commandClean(m_commandCache);
					//add to cache
					m_commandCache[seat] = "boom";
					addTableList(dwHandCard);
					dotime++;//-
					//send to all
					/*SUBCMD_S_Boom cmd = new SUBCMD_S_Boom();
					cmd.strPai = vecArg[1];
					cmd.seatId = _Uid;
					cmd.nextUser=m_pIUserItem[m_GameLogic.circle(seat)].dwUserID;
					SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());*/
					TimeOut_Pass(seat);
					//if end
					if (m_GameLogic.isEnd( m_bTableCardArray[seat])){
						/*SUBCMD_S_End cmdEnd = new SUBCMD_S_End();
						cmdEnd.winnerseat = seat;
						//getScores
						SendData(CardTypeDzz.INVALID_CHAIR, cmdEnd.getJsonData());
						FinalCount(_Uid);
						End();*/
					}
				} else if (m_GameLogic.Type(dwHandCard,dwHandCardCount) == GetFollowedType() &&
						m_GameLogic.Done(GetFollowedCard(),GetFollowedCard().length,dwHandCard,dwHandCardCount)){

					point=seat;
					//clear
					m_GameLogic.ClearCardSend( m_bTableCardArray[seat],dwHandCard);
					m_GameLogic.commandClean(m_commandCache);
					//add to cache
					m_commandCache[seat] = "boom";
					addTableList(dwHandCard);
					//send to all
					/*SUBCMD_S_Boom cmd = new SUBCMD_S_Boom();
					cmd.strPai = vecArg[1];
					cmd.seatId = _Uid;
					cmd.nextUser=m_pIUserItem[m_GameLogic.circle(seat)].dwUserID;
					SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());*/
					//if end
					if (m_GameLogic.isEnd( m_bTableCardArray[seat])){
						/*SUBCMD_S_End cmdEnd = new SUBCMD_S_End();
						cmdEnd.winnerseat = seat;
						//getScores
						SendData(CardTypeDzz.INVALID_CHAIR, cmdEnd.getJsonData());
						FinalCount(_Uid);
						End();*/
					}

				}// else dthall.getInstance().onSend(_Uid, GetErrMsg("boom","noenough weight"));




			}//else dthall.getInstance().onSend(_Uid, GetErrMsg("boom","illegal cards"));
		}//else dthall.getInstance().onSend(_Uid, GetErrMsg("boom","illegal cards"));
		}//else SendData(_Uid,GetErrMsg("switchLog","please do switchLog"));
		}//else dthall.getInstance().onSend(_Uid, GetErrMsg("boom","not your turn"));
	}

	void Handle_onPass(int _Uid){

		int seat = FindSeat(_Uid);

		if (seat==GetNextSeat(point)) {
			if (switchdone){
			if (m_GameLogic.wrongDone(m_commandCache)) {
				//dthall.getInstance().onSend(_Uid, GetErrMsg("pass", "pass enough"));
			}else {
				m_commandCache[seat] = "pass";//add to cache
				tableList.addFirst(new int[1]);
				point=seat;
				System.out.println("############### point 指向(had pass)) seat="+point);
				dotime++;//-
				//send to all
/*
				SUBCMD_S_Pass cmd = new SUBCMD_S_Pass();
				cmd.seatId = _Uid;
				cmd.nextUser = m_pIUserItem[m_GameLogic.circle(seat)].dwUserID;
				SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());
*/
			}

			if (m_GameLogic.passDone(m_commandCache)) {

				point=seat;
				//add to cache
				roundConunt += 1;
				/*int[] done = {0x00};
				tableList.addFirst(done);*/
				//send to all
/*
				SUBCMD_S_Done cmd = new SUBCMD_S_Done();
				cmd.doneRound = roundConunt;
				SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());
*/
				TimeOut_Call(seat);
			}else {
				TimeOut_Pass(seat);
			}

			}else SendData(_Uid,GetErrMsg("switchLog","please do switchLog"));
		}//else dthall.getInstance().onSend(_Uid, GetErrMsg("pass","not your turn"));
	}

	void Handle_onSwitch(int _Uid,String vecArg[]){//换牌

		int seat = FindSeat(_Uid);
		//System.out.println("####__"+switchsrc.size());

		if (!switchdone && (switcount[seat]==-1 || switcount[seat]==0)){

			//cardData
			String Pai[] = vecArg[1].split("\\,");
			int dwHandCardCount=Pai.length;
			int dwHandCard[] = new int[dwHandCardCount];
			for (int i = 0; i < dwHandCardCount; i++) {
				dwHandCard[i] = Integer.parseInt(Pai[i]);
				dwHandCard[i] = m_GameLogic.Replace(dwHandCard[i]);//replace
			}

			if (m_GameLogic.GetCardOnLow( m_bTableCardArray[seat],dwHandCard) && dwHandCardCount<4 && dwHandCardCount>0){

				//add to cache
				switcount[seat] = dwHandCardCount;
				switchsrc.set(seat, dwHandCard);
				for (int i = 0; i < dwHandCardCount; i++)
					switcharr.add(dwHandCard[i]);
				//sendData
/*
				SUBCMD_S_Switch cmd = new SUBCMD_S_Switch();
				cmd.seat = seat;
				cmd.switchcount = dwHandCardCount;
				cmd.lastcount = getLastCount();
				SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());
*/
				//return
				if (getLastCount()==0)
					switchReturn(switcount,switcharr,switchsrc,m_bTableCardArray);

			} else SendData(_Uid, GetErrMsg("switchLog","illegal cards"));
		} else SendData(_Uid, GetErrMsg("switchLog", "already switchdone"));
	}

	void Handle_onSwitchPass(int _Uid){//不换牌

		int seat = FindSeat(_Uid);

		if (!switchdone && (switcount[seat]==-1)){

			switcount[seat] = 0;//changeCount
			//sendData		successSend,switchcount,lastcount,
/*
			SUBCMD_S_Switch cmd = new SUBCMD_S_Switch();
			cmd.seat = seat;
			cmd.switchcount = 0;
			cmd.lastcount = getLastCount();
			SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());
*/
			//return
			if (getLastCount()==0)
				switchReturn(switcount,switcharr,switchsrc,m_bTableCardArray);



		} else SendData(_Uid, GetErrMsg("switchLog", "already switchdone"));
	}

	void switchReturn(int[] switchcount,List<Integer> switcharr,List<int[]> switchsrc, int[][]m_bTableCardArray){//还牌

		int count0 = 0;//number of player who passed the switchLog
		for (int i = 0; i < 4; i++)
			if (switcount[i]>0)
				count0 += 1;

		System.out.println(count0);
		int sum = 0;
		int[] counts = new int[5];//counts arr of list index
		int[][] returns = new int[4][3];//arrs of return
		for (int i = 0; i < 4; i++) {
			sum += switchcount[i];
			counts[i + 1] = sum;
		}

		if (count0==1){
			//senddata	status0:1
			/*SUBCMD_S_SwitchReturn cmd = new SUBCMD_S_SwitchReturn();
			for (int i = 0; i < 4; i++) {
				cmd.seat = i;
				cmd.status=0;
				cmd.switcharr = new int[3];
				cmd.fcallseat = m_pIUserItem[point].dwUserID;
				SendData(m_pIUserItem[i].dwUserID, cmd.getJsonData());
			}*/
			//add cache
			switchdone = true;
			TimeOut_FirstCall();

		}else {

			//randcard
			Collections.shuffle(switcharr);
			//add to arr
			for (int i = 0; i < 4; i++)
				if (switcount[i]!=0){
					int c = 0;
					for (int j = counts[i]; j < counts[i+1]; j++) {
						returns[i][c] = switcharr.get(j);
						c += 1;
					}
				} else continue;

			//switchLog handcard
			switchchange(switchsrc,returns,m_bTableCardArray,switchcount);
			//get first called player
			int fcallseat = getFcallseat();
			point = fcallseat;
			int firstCallUid = m_pIUserItem[fcallseat].dwUserID;
			//sendData
/*
			SUBCMD_S_SwitchReturn cmd = new SUBCMD_S_SwitchReturn();
			for (int i = 0; i < 4; i++) {
				cmd.seat = i;
				cmd.status=1;
				for (int j = 0; j < 3; j++) returns[i][j] = m_GameLogic.Replace(returns[i][j]);
				cmd.switcharr = returns[i];
				cmd.fcallseat = firstCallUid;
				SendData(m_pIUserItem[i].dwUserID, cmd.getJsonData());
			}
*/
			//add cache
			switchdone = true;
			TimeOut_FirstCall();
		}

	}

	void switchchange(List<int[]> switchsrc,int[][]resturns, int[][]m_bTableCardArray,int[] switchcount){

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 13; j++)
				for (int k = 0; k < switchcount[i]; k++)
					if (m_bTableCardArray[i][j]==switchsrc.get(i)[k])
						m_bTableCardArray[i][j] = 0x00;

		for (int i = 0; i < 4; i++)
			if (switchcount[i]!=0)
			for (int k = 0; k < switchcount[i]; k++)
				for (int j = 0; j < 13; j++)
					if (m_bTableCardArray[i][j]==0x00) {
						m_bTableCardArray[i][j] = resturns[i][k];
						break;
					}
	}

	//================================
	int roundtime = 5;
	int waittime = roundtime + 1;
	int switchdonetime = 10;
	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	//time out after do(call,follow,boom)
	void TimeOut_Pass(int seat){
		int dotimebefore = dotime;
		Runnable callable = new Runnable() {
			@Override
			public void run() {
				if (dotime == dotimebefore) {
					int uid=m_pIUserItem[m_GameLogic.circle(seat)].dwUserID;
					Handle_onPass(uid);
				}
			}
		};
		System.out.println("AI Pass After Do");
		service.schedule(callable, waittime, TimeUnit.SECONDS);
	}

	//time out after pass(if has 3 pass or <3pass)
	void TimeOut_Call(int seat){
		int dotimebefore = dotime;
		Runnable callable = new Runnable() {
			@Override
			public void run() {
				if (dotime == dotimebefore) {

					int nseat = m_GameLogic.circle(seat);
					int uid=m_pIUserItem[nseat].dwUserID;
					//do call--get the smallest card
					int callCard = m_GameLogic.getSmallestCard(m_bTableCardArray[nseat]);
					//16进制转10进制
					String cao = "" + callCard;
					int ca = Integer.parseInt(cao);

					int sendcard = m_GameLogic.Replace(ca);
					String callmessage[] = new String[]{"call", "" + sendcard};
					System.out.println("              call: "+sendcard);
					Handle_onCall(uid,callmessage);
				}
			}
		};
		System.out.println("AI Call After pass");
		service.schedule(callable, waittime, TimeUnit.SECONDS);
	}

	//time out after gamestart
	void TimeOut_SwitchPass(){
		Runnable callable = new Runnable() {
			@Override
			public void run() {

				if (switchdone)	return;
				for (int i = 0; i < 4; i++) {
					if (switcount[i]==-1){
						int uid=m_pIUserItem[i].dwUserID;
						Handle_onSwitchPass(uid);
					}
				}
			}
		};
		System.out.println("AI SwitchPass After GameSTART");
		service.schedule(callable, switchdonetime, TimeUnit.SECONDS);
	}

	//time out after switchdone
	void TimeOut_FirstCall(){
		Runnable callable = new Runnable() {
			@Override
			public void run() {
				if (!switchdone) return;
				String arrMsg[] = new String[]{"firstcall", "19" };
				Handle_onCall(firstcalluid,arrMsg);
			}
		};
		System.out.println("AI FirstCall After JiabeiDone");
		service.schedule(callable, waittime, TimeUnit.SECONDS);
	}



}
