// Arquivo NegExp.java
// 30.Abr.1999	Wladimir

package simula;

/**
 * Distribuicao exponencial
 */
public class NegExp extends Distribution
{
	private double mean;

	/**
	 */
	public NegExp(Sample s, double Mean)
	{super(s); mean = Mean;}

	/**
	 */
	public double Draw(){return mean * stream.NegExp();}
}