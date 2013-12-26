package jp.recognize.scenery.android;


//正規表現を登録するクラス
public class RegularExpression {
	public static final String YEAR = "(([０-９0-9]{2})|[０-９0-9]{4})";
	public static final String MONTH = "[１1][０-２0-2]|[０0]?[１-９1-9]";
	public static final String DAYS = "([３3][０-１0-1]|[１-２1-2][０-９0-9]|[０0]?[０-９0-9])";
	public static final String HOUR = "([２2][０-３0-3]|[０-１0-1]?[０-９0-9])";
	public static final String MINUTE = "([０-５0-5][０-９0-9])";
	public static final String EXPRESSION_DAYS = "(日時|日付|開始|開催|終了|実施|日程)";
	public static final String EXPRESSION_TIME = "(時間|開始|終了|開催|実施|日時|日程)";
	public static final String EXPRESSION_ADDRESS = "(場所|住所|会場|開始|終了|開催)";
	public static final String END_TIME_EXPRESSION = "(から|～|終了時間|終了|‐|－|~)";
	public static final String CONTEXT_PREWORD_YEAR = "(平成|Ｈ|H)";
	public static final String CONTEXT_AFTWORD_YEAR = "(年|．|/|／)";
	public static final String CONTEXT_PREWORD_MONTH = "(．|/|／)";
	public static final String CONTEXT_AFTWORD_MONTH = "(月|．|/|／)";
	public static final String CONTEXT_PREWORD_DAYS = "(月|．|/|／)";
	public static final String CONTEXT_AFTWORD_DAYS = "日";
	public static final String CONTEXT_WORD_HOUR = "(時|：|:)";
	public static final String CONTEXT_PREWORD_MINUTE = "(時|：|:)";
	public static final String CONTEXT_AFTWORD_MINUTE = "分";
}
