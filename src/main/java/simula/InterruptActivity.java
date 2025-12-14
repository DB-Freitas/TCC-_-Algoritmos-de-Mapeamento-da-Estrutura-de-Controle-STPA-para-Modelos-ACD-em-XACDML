// Arquivo InterruptActivity.java
// 22.Abr.1999	Wladimir

package simula;

import java.util.Vector;

public class InterruptActivity extends Activity
{
	private Vector IntVector;
	
	/**
	 */
	public InterruptActivity(Scheduler s)
	{
		super(s);
		IntVector = new Vector(2, 2);
	}
	
	/**
	 */
	public void AddInterruptable(InterruptActivity a){IntVector.add(a);}

	/**
	 */
	public boolean Interrupt(InterruptActivity a)
	{
		IntQEntry e = service_q.FromTail(); // i demorado
		if(e == null)
			return false;	// n seterromper
			
		for(int i = 0; i < e.ve.length; i++)		// devolve astivas filas
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
		if(super.CServed())	// se o ..
			return true;
			
		if(blocked)					// n bloquado
			return false;
			
		int esize = entities_from_v.size();
		boolean ok = true;
		for(int i = 0; i < esize && ok; i++)					
			ok &= ((DeadState)entities_from_v.elementAt(i)).HasEnough();

		if(!ok)
			return false;


		
			
		boolean interrupted = false;
		for(int i = 0; i < IntVector.size() && !interrupted; i++)
			if(((InterruptActivity)IntVector.elementAt(i)).Interrupt(this))
				interrupted = super.CServed(); 	// se conseguiu interromper
																				// seovamente
	
		return interrupted;			// avisa scheduler o que ocorreu
	}
			
}