package org.daisy.reader.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

final class UndeclaredEntityResolver implements XMLResolver {
	private static final UndeclaredEntityResolver instance = new UndeclaredEntityResolver();
	private static Map<String,String> entities;
	private static final String NULL_STRING = ""; //$NON-NLS-1$
		
	public static XMLResolver getInstance() {
		return instance;
	}
	
	public Object resolveEntity(String publicID, String systemID,
			String baseURI, String namespace) throws XMLStreamException {
		
		if(entities.isEmpty()) populate();
		
		return(entities.containsKey(namespace))
			? entities.get(namespace) : NULL_STRING;			
	}
	
	private UndeclaredEntityResolver() {
		entities = new HashMap<String,String>();
	}

	
	private void populate() {
		entities.put("quot", String.copyValueOf(Character.toChars(34))); //$NON-NLS-1$
		entities.put("amp", String.copyValueOf(Character.toChars(38)) + String.copyValueOf(Character.toChars(38))); //$NON-NLS-1$
		entities.put("lt", String.copyValueOf(Character.toChars(38)) + String.copyValueOf(Character.toChars(60))); //$NON-NLS-1$
		entities.put("gt", String.copyValueOf(Character.toChars(62))); //$NON-NLS-1$
		entities.put("apos", String.copyValueOf(Character.toChars(39))); //$NON-NLS-1$
		entities.put("OElig", String.copyValueOf(Character.toChars(338))); //$NON-NLS-1$
		entities.put("oelig", String.copyValueOf(Character.toChars(339))); //$NON-NLS-1$
		entities.put("Scaron", String.copyValueOf(Character.toChars(352))); //$NON-NLS-1$
		entities.put("scaron", String.copyValueOf(Character.toChars(353))); //$NON-NLS-1$
		entities.put("Yuml", String.copyValueOf(Character.toChars(376))); //$NON-NLS-1$
		entities.put("circ", String.copyValueOf(Character.toChars(710))); //$NON-NLS-1$
		entities.put("tilde", String.copyValueOf(Character.toChars(732))); //$NON-NLS-1$
		entities.put("ensp", String.copyValueOf(Character.toChars(8194))); //$NON-NLS-1$
		entities.put("emsp", String.copyValueOf(Character.toChars(8195))); //$NON-NLS-1$
		entities.put("thinsp", String.copyValueOf(Character.toChars(8201))); //$NON-NLS-1$
		entities.put("zwnj", String.copyValueOf(Character.toChars(8204))); //$NON-NLS-1$
		entities.put("zwj", String.copyValueOf(Character.toChars(8205))); //$NON-NLS-1$
		entities.put("lrm", String.copyValueOf(Character.toChars(8206))); //$NON-NLS-1$
		entities.put("rlm", String.copyValueOf(Character.toChars(8207))); //$NON-NLS-1$
		entities.put("ndash", String.copyValueOf(Character.toChars(8211))); //$NON-NLS-1$
		entities.put("mdash", String.copyValueOf(Character.toChars(8212))); //$NON-NLS-1$
		entities.put("lsquo", String.copyValueOf(Character.toChars(8216))); //$NON-NLS-1$
		entities.put("rsquo", String.copyValueOf(Character.toChars(8217))); //$NON-NLS-1$
		entities.put("sbquo", String.copyValueOf(Character.toChars(8218))); //$NON-NLS-1$
		entities.put("ldquo", String.copyValueOf(Character.toChars(8220))); //$NON-NLS-1$
		entities.put("rdquo", String.copyValueOf(Character.toChars(8221))); //$NON-NLS-1$
		entities.put("bdquo", String.copyValueOf(Character.toChars(8222))); //$NON-NLS-1$
		entities.put("dagger", String.copyValueOf(Character.toChars(8224))); //$NON-NLS-1$
		entities.put("Dagger", String.copyValueOf(Character.toChars(8225))); //$NON-NLS-1$
		entities.put("permil", String.copyValueOf(Character.toChars(8240))); //$NON-NLS-1$
		entities.put("lsaquo", String.copyValueOf(Character.toChars(8249))); //$NON-NLS-1$
		entities.put("rsaquo", String.copyValueOf(Character.toChars(8250))); //$NON-NLS-1$
		entities.put("euro", String.copyValueOf(Character.toChars(8364))); //$NON-NLS-1$
		entities.put("fnof", String.copyValueOf(Character.toChars(402))); //$NON-NLS-1$
		entities.put("Alpha", String.copyValueOf(Character.toChars(913))); //$NON-NLS-1$
		entities.put("Beta", String.copyValueOf(Character.toChars(914))); //$NON-NLS-1$
		entities.put("Gamma", String.copyValueOf(Character.toChars(915))); //$NON-NLS-1$
		entities.put("Delta", String.copyValueOf(Character.toChars(916))); //$NON-NLS-1$
		entities.put("Epsilon", String.copyValueOf(Character.toChars(917))); //$NON-NLS-1$
		entities.put("Zeta", String.copyValueOf(Character.toChars(918))); //$NON-NLS-1$
		entities.put("Eta", String.copyValueOf(Character.toChars(919))); //$NON-NLS-1$
		entities.put("Theta", String.copyValueOf(Character.toChars(920))); //$NON-NLS-1$
		entities.put("Iota", String.copyValueOf(Character.toChars(921))); //$NON-NLS-1$
		entities.put("Kappa", String.copyValueOf(Character.toChars(922))); //$NON-NLS-1$
		entities.put("Lambda", String.copyValueOf(Character.toChars(923))); //$NON-NLS-1$
		entities.put("Mu", String.copyValueOf(Character.toChars(924))); //$NON-NLS-1$
		entities.put("Nu", String.copyValueOf(Character.toChars(925))); //$NON-NLS-1$
		entities.put("Xi", String.copyValueOf(Character.toChars(926))); //$NON-NLS-1$
		entities.put("Omicron", String.copyValueOf(Character.toChars(927))); //$NON-NLS-1$
		entities.put("Pi", String.copyValueOf(Character.toChars(928))); //$NON-NLS-1$
		entities.put("Rho", String.copyValueOf(Character.toChars(929))); //$NON-NLS-1$
		entities.put("Sigma", String.copyValueOf(Character.toChars(931))); //$NON-NLS-1$
		entities.put("Tau", String.copyValueOf(Character.toChars(932))); //$NON-NLS-1$
		entities.put("Upsilon", String.copyValueOf(Character.toChars(933))); //$NON-NLS-1$
		entities.put("Phi", String.copyValueOf(Character.toChars(934))); //$NON-NLS-1$
		entities.put("Chi", String.copyValueOf(Character.toChars(935))); //$NON-NLS-1$
		entities.put("Psi", String.copyValueOf(Character.toChars(936))); //$NON-NLS-1$
		entities.put("Omega", String.copyValueOf(Character.toChars(937))); //$NON-NLS-1$
		entities.put("alpha", String.copyValueOf(Character.toChars(945))); //$NON-NLS-1$
		entities.put("beta", String.copyValueOf(Character.toChars(946))); //$NON-NLS-1$
		entities.put("gamma", String.copyValueOf(Character.toChars(947))); //$NON-NLS-1$
		entities.put("delta", String.copyValueOf(Character.toChars(948))); //$NON-NLS-1$
		entities.put("epsilon", String.copyValueOf(Character.toChars(949))); //$NON-NLS-1$
		entities.put("zeta", String.copyValueOf(Character.toChars(950))); //$NON-NLS-1$
		entities.put("eta", String.copyValueOf(Character.toChars(951))); //$NON-NLS-1$
		entities.put("theta", String.copyValueOf(Character.toChars(952))); //$NON-NLS-1$
		entities.put("iota", String.copyValueOf(Character.toChars(953))); //$NON-NLS-1$
		entities.put("kappa", String.copyValueOf(Character.toChars(954))); //$NON-NLS-1$
		entities.put("lambda", String.copyValueOf(Character.toChars(955))); //$NON-NLS-1$
		entities.put("mu", String.copyValueOf(Character.toChars(956))); //$NON-NLS-1$
		entities.put("nu", String.copyValueOf(Character.toChars(957))); //$NON-NLS-1$
		entities.put("xi", String.copyValueOf(Character.toChars(958))); //$NON-NLS-1$
		entities.put("omicron", String.copyValueOf(Character.toChars(959))); //$NON-NLS-1$
		entities.put("pi", String.copyValueOf(Character.toChars(960))); //$NON-NLS-1$
		entities.put("rho", String.copyValueOf(Character.toChars(961))); //$NON-NLS-1$
		entities.put("sigmaf", String.copyValueOf(Character.toChars(962))); //$NON-NLS-1$
		entities.put("sigma", String.copyValueOf(Character.toChars(963))); //$NON-NLS-1$
		entities.put("tau", String.copyValueOf(Character.toChars(964))); //$NON-NLS-1$
		entities.put("upsilon", String.copyValueOf(Character.toChars(965))); //$NON-NLS-1$
		entities.put("phi", String.copyValueOf(Character.toChars(966))); //$NON-NLS-1$
		entities.put("chi", String.copyValueOf(Character.toChars(967))); //$NON-NLS-1$
		entities.put("psi", String.copyValueOf(Character.toChars(968))); //$NON-NLS-1$
		entities.put("omega", String.copyValueOf(Character.toChars(969))); //$NON-NLS-1$
		entities.put("thetasym", String.copyValueOf(Character.toChars(977))); //$NON-NLS-1$
		entities.put("upsih", String.copyValueOf(Character.toChars(978))); //$NON-NLS-1$
		entities.put("piv", String.copyValueOf(Character.toChars(982))); //$NON-NLS-1$
		entities.put("bull", String.copyValueOf(Character.toChars(8226))); //$NON-NLS-1$
		entities.put("hellip", String.copyValueOf(Character.toChars(8230))); //$NON-NLS-1$
		entities.put("prime", String.copyValueOf(Character.toChars(8242))); //$NON-NLS-1$
		entities.put("Prime", String.copyValueOf(Character.toChars(8243))); //$NON-NLS-1$
		entities.put("oline", String.copyValueOf(Character.toChars(8254))); //$NON-NLS-1$
		entities.put("frasl", String.copyValueOf(Character.toChars(8260))); //$NON-NLS-1$
		entities.put("weierp", String.copyValueOf(Character.toChars(8472))); //$NON-NLS-1$
		entities.put("image", String.copyValueOf(Character.toChars(8465))); //$NON-NLS-1$
		entities.put("real", String.copyValueOf(Character.toChars(8476))); //$NON-NLS-1$
		entities.put("trade", String.copyValueOf(Character.toChars(8482))); //$NON-NLS-1$
		entities.put("alefsym", String.copyValueOf(Character.toChars(8501))); //$NON-NLS-1$
		entities.put("larr", String.copyValueOf(Character.toChars(8592))); //$NON-NLS-1$
		entities.put("uarr", String.copyValueOf(Character.toChars(8593))); //$NON-NLS-1$
		entities.put("rarr", String.copyValueOf(Character.toChars(8594))); //$NON-NLS-1$
		entities.put("darr", String.copyValueOf(Character.toChars(8595))); //$NON-NLS-1$
		entities.put("harr", String.copyValueOf(Character.toChars(8596))); //$NON-NLS-1$
		entities.put("crarr", String.copyValueOf(Character.toChars(8629))); //$NON-NLS-1$
		entities.put("lArr", String.copyValueOf(Character.toChars(8656))); //$NON-NLS-1$
		entities.put("uArr", String.copyValueOf(Character.toChars(8657))); //$NON-NLS-1$
		entities.put("rArr", String.copyValueOf(Character.toChars(8658))); //$NON-NLS-1$
		entities.put("dArr", String.copyValueOf(Character.toChars(8659))); //$NON-NLS-1$
		entities.put("hArr", String.copyValueOf(Character.toChars(8660))); //$NON-NLS-1$
		entities.put("forall", String.copyValueOf(Character.toChars(8704))); //$NON-NLS-1$
		entities.put("part", String.copyValueOf(Character.toChars(8706))); //$NON-NLS-1$
		entities.put("exist", String.copyValueOf(Character.toChars(8707))); //$NON-NLS-1$
		entities.put("empty", String.copyValueOf(Character.toChars(8709))); //$NON-NLS-1$
		entities.put("nabla", String.copyValueOf(Character.toChars(8711))); //$NON-NLS-1$
		entities.put("isin", String.copyValueOf(Character.toChars(8712))); //$NON-NLS-1$
		entities.put("notin", String.copyValueOf(Character.toChars(8713))); //$NON-NLS-1$
		entities.put("ni", String.copyValueOf(Character.toChars(8715))); //$NON-NLS-1$
		entities.put("prod", String.copyValueOf(Character.toChars(8719))); //$NON-NLS-1$
		entities.put("sum", String.copyValueOf(Character.toChars(8721))); //$NON-NLS-1$
		entities.put("minus", String.copyValueOf(Character.toChars(8722))); //$NON-NLS-1$
		entities.put("lowast", String.copyValueOf(Character.toChars(8727))); //$NON-NLS-1$
		entities.put("radic", String.copyValueOf(Character.toChars(8730))); //$NON-NLS-1$
		entities.put("prop", String.copyValueOf(Character.toChars(8733))); //$NON-NLS-1$
		entities.put("infin", String.copyValueOf(Character.toChars(8734))); //$NON-NLS-1$
		entities.put("ang", String.copyValueOf(Character.toChars(8736))); //$NON-NLS-1$
		entities.put("and", String.copyValueOf(Character.toChars(8743))); //$NON-NLS-1$
		entities.put("or", String.copyValueOf(Character.toChars(8744))); //$NON-NLS-1$
		entities.put("cap", String.copyValueOf(Character.toChars(8745))); //$NON-NLS-1$
		entities.put("cup", String.copyValueOf(Character.toChars(8746))); //$NON-NLS-1$
		entities.put("int", String.copyValueOf(Character.toChars(8747))); //$NON-NLS-1$
		entities.put("there4", String.copyValueOf(Character.toChars(8756))); //$NON-NLS-1$
		entities.put("sim", String.copyValueOf(Character.toChars(8764))); //$NON-NLS-1$
		entities.put("cong", String.copyValueOf(Character.toChars(8773))); //$NON-NLS-1$
		entities.put("asymp", String.copyValueOf(Character.toChars(8776))); //$NON-NLS-1$
		entities.put("ne", String.copyValueOf(Character.toChars(8800))); //$NON-NLS-1$
		entities.put("equiv", String.copyValueOf(Character.toChars(8801))); //$NON-NLS-1$
		entities.put("le", String.copyValueOf(Character.toChars(8804))); //$NON-NLS-1$
		entities.put("ge", String.copyValueOf(Character.toChars(8805))); //$NON-NLS-1$
		entities.put("sub", String.copyValueOf(Character.toChars(8834))); //$NON-NLS-1$
		entities.put("sup", String.copyValueOf(Character.toChars(8835))); //$NON-NLS-1$
		entities.put("nsub", String.copyValueOf(Character.toChars(8836))); //$NON-NLS-1$
		entities.put("sube", String.copyValueOf(Character.toChars(8838))); //$NON-NLS-1$
		entities.put("supe", String.copyValueOf(Character.toChars(8839))); //$NON-NLS-1$
		entities.put("oplus", String.copyValueOf(Character.toChars(8853))); //$NON-NLS-1$
		entities.put("otimes", String.copyValueOf(Character.toChars(8855))); //$NON-NLS-1$
		entities.put("perp", String.copyValueOf(Character.toChars(8869))); //$NON-NLS-1$
		entities.put("sdot", String.copyValueOf(Character.toChars(8901))); //$NON-NLS-1$
		entities.put("lceil", String.copyValueOf(Character.toChars(8968))); //$NON-NLS-1$
		entities.put("rceil", String.copyValueOf(Character.toChars(8969))); //$NON-NLS-1$
		entities.put("lfloor", String.copyValueOf(Character.toChars(8970))); //$NON-NLS-1$
		entities.put("rfloor", String.copyValueOf(Character.toChars(8971))); //$NON-NLS-1$
		entities.put("lang", String.copyValueOf(Character.toChars(9001))); //$NON-NLS-1$
		entities.put("rang", String.copyValueOf(Character.toChars(9002))); //$NON-NLS-1$
		entities.put("loz", String.copyValueOf(Character.toChars(9674))); //$NON-NLS-1$
		entities.put("spades", String.copyValueOf(Character.toChars(9824))); //$NON-NLS-1$
		entities.put("clubs", String.copyValueOf(Character.toChars(9827))); //$NON-NLS-1$
		entities.put("hearts", String.copyValueOf(Character.toChars(9829))); //$NON-NLS-1$
		entities.put("diams", String.copyValueOf(Character.toChars(9830))); //$NON-NLS-1$
		entities.put("nbsp", String.copyValueOf(Character.toChars(160))); //$NON-NLS-1$
		entities.put("iexcl", String.copyValueOf(Character.toChars(161))); //$NON-NLS-1$
		entities.put("cent", String.copyValueOf(Character.toChars(162))); //$NON-NLS-1$
		entities.put("pound", String.copyValueOf(Character.toChars(163))); //$NON-NLS-1$
		entities.put("curren", String.copyValueOf(Character.toChars(164))); //$NON-NLS-1$
		entities.put("yen", String.copyValueOf(Character.toChars(165))); //$NON-NLS-1$
		entities.put("brvbar", String.copyValueOf(Character.toChars(166))); //$NON-NLS-1$
		entities.put("sect", String.copyValueOf(Character.toChars(167))); //$NON-NLS-1$
		entities.put("uml", String.copyValueOf(Character.toChars(168))); //$NON-NLS-1$
		entities.put("copy", String.copyValueOf(Character.toChars(169))); //$NON-NLS-1$
		entities.put("ordf", String.copyValueOf(Character.toChars(170))); //$NON-NLS-1$
		entities.put("laquo", String.copyValueOf(Character.toChars(171))); //$NON-NLS-1$
		entities.put("not", String.copyValueOf(Character.toChars(172))); //$NON-NLS-1$
		entities.put("shy", String.copyValueOf(Character.toChars(173))); //$NON-NLS-1$
		entities.put("reg", String.copyValueOf(Character.toChars(174))); //$NON-NLS-1$
		entities.put("macr", String.copyValueOf(Character.toChars(175))); //$NON-NLS-1$
		entities.put("deg", String.copyValueOf(Character.toChars(176))); //$NON-NLS-1$
		entities.put("plusmn", String.copyValueOf(Character.toChars(177))); //$NON-NLS-1$
		entities.put("sup2", String.copyValueOf(Character.toChars(178))); //$NON-NLS-1$
		entities.put("sup3", String.copyValueOf(Character.toChars(179))); //$NON-NLS-1$
		entities.put("acute", String.copyValueOf(Character.toChars(180))); //$NON-NLS-1$
		entities.put("micro", String.copyValueOf(Character.toChars(181))); //$NON-NLS-1$
		entities.put("para", String.copyValueOf(Character.toChars(182))); //$NON-NLS-1$
		entities.put("middot", String.copyValueOf(Character.toChars(183))); //$NON-NLS-1$
		entities.put("cedil", String.copyValueOf(Character.toChars(184))); //$NON-NLS-1$
		entities.put("sup1", String.copyValueOf(Character.toChars(185))); //$NON-NLS-1$
		entities.put("ordm", String.copyValueOf(Character.toChars(186))); //$NON-NLS-1$
		entities.put("raquo", String.copyValueOf(Character.toChars(187))); //$NON-NLS-1$
		entities.put("frac14", String.copyValueOf(Character.toChars(188))); //$NON-NLS-1$
		entities.put("frac12", String.copyValueOf(Character.toChars(189))); //$NON-NLS-1$
		entities.put("frac34", String.copyValueOf(Character.toChars(190))); //$NON-NLS-1$
		entities.put("iquest", String.copyValueOf(Character.toChars(191))); //$NON-NLS-1$
		entities.put("Agrave", String.copyValueOf(Character.toChars(192))); //$NON-NLS-1$
		entities.put("Aacute", String.copyValueOf(Character.toChars(193))); //$NON-NLS-1$
		entities.put("Acirc", String.copyValueOf(Character.toChars(194))); //$NON-NLS-1$
		entities.put("Atilde", String.copyValueOf(Character.toChars(195))); //$NON-NLS-1$
		entities.put("Auml", String.copyValueOf(Character.toChars(196))); //$NON-NLS-1$
		entities.put("Aring", String.copyValueOf(Character.toChars(197))); //$NON-NLS-1$
		entities.put("AElig", String.copyValueOf(Character.toChars(198))); //$NON-NLS-1$
		entities.put("Ccedil", String.copyValueOf(Character.toChars(199))); //$NON-NLS-1$
		entities.put("Egrave", String.copyValueOf(Character.toChars(200))); //$NON-NLS-1$
		entities.put("Eacute", String.copyValueOf(Character.toChars(201))); //$NON-NLS-1$
		entities.put("Ecirc", String.copyValueOf(Character.toChars(202))); //$NON-NLS-1$
		entities.put("Euml", String.copyValueOf(Character.toChars(203))); //$NON-NLS-1$
		entities.put("Igrave", String.copyValueOf(Character.toChars(204))); //$NON-NLS-1$
		entities.put("Iacute", String.copyValueOf(Character.toChars(205))); //$NON-NLS-1$
		entities.put("Icirc", String.copyValueOf(Character.toChars(206))); //$NON-NLS-1$
		entities.put("Iuml", String.copyValueOf(Character.toChars(207))); //$NON-NLS-1$
		entities.put("ETH", String.copyValueOf(Character.toChars(208))); //$NON-NLS-1$
		entities.put("Ntilde", String.copyValueOf(Character.toChars(209))); //$NON-NLS-1$
		entities.put("Ograve", String.copyValueOf(Character.toChars(210))); //$NON-NLS-1$
		entities.put("Oacute", String.copyValueOf(Character.toChars(211))); //$NON-NLS-1$
		entities.put("Ocirc", String.copyValueOf(Character.toChars(212))); //$NON-NLS-1$
		entities.put("Otilde", String.copyValueOf(Character.toChars(213))); //$NON-NLS-1$
		entities.put("Ouml", String.copyValueOf(Character.toChars(214))); //$NON-NLS-1$
		entities.put("times", String.copyValueOf(Character.toChars(215))); //$NON-NLS-1$
		entities.put("Oslash", String.copyValueOf(Character.toChars(216))); //$NON-NLS-1$
		entities.put("Ugrave", String.copyValueOf(Character.toChars(217))); //$NON-NLS-1$
		entities.put("Uacute", String.copyValueOf(Character.toChars(218))); //$NON-NLS-1$
		entities.put("Ucirc", String.copyValueOf(Character.toChars(219))); //$NON-NLS-1$
		entities.put("Uuml", String.copyValueOf(Character.toChars(220))); //$NON-NLS-1$
		entities.put("Yacute", String.copyValueOf(Character.toChars(221))); //$NON-NLS-1$
		entities.put("THORN", String.copyValueOf(Character.toChars(222))); //$NON-NLS-1$
		entities.put("szlig", String.copyValueOf(Character.toChars(223))); //$NON-NLS-1$
		entities.put("agrave", String.copyValueOf(Character.toChars(224))); //$NON-NLS-1$
		entities.put("aacute", String.copyValueOf(Character.toChars(225))); //$NON-NLS-1$
		entities.put("acirc", String.copyValueOf(Character.toChars(226))); //$NON-NLS-1$
		entities.put("atilde", String.copyValueOf(Character.toChars(227))); //$NON-NLS-1$
		entities.put("auml", String.copyValueOf(Character.toChars(228))); //$NON-NLS-1$
		entities.put("aring", String.copyValueOf(Character.toChars(229))); //$NON-NLS-1$
		entities.put("aelig", String.copyValueOf(Character.toChars(230))); //$NON-NLS-1$
		entities.put("ccedil", String.copyValueOf(Character.toChars(231))); //$NON-NLS-1$
		entities.put("egrave", String.copyValueOf(Character.toChars(232))); //$NON-NLS-1$
		entities.put("eacute", String.copyValueOf(Character.toChars(233))); //$NON-NLS-1$
		entities.put("ecirc", String.copyValueOf(Character.toChars(234))); //$NON-NLS-1$
		entities.put("euml", String.copyValueOf(Character.toChars(235))); //$NON-NLS-1$
		entities.put("igrave", String.copyValueOf(Character.toChars(236))); //$NON-NLS-1$
		entities.put("iacute", String.copyValueOf(Character.toChars(237))); //$NON-NLS-1$
		entities.put("icirc", String.copyValueOf(Character.toChars(238))); //$NON-NLS-1$
		entities.put("iuml", String.copyValueOf(Character.toChars(239))); //$NON-NLS-1$
		entities.put("eth", String.copyValueOf(Character.toChars(240))); //$NON-NLS-1$
		entities.put("ntilde", String.copyValueOf(Character.toChars(241))); //$NON-NLS-1$
		entities.put("ograve", String.copyValueOf(Character.toChars(242))); //$NON-NLS-1$
		entities.put("oacute", String.copyValueOf(Character.toChars(243))); //$NON-NLS-1$
		entities.put("ocirc", String.copyValueOf(Character.toChars(244))); //$NON-NLS-1$
		entities.put("otilde", String.copyValueOf(Character.toChars(245))); //$NON-NLS-1$
		entities.put("ouml", String.copyValueOf(Character.toChars(246))); //$NON-NLS-1$
		entities.put("divide", String.copyValueOf(Character.toChars(247))); //$NON-NLS-1$
		entities.put("oslash", String.copyValueOf(Character.toChars(248))); //$NON-NLS-1$
		entities.put("ugrave", String.copyValueOf(Character.toChars(249))); //$NON-NLS-1$
		entities.put("uacute", String.copyValueOf(Character.toChars(250))); //$NON-NLS-1$
		entities.put("ucirc", String.copyValueOf(Character.toChars(251))); //$NON-NLS-1$
		entities.put("uuml", String.copyValueOf(Character.toChars(252))); //$NON-NLS-1$
		entities.put("yacute", String.copyValueOf(Character.toChars(253))); //$NON-NLS-1$
		entities.put("thorn", String.copyValueOf(Character.toChars(254))); //$NON-NLS-1$
		entities.put("yuml", String.copyValueOf(Character.toChars(255)));		 //$NON-NLS-1$
	}	
}

