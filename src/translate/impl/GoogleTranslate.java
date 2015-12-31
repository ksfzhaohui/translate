package translate.impl;

import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import translate.AbstractTranslate;
import translate.impl.result.Result;
import translate.util.Language;

public class GoogleTranslate extends AbstractTranslate {

	private static Log logger = LogFactory.getLog(GoogleTranslate.class);

	private final String POST_URL = "http://translate.google.cn/translate_a/single?client=t";

	@Override
	public Result translate(String text, Language src_lang, Language target_lang) {
		Result result = new Result();
		StringBuffer resultBuffer = new StringBuffer();
		try {
			String url = POST_URL
					+ "&sl="
					+ src_lang.getValue()
					+ "&tl="
					+ target_lang.getValue()
					+ "&hl="
					+ src_lang
					+ "&dt=bd&dt=ex&dt=ld&dt=md&dt=qc&dt=rw&dt=rm&dt=ss&dt=t&dt=at&dt=sw&ie="
					+ ENCODING + "&oe=" + ENCODING
					+ "&otf=2&ssel=0&tsel=0&kc=2&q="
					+ URLEncoder.encode(text, ENCODING);

			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			String allInfo = EntityUtils.toString(response.getEntity());
			String resultArray[] = allInfo.split("]]")[0].split("]");
			for (int i = 0; i < resultArray.length - 1; i++) {
				resultBuffer.append(resultArray[i].split("\"")[1]);
			}
			String resultStr = resultBuffer.toString();
			resultStr = resultStr.replace("\\n", "\r\n");
		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}

}
