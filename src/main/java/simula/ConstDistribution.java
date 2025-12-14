// Arquivo ConstDistribution.java
// 26.Mar.1999	Wladimir

package simula;

public class ConstDistribution extends Distribution
{
	private double value;
	
	/**
	 */
	public ConstDistribution(Sample s, double value)
	{super(s); this.value = value;}
	
	/**
	 */
	public double Draw(){return value;}
}
