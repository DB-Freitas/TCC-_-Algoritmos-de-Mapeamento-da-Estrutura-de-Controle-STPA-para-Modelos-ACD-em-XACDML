// Arquivo Generate.java
// 26.Mar.1999	Wladimir

package simula;

/**
 * Classe que implementa um Generate
 */
public class Generate extends ActiveState
{
	/**
	 */
	protected DeadState to_q;		
	/**
	 * flag "gerando entidade"
	 */
	protected boolean inservice;
	/**
	 */
	protected Distribution d;		

	/**
	 * valor inicial dos atributos
	 * das entidades geradas
	 */
	protected float[] attvals;		
	/**
	 * ids dos atributos das entidades
	 */
	protected String[] attids;		
	/**
	 */
	public int Generated = 0;	
	/**
	 */
	public int Wasted = 0;			

	/**
	 */
	public Generate(Scheduler s){super(s);}
	
	/**
	 * determina destino das entidades geradas.
	 */
	public void ConnectQueue(DeadState to)
	{
		if(to_q == null)
			to_q = to;
	}
	
	/**

	 */
	public void SetServiceTime(Distribution d)
	{
		this.d = d;
		RegisterEvent((float)d.Draw());
		inservice = true;
	}

	/**
	 */
	public void Clear()
	{
		super.Clear();
		Generated = 0;
		Wasted = 0;
		RegisterEvent((float)d.Draw());
		inservice = true;
	}
	
	/**
	 * iniciais dos atributos de cada entidade gerada por
	 */
	public void SetEntitiesAtts(String[] ids, float[] values)
	{
		if(ids.length != values.length)
			throw new IllegalArgumentException
				("Vetores de ids e valores devem ter o mesmo n�mero de elementos");
		attids = ids;
		attvals = values;
	}

	/**
	 * implementa protocolo.
	 */
	public boolean BServed(float time)
	{
		Entity e = new Entity(time);	// cria entidade e atribui-lhe instant
		
		// atribui atributos espec�ficos a e

		if(attids != null)
		{
			for(int i = 0; i < attids.length; i++)
				e.SetAttribute(attids[i], attvals[i]);	
		}		
		
		if(to_q.HasSpace())				// se teentidade na fila
		{
			to_q.Enqueue(e);
			Log.LogMessage(name + ":Entity " + e.GetId() + 
				" generated and sent to " + to_q.name);
			if(obs != null)
				obs.Outgoing(e);
		}
		else
		{
			Wasted++;					// mais uma entidade despe
			Log.LogMessage(name + ":Entity " + e.GetId() +
				" generated but wasted");
		}
		
		Generated++;					// mais uma entidade gerada

		inservice = false;				// libera a s entidades
		if(obs != null)
			obs.StateChange(Observer.BUSY);// marca fim do idle-time => determina inter-arrival

		return true;			
	}

	/**
	 * implementa protocolo; agenda evento entidade.
	 * sempre retorna false po.
	 */
	public boolean CServed()
	{
		if(!inservice)					// se ra entidade...
		{
			float t = (float)d.Draw();		// otidade
			RegisterEvent(t);				// ag
			inservice = true;				// e
			if(obs != null)
				obs.StateChange(Observer.IDLE);// para o Generate, o idler-arrival
			Log.LogMessage(name + ":Scheduled entity generation to " + t);
		}

		return false;

	}
}