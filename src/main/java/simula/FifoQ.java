// Arquivo FifoQ.java
// 26.Mar.1999	Wladimir

package simula;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Implements a First-In First-Out Queue
 */
public class FifoQ extends DeadState
{
	private Vector q;					// implementa fila como vetor

	/**
	 */
	public FifoQ(Scheduler s, short max)
	{
		super(s, max);
		q = new Vector(max);	
	} 
	
	/**
	 */
	public FifoQ(Scheduler s)
	{
		super(s, 0);
		q = new Vector(10, 10);	
	}
	
	/**
	 */
	public void Clear()
	{
		super.Clear();
		q.clear();
	}

	/**
	 */
	public void Enqueue(Entity e)
	{
		if(obs != null)
			obs.Incoming(e);
		q.addElement(e);
		count++;
		e.EnteredQueue(s.GetClock());
	}
	/**
	 */
	public void PutBack(Entity e)
	{	
		if(obs != null)
			obs.Incoming(e);
		q.insertElementAt(e, 0);
		count++;
		e.EnteredQueue(s.GetClock());
	}
	/**
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