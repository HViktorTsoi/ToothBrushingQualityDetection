package tooth.data;

import org.json.JSONException;
import org.json.JSONObject;


public class GetHistoryServlet extends BaseServlet {
	public static String userid = "0";
	@Override
	protected String setServletName() {
		return "GetHistoryServlet";
	}
	@Override
	protected JSONObject setInputJSONObject() {
		JSONObject _ret = new JSONObject();
		try {
			_ret.put("userid", userid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return _ret;
	}

}
