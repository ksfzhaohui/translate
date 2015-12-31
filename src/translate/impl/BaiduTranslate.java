package translate.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import translate.AbstractTranslate;
import translate.impl.result.Part;
import translate.impl.result.Result;
import translate.util.ConnectionManager;
import translate.util.Language;

public class BaiduTranslate extends AbstractTranslate {

	private static Log logger = LogFactory.getLog(BaiduTranslate.class);

	private final String POST_URL = "http://fanyi.baidu.com/v2transapi";

	@Override
	public Result translate(String text, Language src_lang, Language target_lang) {
		Result result = new Result();
		HttpClient httpClient = ConnectionManager.getHttpClient();
		try {
			HttpPost httppost = new HttpPost(POST_URL);
			List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
			formparams.add(new BasicNameValuePair("from", src_lang.getValue()));
			formparams
					.add(new BasicNameValuePair("to", target_lang.getValue()));
			formparams.add(new BasicNameValuePair("query", text));
			formparams.add(new BasicNameValuePair("transtype", "realtime"));
			formparams.add(new BasicNameValuePair("simple_means_flag", "3"));
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(
					formparams, ENCODING);
			httppost.setEntity(uefEntity);

			HttpResponse response = httpClient.execute(httppost);
			String resultJsonStr = EntityUtils.toString(response.getEntity());
			if (resultJsonStr == null || resultJsonStr.equals("")) {
				return null;
			}

			JSONObject jsonObject = new JSONObject(resultJsonStr);
			String trans_result = jsonObject.getJSONObject("trans_result")
					.getJSONArray("data").getJSONObject(0).getString("dst");
			result.setResult(trans_result);

			JSONArray parts = jsonObject.getJSONObject("dict_result")
					.getJSONObject("simple_means").getJSONArray("symbols")
					.getJSONObject(0).getJSONArray("parts");
			analysisParts(parts, result);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	private void analysisParts(JSONArray parts, Result result) {
		int length = parts.length();
		List<Part> partList = new ArrayList<Part>(length);
		for (int i = 0; i < length; i++) {
			JSONObject partObj = parts.getJSONObject(i);
			String partName = "";
			if (partObj.has("part")) {
				partName = partObj.getString("part");
			} else {
				partName = partObj.getString("part_name");
			}
			StringBuffer meansStr = new StringBuffer();
			JSONArray means = partObj.getJSONArray("means");
			for (int j = 0; j < means.length(); j++) {
				Object obj = means.get(j);
				if (obj instanceof JSONObject) {
					JSONObject jsonObj = (JSONObject) obj;
					meansStr.append(jsonObj.get("word_mean")).append(";");
				} else {
					meansStr.append(obj).append(";");
				}
			}
			Part part = new Part(partName, meansStr.toString());
			partList.add(part);
		}

		result.setParts(partList);
	}
}
