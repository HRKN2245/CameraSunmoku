package sample.extract;


//正規表現を登録するクラス
public class RegularExpression {
	public static final String YEAR = "[０-９0-9]{1,4}";
	public static final String MONTH = "[０0]?[０-９0-9]|[１1][０-２0-2]";
	public static final String DAY = "[１-２1-2][０-９0-9]|[３3][０-１0-1]|[０0]?[０-９0-9]|日";
	public static final String TIME = "([０-５0-5]?[０-９0-9])|([２2][０-３0-3])";
	public static final String END_TIME_EXPRESSION = "(から|～|終了時間|終了|‐|－|~)";
	public static final String COUNTER_WORD_DAYS = "(年|月|日|．|/|／|平成|H|Ｈ)";
	public static final String COUNTER_WORD_TIME = "(午前|午後|ＡＭ|ａｍ|ＰＭ|ｐｍ|時|分|：|:)";
	public static final String EXPRESSION_DAYS = "(日時|日付|開始|開催|終了|実施)";
	public static final String EXPRESSION_TIME = "(時間|開始|終了|開催|実施|日時)";
	public static final String EXPRESSION_ADDRESS = "(場所|住所|会場|開始|終了|開催)";
}
