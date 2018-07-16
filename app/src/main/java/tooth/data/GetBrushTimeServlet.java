package tooth.data;

import org.json.JSONObject;


public class GetBrushTimeServlet extends BaseServlet{
	   @Override
	   protected String setServletName() {
		   return "GetBrushTimeServlet";
	   }

	   @Override
	   protected JSONObject setInputJSONObject() {
		   JSONObject _ret = new JSONObject();
		   return _ret;
	   }	   
}
