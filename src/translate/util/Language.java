package translate.util;

public enum Language {

	AUTO("自动", "auto"), //
	TAIWAN("台湾", "zh-TW"), //
	CHINA_CN("中文", "zh-CN"), //
	CHINA("中文", "zh"), //
	ENGLISH("英语", "en"), //
	JAPAN("日文", "ja");//

	private String name;
	private String value;

	private Language(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
