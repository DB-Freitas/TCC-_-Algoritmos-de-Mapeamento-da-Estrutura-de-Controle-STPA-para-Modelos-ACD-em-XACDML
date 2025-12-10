// Arquivo PriorityQ.java
// Implementa��o das Classes do Grupo de Modelagem da Biblioteca de Simula��o JAVA
// 26.Mar.1999	Wladimir

package simula;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Classe que implementa uma fila de prioridades
 */
public class PriorityQ extends DeadState
{
	private Vector q;					// implementa fila como vetor

	/**
	 * constr�i uma fila vazia com capacidade para max entidades. 
	 */
	public PriorityQ(Scheduler s, int max)
	{
		super(s, max);
		q = new Vector(max);	
	} 
	
	/**
	 * constr�i uma fila vazia com capacidade ilimitada. 
	 */
	public PriorityQ(Scheduler s)
	{
		super(s, 0);
		q = new Vector(10, 10);	
	}
	
	/**
	 * Coloca objeto em seu estado inicial para simula��o
	 */
	public void Clear()
	{
		super.Clear();
		q.clear();
	}
	
	/**
	 * implementa a interface segundo a pol�tica priority FIFO; atualiza atributos de tamanho.
	 */
	public void Enqueue(Entity e)
	{
		if(obs != null)
			obs.Incoming(e);
		e.EnteredQueue(s.GetClock());
		int min, max, cur;	// max pode ser negativo (qdo for inserir no come�o)
		Entity e2;
		// encontra posi��o de inser��o baseado na prioridade da Entity e.
		// implementa busca bin�ria, j� que os elementos es�o ordenados por prioridade.
		min = 0;
		max = q.size() - 1;
		cur = 0;
		while(min <= max)
		{
			cur = (min + max) / 2;
			e2 = (Entity)q.elementAt(cur);
			if(e.GetPriority() < e2.GetPriority())
				max = cur - 1;
			else if(e.GetPriority() >= e2.GetPriority())	// ap�s os de mesma prioridade 
				min = ++cur;								// que j� est�o na fila
		}
		// cur cont�m a posi��o de inser��o
		q.insertElementAt(e, cur);
		count++;
	}
	/**
	 * implementa a interface segundo a pol�tica priority FIFO; atualiza atributos de tamanho.
	 */
	public void PutBack(Entity e)
	{	
		e.EnteredQueue(s.GetClock());
		if(obs != null)
			obs.Incoming(e);
		q.insertElementAt(e, 0);
		count++;
	}
	/**
	 * implementa a interface segundo a pol�tica priority FIFO; atualiza atributos de tamanho.
	 */
	public Entity Dequeue()
	{
		try
		{
			Entity e = (Entity)q.firstElement();
			e.LeftQueue(s.GetClock());
			if(obs != null)
				obs.Outgoing(e);
			q.removeElementAt(0);
			count--;
			return e;
		}
		catch(NoSuchElementException x){return null;}
	}
}