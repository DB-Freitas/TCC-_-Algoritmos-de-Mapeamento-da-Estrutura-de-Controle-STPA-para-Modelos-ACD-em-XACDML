// Arquivo StackQ.java
// 26.Mar.1999	Wladimir

package simula;

import java.util.EmptyStackException;
import java.util.Stack;

public class StackQ extends DeadState
{
	private Stack q;					// implementa fila como vetor

	/**
	 */
	public StackQ(Scheduler s, short max)
	{
		super(s, max);
		q = new Stack();
		q.ensureCapacity(max);	
	} 
	
	/**
	 */
	public StackQ(Scheduler s)
	{
		super(s, 0);
		q = new Stack();	
		q.ensureCapacity(10);
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
		q.push(e);
		count++;
		e.EnteredQueue(s.GetClock());
	}
	
	/**
	 */
	public void PutBack(Entity e)
	{	
		if(obs != null)
			obs.Incoming(e);
		q.push(e);
		count++;
		e.EnteredQueue(s.GetClock());
	}
	/**
	 */
	public Entity Dequeue()
	{
		try
		{
			Entity e = (Entity)q.peek();
			e.LeftQueue(s.GetClock());
			if(obs != null)
				obs.Outgoing(e);
			q.pop();
			count--;
			return e;
		}
		catch(EmptyStackException x){return null;}	
	}
}