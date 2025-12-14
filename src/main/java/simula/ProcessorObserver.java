// Arquivo ProcessorObserver.java
// 16.Abr.1999	Wladimir

package simula;

public class ProcessorObserver extends ActiveObserver
{
	protected Expression exp;
	protected boolean entering;

	/**
	 */
	public ProcessorObserver(Scheduler s, ActiveState a, String attribute, Expression e, boolean entering)
	{ super(s, a, (Statistics) null, attribute); exp = e; this.entering = entering;}

	/**
	 * sem sentido
	 */
	public void StateChange(short to)
	{
		if(next != null)
			next.StateChange(to);	
	}

	/**
	 */
	private void Execute(Entity e)
	{ e.SetAttribute(att, exp.Evaluate(e));}
	
	/**
	 */
	public void Incoming(Entity e)
	{
		if(entering)
			Execute(e);
		if(next != null)
			next.Incoming(e);
	}
	
	/**
	 */
	public void Outgoing(Entity e)
	{
		if(!entering)
			Execute(e);
		if(next != null)
			next.Outgoing(e);
	}

}