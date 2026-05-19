package net.shopxx;

/**
 * Analyzer - Ansj
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public class AnsjAnalyzer extends org.ansj.lucene5.AnsjAnalyzer {

	/**
	 * 默认类型
	 */
	public static final org.ansj.lucene5.AnsjAnalyzer.TYPE DEFAULT_TYPE = org.ansj.lucene5.AnsjAnalyzer.TYPE.dic_ansj;

	/**
	 * 构造方法
	 */
	public AnsjAnalyzer() {
		super(DEFAULT_TYPE);
	}

}