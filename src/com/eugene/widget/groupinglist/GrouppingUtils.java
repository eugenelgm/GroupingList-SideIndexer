package com.eugene.widget.groupinglist;

import java.util.Arrays;

public class GrouppingUtils {

	public static final int UNKNOWN = -1;
	public static final int HANGUL = 0;
	public static final int ALPHABET = 1;
	public static final int NUMERIC = 2;

	public static final char HANGUL_BEGIN_UNICODE = 44032; // 가
	public static final char HANGUL_END_UNICODE = 55203; // 힣
	public static final char HANGUL_BASE_UNIT = 588;// 각자음 마다 가지는 글자수

	public static final char[] HANGUL_FIRST_PHONEME = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

	//public static final char[] JAPANESE_FIRST_PHONEME = { 'あ', 'か', 'さ', 'た', 'な', 'は', 'ま', 'や', 'ら', 'わ' };

	public static final char[] ALPHABET_FIRST_PHONEME = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z' };
	public static final char[] NUMERIC_FIRST_PHONEME = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	private static char getHangulFirstPhoneme(char koreanChar) {
		int hanBegin = (koreanChar - HANGUL_BEGIN_UNICODE);
		int index = hanBegin / HANGUL_BASE_UNIT;
		return HANGUL_FIRST_PHONEME[index];
	}

//	/**
//	 * 
//	 * @url - http://www.i18nguy.com/unicode/hiragana.html
//	 * @param c
//	 * @return
//	 */
//	private static char getJapaneseFirstPhoneme(char c) {
//		int result = 0;
//		if (isHiragana(c)) {
//			result = c - JAPANESE_BEGIN_UNICODE;
//		} else {
//			result = c - JAPANESE_KATAKANA_BEGIN;
//		}
//		int index = 0;
//		if (result >= 0 && result < 10) {
//			index = 0;
//		} else if (result >= 10 && result < 20) {
//			index = 1;
//		} else if (result >= 20 && result < 30) {
//			index = 2;
//		} else if (result >= 30 && result < 41) {
//			index = 3;
//		} else if (result >= 41 && result < 46) {
//			index = 4;
//		} else if (result >= 46 && result < 61) {
//			index = 5;
//		} else if (result >= 61 && result < 66) {
//			index = 6;
//		} else if (result >= 66 && result < 72) {
//			index = 7;
//		} else if (result >= 72 && result < 77) {
//			index = 8;
//		} else if (result >= 77 && result < 84) {
//			index = 9;
//		}
//		return JAPANESE_FIRST_PHONEME[index];
//	}

	private static boolean isHangul(char unicode) {
		return HANGUL_BEGIN_UNICODE <= unicode && unicode <= HANGUL_END_UNICODE;
	}

//	public static boolean isJapanese(char unicode) {
//		return (JAPANESE_BEGIN_UNICODE <= unicode && unicode <= JAPANESE_END_UNICODE)
//				|| (JAPANESE_HALF_KATAKANA_BEGIN <= unicode && unicode <= JAPANESE_HALF_KATAKANA_END);
//	}

//	public static boolean isJapanese(char[] s) {
//		for (char c : s) {
//			if (isJapanese(c))
//				return true;
//		}
//		return false;
//	}

	private static boolean containAlphabetPhoneme(char c) {
		return Arrays.binarySearch(ALPHABET_FIRST_PHONEME, c) > -1;
	}

	private static boolean containNumericPhoneme(char c) {
		return Arrays.binarySearch(NUMERIC_FIRST_PHONEME, c) > -1;
	}

	public static boolean containHangulFirstPhoneme(char c) {
		return Arrays.binarySearch(HANGUL_FIRST_PHONEME, c) > -1;
	}
	
	public static int startsWith(String source) {
		char first = getFirstPhoneme(source);
		return startsWith(first);
	}

	public static int startsWith(char first) {
		if (containHangulFirstPhoneme(first))
			return HANGUL;
		else if (containAlphabetPhoneme(first))
			return ALPHABET;
		else if (containNumericPhoneme(first))
			return NUMERIC;
		else
			return UNKNOWN;
	}

	/**
	 * first phoneme of value within search string value = 모토로이 search = ㅌ
	 * 
	 * @param value
	 * @param search
	 * @return
	 */
	public static String getHangulFirstPhoneme(String value, String search) {
		if (value == null || search == null)
			return null;
		StringBuilder sb = new StringBuilder();
		int minLen = Math.min(value.length(), search.length());
		for (int i = 0; i < minLen; i++) {
			char ch = value.charAt(i);
			if (isHangul(ch) && containHangulFirstPhoneme(search.charAt(i))) {
				sb.append(getHangulFirstPhoneme(ch));
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * value's first phoneme 모토로이 -> ㅁㅌㄹㅇ
	 * 
	 * @param value
	 * @return
	 */
	public static String getHangulFirstPhoneme(String value) {
		if (value == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (isHangul(c))
				sb.append(getHangulFirstPhoneme(c));
			else
				sb.append(c);
		}
		return sb.toString();
	}

	public static boolean isHangulFirstPhoneme(String value, String search) {
		if (value == null || search == null) {
			if (value != null)
				return true;
			return search == value;
		}
		return getHangulFirstPhoneme(value).contains(search);
	}

	public static char getFirstPhoneme(String value) {
		if (value == null || value.equals("")) {
			return ' ';
		}

		char ch = value.charAt(0);
		if (isHangul(ch)) {
			return getHangulFirstPhoneme(ch);
		} else {
			return Character.toLowerCase(ch);
		}
	}
}
