// Arquivo Scheduler.java
// 19.Mar.1999	Wladimir

package simula;

import java.util.Vector;

public class Scheduler implements Runnable
{
	private Calendar calendar;		// estrutura de armazenamento dos estados ativos a servir
	private float clock = 0;		// r
	private float endclock;			// fim da sim
	private float timeprecision;	// difeve haver entre dois instantes
									// para que sejam considerados diferentes 
	private Vector activestates;	// Vetor dos estados ativos da
	private boolean crescan = true;	// flag de habiura dos eventos C
	private volatile boolean running = false;
									// controla se a siinuar rodando
	private boolean stopped = false;// indica se a simulforme ordenado
	private Thread simulation;		// thread em que a simr
	private byte termreason;		// porque encerrou a
	
	private static Scheduler s;		// uma refa ao Scheduler
									// para permiarada)

	/**
	 * retorna
	 */
	public static Scheduler Get(){return s;}

	public Scheduler()
	{
		activestates = new Vector(20, 10);
		calendar = new Calendar();
		timeprecision = (float)0.001;
		s = this;
	}
	
	/**
	 * Coloca objeto em seu estado inicial para
	 * Apaga todos os eventos agendados. Deve ser chamado ANTES
	 * de todos os Clear() dos Active/DeadState
	 */
	public void Clear()
	{
		if(running)
			return;
		simulation = null;	// impede contin
		clock = 0;					// reinicia r
		stopped = false;
		termreason = 0;
		calendar = new Calendar();
	}

	
	float ScheduleEvent(ActiveState a, double duetime)
	{
		double time = clock + duetime;
		time = Math.floor(time / timeprecision);
		time *= timeprecision;					// trunca parte menor que timeprecision
		calendar.Add(a, time);
		return (float)time;							// retorna instante realmente utilizado
	}
	
	void Register(ActiveState a)
	{
		if(!activestates.contains(a))
			activestates.addElement(a);
	}
	
	/**
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	public void CRescan(boolean on)
	{	
		if(!running)
			crescan = on;
	}

	/**
	 * retorna true se ou
	 */
	public boolean Finished(){return stopped;}
	
	/**
	 * Inicia  thread separada
	 */
	public synchronized boolean Run(double endtime)
	{
		if(endtime < 0.0)				// rele ser negativo
			return false;				// se forem as entidades

		if(!running)
		{
			if(activestates.isEmpty())	// se  ativo registrado,
				return false;			// como executar?
			activestates.trimToSize();
			running = true;
			stopped = false;
			endclock = (float)endtime;
			clock = 0;
			termreason = 0;
			simulation = new Thread(this);
			simulation.setPriority(Thread.MAX_PRIORITY);
			simulation.start();			// inicia exe
			Log.LogMessage("Scheduler: simulation started");

			return true;
		}

		return false;
	}
	
	/**
	 *
	 */
	public synchronized void Stop()
	{
		if(stopped)						// s
			return;
		
		stopped = false;
		running = false;				// encerramento suave
		try
		{
			simulation.join(5000);		// espera aos
		}
		catch(InterruptedException e)
		{
			stopped = true;
			simulation = null;	// n
			termreason = 4;			// para
			Log.LogMessage("Scheduler: simulation stopped drastically");
			Log.Close();
			return;							// jo...
		}
		
		termreason = 3;			// parada suave bem sucedida
			
		if(!stopped)					// se ain..
		{
			try
			{
				simulation.interrupt();		// �stica
			}
			catch(SecurityException e){}
			
			simulation = null;	// nuar
			termreason = 4;
			Log.LogMessage("Scheduler: simulation stopped drastically");
			Log.Close();
		}
		
		Log.LogMessage("Scheduler: simulation paused");
		
	}
	
	/**
	 * Continua Stop()
	 */
	public synchronized boolean Resume()
	{
		if(running || simulation == null || termreason != 3)
			return false;
		
		running = true;
		stopped = false;	
		simulation.start();
		termreason = 0;
		Log.LogMessage("Scheduler: simulation resumed");
		
		return true;
	}
	
	/**
	 * Seta o mo.
	 */
	public void SetPrecision(double timeprec)
	{ 
		if(!running)
			timeprecision = (float)timeprec;
	}

	/**
	 * retorna rel�gio da
	 */
	public float GetClock(){return clock;}

	/**
	 * oda a simulacao. (rodado numa Thread separada)
	 */
	public void run()
	{
		while(running)
		{
			// atualiza rel�gio da simu

			clock = calendar.GetNextClock();
			
//			Log.LogMessage("Scheduler: clock advanced to " + clock);
			Log.LogMessage("Scheduler: clock advanced to " + Math.round(clock));
			// verifica se siim

			if(clock == 0.0)			// fim das entidades
			{
				running = false;
				termreason = 1;			
				Log.LogMessage("Scheduler: simulation finished due to end of entities");
				Log.Close();
				break;
			}
			if(clock >= endclock && endclock != 0.0)	// fim do intervalo
			{
				running = false;
				termreason = 2;
				Log.LogMessage("Scheduler: simulation finished due to end of simulation time");
				Log.Close();
				break;
			}

			boolean executed;			// se algum evento B ou C foi executado
			
			// Fase B
			
			executed = false;
			ActiveState a;

			do
			{
				a = calendar.GetNext();
				executed |= a.BServed(clock);	// se ao menos um executou, fica registrado
			}while(calendar.RemoveNext());

			if(!executed)				// se ada a ser executado nesse instante
				continue;				// pula para o executar a fase C.
										// (as atividades podem ter alterado o tempo localmente)
						
			// Fase C

			do
			{
				executed = false;

				for(short i = 0; i < activestates.size(); i++)
					executed |= ((ActiveState)activestates.elementAt(i)).CServed();
				
			}while(crescan && executed);	
		}

		stopped = true;			// sinaliza o encerramento
		running = false;
	}
}