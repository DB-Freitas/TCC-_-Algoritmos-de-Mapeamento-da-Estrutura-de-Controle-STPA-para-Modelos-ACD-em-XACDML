// Arquivo ActiveState.java
// 26.Mar.1999	Wladimir

package simula;

public abstract class ActiveState
{
	/**
	 */
	protected Scheduler s;		
	/**
	 */
	protected ActiveObserver obs;
	/**
	 */
	public String name = "";		

	/**
	 * registra com o Scheduler o estado ativo para ser servido na fase B 
	 */
	final protected float RegisterEvent(float time)
	{
		return s.ScheduleEvent(this, time);
	}

	/**
	 * referencia Scheduler para registrar-se e registrar eventos da fase B.
	 */
	public ActiveState(Scheduler s)
	{
		this.s = s;
		s.Register(this);
	}

	/**
	 * associa observador
	 */
	public void SetObserver(ActiveObserver o)
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
	 */
	public abstract boolean BServed(float time);
	/**
	 * executa a fase C; retorna true se algum evento condicional ocorreu.
	 */
	public abstract boolean CServed();
}