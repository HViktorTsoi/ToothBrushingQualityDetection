package tooth.data;

import org.json.JSONObject;


public class GetCurrentStateServlet extends BaseServlet{
	   @Override
	   protected String setServletName() {
		   return "GetCurrentStateServlet";
	   }

	   @Override
	   protected JSONObject setInputJSONObject() {
		   JSONObject _ret = new JSONObject();
		   return _ret;
	   }	   
}
