package org.daisy.reader.util;

public class StringUtils {

	public static String normalizeWhitespace(String data) {
		StringBuilder sb = new StringBuilder();
		boolean prevWS = true;
		int codePointCount = data.codePointCount(0, data.length());
		for (int i = 0; i < codePointCount; i++) {
			int codePoint = data.codePointAt(i);
			if(Character.isWhitespace(codePoint)) {
				if(i==codePointCount-1) break;		
				if(!prevWS) sb.append(' ');
				prevWS = true;
			}else{
				sb.append(Character.toChars(codePoint));
				prevWS = false;
			}
		}
		if(Character.isWhitespace(sb.charAt(sb.length()-1))) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	
	/**
	 * @return true if all characters in the string are within the range of 7bit
	 *         ascii, false otherwise
	 */
	public static final boolean isAscii(
			String string) {
		return isAscii(string.toCharArray());
	}

	/**
	 * @return true if all characters in the char array are within the range of
	 *         7bit ascii, false otherwise
	 */
	public static final boolean isAscii(char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isAscii(ch[i])) {
				return false;
			}
		}// ch.length
		return true;
	}

	/**
	 * @return true if the character is within the range of 7bit ascii, false
	 *         otherwise
	 */
	public static boolean isAscii(char ch) {
		return (ch < 128);
	}

	/**
	 * @return true if the character is within the range of alphabetic 7bit ascii (=[A-Za-z]), 
	 * false otherwise
	 */
	public static boolean isAsciiAlpha(char ch) {
		return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
	}

	/**
	 * @return true if the character is within the range of numeric 7bit ascii (=[0-9]), 
	 * false otherwise
	 */

	public static boolean isAsciiNumeric(char ch) {
		return (ch >= '0' && ch <= '9');
	}

	/**
	 * @return true if the character is within the range of alphanumeric 7bit ascii (=[A-Za-z0-9]), 
	 * false otherwise
	 */

	public static boolean isAsciiAlphanumeric(char ch) {
		return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9');
	}
	  
	/**
	 * @return true if all characters in the string are within the printable
	 *         range of 7bit ascii, false otherwise
	 */
	public static final boolean isAsciiPrintable(
			String string) {
		return isAsciiPrintable(string.toCharArray());
	}

	/**
	 * @return true if all characters in the char array are within the printable
	 *         range of 7bit ascii, false otherwise
	 */
	public static final boolean isAsciiPrintable(char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isAsciiPrintable(ch[i])) {
				return false;
			}
		}// ch.length
		return true;
	}

	/**
	 * @return true if the character is within the printable range of 7bit
	 *         ascii, false otherwise
	 */
	public static boolean isAsciiPrintable(char ch) {
		return (ch < 127 && ch > 31);
	}

	/**
	 * @return true if the char is among the
	 * chars allowed by inparam FilenameRestriction
	 */
	public static boolean isFilenameCompatible(char ch, FilenameRestriction rule) {
		//48-57 = 0-9, 65-90 = A-Z, 97-122 = a-z, 45 = -, 95 = _, 46 = .
		if(rule == FilenameRestriction.Z3986) {
			return ((ch>47&&ch<58)||ch==45||ch==46||ch==95||(ch>64&&ch<91)||(ch>96&&ch<123));
		}
		return ((ch>47&&ch<58)||ch==45||ch==95||(ch>64&&ch<91)||(ch>96&&ch<123));
	}
	
	/**
	 * @return true if all characters in the string are among the
	 * chars allowed by inparam FilenameRestriction
	 */
	public static boolean isFilenameCompatible(String string, FilenameRestriction rule) {
		return isFilenameCompatible(string.toCharArray(), rule);
	}
	
	/**
	 * @return true if all chars in the array are among the
	 * chars allowed by inparam FilenameRestriction
	 */
	public static boolean isFilenameCompatible(char[] ch, FilenameRestriction rule) {
		for (int i = 0; i < ch.length; i++) {
			if (!isFilenameCompatible(ch[i], rule)) {
				return false;
			}
		}
		return true;				
	}
	
	public enum FilenameRestriction {
		ISO_9660_LEVEL1,
		Z3986
	}
	
	/**
	 * @return true if the char is within the control character (non
	 *         printable) range of 7bit ascii, false otherwise
	 */
	public static boolean isAsciiControl(char ch) {
		return (ch == 127 || ch < 32);
	}

	/**
	 * @return true if the char is defined (by unicode) as whitespace, false otherwise
	 */
	public static boolean isUnicodeWhitespace(char ch) {
		return isUnicodeWhitespace(Character.valueOf(ch));
	}

	/**
	 * @return true if each char in the array is defined (by unicode) as whitespace, false otherwise
	 */
	public static boolean isUnicodeWhitespace(char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isUnicodeWhitespace(ch[i])) {
				return false;
			}
		}// ch.length
		return true;		
	}
	
	/**
	 * @return true if each char in the String is defined (by unicode) as whitespace, false otherwise
	 */
	public static boolean isUnicodeWhitespace(String string) {
		return isUnicodeWhitespace(string.toCharArray()); 
	}
	
	/**
	 * @return true if the char is defined (by unicode) as whitespace, false otherwise
	 */
	public static boolean isUnicodeWhitespace(
			Character ch) {
		return Character.isSpaceChar(ch.charValue());
	}

	/**
	 * @return true if the char is defined by XML as whitespace, false otherwise.
	 * see: http://www.w3.org/TR/1998/REC-xml-19980210#NT-S
	 */
	public static boolean isXMLWhiteSpace(char ch) {
	  return (ch == 0x20) || (ch == 0x09) || (ch == 0xD) || (ch == 0xA);
	}
	
	/**
	 * @return true if each char in the array is defined by XML as whitespace, false otherwise.
	 * see: http://www.w3.org/TR/1998/REC-xml-19980210#NT-S
	 */
	public static boolean isXMLWhiteSpace(char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isXMLWhiteSpace(ch[i])) {
				return false;
			}
		}// ch.length
		return true;
	}
	
	/**
	 * @return true if each char in the String is defined by XML as whitespace, false otherwise.
	 * see: http://www.w3.org/TR/1998/REC-xml-19980210#NT-S
	 */
	public static boolean isXMLWhiteSpace(String string) {
		return isXMLWhiteSpace(string.toCharArray()); 
	}
	
	/**
	 * Converts each unicode whitespace char in the incoming char array with 
	 * the given replacement char
	 * If the given replacement char is whitespace too, fallback to the underscore char
	 * @param ch the input char array to replace whitespace in
	 * @param replace the char to replace any found whitespace with
	 */
	public static char[] toNonWhitespace(char[] ch, char replace) {
		String result = ""; //$NON-NLS-1$
		char replacement = replace;		
		if(isUnicodeWhitespace(replace)) replacement ='_';
				
		for (int i = 0; i < ch.length; i++) {
			if(isUnicodeWhitespace(ch[i])) {
				result = result + replacement;
			}else{
				result = result + ch[i];
			}
		}
		return result.toCharArray();
	}
	
	public static String toRestrictedSubset(FilenameRestriction restriction, String input) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			if(!isFilenameCompatible(ch, restriction)) {
				String s = toPrintableAscii(ch);
				if(!isFilenameCompatible(s, restriction)) {
					ret.append('_');
				}else{
					ret.append(s);
				}
				
			} else {
				ret.append(ch);
			}
		}
		return ret.toString();
	}

	/**
	 * Converts each unicode whitespace char in the incoming string with 
	 * the given replacement char
	 * If the given replacement char is whitespace too, fallback to the underscore char
	 * @param str the string to replace whitespace in
	 * @param replace the char to replace any found whitespace with
	 */
	public static String toNonWhitespace(String str, char replace) {
		return String.valueOf(toNonWhitespace(str.toCharArray(),replace));	
	}
	
	/**
	 * Converts each unicode whitespace char in the incoming string with the replacement char underscore
	 * @param str the string to replace whitespace in
	 * @see #toNonWhitespace(String, char)
	 */
	public static String toNonWhitespace(String str) {
		return String.valueOf(toNonWhitespace(str.toCharArray(),'_'));	
	}

	/**
	 * Converts each whitespace char in the incoming string with the replacement char underscore
	 * @param ch the char array to replace whitespace in
	 * @see #toNonWhitespace(String, char)
	 */
	public static char[] toNonWhitespace(char[] ch) {
		return toNonWhitespace(ch,'_');	
	}
	
	/**
	 * Converts each non-ascii-printable character in the incoming string to a
	 * printable asccii character
	 * 
	 * @return a string containing printable 7bit ascii only
	 */
	public static final String toPrintableAscii(
			String string) {
		String result = ""; //$NON-NLS-1$
		for (int i = 0; i < string.length(); i++) {
			result = result
					+ toPrintableAscii(string.charAt(i));
		}// ch.length
		return result;
	}

	/**
	 * Converts each non-ascii-printable character in the incoming char array to
	 * a printable asccii character
	 * 
	 * @return a string containing printable 7bit ascii only
	 */
	public static final char[] toPrintableAscii(
			char[] ch) {
		return toPrintableAscii(new String(ch)).toCharArray();
	}

	/**
	 * Converts a non-ascii-printable character to a printable asccii character
	 * 
	 * @return a string containing only chars within the range of printable 7bit
	 *         ascii
	 */
	public static final String toPrintableAscii(char c) {

		if (isAsciiPrintable(c)) {
			return String.valueOf(c);
		}

		if (c == '\u00c0') {
			return "A"; //$NON-NLS-1$
		} else if (c == '\u00e0') {
			return "a"; //$NON-NLS-1$
		} else if (c == '\u00c1') {
			return "A"; //$NON-NLS-1$
		} else if (c == '\u00e1') {
			return "a"; //$NON-NLS-1$
		} else if (c == '\u00c2') {
			return "A"; //$NON-NLS-1$
		} else if (c == '\u00e2') {
			return "a"; //$NON-NLS-1$
		} else if (c == '\u00c3') {
			return "A"; //$NON-NLS-1$
		} else if (c == '\u00e3') {
			return "a"; //$NON-NLS-1$
		} else if (c == '\u00c4') {
			return "AE"; //$NON-NLS-1$
		} else if (c == '\u00e4') {
			return "ae"; //$NON-NLS-1$
		} else if (c == '\u00c5') {
			return "AA"; //$NON-NLS-1$
		} else if (c == '\u00e5') {
			return "aa"; //$NON-NLS-1$
		} else if (c == '\u00c6') {
			return "AE"; //$NON-NLS-1$
		} else if (c == '\u00e6') {
			return "ae"; //$NON-NLS-1$
		} else if (c == '\u00c7') {
			return "C"; //$NON-NLS-1$
		} else if (c == '\u00e7') {
			return "c"; //$NON-NLS-1$
		} else if (c == '\u00c8') {
			return "E"; //$NON-NLS-1$
		} else if (c == '\u00e8') {
			return "e"; //$NON-NLS-1$
		} else if (c == '\u00c9') {
			return "E"; //$NON-NLS-1$
		} else if (c == '\u00e9') {
			return "e"; //$NON-NLS-1$
		} else if (c == '\u00ca') {
			return "E"; //$NON-NLS-1$
		} else if (c == '\u00ea') {
			return "e"; //$NON-NLS-1$
		} else if (c == '\u00cb') {
			return "E"; //$NON-NLS-1$
		} else if (c == '\u00eb') {
			return "e"; //$NON-NLS-1$
		} else if (c == '\u00cc') {
			return "I"; //$NON-NLS-1$
		} else if (c == '\u00ec') {
			return "i"; //$NON-NLS-1$
		} else if (c == '\u00cd') {
			return "I"; //$NON-NLS-1$
		} else if (c == '\u00ed') {
			return "i"; //$NON-NLS-1$
		} else if (c == '\u00ce') {
			return "I"; //$NON-NLS-1$
		} else if (c == '\u00ee') {
			return "i"; //$NON-NLS-1$
		} else if (c == '\u00cf') {
			return "I"; //$NON-NLS-1$
		} else if (c == '\u00ef') {
			return "i"; //$NON-NLS-1$
		} else if (c == '\u00d0') {
			return "D"; //$NON-NLS-1$
		} else if (c == '\u00f0') {
			return "d"; //$NON-NLS-1$
		} else if (c == '\u00d1') {
			return "N"; //$NON-NLS-1$
		} else if (c == '\u00f1') {
			return "n"; //$NON-NLS-1$
		} else if (c == '\u00d2') {
			return "O"; //$NON-NLS-1$
		} else if (c == '\u00f2') {
			return "o"; //$NON-NLS-1$
		} else if (c == '\u00d3') {
			return "O"; //$NON-NLS-1$
		} else if (c == '\u00f3') {
			return "o"; //$NON-NLS-1$
		} else if (c == '\u00d4') {
			return "O"; //$NON-NLS-1$
		} else if (c == '\u00f4') {
			return "o"; //$NON-NLS-1$
		} else if (c == '\u00d5') {
			return "O"; //$NON-NLS-1$
		} else if (c == '\u00f5') {
			return "o"; //$NON-NLS-1$
		} else if (c == '\u00d6') {
			return "OE"; //$NON-NLS-1$
		} else if (c == '\u00f6') {
			return "oe"; //$NON-NLS-1$
		} else if (c == '\u00d8') {
			return "OE"; //$NON-NLS-1$
		} else if (c == '\u00f8') {
			return "oe"; //$NON-NLS-1$
		} else if (c == '\u0160') {
			return "S"; //$NON-NLS-1$
		} else if (c == '\u0161') {
			return "s"; //$NON-NLS-1$
		} else if (c == '\u00d9') {
			return "U"; //$NON-NLS-1$
		} else if (c == '\u00f9') {
			return "u"; //$NON-NLS-1$
		} else if (c == '\u00da') {
			return "U"; //$NON-NLS-1$
		} else if (c == '\u00fa') {
			return "u"; //$NON-NLS-1$
		} else if (c == '\u00db') {
			return "U"; //$NON-NLS-1$
		} else if (c == '\u00fb') {
			return "u"; //$NON-NLS-1$
		} else if (c == '\u00dc') {
			return "U"; //$NON-NLS-1$
		} else if (c == '\u00fc') {
			return "u"; //$NON-NLS-1$
		} else if (c == '\u00dd') {
			return "Y"; //$NON-NLS-1$
		} else if (c == '\u00fd') {
			return "y"; //$NON-NLS-1$
		} else if (c == '\u00de') {
			return "th"; //$NON-NLS-1$
		} else if (c == '\u00fe') {
			return "TH"; //$NON-NLS-1$
		} else if (c == '\u0178') {
			return "Y"; //$NON-NLS-1$
		} else if (c == '\u00ff') {
			return "y"; //$NON-NLS-1$
		}
		return "_"; //$NON-NLS-1$
	}


		
	/**
	 * converts a char to an xml character entity
	 * 
	 * @return an xml character entity representation of the incoming char
	 */
	public static final String xmlEscape(char ch) {
		String ret = String.valueOf(ch);
		char[] a = ret.toCharArray();
		ret = "&#x" //$NON-NLS-1$
				+ Integer.toHexString(Character.codePointAt(a, 0))
				+ ";"; //$NON-NLS-1$
		return ret;
	}

	/**
	 * converts a Character to an xml character entity
	 * 
	 * @return an xml character entity representation of the incoming Character
	 */
	public static final String xmlEscape(
			Character ch) {
		return xmlEscape(ch.charValue());
	}

	/**
	 * converts each character of a string to xml character entities
	 * @return a string of xml character entities
	 */
	public static final String xmlEscape(
			String string) {
		String ret = ""; //$NON-NLS-1$
		char[] a = string.toCharArray();
		for (int i = 0; i < a.length; i++) {
			ret = ret + xmlEscape(a[i]);
		}
		return ret;
	}

	/**
	 * converts a char to a java-style unicode escaped string
	 */
	public static final String unicodeHexEscapeJava(char ch) {
		if (ch < 0x10) {
			return "\\u000" //$NON-NLS-1$
					+ Integer.toHexString(ch);
		} else if (ch < 0x100) {
			return "\\u00" //$NON-NLS-1$
					+ Integer.toHexString(ch);
		} else if (ch < 0x1000) {
			return "\\u0" //$NON-NLS-1$
					+ Integer.toHexString(ch);
		}
		return "\\u" + Integer.toHexString(ch);  //$NON-NLS-1$
	}

	/**
	 * converts a codepoint to a UC-style escaped string
	 */
	public static final String unicodeHexEscape(int codePoint) {
		if (codePoint < 0x10) {
			return "U+000"+ Integer.toHexString(codePoint);  //$NON-NLS-1$
		} else if (codePoint < 0x100) {
			return "U+00"+ Integer.toHexString(codePoint); //$NON-NLS-1$
		} else if (codePoint < 0x1000) {
			return "U+0"+ Integer.toHexString(codePoint); //$NON-NLS-1$
		}
		return "U+" + Integer.toHexString(codePoint); //$NON-NLS-1$
	}

	
	/**
	 * converts a Character to a java-style unicode escaped string
	 */
	public static final String unicodeEscape(
			Character ch) {
		return unicodeHexEscapeJava(ch.charValue());
	}

	/**
	 * converts each char of a string to java-style unicode escaped strings
	 */
	public static final String unicodeEscape(
			String string) {
		String ret = "";  //$NON-NLS-1$
		char[] a = string.toCharArray();
		for (int i = 0; i < a.length; i++) {			
			ret = ret + unicodeHexEscapeJava(a[i]);;
		}
		return ret;
	}
}
