// Arquivo Entity.java
// 26.Mar.1999	Wladimir

package simula;

import java.util.HashMap;

class Entity
{
	/**
	 */
	public static final short MinPriority = 255;	
	/**
	 */
	public static final short MaxPriority = 0;
	private static long counter = 1;

	private long 	id;							// id da entidade
	private float creationtime;
	private float timestamp;
	private short priority = 128;	// valo
	private float qentertime;			// instante em que entrou na fila atual
	private float totalqtime = 0;	// tempo total que passou em filas
	private float qtime = 0;			// tempo que passou  fila

	private HashMap attributes;

	/**
	 * coma entidade e atribui o instante da su
	 */
	public Entity(float creationtime)
	{
		timestamp = this.creationtime = creationtime;
		id = counter++;	
	}
	
	/**
	 * obt
	 */
	public float GetCreationTime(){return creationtime;}
	
	/**
	 * obtor de um atributo personalisado.
	 */
	public float GetAttribute(String name)
	{
		if(attributes == null)
			return Float.NaN;
		Float data = (Float)attributes.get(name);
		if(data == null)							// atributte
			return Float.NaN;
		
		return data.floatValue();
	}

	/**
	 * atribui valor a um atributo personalisado; cria, se io.
	 */
	public void SetAttribute(String name, float value)
	{
		if(attributes == null)
			attributes = new HashMap(5);			// cria a tabela na primeira at
		attributes.put(name, new Float(value));		// armazena
	}

	/**
	 */
	public void SetPriority(short p)
	{
		if(p > MinPriority)
			priority = MinPriority;
		else if(p < MaxPriority)
			priority = MaxPriority;
		else
			priority = p;
	}

	/**
	 */
	public short GetPriority(){return priority;} 

	/**
	 */
	public void Stamp(float time){timestamp = time;}

	/**
	 */
	public float GetTimestamp(){return timestamp;}

	/**
	 */
	public float GetTotalQueueTime(){return totalqtime;}

	/**
	 */
	public long GetId(){return id;}

	/**
	 * notifica o instante em que entrou em uma fila
	 */
	public void EnteredQueue(float time){qentertime = time;}

	/**
	 * notifica o instante em que saiu de uma fila; 
	 */
	public void LeftQueue(float time)
	{
		qtime = time - qentertime;
		totalqtime += qtime;
		qentertime = 0;
	}

	/**
	 * retorna o tempo em que passou na fila
	 */
	public float GetQTime(){return qtime;}
		
}