// Arquivo Statistics.java
// 16.Abr.1999	Wladimir

// Watkins, Kevin. Discrete event simulation in C. 1993. McGraw-Hill International.

package simula;

/**
 * Watkins, Kevin. Discrete event simulation in C. 1993. McGraw-Hill International.
 */
public class Statistics
{
	private Scheduler s;
	private int zero;
	private float  sum_xf;
	private float  sum_xxf;
	private int sum_f;
	private float min_val, max_val;

	/**
	 * cria um observador vazio
	 */
	public Statistics(Scheduler s)
	{
		this.s = s;
		Clear();
	}

	/**
	 */
	public void Clear()
	{
		zero = 0;
		sum_xf = 0;
		sum_xxf = 0;
		sum_f = 0;
		min_val = Float.MAX_VALUE;
		max_val = 0;
	}

	/**
	 */
	public void Add(float  v){Add(1, v);}

	/**
	 */
	public void Add(float weight, float  v)
	{
		sum_f++;
		if (v == 0.0) 
			zero++;
		sum_xf += v * weight;
		sum_xxf += v * v * weight * weight;
		if (v > max_val)
			max_val = v;
		else if (v < min_val)
			min_val = v;
	}

	/**
	 */
	public final float Mean(){return sum_xf / sum_f;}

	/**
	 */
	public final float StdDev(){return (float)Math.sqrt(Variance());}

	/**
	 */
	public final float Variance()
	{	
		if(sum_f <= 1) 
			return 0;
		return (sum_xxf - sum_xf * Mean())/(sum_f - 1);
	}
	
	/**
	 */
	public final float Max(){return max_val;}
	
	/**
	 */
	public final float Min(){return min_val;}
	
	/**
	 */
	public final int NumObs(){return sum_f;}
}