// Arquivo Distribution.java
// 26.Mar.1999	Wladimir

package simula;

/**
 */
public abstract class Distribution
{
	protected Sample stream;

	/**
	 */
	public Distribution(Sample s){stream = s;}
	
	/**
	 */
	public abstract double Draw(); 
}
