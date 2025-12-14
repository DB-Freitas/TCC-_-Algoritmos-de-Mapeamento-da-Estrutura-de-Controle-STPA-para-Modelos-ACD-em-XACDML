// Arquivo Router.java
// 16.Abr.1999	Wladimir

package simula;

import java.util.Vector;

/**
 * Classe que implementa um Router
 */
public class Router extends ActiveState
{
	/**
	 * entities_from_v, entities_to_v, conditions_to_v,
	 * resources_from_v, resources_to_v, resources_qt_v:
	 */
	protected Vector entities_from_v, entities_to_v, conditions_to_v,
						resources_from_v, resources_to_v, resources_qt_v;
													
	private Distribution d;		// gerador de

	/**
	 * fila de entidades/recu
	 */
	protected IntPriorityQ service_q;
		
	/**
	 */
	protected boolean blocked;


	/**
	 */
	public Router(Scheduler s)
	{
		super(s);
		entities_from_v = new Vector(1, 1);
		entities_to_v = new Vector(1, 1);
		conditions_to_v = new Vector(1, 1);
		resources_from_v = new Vector(1, 1);
		resources_to_v = new Vector(1, 1);
		resources_qt_v = new Vector(1, 1);
		service_q = new IntPriorityQ();
	}

	public void SetServiceTime(Distribution d){this.d = d;}

	public void ConnectQueues(DeadState from) {entities_from_v.add(from);}

	public void ConnectQueues(DeadState to, Expression cond)
	{
		entities_to_v.add(to);
		conditions_to_v.add(cond);
	}

	/**
	 */
	public void ConnectResources(ResourceQ from, ResourceQ to, int qty_needed)
	{
		resources_from_v.add(from);
		resources_to_v.add(to);
		resources_qt_v.add(new Integer(qty_needed));
	}
	
	/**
	 */
	public void Clear()
	{
		super.Clear();
		service_q = new IntPriorityQ();
	}

	/**
	 * implementa protocolo.
	 */
	public boolean BServed(float time)
	{
		if(blocked)
			return false;

		IntQEntry e = service_q.Dequeue();

		if(e == null)						//
			return false;

		if(time < e.duetime)				// sido e scheduler
		{									// n
			service_q.PutBack(e);				// devais tarde
			return false;
		}

		// fim de

		for(int j = 0; j < e.ve.length; j++)					//para
		{
			if(e.ve[j] == null)	// este pode ser uqueado
				continue;
			for(int i = 0; i < entities_to_v.size(); i++)
				if(((Expression)conditions_to_v.elementAt(i)).Evaluate(e.ve[j]) != 0)
				{
					DeadState q = (DeadState)entities_to_v.elementAt(i);// osociada
					if(q.HasSpace())									// se te
					{
						q.Enqueue(e.ve[j]);								// envia ao estado morto
						Log.LogMessage(name + ":Entity " + e.ve[j].GetId() +
							" sent to " + q.name);
		
						if(obs != null)
							obs.Outgoing(e.ve[j]);
		
						e.ve[j] = null;
					}
					else
						blocked = true;	// sinaliza e
							
					break;												// p/ p
				}
		}
		
		if(blocked)
		{
			service_q.PutBack(e);	// devolve as que restaram	
			Log.LogMessage(name + ":Blocked");
			return false;					// conside
		}

		for(int i = 0; i < resources_to_v.size(); i++)		// e os recursos.
		{
			int qt;
			ResourceQ q = (ResourceQ)resources_to_v.elementAt(i);	// o
			q.Release(qt = ((Integer)resources_qt_v.elementAt(i)).intValue());// envia ao estado morto
			Log.LogMessage(name + ":Released " + qt + " resources to " + q.name);
		}
		
		if(obs != null && service_q.IsEmpty())
			obs.StateChange(Observer.IDLE);
		
		return true;
	}
	
	/**
	 * implementa protocolo.
	 */
	public boolean CServed()
	{
		// primeiro tenta resolve o estado bloqueado, se for o caso
		if(blocked)
		{
			blocked = false;
			while(BServed(s.GetClock()));	// extrai todos os bloqueados
							
			if(blocked)		// se ainda estiver bloqueado
				return false;
			Log.LogMessage(name + ":Unblocked");
		}
		
		// primeiro verifica se todos os recursos e ent
		boolean ok = true;
		int esize = entities_from_v.size();

		for(int i = 0; i < resources_from_v.size() && ok; i++)	// os recursos...
			ok &= ((ResourceQ)resources_from_v.elementAt(i)).
				HasEnough(((Integer)resources_qt_v.elementAt(i)).intValue());

		for(int i = 0; i < esize && ok; i++)					// as entidades...
			ok &= ((DeadState)entities_from_v.elementAt(i)).HasEnough();

		if(!ok)					// se al
			return false;		// n
		
		IntQEntry entry = new IntQEntry(esize, (float)d.Draw());

		// retira entidades...

		for(int i = 0; i < esize && ok; i++)					
			entry.ve[i] = ((DeadState)entities_from_v.elementAt(i)).Dequeue();
																

		for(int i = 0; i < resources_from_v.size(); i++)	
		{
			int qt;
			((ResourceQ)resources_from_v.elementAt(i)).
				Acquire(qt = ((Integer)resources_qt_v.elementAt(i)).intValue());
			Log.LogMessage(name + ":Acquired " + qt + " resources from " +
				((ResourceQ)resources_from_v.elementAt(i)).name);
		}

		entry.duetime = RegisterEvent(entry.duetime);			// notifica scheduler
		service_q.Enqueue(entry);								// coloca na fil

		if(obs != null)
		{
			obs.StateChange(Observer.BUSY);
			for(int i = 0; i < entry.ve.length; i++)
				obs.Incoming(entry.ve[i]);
		}

		for(int i = 0; i < entry.ve.length; i++)
			Log.LogMessage(name + ":Entity " + entry.ve[i].GetId() +
				" got from " + ((DeadState)entities_from_v.elementAt(i)).name);

		return true;
	}
}