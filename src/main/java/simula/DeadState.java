// Arquivo DeadState.java
// 26.Mar.1999	Wladimir

package simula;

public abstract class DeadState
{
	private short capacity;

	/**
	 */
	protected short count = 0;
	/**
	 */
	protected Scheduler s;
	/**
	 */
	protected Observer obs;	
	/**
	 * nome (para identificar estado para o log)
	 */
	public String name = "";	
	/**
	 * construtor: se max for nulo, tem capacidade ilimitada.
	 */
	public DeadState(Scheduler s, int max){capacity = (short)max; this.s = s;}	
	
	/**
	 */
	public boolean HasSpace(int nentities)
	{return (capacity != 0)? ((short)nentities <= capacity - count) : true;}
	
	/**
	 */
	public boolean HasSpace()
	{return (capacity != 0)? (1 <= capacity - count) : true;}
	
	/**
	 */
	public boolean HasEnough(int nentities)	
	{return (short)nentities <= count;}
	
	/**
	 */
	public boolean HasEnough()	
	{return 1 <= count;}
	
	/**
	 * retorna o tamanho da fila (para ser usado por Observer).
	 */
	public short ObsLength(){return count;}		

	/**
	 * associa observador
	 */
	public void SetObserver(Observer o)
	{
		if(obs == null)
			obs = o;
		else
			obs.Link(o);
	}
	
	/**
	 */
	public void Clear()
	{
		if(obs == null)
			return;
		obs.Clear();
	}

	/**
	 * adiciona entidade e no "final" da fila; 
	 */
	public abstract void Enqueue(Entity e);					
	/**
	 * remove entidade da "frente" da fila; 
	 */
	public abstract Entity Dequeue();						
	/**
	 */
	public abstract void PutBack(Entity e);
}