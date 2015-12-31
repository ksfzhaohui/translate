package translate.test;

import translate.AbstractTranslate;
import translate.impl.BaiduTranslate;

public class TranslateTest {

	public static void main(String[] args) {
		AbstractTranslate translate=new BaiduTranslate();
		translate.translate("China");
	}

}
