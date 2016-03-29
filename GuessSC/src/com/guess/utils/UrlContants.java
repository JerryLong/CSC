package com.guess.utils;

public class UrlContants {
	public static final String LOCATION = "http://api.caisichuan.com";
	/** 你传我猜 */
	public static final String NCWC_ = "/api/v2/ncwc";
	/** 最热题目 */
	public static final String NCWC_HOT = "/getHotNcwcAndVideoQuestions";
	/** 最热题目 */
	public static final String NCWC_NEW_NUM = "/getLatestNcwcAndVideoQuestions";
	/** 最新题目 */
	public static final String NCWC_NEW_ID = "/getNcwcAndVideoQuestionsById";
	/** 官方活动 */
	public static final String OFFICIAL = "/officalActivity/activityList";
	/** 上传题目 */
	public static final String NCWC_QUESTION = "/uploadTopic/normal";
	/** 上传题目  视屏 */
	public static final String NCWC_QUESTION_VIDEO = "/uploadTopic/normal/video";
	/** 上传答案 */
	public static final String NCWC_ANSWER = "/ncwc/submit";
	/** 答案 */
	public static final String NCWC_GET_ANSWER = "/getQuestionsById";
	/** 商城 */
	public static final String CMARK_LIST = "/store/goodsList";
	/** 兑换 */
	public static final String CMARK_GOODS = "/store/buyGoods";
	/** 获取你传我猜评论 */
	public static final String COMMENT_NCWC_GET = "/ncwc/getQuizComments";
	/** 回复你传我猜评论 */
	public static final String COMMENT_NCWC_REPLY = "/ncwc/replyNcwcQuestionComment";
	/** 提交你传我猜评论 */
	public static final String COMMENT_NCWC_SUBMIT = "/ncwc/submitComment";
	/** 删除你传我猜评论 */
	public static final String COMMENT_NCWC_DELETE = "/ncwc/deleteComment";
	/** 点赞，取消点赞 */
	public static final String NCWC_PRAISE = "/ncwc/evaluationQuiz";
	/** 题目提示 */
	public static final String NCWC_QUESTION_PROP = "/api/v1/getNcwcAnswerByIndex";
	/** 获取竞猜广场评论 */
	public static final String COMMENT_GAMBLE_GET = "/gambleGuess/getQuestionComments";
	/** 回复竞猜广场评论 */
	public static final String COMMENT_GAMBLE_REPLY = "/gambleGuess/replyGambleQuestionComment";
	/** 提交竞猜广场评论 */
	public static final String COMMENT_GAMBLE_SUBMIT = "/gambleGuess/submitComment";
	/** 删除竞猜广场评论 */
	public static final String COMMENT_GAMBLE_DELETE = "/gambleGuess/deleteComment";
	/** 删除竞猜广场评论 */
	public static final String GET_UPLOADTOKEN = "/app/uploadToken";

}
