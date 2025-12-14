// Arquivo Destroy.java
// 26.Mar.1999	Wladimir

package simula;

/**
 * Implementa um Destroy no Systema
 * @author Wladimir
 */
public class Destroy extends ActiveState
{
	/**
	 */
	protected DeadState from_q;		
	
	/**
	 */
	public int Destroyed = 0;		

	/**
	 */
	public Destroy(Scheduler s){super(s);}

	/**
	 */
	public void ConnectQueue(DeadState from)
	{
		if(from_q == null)
			from_q = from;
	}

	/**
	 */
	public void Clear()
	{
		super.Clear();
		Destroyed = 0;
	}

	/**
	 * retorna false (nunca executa evento agendado (B)).
	 */
	public boolean BServed(float time){return false;}

	/**
	 */
	public boolean CServed()
	{
		boolean got = false;

		while(from_q.HasEnough())			// enquanto tiver entidades a ss
		{
			Entity e = from_q.Dequeue();	// retira entidade
			if(obs != null)
				obs.Incoming(e);
			got = true;
			Destroyed++;
			Log.LogMessage(name + ":Entity " + e.GetId() + 
				", from " + from_q.name + ", destroyed.");
		}
		
		return got;
	}
}