package com.shine.controller.poker.cdd.old;

public class Constants {

	public static final int INVALID_CHAIR = 255;
	public static final int INIT_NEWROUND = 2;

	//////////////////////////////////////////////////////////////////////////
	// 消息结构体
	// 用户状态定义
	public static final int US_NULL = 0x00; // 没有状态
	public static final int US_READY = 0x01; // 同意状态
	public static final int US_PLAY = 0x02; // 游戏状态


	public final static int MAX_COUNT = 13;                 //最大数目

	public final static int   CT_ERROR			=		0				;					//错误类型
	public final static int   CT_SINGLE			=		1				;					//单牌类型
	public final static int   CT_DOUBLE		=			2				;					//对牌类型
	public final static int   CT_THREE		=			3				;					//三条类型
	public final static int   CT_FIVE_LINE	=			4				;					//顺子
	public final static int   CT_THREE_LINE_TAKE_TWO=	5		;							//葫芦

	public final static int   CT_FOUR_LINE_TAKE_ONE	=			6				;			//铁支
	public final static int   CT_FIVE_LINE_SAMECOLOR=			7				;			//同花顺
	public final static int   CT_FOUR_LINE2_TAKE_THREE=			8				;			//2铁支
	public final static int   CT_FOUR_LINE_TAKE_THREE_JOKER=	9				;			//五虎将
	public final static int   CT_FOUR_LINE2_TAKE_FOUR_JOKER=	10				;			//2五虎将
	public final static int   CT_DRAGON=						11				;			//一条龙

}
