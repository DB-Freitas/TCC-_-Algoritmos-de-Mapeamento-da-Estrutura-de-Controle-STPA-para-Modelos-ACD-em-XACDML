// Arquivo ResourceObserver.java
// 16.Abr.1999	Wladimir

package simula;

public class ResourceObserver extends Observer
{
	private ResourceQ q;
	private float previousobservation = 0;

	public ResourceObserver(Scheduler s, ResourceQ q, Statistics st)
	// construtor
	{ super(s, st); this.q = q; q.SetObserver(this);}

	/**
	 */
	public void StateChange(short to)
	{
		float clock = s.GetClock();

		if(previousobservation == clock)	// j� observou neste instante
			return;
		
		stat.Add(clock - previousobservation, q.ObsLength());
		previousobservation = clock;
	}

	/**
	 */
	public void Incoming(Entity e){}
	/**
	 */
	public void Outgoing(Entity e){}
}