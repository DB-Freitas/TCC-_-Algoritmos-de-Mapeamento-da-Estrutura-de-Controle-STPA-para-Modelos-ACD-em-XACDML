// Arquivo Sample.java
// Implementa��o das Classes do Grupo Utilit�rio da Biblioteca de Simula��o JAVA
// 26.Mar.1999	Wladimir

package simula;

import java.util.Random;

/**
 * C�digo original em C adaptado de 
 * Watkins, Kevin. Discrete event simulation in C. 1993. McGraw-Hill International.
 */
public class Sample
{

	private static double cof[] = {76.18009173, -86.50532033, 24.01409822, -1.231739516,
                             0.120858003e-2, -0.536382e-5};

	/**
	 * retorna log de gamma para xx > 0.
	 */
	public static double  lngamma (double  xx)
	{
		double  x, tmp, ser;
		int j;

		x = xx - 1.0;
		tmp = x + 5.5;
		tmp -= (x + 0.5) * Math.log(tmp);
		ser = 1.0;
		for (j = 0;j < 6;j++) 
		{
			x += 1.0;
			ser += cof[j] / x;
		}
		return (-tmp + Math.log(2.50662827465 * ser));
	}

	private Random str;

	/**
	 * constr�i gerador com semente seed.
	 */
	public Sample(long seed)
	{
		str = new Random(seed);
	}
	
	/**
	 * constr�i gerador com semente seed., mas obt�m semente a partir do rel�gio da m�quina.
	 */
	public Sample()
	{
		str = new Random();
	}
	
	/**
	 * reinicia cadeia com semente seed.
	 */
	public void Reset(long seed){str.setSeed(seed);}
	
	/**
	 * obt�m amostra uniformemente distribu�da entre 0 e 1.
	 */
	public double Uniform(){return str.nextDouble();}
		
	/**
	 * obt�m amostra exponencialmente distribu�da de m�dia 1.
	 */
	public double NegExp()
	{
		return  -Math.log(Uniform());
	}
	
	/**
	 * obt�m amostra distribu�da de acordo com Laplace de m�dia 1.
	 */
	public double Laplace()
	{
		if (Uniform() <= 0.5)
			return  -Math.log(Uniform());
		else
			return  (float)Math.log(Uniform());
	}
}