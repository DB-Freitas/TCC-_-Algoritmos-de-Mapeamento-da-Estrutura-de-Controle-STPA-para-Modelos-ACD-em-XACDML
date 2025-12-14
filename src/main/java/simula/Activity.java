// Arquivo Activity.java
// 9.Abr.1999	Wladimir

package simula;

import java.util.Vector;

public class Activity extends ActiveState
{

	/**
	 * entities_from_v, entities_to_v, conditions_from_v,
	 * resources_from_v, resources_to_v, resources_qt_v:
	 */
	protected Vector entities_from_v, entities_to_v, conditions_from_v,
					 	resources_from_v, resources_to_v, resources_qt_v;
													
	private Distribution d;

	/**
	 * fila de entidades
	 */
	protected IntPriorityQ service_q;
		
	/**
	 * se es
	 */
	protected boolean blocked;	

	/**
	 * cono de s
	 */
	public Activity(Scheduler s)
	{
		super(s);
		// constr�i vetores de liga��es
		entities_from_v = new Vector(1, 1);
		entities_to_v = new Vector(1, 1);
		conditions_from_v = new Vector(1, 1);
		resources_from_v = new Vector(1, 1);
		resources_to_v = new Vector(1, 1);
		resources_qt_v = new Vector(1, 1);
		service_q = new IntPriorityQ();
	}
	
	/**
	 */
	public void SetServiceTime(Distribution d){this.d = d;}
	
	public void ConnectQueues(DeadState from, DeadState to)
	{ConnectQueues(from, ConstExpression.TRUE, to);}
	
	/**
	 */
	public void ConnectResources(ResourceQ from, ResourceQ to, int qty_needed)
	{
		resources_from_v.add(from);
		resources_to_v.add(to);
		resources_qt_v.add(new Integer(qty_needed));
	}
	
	/**
	 * conecta estados moresfeita
	 */
	public void ConnectQueues(DeadState from, Expression cond, DeadState to)
	{
		conditions_from_v.add(cond);
		entities_from_v.add(from);
		entities_to_v.add(to);
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
		if(blocked)									// quanto estiver bloqueado
			return false;
			
		IntQEntry e = service_q.Dequeue();

		if(e == null)								// a a servir
			return false;

		if(time < e.duetime)				// serompido e scheduler
		{														// no
			service_q.PutBack(e);				// devolser servido mais tarde
			return false;
		}


		boolean shouldnotblock = true;

		for(int i = 0; i < entities_to_v.size(); i++)		// as entidades...
		{
			DeadState q = (DeadState)entities_to_v.elementAt(i);	// osociada
			shouldnotblock &= q.HasSpace();												// condibloquear
		}
		
		if(!shouldnotblock)
		{
			service_q.PutBack(e);
			blocked = true;											// bloqueia
			Log.LogMessage(name + ":Blocked");
			return false;
		}

		for(int i = 0; i < entities_to_v.size(); i++)		// as entidades...
		{
			DeadState q = (DeadState)entities_to_v.elementAt(i);	// oa associada
			if(q.HasSpace())										// se tem e
				q.Enqueue(e.ve[i]);									// envia ao estado morto
		}
		
		for(int i = 0; i < resources_to_v.size(); i++)		// e os recursos.
		{
			int qt;
			ResourceQ q = (ResourceQ)resources_to_v.elementAt(i);	// associada
			q.Release(qt = ((Integer)resources_qt_v.elementAt(i)).intValue());// envia ao estado morto
			Log.LogMessage(name + ":Released " + qt + " resources to " +
				((ResourceQ)resources_to_v.elementAt(i)).name);
		}

		if(obs != null)
		{
			if(service_q.IsEmpty())
				obs.StateChange(Observer.IDLE);
			for(int i = 0; i < e.ve.length; i++)
				obs.Outgoing(e.ve[i]);
		}
		
		for(int i = 0; i < e.ve.length; i++)
			Log.LogMessage(name + ":Entity " + e.ve[i].GetId() +
				" sent to " + ((DeadState)entities_to_v.elementAt(i)).name);
				
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
							
			if(blocked)		// se ainda estiver bloqueadoa
				return false;
			Log.LogMessage(name + ":Unblocked");
		}
			
		
		// primeiro verifica se todos os recursos e entidades
		boolean ok = true;
		int esize = entities_from_v.size();
		int i;

		for(i = 0; i < resources_from_v.size() && ok; i++)	// os recursos...
			ok &= ((ResourceQ)resources_from_v.elementAt(i)).
				HasEnough(((Integer)resources_qt_v.elementAt(i)).intValue());

		for(i = 0; i < esize && ok; i++)					// as entidades...
			ok &= ((DeadState)entities_from_v.elementAt(i)).HasEnough();

		IntQEntry possible = new IntQEntry(esize, (float)d.Draw());
		Entity e;
		for(i = 0; i < esize && ok; i++)					// as co
		{
			possible.ve[i] = e = ((DeadState)entities_from_v.elementAt(i)).Dequeue();
																// retira entidades...
			ok &= ((Expression)conditions_from_v.elementAt(i)).Evaluate(e) != 0;
																// e testa co
		}

		if(!ok)
		{
			if(i > 0)		// alguma con satisfeita
			{
				for(i--; i >= 0; i--)		// devolve as entidativas filas
					((DeadState)entities_from_v.elementAt(i)).PutBack(possible.ve[i]);
			}

			return false;
		}


		for(i = 0; i < resources_from_v.size(); i++)
		{
			int qt;	
			((ResourceQ)resources_from_v.elementAt(i)).
				Acquire(qt = ((Integer)resources_qt_v.elementAt(i)).intValue());
				
			Log.LogMessage(name + ":Acquired " + qt + " resources from " +
				((ResourceQ)resources_from_v.elementAt(i)).name);
		}

		possible.duetime = RegisterEvent(possible.duetime);		// notifica scheduler
		service_q.Enqueue(possible);							// coloca na fila d
		
		if(obs != null)
		{
			obs.StateChange(Observer.BUSY);
			for(i = 0; i < possible.ve.length; i++)
				obs.Incoming(possible.ve[i]);
		}

		for(i = 0; i < possible.ve.length; i++)
			Log.LogMessage(name + ":Entity " + possible.ve[i].GetId() +
				" got from " + ((DeadState)entities_from_v.elementAt(i)).name);

		return true;
	}
}