// Arquivo Scheduler.java
// Implementa��o das Classes do Grupo Executivo da Biblioteca de Simula��o JAVA
// 19.Mar.1999	Wladimir

package simula;

import java.util.Vector;

public class Scheduler implements Runnable
{
	private Calendar calendar;		// estrutura de armazenamento dos estados ativos a servir
	private float clock = 0;		// rel�gio da simula��o
	private float endclock;			// fim da simula��o
	private float timeprecision;	// diferen�a m�nima que deve haver entre dois instantes
									// para que sejam considerados diferentes 
	private Vector activestates;	// Vetor dos estados ativos da simula��o
	private boolean crescan = true;	// flag de habilita��o de re-varrudura dos eventos C
	private volatile boolean running = false;
									// controla se a simula��o deve continuar rodando
	private boolean stopped = false;// indica se a simula��o parou conforme ordenado
	private Thread simulation;		// thread em que a simula��o ir� executar
	private byte termreason;		// porque encerrou a simula��o
	
	private static Scheduler s;		// uma refer�ncia est�tica ao Scheduler
									// para permitir a��es de emerg�ncia (parada)

	/**
	 * retorna refer�ncia ao objeto ativo
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
	 * Coloca objeto em seu estado inicial para simula��o
	 * Apaga todos os eventos agendados. Deve ser chamado ANTES
	 * de todos os Clear() dos Active/DeadState
	 */
	public void Clear()
	{
		if(running)
			return;
		simulation = null;	// impede continua��o
		clock = 0;					// reinicia rel�gio
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
	 * retorna true se a simula��o terminou
	 */
	public boolean Finished(){return stopped;}
	
	/**
	 * Inicia execu�ao da simulacao numa thread separada
	 */
	public synchronized boolean Run(double endtime)
	{
		if(endtime < 0.0)				// rel�gio n�o pode ser negativo
			return false;				// se for 0.0 executa at� acabarem as entidades

		if(!running)
		{
			if(activestates.isEmpty())	// se n�o h� nenhum estado ativo registrado, 
				return false;			// como executar?
			activestates.trimToSize();
			running = true;
			stopped = false;
			endclock = (float)endtime;
			clock = 0;
			termreason = 0;
			simulation = new Thread(this);
			simulation.setPriority(Thread.MAX_PRIORITY);
			simulation.start();			// inicia execu��o
			Log.LogMessage("Scheduler: simulation started");

			return true;
		}

		return false;
	}
	
	/**
	 * P�ra a simulacao
	 */
	public synchronized void Stop()
	{
		if(stopped)						// se j� parou
			return;
		
		stopped = false;
		running = false;				// encerramento suave
		try
		{
			simulation.join(5000);		// espera at� 5 segundos
		}
		catch(InterruptedException e)
		{
			stopped = true;
			simulation = null;	// n�o pode continuar
			termreason = 4;			// parada dr�stica
			Log.LogMessage("Scheduler: simulation stopped drastically");
			Log.Close();
			return;							// j� parou mesmo...
		}
		
		termreason = 3;			// parada suave bem sucedida
			
		if(!stopped)					// se ainda n�o parou...
		{
			try
			{
				simulation.interrupt();		// p�ra de forma dr�stica
			}
			catch(SecurityException e){}
			
			simulation = null;	// n�o pode continuar
			termreason = 4;
			Log.LogMessage("Scheduler: simulation stopped drastically");
			Log.Close();
		}
		
		Log.LogMessage("Scheduler: simulation paused");
		
	}
	
	/**
	 * Continua a execu��o de uma simula��o parada por Stop()
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
	 * Seta o m�nimo intervalo entre dois instantes para que sejam considerados o mesmo.
	 */
	public void SetPrecision(double timeprec)
	{ 
		if(!running)
			timeprecision = (float)timeprec;
	}

	/**
	 * retorna rel�gio da simula��o.
	 */
	public float GetClock(){return clock;}

	/**
	 * C�digo que roda a simulacao. (rodado numa Thread separada)
	 */
	public void run()
	{
		while(running)
		{
			// atualiza rel�gio da simula��o

			clock = calendar.GetNextClock();
			
//			Log.LogMessage("Scheduler: clock advanced to " + clock);
			Log.LogMessage("Scheduler: clock advanced to " + Math.round(clock));
			// verifica se simula��o chegou ao fim

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

			if(!executed)				// se n�o havia nada a ser executado nesse instante
				continue;				// pula para o pr�ximo sem executar a fase C.
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