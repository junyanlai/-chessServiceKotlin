package com.shine.controller.poker.maj.old;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class majTable  {

	IServerUserItem m_pIUserItem[] = new IServerUserItem[5];
	GameLogic m_GameLogic = new GameLogic();

	boolean m_bGameStarted;
	boolean m_bTableStarted;
	String m_curStage;
	long needDealTime;
	int m_byRound;

	int m_bTableCardStart[][] = new int[4][24];//����
	int m_bTableCardArray[][] = new int[4][22];//����
	int m_bTableCardFlower[][] = new int[4][8];//����
	int m_bTableCardMount[];//��ɽ
	int m_bTableCardBox[][] = new int[4][40];//�����ƺд���
	int cardBoxIndex = 0;//�ƺ�����

	int m_bdCard = 0;//�������
	int m_bMountainSize = 0;
	int m_bMountainMin = 16;
	int m_seat = -1;
	int indexHand=-1;
	int nextseat = -1;
	int jiagangseat = -1;

	//���״̬	0-�в��� 1-�� 2-�� 3-�� 4-��
	boolean m_bTableCardStatus[][] = new boolean[4][5];
	int m_bCommandCache[] = new int[3];//�����
	String m_bCommandCaches[][] = new String[3][2];//�����
	int cIndex[];//���������±�  4��opNumber
	int ncount[] = new int[4];//OPnumber����
	int qgcount[] = new int[4];//���ܼ���
	String humiankinds[][] = new String[4][5];//����µص��Ƶ�����
	int angangs[][] = new int[4][5];//���еļӸ�
	int minggangs[][] = new int[4][5];//����
	int mocount = 0;//���ƴ���
	int mopercount[] = new int[4];//ÿ�������Ƽ���
	int tingstatus[][] = new int[2][4];//����״̬[ 0-δ�� 1-���� 2-���� 3-����]
	int GangCount=1;//���Ͽ������
	int HuaGangCount=1;//���ܱ��


	public String m_commandCache[];//commamdCache

	LinkedList<int []> tableList=new LinkedList();//cardCache

	int GetNextSeat(int seat){
		if (seat==3) return 0;
		return seat + 1;
	}

	public boolean SendData(int wChairID, String sData) {

		return false;
	}

	public String GetErrMsg(String type,String reason){ return "";}

	void addMianKind(int m_seat,String command,String humiankinds[][]) {

		for (int i = 0; i < 5; i++)
			if (humiankinds[m_seat][i] == null) {
				humiankinds[m_seat][i] = command;
				break;
			}
	}

	void addGangs(int m_seat, int gangCard, int gangs[][]){

		for (int i = 0; i < 5; i++)
			if (gangs[m_seat][i]==0x00){
				gangs[m_seat][i] = gangCard;
				break;
			}
	}

	void FaPai() {

		//״ֵ̬�ı�
		m_bTableStarted = true;
		m_bGameStarted = true;

		//�ƾ�״̬�ı�
		m_curStage = "STAGE_FAPAI";
		needDealTime = 8;

		//�غ���Ϣ�ı�
		if (m_byRound == 0) {
			m_byRound = 1;
		}

		m_bTableCardStart = m_GameLogic.startCard();//�õ����Һ������
		m_bTableCardMount = m_GameLogic.cardMount;//�õ���ɽ
		m_GameLogic.dealStart(m_bTableCardStart, m_bTableCardArray, m_bTableCardFlower);//�����Ƴ�ʼ�����������Ʒ�Ϊ���ƺͻ���
		System.out.println("FAPAI : " + 0);

		//������Ϣ
		/*SUBCMD_S_FaPai cmd = new SUBCMD_S_FaPai();
		cmd.point = feng.seat;
		nextseat = cmd.point;*/

		//ԭΪ�û���Ϣ��
		for (int i = 0; i < 4; i++) {
			//m_vecRoundScore[i] = 0;

			if (m_pIUserItem[i].dwUserID > 0 && (m_pIUserItem[i].cbUserStatus == 2 || m_pIUserItem[i].cbUserStatus == 2)) {
				m_pIUserItem[i].cbUserStatus = 2;
				//cmd.vecIsReady[i] = 1;
			} else {
				m_pIUserItem[i].cbUserStatus = 2;
				//cmd.vecIsReady[i] = 0;
			}
		}

		//��������
		for (int i = 0; i < 4; i++) {
			//cmd.vecFlowerCount[i] = m_GameLogic.IndexLive(m_bTableCardFlower[i], 8);
			//cmd.vecFlowers[i] = m_bTableCardFlower[i];
		}

		for (int i = 0; i < 4; i++) {
			if (m_pIUserItem[i].dwUserID > 0 && m_pIUserItem[i].cbUserStatus == 2) {

				//cmd.byPai = m_bTableCardStart[i];
				//�������鵽ÿ���û�����
				//SendData(m_pIUserItem[i].dwUserID, cmd.getJsonData());
			}
		}

		//�����ҷ���
		//OnDealMo(cmd.point);
		//m_bTableCardStatus[cmd.point][1]=m_GameLogic.xiaohuLegal(m_bTableCardArray[cmd.point]);
		//m_bTableCardStatus[cmd.point][0] = m_bTableCardStatus[cmd.point][1];
	}

	void OnEventGameStart() {

		FaPai();
		for (int i = 0; i < 5; i++) {
			if (m_pIUserItem[i].dwUserID > 0) {
				m_pIUserItem[i].cbUserStatus = 2;
			}
		}
		m_bTableStarted = true;
		m_bGameStarted = true;
	}

	void End(){

		service.shutdown();

		try {
			if (service.awaitTermination(1, TimeUnit.SECONDS))
				service.shutdownNow();
			else service.shutdownNow();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		m_bTableStarted = false;

	}




	//timer about
	int datime = 0;//da times
	int dotime = 0;//do times

	void OnDealCard(int _Uid, String vecArg[]) {

		int finalSeat;
		String command = vecArg[0];
		m_seat =0;

		//ѡ���status�仯
		for (int i = 1; i < 5; i++) m_bTableCardStatus[m_seat][i] = false;
		int commandStatusIndex = m_GameLogic.commandTurn2(command);
		if (commandStatusIndex<5) m_bTableCardStatus[m_seat][commandStatusIndex] = true;


		//���¼� �����ܺ�
		//#region
		if (cIndex[3]==1 && m_bTableCardStatus[nextseat][0]){

			if (m_seat==cIndex[0]){

				finalSeat = nextseat;

				if (command.equals("chi")) {
					Handle_onChi(_Uid, vecArg);
					OnJudgeClaear();
				}
				if (command.equals("peng")) {
					Handle_onPeng(_Uid, vecArg);

					OnJudgeClaear();
				}
				if (command.equals("gang")) {
					Handle_onGang(_Uid, vecArg);
					OnJudgeClaear();
				}
				if (command.equals("hu")) {
					Handle_onHu(_Uid, vecArg);

					OnJudgeClaear();
				}
				if (command.equals("pass")) {
					//send to cardBox
					cardBoxIndex = m_GameLogic.getFirstIndex(m_bTableCardBox[m_seat], 38);//getIndex
					m_bTableCardBox[m_seat][cardBoxIndex] = m_bdCard;//box[index]=daCard
					OnDealMo(finalSeat);
					//clear
					OnJudgeClaear();
				}
			}else SendData(_Uid, GetErrMsg("pass", "wrong pass"));
		}//#endregion
		else {

			//ÿ���������  ncount[m_seat]�������������
			for (int i = 0; i < 3; i++) {
				if (m_seat == cIndex[i] && m_bTableCardStatus[m_seat][0]) {//

					m_bCommandCache[i] = m_GameLogic.commandTurn(vecArg[0]);//command��ֵ
					m_bCommandCaches[i] = vecArg;//command��ֵ

					ncount[m_seat] = 1;//op����

					System.out.println("���"+m_seat+"��ѡ��"+vecArg[0]+" ��󹲵ȴ���"+cIndex[3]+" ���У�"+m_GameLogic.getSum(ncount)+"");
				}
			}

			//δȫ������Ľ��
			if (m_GameLogic.getSum(ncount) != cIndex[3]){//��Ӧλ�õ��˷����������������������hold
				int seat = 0;
				int maxcommand = 0;

				//�õ����Ȩ���ߵ���λ������
				outter:
				for (int j = 1; j < 5; j++) {
					for (int i = 0; i <3; i++) {
						if (m_bTableCardStatus[cIndex[i]][j]){

							seat=cIndex[i];maxcommand=j;
							break outter;
						}
					}
				}

				System.out.println("###��Χbreak֮��_________");
				if (m_seat==seat){//Ϊ�����������

					/*int i = m_GameLogic.getMaxI(m_bCommandCache);
					finalSeat = cIndex[i];
					nextseat = finalSeat;
					int uid = m_pIUserItem[finalSeat].dwUserID;*/

					if (!command.equals("pass")){
						System.out.println("###��ͻֱ�ӽ���");

						if (command.equals("chi") && maxcommand==4) {
							nextseat = m_seat;
							Handle_onChi(_Uid,vecArg);
						}
						if (command.equals("peng") && maxcommand==2) {
							nextseat = m_seat;
							Handle_onPeng(_Uid,vecArg);
						}
						if (command.equals("gang") && maxcommand==3) {
							nextseat = m_seat;
							Handle_onGang(_Uid,vecArg);
						}
						if (command.equals("hu") && maxcommand==1) {
							nextseat = m_seat;
							Handle_onHu(_Uid,vecArg);
						}
						OnJudgeClaear();
					}else {

						for (int j = 1; j <5 ; j++)
							m_bTableCardStatus[m_seat][j]=false;
						//������Ȩ�޵���ѡ����pass
						//Hold

					}
				}else if (command.equals("pass"))
						for (int j = 1; j <5 ; j++)
							m_bTableCardStatus[m_seat][j]=false;
							//hold
			}

			//�������֮��ִ��������֮��ᵼ�� m_GameLogic.getSum(ncount) == cIndex[3] ִ��
			//����  m_GameLogic.getSum(ncount)!=0  ������
			if (m_GameLogic.getSum(ncount)!=0 &&
				m_GameLogic.getSum(ncount) == cIndex[3]){

				//hold done and give result
				int i = m_GameLogic.getMaxI(m_bCommandCache);

				finalSeat = cIndex[i];
				nextseat = finalSeat;
				int uid = m_pIUserItem[finalSeat].dwUserID;

				System.out.println("***��ͻiii��i="+i+" uid="+uid+"***");
				System.out.println("***��ͻ���У�"+Arrays.toString(m_bCommandCache));
				System.out.println("***��ͻ�����cIndex"+Arrays.toString(cIndex)+"***");
				System.out.println("***��ͻ�����CommandCaches"+Arrays.toString(m_bCommandCaches[0])+Arrays.toString(m_bCommandCaches[1])+Arrays.toString(m_bCommandCaches[2])+"***");

				if (m_bCommandCaches[i][0] == null) m_bCommandCaches[i][0] = "pass";

				if (m_bCommandCaches[i][0].equals("chi"))
					Handle_onChi(uid,m_bCommandCaches[i]);

				if (m_bCommandCaches[i][0].equals("peng"))
					Handle_onPeng(uid,m_bCommandCaches[i]);

				if (m_bCommandCaches[i][0].equals("gang"))
					Handle_onGang(uid,m_bCommandCaches[i]);

				if (m_bCommandCaches[i][0].equals("hu"))
					Handle_onHu(uid,m_bCommandCaches[i]);

				if (m_bCommandCaches[i][0].equals("pass")) {

					cardBoxIndex = m_GameLogic.getFirstIndex(m_bTableCardBox[m_seat], 18);//getIndex
					m_bTableCardBox[m_seat][cardBoxIndex] = m_bdCard;//box[index]=daCard
					OnDealMo(finalSeat);
				}
				OnJudgeClaear();
			}
		}
	}

	void OnDealQiangGang(int _Uid, String vecArg[]){

		String command = vecArg[0];//just have 2 commands(qianggang and pass)
		m_seat = 0;

		if (cIndex[3]==0)	SendData(_Uid, GetErrMsg("qianggang", "can't qianggang"));//send Err

		if (m_bTableCardStatus[m_seat][0]) {//saps check[ must can be passs or qianggang ]

			//change status after do choice
			for (int i = 1; i < 5; i++) m_bTableCardStatus[m_seat][i] = false;
			if (command.equals("qianggang")) m_bTableCardStatus[m_seat][1] = true;
			if (command.equals("passs")) m_bTableCardStatus[m_seat][1] = false;

			if (cIndex[3] == 1) {                    //just one can qianggang or passs

				if (command.equals("qianggang"))
					Handle_onHu(_Uid, vecArg);
				if (command.equals("passs")) {
					OnDealMo(jiagangseat);
					OnJudgeClaear();
				}
			} else {//2 or 3 all can do qianggang

				//make choice
				for (int i = 0; i < 3; i++) {
					if (m_seat == cIndex[i] && m_bTableCardStatus[m_seat][0]) {
						m_bCommandCaches[i] = vecArg;
						ncount[m_seat] = 1;//option's counts
						qgcount[m_seat] = command.equals("qianggang") ? 1 : 0;
						System.out.println("### JIAGANG:" + jiagangseat + " |PLAYER_ " + m_seat + "_DO��" + vecArg[0] + " ���ȴ���" + cIndex[3] + " ���У�" + m_GameLogic.getSum(ncount) + "");
					}
				}

				if (m_GameLogic.getSum(ncount) != cIndex[3]) {

					int seat = 0;
					int maxcommand = 0;
					//get biggest command player's command and seat
					outter:
					for (int j = 1; j < 5; j++) {
						for (int i = 0; i < 3; i++) {
							if (m_bTableCardStatus[cIndex[i]][j]) {
								seat = cIndex[i];
								maxcommand = j;
								break outter;
							}
						}
					}

					//when biggestCommand player do choice
					if (m_seat == seat) {
						if (command.equals("qianggang") && maxcommand == 1) {//do qianggang
							nextseat = m_seat;
							Handle_onHu(_Uid, vecArg);
						} else if (command.equals("passs")) {//do pass
							for (int j = 1; j < 5; j++) m_bTableCardStatus[m_seat][j] = false;

							/*SUBCMD_S_Hold cmd = new SUBCMD_S_Hold();
							cmd.seatId = m_seat;
							cmd.holdcommand = command;
							cmd.holdNum = cIndex[3] - m_GameLogic.getSum(ncount);
							SendData(_Uid, cmd.getJsonData());*/
						}
					} else {//when not the biggester one
						if (command.equals("passs")) for (int j = 1; j < 5; j++) m_bTableCardStatus[m_seat][j] = false;

						/*SUBCMD_S_Hold cmd = new SUBCMD_S_Hold();
						cmd.seatId = m_seat;
						cmd.holdcommand = command;
						cmd.holdNum = cIndex[3] - m_GameLogic.getSum(ncount);
						SendData(_Uid, cmd.getJsonData());*/
					}

				} else if (m_GameLogic.getSum(ncount) == cIndex[3]) {

					if (m_GameLogic.getSum(qgcount) == 0) {
						OnDealMo(jiagangseat);
						OnJudgeClaear();
					} else {
						int finalplayer = -1;
						for (int i = 0; i < 3; i++) {
							if (qgcount[cIndex[i]] == 1) {
								finalplayer = cIndex[i];
								int uid = m_pIUserItem[finalplayer].dwUserID;
								Handle_onHu(uid, m_bCommandCaches[i]);//hu
							}
							break;
						}
					}
				}
			}
		}
	}

	void OnJudgeClaear(){
		m_bTableCardStatus = new boolean[4][5];
		m_bCommandCache = new int[3];//�����
		m_bCommandCaches = new String[3][2];//�����
		cIndex = new int[4];//���������±�  4��opNumber
		ncount = new int[4];//OPnumber����
		qgcount = new int[4];
	}

	void Handle_onReady(int _Uid, String vecArg[]) { }

	//��
	void OnDealMo(int targetSeat){

			int mCard = m_bTableCardMount[0];
			m_seat = targetSeat;
			indexHand = m_GameLogic.getFirstIndex(m_bTableCardArray[m_seat], 22);
			int indexFlower = m_GameLogic.getFirstIndex(m_bTableCardFlower[m_seat], 8);
			int indexMount = m_GameLogic.getFirstIndex(m_bTableCardMount, m_bTableCardMount.length);
			m_bMountainSize = indexMount-1;//mountain--

			// if Liuju
			if (m_bMountainSize==14){
				End();
				return;
			}

			//is Flower
			if (mCard>0x40) {

				m_bTableCardFlower[m_seat][indexFlower] = m_bTableCardMount[0];//give flower
				m_GameLogic.moChange(m_bTableCardMount,indexMount);//mountain change


				HuaGangCount=mocount;//���ܼ���
				//mo again
				OnDealMo(targetSeat);

			}//nomal card
			else {

				m_bTableCardArray[m_seat][indexHand] = m_bTableCardMount[0];//give to hand
				m_GameLogic.SortCardList(m_bTableCardArray[m_seat],indexHand+1);
				m_GameLogic.moChange(m_bTableCardMount,indexMount);//mountain change
				m_bdCard = 0x00;
				mocount += 1;
				mopercount[targetSeat] += 1;//counts very person mo's time

				//make timeout check
				TimeOut_Da(targetSeat,mCard);

				System.out.println(m_seat+"������������Ϊ:"+Arrays.toString(m_bTableCardArray[m_seat]));
				System.out.println("       "+GetNextSeat(m_seat)+"������:"+Arrays.toString(m_bTableCardArray[GetNextSeat(m_seat)]));
			}
	}

	//��
	void Handle_onDa(int _Uid,String vecArg[]){

		int dCard = Integer.parseInt(vecArg[1]);
		m_seat = 0;

		indexHand = m_GameLogic.getFirstIndex(m_bTableCardArray[m_seat], 22);

		if (m_seat==nextseat){

			//can da
			if (	(indexHand%3==2) &&
					m_GameLogic.daLegal(dCard,m_bTableCardArray[m_seat],indexHand)){

				//da and change
				m_GameLogic.daChange(dCard, m_bTableCardArray[m_seat]);//da and change
				datime++;

				//write to cache daCard
				m_bdCard = dCard;
				nextseat = GetNextSeat(m_seat);//ָ��

				//dealDa cache
				m_GameLogic.dealStatus(dCard,m_bTableCardArray,m_bTableCardStatus,m_seat);//deal 4players status

				//turns player seats
				cIndex = new int[]{
						nextseat,//player right
						GetNextSeat(nextseat),//player towards
						GetNextSeat(GetNextSeat(nextseat)),//playerleft
						m_GameLogic.getOpNum(m_seat,m_bTableCardStatus)};//deal player's counts

				System.out.println(m_seat+"�Ŵ��:__ "+dCard+"����:__("+cIndex[3]+")__   �¼ң�"+cIndex[0]+" �Լң�"+cIndex[1]+" �ϼң�"+cIndex[2]);
				for (int i = 0; i < 4; i++) {
					if (m_bTableCardStatus[i][0]){
						System.out.println(i+"�ſ��ԣ�"+" | "+(m_bTableCardStatus[i][1]?"��":"_")+" | "+(m_bTableCardStatus[i][2]?"��":"_")
								+" | "+(m_bTableCardStatus[i][3]?"��":"_")+" | "+(m_bTableCardStatus[i][4]?"��":"_"));
					}
				}

				if (cIndex[3]==0){

					//send to cardBox
					cardBoxIndex = m_GameLogic.getFirstIndex(m_bTableCardBox[m_seat], 38);//getIndex
					m_bTableCardBox[m_seat][cardBoxIndex] = dCard;//box[index]=daCard
					System.out.println("ֱ�ӷ��Ƹ�"+nextseat+"��");
					OnDealMo(nextseat);//give mo
					//clear
					OnJudgeClaear();
				}else{

					for (int i = 0; i < 4; i++)
						if (m_bTableCardStatus[i][0])
							TimeOut_Pa(i);
				}

			}else SendData(_Uid, GetErrMsg("da", "not your card"));
		} else SendData(_Uid, GetErrMsg("order", "not your order"));
	}

	//��
	void Handle_onChi(int _Uid,String vecArg[]){

		m_seat = 90;
		indexHand = m_GameLogic.getFirstIndex(m_bTableCardArray[m_seat], 22);
		//cardData
		String Chi[] = vecArg[1].split("\\,");
		int chiCard[] = new int[3];
		for (int i = 0; i < 3; i++) {
			chiCard[i] = Integer.parseInt(Chi[i]);
		}

		if (m_seat==nextseat){
			if (chiCard[0]==m_bdCard){

				//write to cache
				addMianKind(m_seat,"chi",humiankinds);
				m_GameLogic.chiChange(chiCard,m_bTableCardArray[m_seat]);
				dotime++;
				//send message
				/*SUBCMD_S_Chi cmd = new SUBCMD_S_Chi();
				cmd.seatId = m_seat;
				cmd.chiCard = vecArg[1];
				SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());*/

				System.out.println(m_seat+"�ų��ƺ�");
				System.out.println("     |���ƣ�"+Arrays.toString(m_bTableCardArray[m_seat])+" ����"+Arrays.toString(m_bTableCardFlower[m_seat]));

				//timeOut_doa
				TimeOut_Doa(m_seat);

			}else SendData(_Uid, GetErrMsg("chi card", "not this card"));
		} else SendData(_Uid, GetErrMsg("order", "not your order"));
	}

	//��
	void Handle_onPeng(int _Uid,String vecArg[]){

		int pCard = Integer.parseInt(vecArg[1]);
		m_seat = 0;

		indexHand = m_GameLogic.getFirstIndex(m_bTableCardArray[m_seat], 22);

		if (m_seat==nextseat){
			if (pCard==m_bdCard){

				//write to cache
				addMianKind(m_seat,"peng",humiankinds);
				m_GameLogic.pengChange(pCard,m_bTableCardArray[m_seat]);
				dotime++;
				//send message
				/*SUBCMD_S_Peng cmd = new SUBCMD_S_Peng();
				cmd.seatId = m_seat;
				cmd.pengCard = pCard;
				SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());*/

				System.out.println(m_seat+"�����ƺ�");
				System.out.println("     |���ƣ�"+Arrays.toString(m_bTableCardArray[m_seat])+" ����"+Arrays.toString(m_bTableCardFlower[m_seat]));

				//timeOut_doa
				TimeOut_Doa(m_seat);

			}else SendData(_Uid, GetErrMsg("peng card", "not this card"));
		} else SendData(_Uid, GetErrMsg("order", "not your order"));
	}

	//��
	void Handle_onGang(int _Uid,String vecArg[]){

		int gCard = Integer.parseInt(vecArg[1]);
		int m_seat = 0;

		indexHand = m_GameLogic.getFirstIndex(m_bTableCardArray[m_seat], 22);

		if (m_seat==nextseat){
			if (gCard==m_bdCard){

				//write to cache
				addMianKind(m_seat,"gang",humiankinds);//mark
				addGangs(m_seat,gCard,minggangs);
				m_GameLogic.gangChange(gCard,m_bTableCardArray[m_seat]);
				dotime++;
				//send message
				/*SUBCMD_S_Gang cmd = new SUBCMD_S_Gang();
				cmd.seatId = m_seat;
				cmd.gangCard = gCard;
				SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());*/

				System.out.println(m_seat+"�Ÿ��ƺ�");
				System.out.println("     |���ƣ�"+Arrays.toString(m_bTableCardArray[m_seat])+" ����"+Arrays.toString(m_bTableCardFlower[m_seat]));

				GangCount = mocount;//���Ͽ������Ƽ���
				OnDealMo(m_seat);
				m_bMountainMin -= 1;
			}else SendData(_Uid, GetErrMsg("gang Card", "not this card"));
		} else SendData(_Uid, GetErrMsg("order", "not your order"));
	}

	//��
	void Handle_onHu(int _Uid,String vecArg[]){

		m_seat = 0;
		indexHand = m_GameLogic.getFirstIndex(m_bTableCardArray[m_seat], 22);
		if (m_seat==nextseat){

			/*SUBCMD_S_Hu cmd = new SUBCMD_S_Hu();
			cmd.seatId = m_seat;

			SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());*/
			System.out.println(m_seat+"�ź��ƣ�");
			System.out.println("     |���ƣ�"+Arrays.toString(m_bTableCardArray[m_seat])+" ����"+Arrays.toString(m_bTableCardFlower[m_seat]));

			End();
		} else SendData(_Uid, GetErrMsg("order", "not your order"));
	}

	//�Ӹ�
	void Handle_onJiagang(int _Uid,String vecArg[]){

		int jgCard = Integer.parseInt(vecArg[1]);
		m_seat = 0;

		indexHand = m_GameLogic.getFirstIndex(m_bTableCardArray[m_seat], 22);

		if (m_seat==nextseat){
			if (m_GameLogic.jiagangLegal(jgCard,m_bTableCardArray[m_seat],indexHand)){

				addGangs(m_seat,jgCard,minggangs);//mark
				m_GameLogic.jiaGang(jgCard,m_bTableCardArray[m_seat]);

				/*SUBCMD_S_JiaGang cmd = new SUBCMD_S_JiaGang();
				cmd.seatId = m_seat;
				cmd.jiaGangCard = jgCard;

				SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());*/

				//�Ӹܺ���
				jiagangseat = m_seat;
				m_GameLogic.dealQianggang(jgCard,m_bTableCardArray,m_bTableCardStatus,m_seat);//deal qianggang status
				cIndex = new int[]{
						GetNextSeat(nextseat),
						GetNextSeat(GetNextSeat(nextseat)),
						GetNextSeat(GetNextSeat(GetNextSeat(nextseat))),
						m_GameLogic.getOpNum(m_seat, m_bTableCardStatus)};//deal player's counts

				System.out.println("###"+m_seat+"�żӸܣ�"+jgCard+"| _"+cIndex[3]+"_�˿�����");

				if (cIndex[3]==0) {
					GangCount = mocount;//���Ͽ������Ƽ���
					OnDealMo(m_seat);//mo
					m_bMountainMin -= 1;
				}
			}else SendData(_Uid, GetErrMsg("jiagang", "can't jiagang"));
		} else SendData(_Uid, GetErrMsg("order", "not your order"));
	}
	//�Ը�
	void Handle_onZigang(int _Uid,String vecArg[]){

		int zgCard = Integer.parseInt(vecArg[1]);
		m_seat = 0;

		indexHand = m_GameLogic.getFirstIndex(m_bTableCardArray[m_seat], 22);

		if (m_seat==nextseat){
			if (m_GameLogic.zigangLegal(zgCard,m_bTableCardArray[m_seat],indexHand)){

				addGangs(m_seat,zgCard, angangs);
				m_GameLogic.zigangChange(zgCard,m_bTableCardArray[m_seat]);

				/*SUBCMD_S_ZiGang cmd = new SUBCMD_S_ZiGang();
				cmd.seatId = m_seat;
				cmd.ziGangCard = zgCard;

				SendData(CardTypeDzz.INVALID_CHAIR, cmd.getJsonData());*/
				GangCount = mocount;//���Ͽ������Ƽ���
				OnDealMo(m_seat);//
				m_bMountainMin -= 1;
			}else SendData(_Uid, GetErrMsg("ziagang", "can't ziagang"));
		} else SendData(_Uid, GetErrMsg("order", "not your order"));
	}
	//����
	void Handle_onZimo(int _Uid,String vecArg[]){

		m_seat = 0;
			if (m_seat==nextseat){
				if (m_GameLogic.zimohuLegal(0x00,m_bTableCardArray[m_seat],0)){


					System.out.println(m_seat+"�ź��ƣ�");
					System.out.println("     |���ƣ�"+Arrays.toString(m_bTableCardArray[m_seat])+" ����"+Arrays.toString(m_bTableCardFlower[m_seat]));

					End();

			}else SendData(_Uid, GetErrMsg("zimo", "can't hu"));
		} else SendData(_Uid, GetErrMsg("order", "not your order"));
	}
	//��
	void Handle_onTing(int _Uid,String vecArg[]){

		m_seat =0;
		indexHand = m_GameLogic.getFirstIndex(m_bTableCardArray[m_seat], 22);

		System.out.println("����");
		if (m_GameLogic.tingLegal(0x00,m_bTableCardArray[m_seat],22)){

			System.out.println("����1");
			tingstatus[0][m_seat]=1;//ting
			if (mopercount[m_seat]==1) tingstatus[0][m_seat] = 3;//tianting
			if (m_GameLogic.tingCount(m_bTableCardArray[m_seat])==1) {//duting
				System.out.println("������2");
				tingstatus[1][m_seat] = 2;
				if (tingstatus[0][m_seat] == 1)
					tingstatus[0][m_seat] = 0;
			}
			System.out.println("������");

		} else SendData(_Uid, GetErrMsg("ting", "wrongting"));
	}


	//��ʱ����
	int roundtime = 15;
	int waittime = roundtime + 1;
	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	//time out after mo
	void TimeOut_Da(int seat, int moCard){

		int datimebefore = datime;
		Runnable callable = new Runnable() {
			 //
			public void run() {
				if (datime == datimebefore) {

					int uid = m_pIUserItem[seat].dwUserID;
					String damessage[] = new String[]{"da", "" + moCard};

					System.out.println("$$$$$$$$$$$$��ʱ# "+waittime+"�� #����һ�ţ�" + moCard);
					Handle_onDa(uid, damessage);
				}
			}
		};
		service.schedule(callable, waittime, TimeUnit.SECONDS);
	}

	//timeout after cando(can chi,can peng,can gang)
	void TimeOut_Pa(int seat){

		int dotimebefore = dotime;
		Runnable callable = new Runnable() {
			 //
			public void run() {
				if (dotime==dotimebefore) {

					int uid = m_pIUserItem[seat].dwUserID;
					String passmessage[] = new String[]{"pass", "0"};

					System.out.println("############��ʱ# "+waittime+"�� PASS");
					OnDealCard(uid, passmessage);
				}
			}
		};
		service.schedule(callable, waittime, TimeUnit.SECONDS);
	}

	//timeout after doa(chi,peng)
	void TimeOut_Doa(int seat){
		int datimebefore = datime;
		Runnable callable = new Runnable() {
			 //
			public void run() {
				if (datime == datimebefore) {

					int daCard =m_bTableCardArray[seat][0] ;
					int uid = m_pIUserItem[seat].dwUserID;
					String damessage[] = new String[]{"da", "" + daCard};

					System.out.println("$$$$$$$$$$$$��ʱ# "+waittime+"�� #����һ�ţ�" + daCard);
					Handle_onDa(uid, damessage);
				}
			}
		};
		service.schedule(callable, waittime, TimeUnit.SECONDS);
	}


}
