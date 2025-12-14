// Arquivo Uniform.java
// 26.Mar.1999	Wladimir

package simula;

/**
 * Uniform Distribution Generator
 */
public class Uniform extends Distribution
{
	private double upper, lower;

	/**
	 */
	public Uniform(Sample s, double Lower, double Upper)
	{super(s); lower = Lower; upper = Upper;}

	/**
	 */
	public double Draw(){return ( stream.Uniform() * ( upper - lower ) + lower );}
}
