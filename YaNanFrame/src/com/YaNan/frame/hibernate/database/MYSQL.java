package com.YaNan.frame.hibernate.database;

public interface MYSQL {
	public interface CHARSET{
		public static final String DEFAULT = "DEFAULT";
		public static final String ARMSCII8 = "ARMSCII8";
		public static final String ASCII = "ASCII";
		public static final String BIG5 = "BIG5";
		public static final String BINARY = "BINARY";
		public static final String CP1250 = "CP1250";
		public static final String CP1251 = "CP1251";
		public static final String CP1256 = "CP1256";
		public static final String CP1257 = "CP1257";
		public static final String CP850 = "CP850";
		public static final String CP852 = "CP852";
		public static final String CP866 = "CP866";
		public static final String CP932 = "CP932";
		public static final String DEC8 = "DEC8";
		public static final String EUCJPMS = "EUCJPMS";
		public static final String EUCKR = "EUCKR";
		public static final String GB18030 = "GB18030";
		public static final String GB2312 = "GB2312";
		public static final String GBK = "GBK";
		public static final String GEOSTD8 = "GEOSTD8";
		public static final String GREEK = "GREEK";
		public static final String HEBREW = "HEBREW";
		public static final String HP8 = "HP8";
		public static final String KEYBCS2 = "KEYBCS2";
		public static final String KOI8R = "KOI8R";
		public static final String KOI8U = "KOI8U";
		public static final String LATIN1 = "LATIN1";
		public static final String LATIN2 = "LATIN2";
		public static final String LATIN5 = "LATIN5";
		public static final String LATIN7 = "LATIN7";
		public static final String MACCE = "MACCE";
		public static final String MACROMAN = "MACROMAN";
		public static final String SJIS = "SJIS";
		public static final String SWE7 = "SWE7";
		public static final String TIS620 = "TIS620";
		public static final String UCS2 = "UCS2";
		public static final String UJIS = "UJIS";
		public static final String UTF16 = "UTF16";
		public static final String UTF16LE = "UTF16LE";
		public static final String UTF32 = "UTF32";
		public static final String UTF8 = "UTF8";
		public static final String UTF8MB4 = "UTF8MB4";
	}
	public interface DEFAULT{
		public static final String DEFAULT = "DEFAULT";
		
	}
	public interface ARMSCII8{
		public static final String ARMSCII8_BIN = "ARMSCII8_BIN";
		public static final String ARMSCII8_GENERAL_CI = "ARMSCII8_GENERAL_CI";
	}
	public interface ASCII{
		public static final String ASCII_BIN = "ASCII_BIN";
		public static final String ASCII_GENERAL_CI = "ASCII_GENERAL_CI";
	}
	public interface BIG5{
		public static final String BIG5_BIN = "BIG5_BIN";
		public static final String BIG5_CHINESE_CI = "BIG5_CHINESE_CI";
	}
	public interface BINARY{
		public static final String BINARY = "BINARY";
	}
	public interface CP1250{
		public static final String CP1250_BIN = "CP1250_BIN";
		public static final String CP1250_CROATIAN_CI = "CP1250_CROATIAN_CI";
		public static final String CP1250_CZECH_CS = "CP1250_CZECH_CS";
		public static final String CP1250_GENERAL_CI = "CP1250_GENERAL_CI";
		public static final String CP1250_POLISH_CI = "CP1250_POLISH_CI";
	}
	public interface CP1251{
		public static final String CP1251_BIN = "CP1251_BIN";
		public static final String CP1251_BULGARIAN_CI = "CP1251_BULGARIAN_CI";
		public static final String CP1251_GENERAL_CS = "CP1251_GENERAL_CS";
		public static final String CP1251_GENERAL_CI = "CP1251_GENERAL_CI";
		public static final String CP1251_UKRAINIAN_CI = "CP1251_UKRAINIAN_CI";
	}
	public interface CP1256{
		public static final String CP1256_BIN = "CP1256_BIN";
		public static final String CP1256_GENERAL_CI = "CP1256_GENERAL_CI";
	}
	public interface CP1257{
		public static final String CP1257_BIN = "CP1257_BIN";
		public static final String CP1257_GENERAL_CI = "CP1257_GENERAL_CI";
		public static final String CP1257_LITHUANIAN_CI = "CP1257_LITHUANIAN_CI";
	}
	public interface CP850{
		public static final String CP850_BIN = "CP850_BIN";
		public static final String CP850_GENERAL_CI = "CP850_GENERAL_CI";
	}
	public interface CP852{
		public static final String CP852_BIN = "CP852_BIN";
		public static final String CP852_GENERAL_CI = "CP852_GENERAL_CI";
	}
	public interface CP866{
		public static final String CP866_BIN = "CP866_BIN";
		public static final String CP866_GENERAL_CI = "CP866_GENERAL_CI";
	}
	public interface CP932{
		public static final String CP932_BIN = "CP932_BIN";
		public static final String CP932_GENERAL_CI = "CP932_GENERAL_CI";
	}
	public interface DEC8{
		public static final String DEC8_BIN = "DEC8_BIN";
		public static final String DEC8_SWEDISH_CI = "DEC8_SWEDISH_CI";
	}
	public interface EUCJPMS{
		public static final String EUCJPMS_BIN = "EUCJPMS_BIN";
		public static final String EUCJPMS_JAPANESE_CI = "EUCJPMS_JAPANESE_CI";
	}
	public interface EUCKR{
		public static final String EUCKR_BIN = "EUCKR_BIN";
		public static final String EUCKR_KOREAN_CI = "EUCKR_KOREAN_CI";
	}
	public interface GB18030{
		public static final String GB18030_BIN = "GB18030_BIN";
		public static final String GB18030_CHINESE_CI = "GB18030_CHINESE_CI";
		public static final String GB18030_UNICODE_520_CI = "GB18030_UNICODE_520_CI";
	}
	public interface GB2312{
		public static final String GB2312_BIN = "GB2312_BIN";
		public static final String GB2312_CHINESE_CI = "GB2312_CHINESE_CI";
	}
	public interface GBK{
		public static final String GBK_BIN = "GBK_BIN";
		public static final String GBK_CHINESE_CI = "GBK_CHINESE_CI";
	}
	public interface GEOSTD8{
		public static final String GEOSTD8_BIN = "GEOSTD8_BIN";
		public static final String GEOSTD8_GENERAL_CI = "GEOSTD8_GENERAL_CI";
	}
	public interface GREEK{
		public static final String GREEK_BIN = "GREEK_BIN";
		public static final String GREEK_GENERAL_CI = "GREEK_GENERAL_CI";
	}
	public interface HEBREW{
		public static final String HEBREW_BIN = "HEBREW_BIN";
		public static final String HEBREW_GENERAL_CI = "HEBREW_GENERAL_CI";
	}
	public interface HP8{
		public static final String HP8_BIN = "HP8_BIN";
		public static final String HP8_ENGLISH_CI = "HP8_ENGLISH_CI";
	}
	public interface KEYBCS2{
		public static final String KEYBCS2_BIN = "KEYBCS2_BIN";
		public static final String KEYBCS2_GENERAL_CI = "KEYBCS2_GENERAL_CI";
	}
	public interface KOI8R{
		public static final String KOI8R_BIN = "KOI8R_BIN";
		public static final String KOI8R_GENERAL_CI = "KOI8R_GENERAL_CI";
	}
	public interface KOI8U{
		public static final String KOI8U_BIN = "KOI8U_BIN";
		public static final String KOI8U_GENERAL_CI = "KOI8U_GENERAL_CI";
	}
	public interface LATIN1{
		public static final String LATIN1_BIN = "LATIN1_BIN";
		public static final String LATIN1_DANISH_CI = "LATIN1_DANISH_CI";
		public static final String LATIN1_GENERAL_CI = "LATIN1_GENERAL_CI";
		public static final String LATIN1_GENERAL_CS = "LATIN1_GENERAL_CS";
		public static final String LATIN1_GERMAN1_CI = "LATIN1_GERMAN1_CI";
		public static final String LATIN1_GERMAN2_CI = "LATIN1_GERMAN2_CI";
		public static final String LATIN1_SPANISH_CI = "LATIN1_SPANISH_CI";
		public static final String LATIN1_SWEDISH_CI = "LATIN1_SWEDISH_CI";
	}
	public interface LATIN2{
		public static final String LATIN2_BIN = "LATIN2_BIN";
		public static final String LATIN2_CROATIAN_CI = "LATIN2_CROATIAN_CI";
		public static final String LATIN2_CZECH_CS = "LATIN2_CZECH_CS";
		public static final String LATIN2_GERMAN1_CI = "LATIN2_GERMAN1_CI";
		public static final String LATIN2_HUNGARIAN_CI = "LATIN2_HUNGARIAN_CI";
	}
	public interface LATIN5{
		public static final String LATIN5_BIN = "LATIN5_BIN";
		public static final String LATIN5_TURKISH_CI = "LATIN5_TURKISH_CI";
	}
	public interface LATIN7{
		public static final String LATIN7_BIN = "LATIN7_BIN";
		public static final String LATIN7_ESTONIAN_CS = "LATIN7_ESTONIAN_CS";
		public static final String LATIN7_GENERAL_CI = "LATIN7_GENERAL_CI";
		public static final String LATIN7_GENERAL_CS = "LATIN7_GENERAL_CS";
	}
	public interface MACCE{
		public static final String MACCE_BIN = "MACCE_BIN";
		public static final String MACCE_GENERAL_CI = "MACCE_GENERAL_CI";
	}
	public interface MACROMAN{
		public static final String MACROMAN_BIN = "MACROMAN_BIN";
		public static final String MACROMAN_GENERAL_CI = "MACROMAN_GENERAL_CI";
	}
	public interface SJIS{
		public static final String SJIS_BIN = "SJIS_BIN";
		public static final String SJIS_JAPANESE_CI = "SJIS_JAPANESE_CI";
	}
	public interface SWE7{
		public static final String SWE7_BIN = "SWE7_BIN";
		public static final String SWE7_SWEDISH_CI = "SWE7_SWEDISH_CI";
	}
	public interface TIS620{
		public static final String TIS620_BIN = "TIS620_BIN";
		public static final String TIS620_THAI_CI = "TIS620_THAI_CI";
	}
	public interface UCS2{
		public static final String UCS2_BIN = "UCS2_BIN";
		public static final String UCS2_CROATIAN_CI = "UCS2_CROATIAN_CI";
		public static final String UCS2_CZECH_CI = "UCS2_CZECH_CI";
		public static final String UCS2_DANISH_CI = "UCS2_DANISH_CI";
		public static final String UCS2_ESPERANTO_CI = "UCS2_ESPERANTO_CI";
		public static final String UCS2_ESTONIAN_CI = "UCS2_ESTONIAN_CI";
		public static final String UCS2_GENERAL_CI = "UCS2_GENERAL_CI";
		public static final String UCS2_GENERAL_MYSQL500_CI = "UCS2_GENERAL_MYSQL500_CI";
		public static final String UCS2_GENERAL2_CI = "UCS2_GENERAL2_CI";
		public static final String UCS2_HUNGARIAN_CI = "UCS2_HUNGARIAN_CI";
		public static final String UCS2_ICELANDIC_CI = "UCS2_ICELANDIC_CI";
		public static final String UCS2_LATVIAN_CI = "UCS2_LATVIAN_CI";
		public static final String UCS2_LITHUANIAN_CI = "UCS2_LITHUANIAN_CI";
		public static final String UCS2_PERSIAN_CI = "UCS2_PERSIAN_CI";
		public static final String UCS2_POLISH_CI = "UCS2_POLISH_CI";
		public static final String UCS2_ROMAN_CI = "UCS2_ROMAN_CI";
		public static final String UCS2_ROMANIAN_CI = "UCS2_ROMANIAN_CI";
		public static final String UCS2_SINHALA_CI = "UCS2_SINHALA_CI";
		public static final String UCS2_SLOVAK_CI = "UCS2_SLOVAK_CI";
		public static final String UCS2_SPANISH2_CI = "UCS2_SPANISH2_CI";
		public static final String UCS2_SPANISH_CI = "UCS2_SPANISH_CI";
		public static final String UCS2_SWEDISH_CI = "UCS2_SWEDISH_CI";
		public static final String UCS2_TURKISH_CI = "UCS2_TURKISH_CI";
		public static final String UCS2_UNICODE_520_CI = "UCS2_UNICODE_520_CI";
		public static final String UCS2_UNICODE_CI = "UCS2_UNICODE_CI";
		public static final String UCS2_VIETNAMESE_CI = "UCS2_VIETNAMESE_CI";
	}
	public interface UJIS{
		public static final String UJIS_BIN = "UJIS_BIN";
		public static final String UJIS_JAPANESE_CI = "UJIS_JAPANESE_CI";
	}
	public interface UTF16{
		public static final String UTF16_BIN = "UTF16_BIN";
		public static final String UTF16_CROATIAN_CI = "UTF16_CROATIAN_CI";
		public static final String UTF16_CZECH_CI = "UTF16_CZECH_CI";
		public static final String UTF16_DANISH_CI = "UTF16_DANISH_CI";
		public static final String UTF16_ESPERANTO_CI = "UTF16_ESPERANTO_CI";
		public static final String UTF16_ESTONIAN_CI = "UTF16_ESTONIAN_CI";
		public static final String UTF16_GENERAL_CI = "UTF16_GENERAL_CI";
		public static final String UTF16_GENERAL2_CI = "UTF16_GENERAL2_CI";
		public static final String UTF16_HUNGARIAN_CI = "UTF16_HUNGARIAN_CI";
		public static final String UTF16_ICELANDIC_CI = "UTF16_ICELANDIC_CI";
		public static final String UTF16_LATVIAN_CI = "UTF16_LATVIAN_CI";
		public static final String UTF16_LITHUANIAN_CI = "UTF16_LITHUANIAN_CI";
		public static final String UTF16_PERSIAN_CI = "UTF16_PERSIAN_CI";
		public static final String UTF16_POLISH_CI = "UTF16_POLISH_CI";
		public static final String UTF16_ROMAN_CI = "UTF16_ROMAN_CI";
		public static final String UTF16_ROMANIAN_CI = "UTF16_ROMANIAN_CI";
		public static final String UTF16_SINHALA_CI = "UTF16_SINHALA_CI";
		public static final String UTF16_SLOVAK_CI = "UTF16_SLOVAK_CI";
		public static final String UTF16_SLOVENIAN_CI = "UTF16_SLOVENIAN_CI";
		public static final String UTF16_SPANISH_CI = "UTF16_SPANISH_CI";
		public static final String UTF16_SPANISH2_CI = "UTF16_SPANISH2_CI";
		public static final String UTF16_SWEDISH_CI = "UTF16_SWEDISH_CI";
		public static final String UTF16_TURKISH_CI = "UTF16_TURKISH_CI";
		public static final String UTF16_UNICODE_520_CI = "UTF16_UNICODE_520_CI";
		public static final String UTF16_UNICODE_CI = "UTF16_UNICODE_CI";
		public static final String UTF16_VIETNAMESE_CI = "UTF16_VIETNAMESE_CI";
		public static final String UTF16LE_BIN = "UTF16LE_BIN";
		public static final String UTF16LE_GENERAL_BIN = "UTF16LE_GENERAL_BIN";
	}
	public interface UTF16LE{
		public static final String UTF16LE_BIN = "UTF16LE_BIN";
		public static final String UTF16LE_GENERAL_BIN = "UTF16LE_GENERAL_BIN";
	}
	public interface UTF32{
		public static final String UTF32_BIN = "UTF32_BIN";
		public static final String UTF32_CROATIAN_CI = "UTF32_CROATIAN_CI";
		public static final String UTF32_CZECH_CI = "UTF32_CZECH_CI";
		public static final String UTF32_DANISH_CI = "UTF32_DANISH_CI";
		public static final String UTF32_ESPERANTO_CI = "UTF32_ESPERANTO_CI";
		public static final String UTF32_ESTONIAN_CI = "UTF32_ESTONIAN_CI";
		public static final String UTF32_GENERAL_CI = "UTF32_GENERAL_CI";
		public static final String UTF32_GENERAL2_CI = "UTF32_GENERAL2_CI";
		public static final String UTF32_HUNGARIAN_CI = "UTF32_HUNGARIAN_CI";
		public static final String UTF32_ICELANDIC_CI = "UTF32_ICELANDIC_CI";
		public static final String UTF32_LATVIAN_CI = "UTF32_LATVIAN_CI";
		public static final String UTF32_LITHUANIAN_CI = "UTF32_LITHUANIAN_CI";
		public static final String UTF32_PERSIAN_CI = "UTF32_PERSIAN_CI";
		public static final String UTF32_POLISH_CI = "UTF32_POLISH_CI";
		public static final String UTF32_ROMAN_CI = "UTF32_ROMAN_CI";
		public static final String UTF32_ROMANIAN_CI = "UTF32_ROMANIAN_CI";
		public static final String UTF32_SINHALA_CI = "UTF32_SINHALA_CI";
		public static final String UTF32_SLOVAK_CI = "UTF32_SLOVAK_CI";
		public static final String UTF32_SLOVENIAN_CI = "UTF32_SLOVENIAN_CI";
		public static final String UTF32_SPANISH_CI = "UTF32_SPANISH_CI";
		public static final String UTF32_SPANISH2_CI = "UTF32_SPANISH2_CI";
		public static final String UTF32_SWEDISH_CI = "UTF32_SWEDISH_CI";
		public static final String UTF32_TURKISH_CI = "UTF32_TURKISH_CI";
		public static final String UTF32_UNICODE_520_CI = "UTF32_UNICODE_520_CI";
		public static final String UTF32_UNICODE_CI = "UTF32_UNICODE_CI";
		public static final String UTF32_VIETNAMESE_CI = "UTF32_VIETNAMESE_CI";
	}
	public interface UTF8{
		public static final String UTF8_BIN = "UTF8_BIN";
		public static final String UTF8_CROATIAN_CI = "UTF8_CROATIAN_CI";
		public static final String UTF8_CZECH_CI = "UTF8_CZECH_CI";
		public static final String UTF8_DANISH_CI = "UTF8_DANISH_CI";
		public static final String UTF8_ESPERANTO_CI = "UTF8_ESPERANTO_CI";
		public static final String UTF8_ESTONIAN_CI = "UTF8_ESTONIAN_CI";
		public static final String UTF8_GENERAL_CI = "UTF8_GENERAL_CI";
		public static final String UTF8_GENERAL_MYSQL500_CI = "UTF8_GENERAL_MYSQL500_CI";
		public static final String UTF8_GENERAL2_CI = "UTF8_GENERAL2_CI";
		public static final String UTF8_HUNGARIAN_CI = "UTF8_HUNGARIAN_CI";
		public static final String UTF8_ICELANDIC_CI = "UTF8_ICELANDIC_CI";
		public static final String UTF8_LATVIAN_CI = "UTF8_LATVIAN_CI";
		public static final String UTF8_LITHUANIAN_CI = "UTF8_LITHUANIAN_CI";
		public static final String UTF8_PERSIAN_CI = "UTF8_PERSIAN_CI";
		public static final String UTF8_POLISH_CI = "UTF8_POLISH_CI";
		public static final String UTF8_ROMAN_CI = "UTF8_ROMAN_CI";
		public static final String UTF8_ROMANIAN_CI = "UTF8_ROMANIAN_CI";
		public static final String UTF8_SINHALA_CI = "UTF8_SINHALA_CI";
		public static final String UTF8_SLOVAK_CI = "UTF8_SLOVAK_CI";
		public static final String UTF8_SLOVENIAN_CI = "UTF8_SLOVENIAN_CI";
		public static final String UTF8_SPANISH_CI = "UTF8_SPANISH_CI";
		public static final String UTF8_SPANISH2_CI = "UTF8_SPANISH2_CI";
		public static final String UTF8_SWEDISH_CI = "UTF8_SWEDISH_CI";
		public static final String UTF8_TURKISH_CI = "UTF8_TURKISH_CI";
		public static final String UTF8_UNICODE_520_CI = "UTF8_UNICODE_520_CI";
		public static final String UTF8_UNICODE_CI = "UTF8_UNICODE_CI";
		public static final String UTF8_VIETNAMESE_CI = "UTF8_VIETNAMESE_CI";
		
		public static final String UTF8MB4_BIN = "UTF8MB4_BIN";
		public static final String UTF8MB4_CROATIAN_CI = "UTF8_CROATIAN_CI";
		public static final String UTF8MB4_CZECH_CI = "UTF8_CZECH_CI";
		public static final String UTF8MB4_DANISH_CI = "UTF8_DANISH_CI";
		public static final String UTF8MB4_ESPERANTO_CI = "UTF8_ESPERANTO_CI";
		public static final String UTF8MB4_ESTONIAN_CI = "UTF8_ESTONIAN_CI";
		public static final String UTF8MB4_GENERAL_CI = "UTF8_GENERAL_CI";
		public static final String UTF8MB4_GENERAL2_CI = "UTF8_GENERAL2_CI";
		public static final String UTF8MB4_HUNGARIAN_CI = "UTF8_HUNGARIAN_CI";
		public static final String UTF8MB4_ICELANDIC_CI = "UTF8_ICELANDIC_CI";
		public static final String UTF8MB4_LATVIAN_CI = "UTF8_LATVIAN_CI";
		public static final String UTF8MB4_LITHUANIAN_CI = "UTF8_LITHUANIAN_CI";
		public static final String UTF8MB4_PERSIAN_CI = "UTF8_PERSIAN_CI";
		public static final String UTF8MB4_POLISH_CI = "UTF8_POLISH_CI";
		public static final String UTF8MB4_ROMAN_CI = "UTF8_ROMAN_CI";
		public static final String UTF8MB4_ROMANIAN_CI = "UTF8_ROMANIAN_CI";
		public static final String UTF8MB4_SINHALA_CI = "UTF8_SINHALA_CI";
		public static final String UTF8MB4_SLOVAK_CI = "UTF8_SLOVAK_CI";
		public static final String UTF8MB4_SLOVENIAN_CI = "UTF8_SLOVENIAN_CI";
		public static final String UTF8MB4_SPANISH_CI = "UTF8_SPANISH_CI";
		public static final String UTF8MB4_SPANISH2_CI = "UTF8_SPANISH2_CI";
		public static final String UTF8MB4_SWEDISH_CI = "UTF8_SWEDISH_CI";
		public static final String UTF8MB4_TURKISH_CI = "UTF8_TURKISH_CI";
		public static final String UTF8MB4_UNICODE_520_CI = "UTF8_UNICODE_520_CI";
		public static final String UTF8MB4_UNICODE_CI = "UTF8_UNICODE_CI";
		public static final String UTF8MB4_VIETNAMESE_CI = "UTF8_VIETNAMESE_CI";
	}
	public interface UTF8MB4{
		public static final String UTF8MB4_BIN = "UTF8MB4_BIN";
		public static final String UTF8MB4_CROATIAN_CI = "UTF8_CROATIAN_CI";
		public static final String UTF8MB4_CZECH_CI = "UTF8_CZECH_CI";
		public static final String UTF8MB4_DANISH_CI = "UTF8_DANISH_CI";
		public static final String UTF8MB4_ESPERANTO_CI = "UTF8_ESPERANTO_CI";
		public static final String UTF8MB4_ESTONIAN_CI = "UTF8_ESTONIAN_CI";
		public static final String UTF8MB4_GENERAL_CI = "UTF8_GENERAL_CI";
		public static final String UTF8MB4_GENERAL2_CI = "UTF8_GENERAL2_CI";
		public static final String UTF8MB4_HUNGARIAN_CI = "UTF8_HUNGARIAN_CI";
		public static final String UTF8MB4_ICELANDIC_CI = "UTF8_ICELANDIC_CI";
		public static final String UTF8MB4_LATVIAN_CI = "UTF8_LATVIAN_CI";
		public static final String UTF8MB4_LITHUANIAN_CI = "UTF8_LITHUANIAN_CI";
		public static final String UTF8MB4_PERSIAN_CI = "UTF8_PERSIAN_CI";
		public static final String UTF8MB4_POLISH_CI = "UTF8_POLISH_CI";
		public static final String UTF8MB4_ROMAN_CI = "UTF8_ROMAN_CI";
		public static final String UTF8MB4_ROMANIAN_CI = "UTF8_ROMANIAN_CI";
		public static final String UTF8MB4_SINHALA_CI = "UTF8_SINHALA_CI";
		public static final String UTF8MB4_SLOVAK_CI = "UTF8_SLOVAK_CI";
		public static final String UTF8MB4_SLOVENIAN_CI = "UTF8_SLOVENIAN_CI";
		public static final String UTF8MB4_SPANISH_CI = "UTF8_SPANISH_CI";
		public static final String UTF8MB4_SPANISH2_CI = "UTF8_SPANISH2_CI";
		public static final String UTF8MB4_SWEDISH_CI = "UTF8_SWEDISH_CI";
		public static final String UTF8MB4_TURKISH_CI = "UTF8_TURKISH_CI";
		public static final String UTF8MB4_UNICODE_520_CI = "UTF8_UNICODE_520_CI";
		public static final String UTF8MB4_UNICODE_CI = "UTF8_UNICODE_CI";
		public static final String UTF8MB4_VIETNAMESE_CI = "UTF8_VIETNAMESE_CI";
	}
}
