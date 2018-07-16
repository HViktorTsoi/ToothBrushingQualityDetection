package tooth.data;

import org.json.JSONException;


public class ServerInterface {
	public static int getCurrentState() {
		GetCurrentStateServlet mServlet = new GetCurrentStateServlet();
		int state = 0;
		try {
			state = Integer.valueOf(mServlet.getOutput_jsonObject().getString("current"));			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return state;
	}
	public static int getLatestScore() {
		GetBrushTimeServlet mServlet = new GetBrushTimeServlet();
		float score = 0;
		try {			
			score = score + oneActionScore(mServlet.getOutput_jsonObject().getString("class1"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class2"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class3"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class4"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class5"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class6"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class7"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class8"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class9"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class10"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class11"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class12"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class13"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class14"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class15"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class16"))
			              + oneActionScore(mServlet.getOutput_jsonObject().getString("class17"));		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		int _ret = (int)score;
		if (_ret > 100) _ret = 100;
		return _ret;
	}
	private static float oneActionScore(String time_str) {
		long time = Long.valueOf(time_str);
		if (time <= 2000) return (float) (6.25 * 0.4);
		if (time <= 5000) return (float) (6.25 * 0.5);
		if (time <= 7000) return (float) (6.25 * 0.7);
		else return (float) (6.25 * 1);		
	}
	public static String getNotYet() {
		String _ret = "";
		GetBrushTimeServlet mServlet = new GetBrushTimeServlet();
		try {
			_ret = _ret + hasFinish(mServlet.getOutput_jsonObject().getString("class1"), 1)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class2"), 2)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class3"), 3)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class4"), 4)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class5"), 5)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class6"), 6)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class7"), 7)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class8"), 8)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class9"), 9)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class10"), 10)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class11"), 11)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class12"), 12)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class13"), 13)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class14"), 14)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class15"), 15)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class16"), 16)
			            + hasFinish(mServlet.getOutput_jsonObject().getString("class17"), 17);		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (_ret.equals("")) return "";
		else return _ret.substring(1);
	}
	private static String hasFinish(String time_str, int classtype) {
		long time = Long.valueOf(time_str);
		if (time < 10000) return  "," + String.valueOf(classtype);
		else return "";
	}
	public static String getHistory(int userid) {
		GetHistoryServlet.userid = String.valueOf(userid);
		GetHistoryServlet mServlet = new GetHistoryServlet();
		String _ret = "";
		try {
			_ret = mServlet.getOutput_jsonObject().getString("history");
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return _ret;
	}
}
