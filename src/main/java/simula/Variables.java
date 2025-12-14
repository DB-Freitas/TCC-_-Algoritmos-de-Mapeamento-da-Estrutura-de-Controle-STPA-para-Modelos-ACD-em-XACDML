// Arquivo Variables.java
// 22.Abr.1999	Wladimir

package simula;

import java.util.HashMap;

/**
 */
public class Variables
{
	private HashMap table;			// armais
	private HashMap queues;			// arm filas
										// para obter seus comprimentos
	
	public Variables()
	// construtor
	{
		table = new HashMap(20);		// capacidade inicial pa
	}
	
	/**
	 * cria e inicializa com inival;
	 * se name  true
	 */
	public boolean CreateVar(String name, float inival)
	{
		if(table.containsKey(name))
			return false;
			
		table.put(name, new Float(inival));
		return true;
	}
	
	/**
	 * cria var inicializa com zero;
	 * se namerue
	 */
	public boolean CreateVar(String name){return CreateVar(name, 0);}
	
	/**
	 */
	public void DeleteVar(String name){table.remove(name);}
	
	/**
	 */
	public boolean SetVar(String name, float value)
	{
		if(!table.containsKey(name))
			return false;
		
		table.put(name, new Float(value));
		return true;
	}
	
	/**
	 */
	public float Value(String name)
	{
		if(!table.containsKey(name))
		{
			if(!queues.containsKey(name))
				return Float.NaN;
			
			DeadState q = (DeadState)queues.get(name);
			return q.ObsLength();
		}
		
		return ((Float)table.get(name)).floatValue();
	}

	public void AssignQueuesTable(HashMap qtable){queues = qtable;}
	
}