// Arquivo Observer.java
// 16.Abr.1999	Wladimir

package simula;

/**
 * Implements an observer of a System's Entry
 */
public abstract class Observer
{
	Histogram hist;
	Statistics stat;
	protected Scheduler s;
	protected Observer next;

	/**
	 * IDLE, BUSY:
	 * constantes que definem estado de recursos
	 * e estados ativos 
	 */
	public static final short IDLE = 0, BUSY = 1;	

	public Observer(Scheduler s, Histogram h){ this.s = s; hist = h;}
	public Observer(Scheduler s, Statistics st){ this.s = s; stat = st;}
	
	/**
	 * liga em uma lista os observadores
	 */
	public final void Link(Observer obs)
	{
		if(next == null)
			next = obs;
		else
			next.Link(obs);
	}
	
	/**
	 */
	public void Clear()
	{
		if(hist != null)	// limpa estat�sticas
			hist.Clear();
		if(stat != null)
			stat.Clear();
		if(next != null)	// e todos observers ligados
			next.Clear();
	}

	/**
	 */
	abstract public void StateChange(short to);
	
	/**
	 * realiza processamento na entidade e que acaba de chegar ao Active/DeadState;
	 */
	abstract public void Incoming(Entity e);
	
	/**
	 * realiza processamento na entidade e prestes a sair ao Active/DeadState;
	 */
	abstract public void Outgoing(Entity e);

	/**
	 * calcula a m�dia.
	 */
	public final float Mean()
	{
		if(hist == null)
			return stat.Mean();
		return hist.Mean();
	}

	/**
	 */
	public final float StdDev()
	{
		if(hist == null)
			return stat.StdDev();
		return hist.StdDev();
	}

	/**
	 */
	public final float Variance()
	{
		if(hist == null)
			return stat.Variance();
		return hist.Variance();
	}

	/**
	 */
	public final float Max()
	{
		if(hist == null)
			return stat.Max();
		return hist.Max();
	}
	
	/**
	 */
	public final float Min()
	{
		if(hist == null)
			return stat.Min();
		return hist.Min();
	}
	
	/**
	 */
	public final int NumObs()
	{
		if(hist == null)
			return stat.NumObs();
		return hist.NumObs();
	}	

}