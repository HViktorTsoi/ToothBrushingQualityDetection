package tooth.data;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import tooth.util.ServerConstant;


public abstract class BaseServlet {

    private String servletName = "";
    private JSONObject input_jsonObject = new JSONObject();
    private int responseCode = 0;
    private JSONObject output_jsonObject = new JSONObject();

    public BaseServlet() {
        servletName = setServletName();
        input_jsonObject = setInputJSONObject();
        runServlet();
    }

    private void runServlet() {
        try {
            URL url = new URL("http://" + ServerConstant.IP_ADDRESS + ":" + ServerConstant.PORT + "/" + ServerConstant.PROJECT_NAME + "/" + servletName);
            String content =  String.valueOf(input_jsonObject);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            /////设置connection的一些参数_line5/////
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(content.getBytes());
            os.close();
            responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                output_jsonObject = new JSONObject(readString(is));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String readString(InputStream is) {
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            return new String(baos.toByteArray());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @describe 直接return一个Servlet的名字即可
     * @return servletName
     */
    protected abstract String setServletName();

    /**
     * @descirbe return一个输入参数
     * @return JSONObject
     */
    protected abstract JSONObject setInputJSONObject();

    public int getResponseCode() {
        return responseCode;
    }

    public JSONObject getOutput_jsonObject() {
        return output_jsonObject;
    }


}
