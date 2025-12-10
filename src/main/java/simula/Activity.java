// Arquivo Activity.java
// Implementa��o das Classes do Grupo de Modelagem da Biblioteca de Simula��o JAVA
// 9.Abr.1999	Wladimir

package simula;

import java.util.Vector;

public class Activity extends ActiveState
{

	/**
	 * entities_from_v, entities_to_v, conditions_from_v,
	 * resources_from_v, resources_to_v, resources_qt_v:
	 * vetores que mant�m as liga��es e par�metros
	 */
	protected Vector entities_from_v, entities_to_v, conditions_from_v,
					 	resources_from_v, resources_to_v, resources_qt_v;
													
	private Distribution d;		// gerador de n�meros aleat�rios de uma dada distribui��o

	/**
	 * fila de entidades/recursos em servi�o
	 */
	protected IntPriorityQ service_q;
		
	/**
	 * se est� bloqueado
	 */
	protected boolean blocked;	

	/**
	 * constr�i um estado ativo sem conex�es ou tempo de servi�o definidos.
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
	 * determina o tempo de servi�o de acordo com a distribui��o especificada;
	 * os par�metros da distribui��o s�o passados na cria��o do objeto.
	 */
	public void SetServiceTime(Distribution d){this.d = d;}
	
	public void ConnectQueues(DeadState from, DeadState to)
	{ConnectQueues(from, ConstExpression.TRUE, to);}
	
	/**
	 * conecta estados mortos � atividade de forma que 
	 * a(s) entidade(s) (recurso(s)) n�o se misturem.
	 */
	public void ConnectResources(ResourceQ from, ResourceQ to, int qty_needed)
	{
		resources_from_v.add(from);
		resources_to_v.add(to);
		resources_qt_v.add(new Integer(qty_needed));
	}
	
	/**
	 * conecta estados mortos � atividade de forma que 
	 * a(s) entidade(s) (recurso(s)) n�o se misturem.
	 * mas a entidade � obtida de from somente se cond � satisfeita
	 */
	public void ConnectQueues(DeadState from, Expression cond, DeadState to)
	{
		conditions_from_v.add(cond);
		entities_from_v.add(from);
		entities_to_v.add(to);
	}
	
	/**
	 * Coloca objeto em seu estado inicial para simula��o
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
		if(blocked)									// n�o faz nada enquanto estiver bloqueado
			return false;
			
		IntQEntry e = service_q.Dequeue();

		if(e == null)								// n�o h� mais nada a servir
			return false;

		if(time < e.duetime)				// servi�o foi interrompido e scheduler 
		{														// n�o foi notificado
			service_q.PutBack(e);				// devolve � fila para ser servido mais tarde
			return false;
		}

		// fim de servi�o!
		
		boolean shouldnotblock = true;

		for(int i = 0; i < entities_to_v.size(); i++)		// as entidades...
		{
			DeadState q = (DeadState)entities_to_v.elementAt(i);	// obt�m fila associada
			shouldnotblock &= q.HasSpace();												// condi��o para n�o bloquear
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
			DeadState q = (DeadState)entities_to_v.elementAt(i);	// obt�m fila associada
			if(q.HasSpace())										// se tem espa�o
				q.Enqueue(e.ve[i]);									// envia ao estado morto
		}
		
		for(int i = 0; i < resources_to_v.size(); i++)		// e os recursos.
		{
			int qt;
			ResourceQ q = (ResourceQ)resources_to_v.elementAt(i);	// obt�m fila associada
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
							
			if(blocked)		// se ainda estiver bloqueado, n�o faz nada
				return false;
			Log.LogMessage(name + ":Unblocked");
		}
			
		
		// primeiro verifica se todos os recursos e entidades est�o dispon�veis
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
		for(i = 0; i < esize && ok; i++)					// as condi��es.
		{
			possible.ve[i] = e = ((DeadState)entities_from_v.elementAt(i)).Dequeue();
																// retira entidades...
			ok &= ((Expression)conditions_from_v.elementAt(i)).Evaluate(e) != 0;
																// e testa condi��o
		}

		if(!ok)
		{
			if(i > 0)		// alguma condi��o n�o foi satisfeita
			{
				for(i--; i >= 0; i--)		// devolve as entidades �s respectivas filas
					((DeadState)entities_from_v.elementAt(i)).PutBack(possible.ve[i]);
			}

			return false;
		}

		// obt�m os recursos

		for(i = 0; i < resources_from_v.size(); i++)
		{
			int qt;	
			((ResourceQ)resources_from_v.elementAt(i)).
				Acquire(qt = ((Integer)resources_qt_v.elementAt(i)).intValue());
				
			Log.LogMessage(name + ":Acquired " + qt + " resources from " +
				((ResourceQ)resources_from_v.elementAt(i)).name);
		}

		possible.duetime = RegisterEvent(possible.duetime);		// notifica scheduler
		service_q.Enqueue(possible);							// coloca na fila de servi�o
		
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