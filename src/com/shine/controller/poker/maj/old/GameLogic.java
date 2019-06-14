package com.shine.controller.poker.maj.old;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameLogic {

	int LOGIC_MASK_COLOR=	0xF0	;					//��ɫ����
	int	LOGIC_MASK_VALUE=	0x0F	;					//��ֵ����

	//�齫����
	final int m_cbCardListData[]={//0�� 1�� 2Ͳ 3�� 4��
			0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,						//����
			0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,						//����
			0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,						//����
			0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,						//����
			0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,						//����
			0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,						//����
			0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,						//����
			0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,						//����
			0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,						//Ͳ��
			0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,						//Ͳ��
			0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,						//Ͳ��
			0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,						//Ͳ��
			0x31,0x32,0x33,0x34,0x35,0x36,0x37,									//����
			0x31,0x32,0x33,0x34,0x35,0x36,0x37,									//����
			0x31,0x32,0x33,0x34,0x35,0x36,0x37,									//����
			0x31,0x32,0x33,0x34,0x35,0x36,0x37,									//����
			0x41,0x42,0x43,0x44,0x45,0x46,0x47,0x48,							//�����ﶬ÷������
	};

	final int m_cbCardKindData[] = {

			0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,						//����
			0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,						//����
			0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,						//Ͳ��
			0x31,0x32,0x33,0x34,0x35,0x36,0x37,									//����
	};

	public	int[] m_byPokerPai;
	public GameLogic() {
		m_byPokerPai = new int[144];
	}
	boolean [][]newStatus = new boolean[4][5];//��ʼ��״ֵ̬

	//��ȡ��ֵ;
	int GetCardValue(int cbCardData) {
		return (int)(cbCardData & LOGIC_MASK_VALUE);
	}
	//��ȡ��ɫ
	int GetCardColor(int cbCardData) {
		return (int)((cbCardData&LOGIC_MASK_COLOR) >> 4);
	}
	//������ֵ��0���
	int GetSortValue(int cbCardData){
		if (cbCardData==0x00) return 0x0A;
		return GetCardValue(cbCardData);
	}
	//����ɫ��0���
	int GetSortColor(int cbCardData){
		if (cbCardData==0x00) return 0x05;
		return GetCardColor(cbCardData);
	}
	//�����齫
	void RandCard(int cbCardBuffer[]){
		List<Integer> list = new LinkedList<>();
		for (int i = 0; i <144; i++) list.add(cbCardBuffer[i]);
		Collections.shuffle(list);
		for (int i = 0; i < 144; i++) cbCardBuffer[i] = list.get(i);
	}

	//�����齫FCLV0E
	void SortCardList(int cbCardData[],int cbCardCount){
		//��ͳð��
		int temp=0;
		for (int i = 0; i <cbCardCount-1; i++) {
			for(int j=0;j<cbCardCount-1-i;j++){
				if(GetSortColor(cbCardData[j])>GetSortColor(cbCardData[j+1])){

					cbCardData[j] = cbCardData[j] ^ cbCardData[j + 1];
					cbCardData[j+1] = cbCardData[j] ^ cbCardData[j + 1];
					cbCardData[j] = cbCardData[j] ^ cbCardData[j + 1];
				}
				if(GetSortColor(cbCardData[j])==GetSortColor(cbCardData[j+1] ) && GetSortValue(cbCardData[j])>GetSortValue(cbCardData[j+1])){

					temp=cbCardData[j];
					cbCardData[j]=cbCardData[j+1];
					cbCardData[j+1]=temp;
				}
			}
		}
	}
	//�¼����
	int GetNextSeat(int seat){
		if (seat==3) return 0;
		return seat + 1;
	}
	//get next user
	int GetNextUser(int seat){
		if (seat>=3) return 0;
		return seat + 1;
	}

	//�л�true
	boolean haveCard(int cacheCard, int[] cbHandCard, int cbHandCardCount){
		for (int i = 0; i < cbHandCardCount; i++) {
			if (cacheCard==cbHandCard[i]) return true;
		}
		return false;
	}
	//�л�ֵ
	int haveCardNum(int cacheCard,int[] cbHandCard,int cbHandCardCount){
		int num = 0;
		for (int i = 0; i < cbHandCardCount; i++) {
			if (cbHandCard[i]==cacheCard) num += 1;
		}
		return num;
	}


	//���0��
	int getFirstIndex(int[] cbHandCard,int cbHandCardCount){
		for (int i = 0; i < cbHandCardCount; i++)
			if (cbHandCard[i]==0x00) return i;
		return cbHandCardCount;
	}
	//���0β
	int getLastIndex(int[] cbHandCard){
		for (int i = 21; i >-1; i--) {
			if (cbHandCard[i]==0x00) return i;
		}
		return -1;
	}

	//getPointIndex
	int getPointIndex(int point,int[] cbHandCard){
		for (int i = 21; i >-1; i--) {
			if (cbHandCard[i]==point) return i;
		}
		return -1;
	}
	//IndexPointF
	int getPointFirst(int point,int[] cbHandCard){
		for (int i = 0; i < 21; i++) {
			if (cbHandCard[i]==point) return i;
		}
		return 22;
	}
	//getPointNum
	int getPointNum(int point,int[] cbHandCard){
		int num = 0;
		for (int i = 0; i < cbHandCard.length; i++) if (cbHandCard[i] == point) num += 1;
		return num;
	}

	//�����
	//�����ƺС�����
	Integer[] getCardCount4(int[][] cbCardBox,int[][] cbCardArray,int seat){

		int[] allCardCount = new int[0x38];
		List failTingArr = new LinkedList();
		//�ƺм���
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 18; j++)
				allCardCount[cbCardBox[i][j]] += 1;

		//���Ƽ���
		for (int i = 0; i < 4; i++) {
			int last = getLastIndex(cbCardArray[i]);
			for (int j = last+1; j < 22; j++)
				allCardCount[cbCardArray[i][j]] += 1;
		}

		//�Լ����Ƽ���
		int index = getFirstIndex(cbCardArray[seat], 22);
		for (int i = 0; i < index; i++)
			allCardCount[cbCardArray[seat][i]] += 1;

		//�������[ counts==4 ]
		for (int i = 0x01; i < 0x38; i++)
			if (allCardCount[i]==4)
				failTingArr.add(i);

		Integer[] result = (Integer[]) failTingArr.toArray(new Integer[0]);

		return result;
	}
	//���Զ���
	int tingCount(int[] cbHandCard){
		int result = 0;
		for (int i = 0; i < 34; i++)
			if (huLegal(m_cbCardKindData[i],cbHandCard,22)) result+=1;
		return result;
	}



	//�Ϸ��ж�
	//�ԺϷ�
	boolean chiLegal(int cacheCard,int[] cbHandCard,int cbHandCardCount){

		if (cacheCard>0x29) return false;
		if (haveCard(cacheCard + 1, cbHandCard, cbHandCardCount)) {
			if (haveCard(cacheCard + 2, cbHandCard, cbHandCardCount) || haveCard(cacheCard - 1, cbHandCard, cbHandCardCount))
				return true;
		}
		if (haveCard(cacheCard - 1, cbHandCard, cbHandCardCount)) {
			if (haveCard(cacheCard - 2, cbHandCard, cbHandCardCount) || haveCard(cacheCard + 1, cbHandCard, cbHandCardCount))
				return true;
		}
		if (haveCard(cacheCard + 2, cbHandCard, cbHandCardCount)) {
			if (haveCard(cacheCard + 1, cbHandCard, cbHandCardCount)) return true;
		}
		if (haveCard(cacheCard - 2, cbHandCard, cbHandCardCount)) {
			if (haveCard(cacheCard - 1, cbHandCard, cbHandCardCount)) return true;
		}

		return false;
	}


	//���Ϸ�
	boolean pengLegal(int cacheCard,int[] cbHandCard,int cbHandCardCount ){
		if (haveCardNum(cacheCard,cbHandCard,cbHandCardCount)>=2) return true;
		return false;
	}
	//�ܺϷ�
	boolean gangLegal(int cacheCard,int[] cbHandCard,int cbHandCardCount ){
		if (haveCardNum(cacheCard,cbHandCard,cbHandCardCount)==3) return true;
		return false;
	}
	//�ӸܺϷ�
	boolean jiagangLegal(int cacheCard,int[] cbHandCard,int cbHandCardCount ){
		int last = getLastIndex(cbHandCard);//�õ�0β
		int point=getPointFirst(cacheCard, cbHandCard);
		int [] deadCards=new int[21 - last];//�������ƶ�
		System.arraycopy(cbHandCard,last+1,deadCards,0,21 - last);
		//System.out.println(Arrays.toString(deadCards)+"  point:"+point+" last:"+last);
		if ( (point<last) && haveCardNum(cacheCard,deadCards,21 - last)==3) return true;
		return false;
	}

	//�ԸܺϷ�
	boolean zigangLegal(int cacheCard,int[] cbHandCard,int cbHandCardCount ){
		if (haveCardNum(cacheCard,cbHandCard,cbHandCardCount)==4) return true;
		return false;
	}

	//���Ϸ�
	boolean tingLegal(int cacheCard,int[] cbHandCard,int cbHandCardCount){
		int first = getFirstIndex(cbHandCard,22);

		for (int i = 0; i < 34; i++)
			for (int j = 0; j < first; j++) {

				int src = cbHandCard[j];
				cbHandCard[j] = m_cbCardKindData[i];
				if (xiaohuLegal(cbHandCard)) {cbHandCard[j] =src;return true;}
				else cbHandCard[j] =src;
			}

		return false;
	}

	//��Ϸ�
	boolean daLegal(int cacheCard,int[] cbHandCard,int cbHandCardCount){
		return haveCard(cacheCard, cbHandCard, cbHandCardCount);
	}


	//�Ա仯�����Ʒ�������ǰ��
	void chiChange(int[] cbCardsIn,int[] cbHandCard){
		int first = getFirstIndex(cbHandCard,22);
		int last = getLastIndex(cbHandCard);
		boolean flag1=false,flag2 = false;

		for (int i = 0; i < first; i++) {//�������еĳԵ����Ʋ���λ�滻

			if (flag1 && flag2) break;//�����ظ�bug

			if (cbHandCard[i]==cbCardsIn[1] && !flag1){ cbHandCard[i] = 0x00;flag1 = true;}
			if (cbHandCard[i]==cbCardsIn[2] && !flag2){ cbHandCard[i] = 0x00;flag2 = true;}
		}
		SortCardList(cbHandCard,first);//���У��������е�0�������
		for (int i = 0; i < 3; i++) {//���Ʒŵ�����λ��
			cbHandCard[last - 2 + i] = cbCardsIn[i];
		}
	}



	//���仯�����Ʒ������ƺ�
	void pengChange(int cbCardIn,int[] cbHandCard){
		int first = getFirstIndex(cbHandCard,22);
		int last = getLastIndex(cbHandCard);
		int time = 0;
		for (int i = 0; i < first; i++) {//�������е��������Ʋ���λ�滻
			if (time==2)	break;			//�滻�������ξ�ֹͣ����ֹ�����滻��
			if (cbHandCard[i]==cbCardIn){
				cbHandCard[i] = 0x00;time += 1;
			}
		}
		SortCardList(cbHandCard,first);//���У��������е�0�������
		for (int i = 0; i < 3; i++) {//���Ʒŵ�����λ��
			cbHandCard[last - 2 + i] = cbCardIn;
		}
	}
	//�ܱ仯�����Ʒŵ����ƺ�
	void gangChange(int cbCardIn,int[] cbHandCard){
		int first = getFirstIndex(cbHandCard,22);
		int last = getLastIndex(cbHandCard);

		int time = 0;
		for (int i = 0; i < first; i++) {//�������иܵ����Ʋ���λ�滻
			if (time==3)	break;			//�滻�������ξ�ֹͣ����ֹ�����滻��
			if (cbHandCard[i]==cbCardIn){
				cbHandCard[i] = 0x00;time += 1;
			}
		}
		SortCardList(cbHandCard,first);//���У��������е�0�������
		//����ǰ��4λ����ֵ21-last��
		int length = 21 - last;
		for (int i = 0; i < length; i++) {
			cbHandCard[last-3+i] = cbHandCard[last+1+i];
		}
		//���Ʒŵ����
		for (int i = 21; i >17 ; i--) {
			cbHandCard[i] = cbCardIn;
		}
	}
	//�Ӹܱ仯			(�Ӹܵ�ʱ�����������봦�����ƺ�)
	void jiaGang(int cbCardIn,int[] cbHandCard){

		int last = getLastIndex(cbHandCard);
		int point = getPointFirst(cbCardIn, cbHandCard);
		cbHandCard[point] = 0x00;//point����֮������
		SortCardList(cbHandCard, last);
		point = getPointFirst(cbCardIn, cbHandCard);//�ض�����point
		System.out.println("point:"+point+" last:"+last);


		for (int i = last; i <point ; i++) {
			cbHandCard[i] = cbHandCard[i + 1];
		}
	}

	//�Ըܱ仯(����)
	void zigangChange(int cbCardIn,int[] cbHandCard){
		int first = getFirstIndex(cbHandCard,22);
		int last = getLastIndex(cbHandCard);

		for (int i = 0; i < first; i++) {//�������иܵ����Ʋ���λ�滻
			if (cbHandCard[i]==cbCardIn)	cbHandCard[i] = 0x00;
		}
		SortCardList(cbHandCard,first);//���У��������е�0�������

		//����ǰ��4λ����ֵ21-last��
		int length = 21 - last;
		for (int i = 0; i < length; i++) {
			cbHandCard[last-3+i] = cbHandCard[last+1+i];
		}
		//���Ʒŵ����
		for (int i = 21; i >17 ; i--) {
			cbHandCard[i] = cbCardIn;
		}
	}
	//���仯
	void moChange(int m_bTableCardMount[],int cardMountHeight){

		//�ױ�β��β��0
		m_bTableCardMount[0] = m_bTableCardMount[cardMountHeight-1];
		m_bTableCardMount[cardMountHeight-1] = 0x00;//��ɽβ����0
	}
	//��仯
	void daChange(int cbCardOut,int[] cbHandCard){

		int first = getFirstIndex(cbHandCard,22);
		for (int i = 0; i < first; i++){
			if (cbCardOut==cbHandCard[i]){

				cbHandCard[i] = 0x00;
				break;
			}
		}

		SortCardList(cbHandCard, first);
	}


	//��ɽ
	int[] cardMount;
	//����[����֮�󷵻�24*4����]
	int [][]startCard(){

		int tablecard[][] = new int[4][24];

		RandCard(m_cbCardListData);//�����ƶ�

		int index = 0;
		int length = 16;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 24; j++) {
				if (j<length){
					tablecard[i][j] = m_cbCardListData[index];
					index+=1;

					if (tablecard[i][j]>0x40){//�����������һ��
						length+=1;
					}
				}
			}
			length = 16;
		}
		cardMount = new int[144-index];//������ɽ
		System.arraycopy(m_cbCardListData, index, cardMount, 0, 144-index);//������ɽ
		return tablecard;
	}
	//���ƴ���[��������24�ֽ�Ϊ����22�ͻ���8]
	void dealStart(	int m_bTableCardStart[][] ,int m_bTableCardArray[][],int m_bTableCardFlower[][]){//24,22,8

		int index = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 24; j++) {

				//���Ƹ�ֵ
				if (m_bTableCardStart[i][j]>0x40){
					m_bTableCardFlower[i][index] = m_bTableCardStart[i][j];
					index+=1;
				}else if (m_bTableCardStart[i][j]!=0x00 ){
					m_bTableCardArray[i][j-index]=m_bTableCardStart[i][j];
				}
			}
			index = 0;
		}
	}

	//״̬����
	void dealStatus(int cacheCard,int m_bTableCardArray[][],boolean m_bTableCardStatus[][],int m_seat ){
		int index = 0;
		for (int i = 0; i < 4; i++) {
			index = getFirstIndex(m_bTableCardArray[i],22);

			if (huLegal(cacheCard,m_bTableCardArray[i],index)){
				m_bTableCardStatus[i][1] = true;m_bTableCardStatus[i][0] = true;
			}
			if (pengLegal(cacheCard,m_bTableCardArray[i],index)){
				m_bTableCardStatus[i][2] = true;m_bTableCardStatus[i][0] = true;
			}
			if (gangLegal(cacheCard,m_bTableCardArray[i],index)){
				m_bTableCardStatus[i][3] = true;m_bTableCardStatus[i][0] = true;
			}
			if ( i == GetNextSeat(m_seat)) {
				if (chiLegal(cacheCard, m_bTableCardArray[i], index)) {
					m_bTableCardStatus[i][4] = true;m_bTableCardStatus[i][0] = true;
				}
			}
		}

		for (int i = 0; i < 5; i++) m_bTableCardStatus[m_seat][i] = false;

	}
	//���ܴ���
	void dealQianggang(int cacheCard,int m_bTableCardArray[][],boolean m_bTableCardStatus[][],int m_seat){
		int index = 0;
		for (int i = 0; i < 4; i++) {
			index = getFirstIndex(m_bTableCardArray[i],22);
			m_bTableCardStatus[i][1] = huLegal(cacheCard,m_bTableCardArray[i],index);//���
			m_bTableCardStatus[i][0] = m_bTableCardStatus[i][1];
			for (int j = 2; j < 5; j++) 	m_bTableCardStatus[i][j]=false;
		}
		m_bTableCardStatus[m_seat][0] = false;
		m_bTableCardStatus[m_seat][1] = false;
	}
	//״̬����
	void clearStatus(boolean m_bTableCardStatus[][]){
		m_bTableCardStatus = newStatus;
	}
	//���ز���������
	int getOpNum(int seat,boolean status[][]){
		int ops = 0;
		for (int i = 0; i < 4; i++) {
			if (i==seat)	continue;
			if (status[i][0]) ops += 1;
		}
		return ops;
	}
	//���������
	int getSum(int ncount[]){
		int sum = 0;
		for (int i = 0; i < 4; i++) {
			sum += ncount[i];
		}
		return sum;
	}
	//stringתint
	int commandTurn(String command){
		if (command.equals("chi")) return 1;
		if (command.equals("peng")||command.equals("gang")) return 2;
		if (command.equals("hu")) return 3;
		if (command.equals("pass")) return 0;
		return 0;
	}
	int commandTurn2(String command){
		switch (command){
			case "chi": return 4;
			case "peng": return 2;
			case "gang": return 3;
			case "hu": return 1;
			case "qianggang": return 1;
		}
		return 5;
	}
	//�����±�
	int getMaxI(int m_bCommandCache[]){
		int maxi;
		if (m_bCommandCache[0]>=m_bCommandCache[1]) maxi = 0;
		else maxi = 1;
		if (m_bCommandCache[maxi]<m_bCommandCache[2]) maxi = 2;
		return maxi;
	}

	//�������Ϸ�
	boolean zimohuLegal(int cacheCard,int[] cbHandCard,int cbHandCardCount){

		if (cacheCard==0){
			int first = getFirstIndex(cbHandCard,22);
			cacheCard = cbHandCard[first - 1];
			cbHandCard[first - 1] = 0;
			return huLegal(cacheCard, cbHandCard, 22);
		}else return huLegal(cacheCard, cbHandCard, 0);

	}

	//����С��
	boolean xiaohuLegal(int[] cbHandCard){

		int first = getFirstIndex(cbHandCard,cbHandCard.length);
		int[] card = new int[first];
		System.arraycopy(cbHandCard,0,card,0,first);
		int wei = getWei(card, card.length);

		if (first==2 && cbHandCard[0]==cbHandCard[1]) return true;//�ཫ
		//if (ArrayUtils.contains(angang3,wei)) return true;//3angang
		if (wei==11) return true;
		else if ((first)%3!=2) return false;//cannot 3n+2
		else {

			int[] jiangs = getJiang(card, first);
			List<int[]> mians = quJiangArrs(card, jiangs, first);

			int size = mians.size();
			boolean[] ones = new boolean[size];
			int[] breaks;

			for (int i = 0; i < size; i++) {

				//System.out.println(Arrays.toString(mians.get(i)));
				breaks = getBreaks(mians.get(i), first - 2);
				if (breaksCheck(breaks,breaks.length)) ones[i]=false;
				else {

					int[] miansparts = allParts(mians.get(i), breaks);
					ones[i]= finalHu(miansparts, miansparts.length);
				}
			}

			for (int i = 0; i < size; i++) {
				if (ones[i]) return true;
			}

		}

		return false;
	}

	//���Ϸ�
	boolean huLegal(int cacheCard,int[] cbHandCard,int cbHandCardCount){

		int first = getFirstIndex(cbHandCard,22);
		int mianNeed = needMian(cbHandCard);
		int[] card = new int[first + 1];
		System.arraycopy(cbHandCard,0,card,0,first);
		card[first] = cacheCard;
		SortCardList(card,first+1);//sortCardList
		int wei = getWei(card, card.length);
		if (mianNeed==0){
			//System.out.println("0��");
			if (cbHandCard[0]==cacheCard) return true;
			else return false;
		}else if (mianNeed==5){
			//System.out.println("5��");

			/*if (ArrayUtils.contains(angang3,wei)) return true;//3angang
			else*/ if ((first+1)%3!=2) return false;//cannot 3n+2
			else {

				int[] jiangs = getJiang(card, first+1);
				List<int[]> mians = quJiangArrs(card, jiangs, first + 1);

				int size = mians.size();
				boolean[] ones = new boolean[size];
				int[] breaks;

				for (int i = 0; i < size; i++) {

					//System.out.println(Arrays.toString(mians.get(i)));
					breaks = getBreaks(mians.get(i), first - 1);
					if (breaksCheck(breaks,breaks.length)) ones[i]=false;
					else {

						int[] miansparts = allParts(mians.get(i), breaks);
						ones[i]= finalHu(miansparts, miansparts.length);
					}
				}

				for (int i = 0; i < size; i++) {
					if (ones[i]) return true;
				}

			}

		}else{
			//if (ArrayUtils.contains(angang3,wei)) return true;//3angang
			if ((first+1)%3!=2) return false;//cannot 3n+2
			else {

				int[] jiangs = getJiang(card, first+1);
				List<int[]> mians = quJiangArrs(card, jiangs, first + 1);

				int size = mians.size();
				boolean[] ones = new boolean[size];
				int[] breaks;

				for (int i = 0; i < size; i++) {

					//System.out.println(Arrays.toString(mians.get(i)));
					breaks = getBreaks(mians.get(i), first - 1);
					if (breaksCheck(breaks,breaks.length)) ones[i]=false;
					else {

						int[] miansparts = allParts(mians.get(i), breaks);
						ones[i]= finalHu(miansparts, miansparts.length);
					}
				}

				for (int i = 0; i < size; i++) {
					if (ones[i]) return true;
				}

			}
		}

		return false;
	}

	//hu's functions series/////////////////////////////////////////////////////////////////
	int[] ligu=new int[]{22222223, 22222232, 22222322, 22223222, 22232222, 22322222, 23222222, 32222222};
	int[] angang3 = new int[]{	4442,	34442,43442,44342,44432,44423,
								4424,	34424,43424,44324,44234,44243,
								4244,	34244,43244,42344,42434,42443,
								2444,	32444,23444,24344,24434,24443,

										1114442,4111442,4411142,	444113,444131,444311,
										1114424,4111424,4424111,	441134,441314,443114,
										1114244,4241114,4244111,	411344,413144,431144,
										2411144,2441114,2444111,	113444,131444,311444};
	int[] anke5 = new int[]{	333332,333323,333233,
								332333,323333,233333};

	//make carry
	int carry(int key){//��λ
		switch (key){
			case 3: return 3;
			case 4: return 3;

			case 31: return 30;
			case 32: return 30;
			case 33: return 33;
			case 44: return 33;

			case 111: return 111;
			case 112: return 111;
			case 113: return 111;
			case 114: return 114;

			case 122: return 111;
			case 123: return 111;
			case 124: return 111;

			case 133: return 111;
			case 134: return 111;

			case 141: return 141;
			case 142: return 141;
			case 143: return 141;
			case 144: return 144;

			case 222: return 222;
			case 223: return 222;
			case 224: return 222;

			case 233: return 222;
			case 234: return 222;
			case 244: return 222;

			case 311: return 300;
			case 312: return 300;
			case 313: return 300;
			case 314: return 300;

			case 322: return 300;
			case 323: return 300;
			case 324: return 300;

			case 331: return 330;
			case 332: return 330;
			case 333: return 333;
			case 334: return 333;

			case 341: return 330;
			case 342: return 330;
			case 343: return 330;
			case 344: return 333;

			case 411: return 411;
			case 412: return 411;
			case 413: return 411;
			case 414: return 414;

			case 422: return 411;
			case 423: return 411;
			case 424: return 411;

			case 433: return 411;
			case 434: return 411;

			case 441: return 441;
			case 442: return 441;
			case 443: return 441;
			case 444: return 444;

		}
		return 0;
	}

	//carry keystart with 3key,if con't carry,change to 2key,1key. else return itself
	int carryChange(int cbCardWei){

		int keyNum = 3;
		int result = cbCardWei;
		int weilength = (int) Math.log10(cbCardWei)+1;//λ�ĳ���

		int weihead = (int) (cbCardWei / Math.pow(10, (weilength - keyNum)));//λ��ͷ��
		if (carry(weihead)!=0){
			result = (int) (cbCardWei - (carry(weihead) * Math.pow(10, (weilength - keyNum))));//λ������
		}//ǰ��λ����޽����ǰ��λ
		else {

			keyNum = 2;
			weihead = (int) (cbCardWei / Math.pow(10, (weilength - keyNum)));

			if (carry(weihead)!=0){
				result = (int) (cbCardWei - (carry(weihead) * Math.pow(10, (weilength - keyNum))));//λ������
			}//ǰ��λ����޽��ֻ��ǰһλ
			else {

				keyNum = 1;
				weihead = (int) (cbCardWei / Math.pow(10, (weilength - keyNum)));
				if (carry(weihead)!=0) {
					result = (int) (cbCardWei - (carry(weihead) * Math.pow(10, (weilength - keyNum))));//λ������
				}//��һλ�޽����result������ı�
			}
		}
		return result;
	}

	//circle carryChange until result==0,else return fase
	boolean isMianPart(int cbCardWei) {

		int cache = cbCardWei;
		for (int i = 0; i < 9; i++) {
			cache = carryChange(cache);
			if (cache == 0) return true;
		}
		return false;
	}

	//get mianCount we need
	int needMian(int[] cbHandCard){//�����Ҫ����
		int last = getLastIndex(cbHandCard);
		if (last==9){
			if (cbHandCard[10]==cbHandCard[13] && cbHandCard[14]==cbHandCard[17] &&cbHandCard[18]==cbHandCard[21])//4*3
				return 2;//3*4
			else return 1;
		}else if (last==6){
			if (cbHandCard[10]==cbHandCard[13] && cbHandCard[14]==cbHandCard[17] &&cbHandCard[18]==cbHandCard[21])//4*3+3
				return 1;//5*3
			else return 0;//5*3
		}else if (last==5){
			if (cbHandCard[6]==cbHandCard[9]&&cbHandCard[10]==cbHandCard[13]
					&& cbHandCard[14]==cbHandCard[17] &&cbHandCard[18]==cbHandCard[21])//4*4
				return 1;
			else return 0;//3*4+4

		}else{
			switch (last){
				case 21: return 5;

				case 18: return 4;
				case 17: return 4;

				case 15: return 3;
				case 14: return 3;
				case 13: return 3;

				case 12: return 2;
				case 11: return 2;
				case 10: return 2;

				case 8: return 1;
				case 7: return 1;

				case 4: return 0;
				case 3: return 0;
				case 2: return 0;

				case -1: return 0;
			}
		}
		return -1;
	}

	//get arr's Wei
	int getWei(int[] cbHandCard, int cbHandCardCount){

			int allwei = 1;//������������ֵ
			int length;
			for (int i = cbHandCardCount-2; i >-1; i--) {
				if (cbHandCard[i]!=cbHandCard[i+1] && cbHandCard[i]!=0){
					length = (int) Math.log10(allwei) + 1;
					allwei = allwei + (int) Math.pow(10, length);
				}else if (cbHandCard[i]==cbHandCard[i+1] && cbHandCard[i]!=0){
					length = (int) Math.log10(allwei);
					allwei = allwei + (int) Math.pow(10, length);
				}
			}

		return allwei;
	}

	//get card's breaks
	int[] getBreaks(int[] cbHandCard,int cbHandCardCount){

		int[] breaks = new int[22];//��λ0
		int count = 1;
		for (int i = 1; i < cbHandCardCount; i++) {
			if ((cbHandCard[i]-cbHandCard[i-1])>1){
				breaks[count] = i;
				count += 1;
			}
		}
		breaks[count] = cbHandCardCount;
		int breakss[] = new int[count + 1];//��λ0 βλlength
		System.arraycopy(breaks,0,breakss,0,count+1);
		return breakss;
	}

	//slice handCard to partArr and return to Wei arr
	int[] allParts(int[]cbHandCard, int[]breaks){

		int partnum = breaks.length-1;
		int[] parts = new int[partnum];
		int[] arr;
		for (int i = 0; i < partnum; i++) {

			arr = new int[breaks[i+1]-breaks[i]];
			System.arraycopy(cbHandCard,breaks[i],arr,0,breaks[i+1]-breaks[i]);

			parts[i]= getWei(arr,arr.length);
			//System.out.println(Arrays.toString(arr));
		}

		return parts;
	}

	//check breaks[1++] is 3n
	boolean breaksCheck(int[] breaks,int length){
		for (int i = 1; i < length; i++)
			if (breaks[i]%3!=0)
				return true;

		return false;
	}

	//return all jiangs and in it's first index,same length as cbHardCard
	int[] getJiang(int[] cbHandCard,int cbHandCardCount){

		int[] arr = new int[cbHandCardCount];
		int count = 1;

		for (int i = 1; i < cbHandCardCount; i++) {
			if (cbHandCard[i]==cbHandCard[i-1]){
				if (count==1) arr[i-1] = cbHandCard[i];
				count += 1;

			}else count = 1;
		}
		//System.out.println(Arrays.toString(arr));
		return arr;
	}

	//retrun all kindsof (card[]-jiang) as one List
	List quJiangArrs(int[]card,int[]jiangs,int length){
		List<int[]> list = new LinkedList();

		for (int i = 0; i < length; i++) {
			if (jiangs[i]!=0){

				int[] src = new int[length];
				int[] arr = new int[length - 2];
				System.arraycopy(card,0,src,0,length);

				src[i] = 0x00;
				src[i+1] = 0x00;

				SortCardList(src,length);
				System.arraycopy(src,0,arr,0,length-2);

				//System.out.println(Arrays.toString(arr));
				list.add(arr);
			}
		}
		return list;
	}

	//check all quJiangful card[]'s mianPart,Just one not mian will return false
	boolean finalHu(int[] weis,int weisnum){
		for (int i = 0; i <weisnum ; i++) {
			if (isMianPart(weis[i]) == false) return false;
		}
		return true;
	}

	//hu's kinds series/////////////////////////////////////////////////

}


