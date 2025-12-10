// Arquivo Entity.java
// Implementa��o das Classes do Grupo de Modelagem da Biblioteca de Simula��o JAVA
// 26.Mar.1999	Wladimir

package simula;

import java.util.HashMap;

class Entity
{
	/**
	 * A m�nima prioridade (255)
	 */
	public static final short MinPriority = 255;	
	/**
	 * A m�xima prioridade (ZERO)
	 */
	public static final short MaxPriority = 0;
	private static long counter = 1;

	private long 	id;							// id da entidade
	private float creationtime;
	private float timestamp;
	private short priority = 128;	// valor padr�o
	private float qentertime;			// instante em que entrou na fila atual
	private float totalqtime = 0;	// tempo total que passou em filas
	private float qtime = 0;			// tempo que passou na �ltima fila

	private HashMap attributes;

	/**
	 * constr�i uma entidade e atribui o instante da sua cria��o.
	 */
	public Entity(float creationtime)
	{
		timestamp = this.creationtime = creationtime;
		id = counter++;	
	}
	
	/**
	 * obt�m instante de cria��o.
	 */
	public float GetCreationTime(){return creationtime;}
	
	/**
	 * obt�m valor de um atributo personalisado.
	 */
	public float GetAttribute(String name)
	{
		if(attributes == null)
			return Float.NaN;
		Float data = (Float)attributes.get(name);
		if(data == null)							// atributo n�o existe
			return Float.NaN;
		
		return data.floatValue();
	}

	/**
	 * atribui valor a um atributo personalisado; cria, se necess�rio. 
	 */
	public void SetAttribute(String name, float value)
	{
		if(attributes == null)
			attributes = new HashMap(5);			// cria a tabela na primeira atribui��o
		attributes.put(name, new Float(value));		// armazena
	}

	/**
	 * determina prioridade de uma entidade em rela��o �s outras.
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
	 * obt�m prioridade da entidade.
	 */
	public short GetPriority(){return priority;} 

	/**
	 * faz uma marca��o de tempo na entidade.
	 */
	public void Stamp(float time){timestamp = time;}

	/**
	 * obt�m valor da �ltima marca��o.
	 */
	public float GetTimestamp(){return timestamp;}

	/**
	 * obt�m o tempo em que a entidade ficou em filas
	 */
	public float GetTotalQueueTime(){return totalqtime;}

	/**
	 * obt�m o tempo em que a entidade ficou em filas
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