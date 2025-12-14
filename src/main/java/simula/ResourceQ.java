// Arquivo ResourceQ.java
// 26.Mar.1999	Wladimir

package simula;

public class ResourceQ extends DeadState
{
	private short init_qty;

	/**
	 */
	public ResourceQ(Scheduler s, int init_qty)
	{
		super(s, 0);	// sinaliza que este estado tem capacidade ilimitada
		this.init_qty = count = (short)init_qty;
	}
	
	/**
	 */
	public ResourceQ(Scheduler s){super(s, 0);}
	
	/**
	 */
	public void Clear()
	{
		super.Clear();
		count = init_qty;;
	}
	
	/**
	 */
	public void Enqueue(Entity e)
	{
		System.err.println("\nChamou Enqueue() de um objeto ResourceQ!\nEncerrando simula��o!");
		Scheduler.Get().Stop();
	}
	
	/**
	 */
	public Entity Dequeue()
	{
		System.err.println("\nChamou Dequeue() de um objeto ResourceQ!\nEncerrando simula��o!");
		Scheduler.Get().Stop();
		return null;
	}
	/**
	 */
	public void PutBack(Entity e) 
	{
		System.err.println("\nChamou PutBack() de um objeto ResourceQ!\nEncerrando simula��o!");
		Scheduler.Get().Stop();
	}
	
	/**
	 */
	public void Acquire(int n)
	{
		if(obs != null)
			obs.StateChange(Observer.IDLE);
		count -= (short)n;
	}

	/**
	 */
	public void Release(int n)
	{
		if(obs != null)
			obs.StateChange(Observer.IDLE);
		count += (short)n;
	}

}