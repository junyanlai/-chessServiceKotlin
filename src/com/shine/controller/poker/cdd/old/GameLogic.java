package com.shine.controller.poker.cdd.old;

import java.util.*;

public class GameLogic {

	int LOGIC_MASK_COLOR=	0xF0	;					//花色掩码
	int	LOGIC_MASK_VALUE=	0x0F	;					//数值掩码
	//扑克数据
	final int m_cbCardListData[] = {
			0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D,	//梅花 A - K
			0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D,	//方块 A - K
			0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D,	//红桃 A - K
			0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D,	//黑桃 A - K
	};

	public GameLogic(){}


	void RandCardList(int cbCardBuffer[], int cbBufferCount){

		List<Integer> list = new LinkedList<>();
		for (int i = 0; i <cbBufferCount; i++) list.add(m_cbCardListData[i]);
		Collections.shuffle(list);
		for (int i = 0; i <cbBufferCount; i++) cbCardBuffer[i] = list.get(i);
	}


	//获取数值;
	int V(int cbCardData) {
		return (int)(cbCardData&LOGIC_MASK_VALUE);
	}
	//获取花色
	int C(int cbCardData) {
		return (int)((cbCardData&LOGIC_MASK_COLOR) >> 4);
	}
	// 逻辑大小
	int SV(int cbCardData) {
		if (!IsValidCard(cbCardData))
			return 0;
		// 扑克属性
		int cbCardColor = C(cbCardData);
		int cbCardValue = V(cbCardData);

		// 转换数值
		if (cbCardColor == 0x40)
			return cbCardValue + 2;
		if (cbCardValue==0x0E || cbCardValue==0x0F)
			return cbCardValue + 2;
		if (cbCardValue== 1  || cbCardValue==2)
			return cbCardValue + 13;
		return  cbCardValue;
	}
	//权值大小
	int Weight(int cbCardData){
		return SV(cbCardData)*100 + C(cbCardData);
	}


	//获取牌型[需要先以数值排序]
	int Type(int cbCardData[], int cbCardCount){

		//根据牌组数量判断
		switch (cbCardCount){

			case 1: return Constants.CT_SINGLE;
			case 2: return Constants.CT_DOUBLE;
			case 3: return Constants.CT_THREE;
			case 5: return Type57(cbCardData, cbCardCount);
			case 7:	return Type57(cbCardData, cbCardCount);
			case 8: return Constants.CT_FOUR_LINE_TAKE_THREE_JOKER;
			case 9: return Constants.CT_FOUR_LINE2_TAKE_FOUR_JOKER;
			case 13:return Constants.CT_DRAGON;
		}
		return 0;
	}
	//排列扑克
	void Sort(int cards[], int len){

		//传统冒泡【只对7或5张】
		int temp=0;
		for (int i = 0; i <len-1; i++) {

			for(int j=0;j<len-1-i;j++){
				if(SV(cards[j])> SV(cards[j+1])){

					temp=cards[j];
					cards[j]=cards[j+1];
					cards[j+1]=temp;


				}
				if(SV(cards[j])== SV(cards[j+1]) && C(cards[j])> C(cards[j+1])){

					temp=cards[j];
					cards[j]=cards[j+1];
					cards[j+1]=temp;
				}
			}
		}
		return ;
	}//正排FVLC




	//顺子按值排列
	void SortShun(int cards[], int len){
		int temp=0;
		for (int i = 0; i <len-1; i++)
			for(int j=0;j<len-1-i;j++)
				if(V(cards[j])> V(cards[j+1])){
					temp=cards[j];
					cards[j]=cards[j+1];
					cards[j+1]=temp;
				}
	}
	//算积分时用
	//牌型判断5,7类型
	int Type57(int cards[], int len){

		Sort(cards, len);
		System.out.println(V(cards[0])+"-"+ V(cards[1])+"-"+ V(cards[2])+"-"+ V(cards[3])+"-"+ V(cards[4]));
		if (len==5){

			if (V(cards[1])== V(cards[2]) && V(cards[2])== V(cards[3])){
				return Constants.CT_FOUR_LINE_TAKE_ONE;
			}else if (V(cards[0])== V(cards[1])){
				return Constants.CT_THREE_LINE_TAKE_TWO;
			}else {
				return Constants.CT_FIVE_LINE;
			}
		}
		if (len==7){

			if (isFour2(cards))
				return Constants.CT_FOUR_LINE2_TAKE_THREE;
			else
				return Constants.CT_FIVE_LINE_SAMECOLOR;

		}

		return 0;
	}
	//7是否为2铁支
	boolean isFour2(int cards[]){

		int count=0;
		for (int i = 0; i < 7; i++) {
			if (V(cards[i])==2) 	count += 1;
			if (count==4) return true;
		}
		return false;
	}

	// 有效判断
	boolean IsValidCard(int cbCardData) {
		// 获取属性
		int cbCardColor = C(cbCardData);
		int cbCardValue = V(cbCardData);

		// 有效判断
		if ((cbCardData == 0x4E) || (cbCardData == 0x4F))
			return true;
		if ((cbCardColor <= 0x30) && (cbCardValue >= 0x01) && (cbCardValue <= 0x0D))
			return true;

		return false;
	}

	
	//合法判断
	boolean GetCardOnLow(int cards[],int hands[]){
		int count=0;
		for (int h :hands) 
			for (int c : cards) 
				if (h==c) 
				    count ++;
			
		
		if (count==cards.length) return true;
		return  false/*Arrays.asList(cbCardData).containsAll(Arrays.asList(userIsCardData))*/;
	}
	
	//清除玩家已提交牌组
	void ClearCardSend(int cbCardData[],int userIsCardData[]){
		for (int i = 0; i <13 ; i++)
			for (int cu : userIsCardData)
				if(cu==cbCardData[i])
					cbCardData[i]=0x00;

	}
	//获得剩余牌的数量
	int getLastCount(int cbCardData[]){
		int result = 0;
		for (int i = 0; i < 13; i++)
			if (cbCardData[i]==0x00)
				result += 1;
		return 13-result;
	}
	//大小比较
	boolean Done(int cardCall[], int lenCall, int cardFollow[], int lenFollow){
		boolean done = false;

		//牌组数量不同返回true
		if (lenCall!=lenFollow) 	done=true;
		if (lenCall==lenFollow){

			//根据牌组数量判断
			switch (lenCall){

				case 1: return SLDone(cardCall,cardFollow);
				case 2: return SLDone(cardCall,cardFollow);
				case 3: return SLDone(cardCall,cardFollow);
				case 5: return SLDone5(cardCall,cardFollow);
				case 7:	return SLDone7(cardCall,cardFollow);
				case 8: return SLDone(cardCall,cardFollow);
				case 9: return SLDone(cardCall,cardFollow);
				case 13:return SLDone(cardCall,cardFollow);
			}
		}

		return done;
	}
	//牌组数量不同的情况【牌组数量相同的情况下对比】
	boolean SLDone(int cardC[], int cardF[]){

        int len = cardC.length;
		boolean result = false;

		//牌组同为1
		if (len == Constants.CT_SINGLE) {
			result = Weight(cardF[0])> Weight(cardC[0]);
		}
		//牌组同为2
		if (len==Constants.CT_DOUBLE){
			return SLDone2(cardC, cardF);
		}
		//牌组同为3
		if (len==Constants.CT_THREE){
			Sort(cardC,Constants.CT_THREE);
			Sort(cardF,Constants.CT_THREE);
			result= SV(cardF[0])> SV(cardC[0]);
		}
		//牌组数量为5和7
		if (len==5)	result= SLDone5(cardC,cardF);
		if (len==7)	result= SLDone7(cardC,cardF);
		//牌组同为13
		if (len==13){
			Sort(cardC,13);
			Sort(cardF,13);
			//首龙有鬼
			if (cardC[12]==0x4E && cardF[12]!=0x4E){
				//call无2
				if (V(cardC[0])==1 && V(cardC[1])==3) result = true;
				if (V(cardC[0])==1 && V(cardC[1])==2)	result = cardF[1] > cardC[1];//有
				if (V(cardC[0])==2)	result = cardF[1] > cardC[0];//有
			}//未龙有鬼
			else if (cardC[12]!=0x4E && cardF[12]==0x4E){
				//done无2
				if (V(cardF[0])==1 && V(cardF[1])==3) result = false;
				if (V(cardF[0])==1 && V(cardC[1])==2)	result = cardF[1] > cardC[1];//有
				if (V(cardF[0])==2)	result = cardF[0] > cardC[1];//有
			}//双龙
			else {
				result = cardF[1] > cardC[1];
			}
		}

		return result;
	}

	void SortCard2(int[] cards){
		if (Weight(cards[0]) > Weight(cards[1])){
			cards[0] = cards[0] ^ cards[1];
			cards[1] = cards[0] ^ cards[1];
			cards[0] = cards[0] ^ cards[1];
		}
	}
	boolean SLDone2(int cardC[], int cardF[]){

		SortCard2(cardC);SortCard2(cardF);

		if (SV(cardC[0])== SV(cardF[0])){
			if (V(cardC[0])==2){

				if (cardC[0]==0x32 || cardC[1]==0x32 || cardC[1]==0x4E) return false;
				else return true;
			}	else return Weight(cardF[1])> Weight(cardC[1]);
		}else	return SV(cardF[0])> SV(cardC[0]);
	}
	boolean SLDone5(int cardC[], int cardF[]){
		boolean result = false;

		Sort(cardC,5);
		Sort(cardF,5);

		int callCardType = Type57(cardC, 5);
		int doneCardType = Type57(cardF, 5);
		if (callCardType!=doneCardType) 	result = true;
		//顺子
		if (callCardType==4){

			System.out.println("##_ "+Arrays.toString(cardF));

			//首顺有鬼
			if (cardC[4]==0x4E && cardF[4]!=0x4E){

				int call = getWei(cardC, 4);
				int done4 = getWei(cardC, 4);
				int done = getWei(cardF, 5);


				if (call==3456)
					return false;
				else if ((call==3452 || call==3462 || call==3562 || call==4562)&& done==34562)
					return Weight(cardF[4]) > Weight(cardC[3]);
				else if ((call==3452 || call==3462 || call==3562 || call==4562)&& done==34512)
					return false;
				else if (call==3452 || call==3462 || call==3562 || call==4562)
					return false;
				else if ((call==3412 || call==3512 ||call==4512 || call==3451)&& done==34562)
					return true;
				else if ((call==3412 || call==3512 ||call==4512) && done==34512)
					return Weight(cardF[4]) > Weight(cardC[3]);
				else if (call==3451 && done==34512)
					return false;
				else if (call==3412 || call==3512 ||call==4512 || call==3451)
					return true;
				else {
					//鬼在中
					if (SV(cardC[0]) + 4 == SV(cardC[3])) {
						return Weight(cardF[4]) > Weight(cardC[3]);
					}else if (SV(cardC[0])+3== SV(cardC[3])) {//鬼在尾
						if (call==done4) 	return false;
						else				return Weight(cardF[3]) > Weight(cardC[3]);
					}
				}
			}//末顺有鬼
			else if (cardC[4]!=0x4E && cardF[4]==0x4E){

				int call = getWei(cardC, 5);
				int call4 = getWei(cardC, 4);
				int done = getWei(cardF, 4);

				if (call==34562 && cardC[4]==0x32)
					return false;
				else if (call==34562 && done==3456)
					return true;
				else if (call==34562 && (done==3452 || done==3462 || done==3562 || done==4562))
					return Weight(cardF[3]) > Weight(cardC[4]);
				else if (call==34562 && (done==3412 || done==3512 || done==4512 || done==34552))
					return false;
				else if (call==34562)
					return false;
				else if (call==34512 && done==3451 && cardC[4]==0x32)
					return false;
				else if (call==34512 && done==3451)
					return true;
				else if (call==34512 && (done==3412 || done==3512 || done==4512))
					return Weight(cardF[3]) > Weight(cardC[4]);
				else if (call==34512 && (done==3456 || done==3452 || done==3462 || done==3562 || done==4562))
					return true;
				else if (call==34512)
					return true;
				else {//鬼在中
					if (SV(cardF[0]) + 4 == SV(cardF[3])) {
						return Weight(cardF[3]) > Weight(cardC[4]);
					}else if (SV(cardF[0])+3== SV(cardF[3])) {//鬼在尾
						if (call4==done)	return true;
						else				return Weight(cardF[3]) > Weight(cardC[3]);
					}
				}
			}else{//无鬼
				SortShun(cardC,5);
				SortShun(cardF,5);

				if (V(cardF[4])==6 && V(cardC[4])==6)
					result = C(cardF[4]) > C(cardF[4]);
				else if (V(cardF[4])==6 && V(cardC[4])!=6)
					result = true;
				else if (V(cardF[4])!=6 && V(cardC[4])==6)
					result = false;
				else if (V(cardF[4])==13 && V(cardC[4])==13)//A10JOK-910JQK
					result = Weight(cardF[0]) > Weight(cardC[0]);
				else
					result = Weight(cardF[4]) > Weight(cardC[4]);
			}
		}//葫芦-铁枝
		if (callCardType==5 || callCardType==6){
			result = SV(cardF[2]) > SV(cardC[2]);
		}
		return result;
	}
	boolean SLDone7(int cardCall[], int cardFollow[]){
		boolean result = false;

		Sort(cardCall,7);
		Sort(cardFollow,7);

		int callCardType = Type57(cardCall, 7);
		int doneCardType = Type57(cardFollow, 7);

		if (callCardType!=doneCardType) 	result = true;

		if (callCardType==7){
			result = Weight(cardFollow[0]) > Weight(cardCall[0]);
		}

		return result;
	}
	
	
	
	//草花方块替换
	int Replace(int card){
		if (card>0x00 && card<0x0E) return  (card+0x10);
		if (card>0x10 && card<0x1E) return  (card-0x10);
		return card;
	}
	int getWei(int[] arr,int num){
		int result = 0;
		for (int i = 0; i < num; i++)
			result += (V(arr[i]) * (int) Math.pow(10,(num-i-1)));
		return result;
	}


	//cleanCommand
	void commandClean(String m_commandCache[]){
		for (int i = 0; i < 4; i++) m_commandCache[i] = null;
	}
	//done(pass*3)
	boolean passDone(String m_commandCache[]){
		int count = 0;

		for (int i = 0; i < 4; i++)
			if (m_commandCache[i]!=null) if (m_commandCache[i].equals("pass")) count += 1;

		if (count==3){
			commandClean(m_commandCache);
			return true;
		}

		return false;
	}
	//wrongDone(pass*4)
	boolean wrongDone(String m_commandCache[]){
		int count = 0;
		for (int i = 0; i < 4; i++)
			if (m_commandCache[i]!=null)
				if (m_commandCache[i].equals("pass")) count += 1;

		if (count==4){
			commandClean(m_commandCache);
			return true;
		}
		return false;
	}
	//doned
	boolean isDone(String m_commandCache[]){

		int count = 0;

		for (int i = 0; i < 4; i++)
			if (m_commandCache[i]==null)
				count += 1;

		if (count==4) return true;
		return false;
	}
	//草花3
	boolean isFirstCall(int dwHandCard[]){
		for (int i : dwHandCard) {
			if (i==0x03) return true;
		}
		return false;
	}
	//end judge
	boolean isEnd(int userCard[]){
		int count = 0;
		for (int i = 0; i < 13; i++)
			if (userCard[i]==0x00)
				count += 1;
		if (count==13) return true;
		return false;
	}

	//数组求和
	int ArraySum(int userScore[]){
		int sum = 0;
		for (int i = 0; i < userScore.length; i++) {
			sum += userScore[i];
		}
		return sum;
	}
	//得到倍数
	int GetMultiple(int userCard[],int userCardcount){

		int multiple = 1;
		//定长
		if (userCardcount<8) multiple *= 1;
		if (userCardcount>=8 && userCardcount<10) multiple *= 2;
		if (userCardcount>=10 && userCardcount<13) multiple *= 4;
		if (userCardcount==13) multiple *= 8;
		//鬼牌，大老二
		int jkc = 0;
		for (int i = 0; i < userCardcount; i++) {
			if (SV(userCard[i])==15 || SV(userCard[i])==16)	jkc+=1;
		}
		System.out.println("jkc="+jkc);
		multiple *= Math.pow(2, jkc);
		//特殊牌型


		return multiple;
	}

	//先家指针
	int card3Seat(int m_bTableCardArray[][]){

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 13; j++)
				if (m_bTableCardArray[i][j]==0x03)
					return i;
		return 4;
	}

	int circle(int seat){
		//下一个玩家指针
		if (seat==3) return 0;
		return seat + 1;
	}

	//smallest card
	int getSmallestCard(int cbCardData[]){

		int small=0x4F;
		for (int i = 0; i < 13; i++) {
			if (cbCardData[i]==0x00)	continue;
			if (cbCardData[i]<small) small = cbCardData[i];
		}
		return small;
	}


}


