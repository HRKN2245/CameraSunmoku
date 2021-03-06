package jp.recognize.scenery.android;

//正規表現を登録するクラス
public class RegularExpression {
	public static final String YEAR = "(平成|昭和|Ｈ)?([０-９]{1,4})[年．／]";
	public static final String MONTH = "(([０]?[０-９]|[１][０-２])[月．／])";
	public static final String DAY = "(([１-２][０-９]|[３][０-１]|[０]?[０-９])[日]?)";
	public static final String TIME = "((午前|午後|ＡＭ|ａｍ|ＰＭ|ｐｍ)?([０-１0-1]?[０-９0-9]|[２2][０-３0-3])[時：:])(([０-５0-5]?[０-９0-9])[分]?)?";
	public static final String END_TIME_EXPRESSION = "(から|～|終了時間|終了|‐|－|~)";
	public static final String ScheduleExpression = "(日時|時間|日付|場所|会場|住所|実施|開始|終了|開催)";
}
