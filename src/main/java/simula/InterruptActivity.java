// Arquivo InterruptActivity.java
// Implementa��o das Classes do Grupo de Modelagem da Biblioteca de Simula��o JAVA
// 22.Abr.1999	Wladimir

package simula;

import java.util.Vector;

public class InterruptActivity extends Activity
{
	private Vector IntVector;
	
	/**
	 * constr�i uma atividade que interrompe e pode ser interrompida
	 */
	public InterruptActivity(Scheduler s)
	{
		super(s);
		IntVector = new Vector(2, 2);
	}
	
	/**
	 * adiciona a � lista das atividades que podem ser interrompidas
	 * quando se fizer necess�rio (obter recurso)
	 */
	public void AddInterruptable(InterruptActivity a){IntVector.add(a);}

	/**
	 * interrompe servi�o dessa atividade em favor de a;
	 * se interrompeu, retorn true, sen�o false
	 */
	public boolean Interrupt(InterruptActivity a)
	{
		IntQEntry e = service_q.FromTail(); // interrompe o servi�o mais demorado
		if(e == null)
			return false;	// n�o tinha servi�o para interromper
			
		for(int i = 0; i < e.ve.length; i++)		// devolve as entidades �s respectivas filas
			((DeadState)entities_from_v.elementAt(i)).PutBack(e.ve[i]);
		
		for(int i = 0; i < resources_from_v.size(); i++)	// e os recursos
			((ResourceQ)resources_from_v.elementAt(i)).
				Release(((Integer)resources_qt_v.elementAt(i)).intValue());

		Log.LogMessage(name + ":Interrupted by " + a.name);
		
		return true;
	}

	/**
	 * implementa protocolo
	 */
	public boolean CServed()
	{
		if(super.CServed())	// se o servi�o normal foi poss�vel...
			return true;
			
		if(blocked)					// n�o vai interromper ningu�m se estiver bloquado
			return false;
			
		int esize = entities_from_v.size();
		boolean ok = true;
		for(int i = 0; i < esize && ok; i++)					
			ok &= ((DeadState)entities_from_v.elementAt(i)).HasEnough();

		if(!ok)
			return false;

		// se n�o foi, tenta interromper algu�m, mas s� se realmente houver entidades
		// suficientes para se iniciar o servi�o.
		
			
		boolean interrupted = false;
		for(int i = 0; i < IntVector.size() && !interrupted; i++)
			if(((InterruptActivity)IntVector.elementAt(i)).Interrupt(this))
				interrupted = super.CServed(); 	// se conseguiu interromper e fazer o servi�o
																				// sen�o tenta novamente
	
		return interrupted;			// avisa scheduler o que ocorreu
	}
			
}