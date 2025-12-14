// Arquivo RandomExpression.java
// 3.Dez.1999	Wladimir

package simula;

public class RandomExpression extends Expression
{
	private static Sample sp = new Sample();
	private Distribution dist;

	/**

	 */
	public RandomExpression(String exp)
	{
		super(0);
		exp.toLowerCase();
		if(exp.charAt(0) != '#')
			throw new IllegalArgumentException("Express�es aleat�rias devem iniciar por #!");
		String temp = exp.substring(1);
		int spindex = temp.indexOf(' ');
		String type = temp.substring(0, spindex);
		temp = temp.substring(spindex + 1);
		spindex = temp.indexOf(' ');
		String par1 = temp.substring(0, spindex);
		try
		{
			if(type.equals("uniform"))
			{
				String par2 = temp.substring(spindex + 1);
				
				dist = new Uniform(sp, Float.parseFloat(par1), Float.parseFloat(par2));
			}
			else if(type.equals("normal"))
			{
				String par2 = temp.substring(spindex + 1);	
				
				dist = new Normal(sp, Float.parseFloat(par1), Float.parseFloat(par2));
			}
			else if(type.equals("poisson"))
			{
				dist = new Poisson(sp, Float.parseFloat(par1));
			}
			else if(type.equals("negexp"))
			{
				dist = new NegExp(sp, Float.parseFloat(par1));
			}
			else
				throw new IllegalArgumentException("Tipo de distribui��o n�o suportada!");
		}
		catch(NumberFormatException x)
		{
			throw new IllegalArgumentException("N�meros n�o reconhecidos como Float!");
		}
	}

	/**
	 * para manter o protocolo
	 */
	public float Evaluate(Entity e)
	{return (float)dist.Draw();}
}