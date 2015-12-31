package translate;

import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import translate.impl.result.Part;
import translate.impl.result.Result;
import translate.util.ConnectionManager;
import translate.util.Language;

public abstract class AbstractTranslate {

	protected final String ENCODING = "UTF-8";
	private final String NEW_LINE = "\n\r";

	protected DefaultHttpClient client;

	public AbstractTranslate() {
		client = ConnectionManager.getHttpClient();
	}

	/**
	 * 翻译(英文和中文互相转化)
	 * 
	 * @param text
	 *            文本
	 * @return
	 */
	public String translate(final String text) {
		Language src_lang = Language.ENGLISH;
		Language target_lang = Language.CHINA;
		if (isEnglish(text)) {
			src_lang = Language.ENGLISH;
			target_lang = Language.CHINA;
		} else if (isChina(text)) {
			src_lang = Language.CHINA;
			target_lang = Language.ENGLISH;
		}
		return translateConversion(text, src_lang, target_lang);
	}

	/**
	 * 翻译
	 * 
	 * @param text
	 *            待翻译的文本
	 * @param src_lang
	 *            待翻译文本的语言
	 * @param target_lang
	 *            翻译后的语言
	 * @return
	 */
	private String translateConversion(final String text,
			final Language src_lang, final Language target_lang) {
		Result result = translate(text, src_lang, target_lang);
		StringBuffer buffer = new StringBuffer();
		buffer.append(result.getResult()).append(NEW_LINE);
		List<Part> parts = result.getParts();
		if (parts != null && parts.size() > 0) {
			for (Part part : parts) {
				buffer.append(part.getPart()).append(part.getMeans())
						.append(NEW_LINE);
			}
		}
		return buffer.toString();
	}

	public abstract Result translate(final String text,
			final Language src_lang, final Language target_lang);

	/**
	 * 检查首字母是否为英文
	 * 
	 * @param stc
	 * @return
	 */
	private boolean isEnglish(String stc) {
		char c = stc.charAt(0);
		if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是中文
	 * 
	 * @param src
	 * @return
	 */
	private boolean isChina(String src) {
		return src.substring(0, 1).matches("[\\u4e00-\\u9fa5]+");
	}

}
