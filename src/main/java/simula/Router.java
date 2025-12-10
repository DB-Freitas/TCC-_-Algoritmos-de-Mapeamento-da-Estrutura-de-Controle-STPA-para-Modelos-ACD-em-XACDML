// Arquivo Router.java
// Implementa��o das Classes do Grupo de Modelagem da Biblioteca de Simula��o JAVA
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
	 * vetores que mant�m as liga��es e par�metros
	 */
	protected Vector entities_from_v, entities_to_v, conditions_to_v,
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
	public Router(Scheduler s)
	{
		super(s);
		// constr�i vetores de liga��es
		entities_from_v = new Vector(1, 1);
		entities_to_v = new Vector(1, 1);
		conditions_to_v = new Vector(1, 1);
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

	/**
	 * Receive a connection FROM a queue
	 * @param from the Origin of the connection
	 */
	public void ConnectQueues(DeadState from) {entities_from_v.add(from);}

	/**
	 * conecta estados mortos � atividade,
	 * mas a entidade � liberada somente se cond � satisfeita;
	 * o usu�rio deve garantir que a entidade siga um caminho apenas (com mais condi��es).
	 */
	public void ConnectQueues(DeadState to, Expression cond)
	{
		entities_to_v.add(to);
		conditions_to_v.add(cond);
	}

	/**
	 * conecta recursos � atividade de forma que n�o se misturem.
	 */
	public void ConnectResources(ResourceQ from, ResourceQ to, int qty_needed)
	{
		resources_from_v.add(from);
		resources_to_v.add(to);
		resources_qt_v.add(new Integer(qty_needed));
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

		if(e == null)						// n�o h� mais nada a servir
			return false;

		if(time < e.duetime)				// servi�o foi interrompido e scheduler 
		{									// n�o foi notificado
			service_q.PutBack(e);				// devolve � fila para ser servido mais tarde
			return false;
		}

		// fim de servi�o!

		for(int j = 0; j < e.ve.length; j++)					//para cada entidade em servi�o...
		{
			if(e.ve[j] == null)	// este pode ser um servi�o que estava bloqueado
				continue;
			for(int i = 0; i < entities_to_v.size(); i++)
				if(((Expression)conditions_to_v.elementAt(i)).Evaluate(e.ve[j]) != 0)
				{
					DeadState q = (DeadState)entities_to_v.elementAt(i);// obt�m fila associada
					if(q.HasSpace())									// se tem espa�o
					{
						q.Enqueue(e.ve[j]);								// envia ao estado morto
						Log.LogMessage(name + ":Entity " + e.ve[j].GetId() +
							" sent to " + q.name);
		
						if(obs != null)
							obs.Outgoing(e.ve[j]);
		
						e.ve[j] = null;
					}
					else
						blocked = true;	// sinaliza e n�o faz nada com a entidade
							
					break;												// p/ pr�x. ent em servi�o
				}
		}
		
		if(blocked)
		{
			service_q.PutBack(e);	// devolve as que restaram	
			Log.LogMessage(name + ":Blocked");
			return false;					// considera como n�o processado
		}

		for(int i = 0; i < resources_to_v.size(); i++)		// e os recursos.
		{
			int qt;
			ResourceQ q = (ResourceQ)resources_to_v.elementAt(i);	// obt�m fila associada
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
							
			if(blocked)		// se ainda estiver bloqueado, n�o faz nada
				return false;
			Log.LogMessage(name + ":Unblocked");
		}
		
		// primeiro verifica se todos os recursos e entidades est�o dispon�veis
		boolean ok = true;
		int esize = entities_from_v.size();

		for(int i = 0; i < resources_from_v.size() && ok; i++)	// os recursos...
			ok &= ((ResourceQ)resources_from_v.elementAt(i)).
				HasEnough(((Integer)resources_qt_v.elementAt(i)).intValue());

		for(int i = 0; i < esize && ok; i++)					// as entidades...
			ok &= ((DeadState)entities_from_v.elementAt(i)).HasEnough();

		if(!ok)					// se alguma entidade ou recurso n�o estiver dispon�vel
			return false;		// n�o realiza nada e informa scheduler
		
		IntQEntry entry = new IntQEntry(esize, (float)d.Draw());

		// retira entidades...

		for(int i = 0; i < esize && ok; i++)					
			entry.ve[i] = ((DeadState)entities_from_v.elementAt(i)).Dequeue();
																
		// obt�m os recursos

		for(int i = 0; i < resources_from_v.size(); i++)	
		{
			int qt;
			((ResourceQ)resources_from_v.elementAt(i)).
				Acquire(qt = ((Integer)resources_qt_v.elementAt(i)).intValue());
			Log.LogMessage(name + ":Acquired " + qt + " resources from " +
				((ResourceQ)resources_from_v.elementAt(i)).name);
		}

		entry.duetime = RegisterEvent(entry.duetime);			// notifica scheduler
		service_q.Enqueue(entry);								// coloca na fila de servi�o

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