package sample.extract;


//正規表現を登録するクラス
public class RegularExpression {
	public static final String YEAR = "(平成|昭和|Ｈ|([０-９0-9]{1,4})|年|．|/|／)";
	public static final String MONTH = "[０0]?[０-９0-9]|[１1][０-２0-2]";
	public static final String DAY = "[１-２1-2][０-９0-9]|[３3][０-１0-1]|[０0]?[０-９0-9]|日";
	public static final String TIME = "(午前|午後|ＡＭ|ａｍ|ＰＭ|ｐｍ|([０-１0-1]?[０-９0-9]|[２2][０-３0-3])|時|：|:|([０-５0-5]?[０-９0-9])|分])";
	public static final String END_TIME_EXPRESSION = "(から|～|終了時間|終了|‐|－|~)";
	public static final String EXPRESSION_DAYS = "(日時|日付|開始|開催|終了|実施)";
	public static final String EXPRESSION_TIME = "(時間|開始|終了|開催|実施|日時)";
	public static final String EXPRESSION_ADDRESS = "(場所|住所|会場|開始|終了|開催)";
}
