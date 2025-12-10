// Arquivo Variables.java
// Implementa��o das Classes do Grupo de Modelagem da Biblioteca de Simula��o JAVA
// 22.Abr.1999	Wladimir

package simula;

import java.util.HashMap;

/**
 * Classe que guarda vari�veis - pares (nome, valor)
 */
public class Variables
{
	private HashMap table;			// armaz�m de vari�veis
	private HashMap queues;			// armaz�m de refer�ncias �s filas
										// para obter seus comprimentos
	
	public Variables()
	// construtor
	{
		table = new HashMap(20);		// capacidade inicial para 20 vari�veis	
	}
	
	/**
	 * cria vari�vel name e inicializa com inival;
	 * se name j� existir, retorna false, sen�o true
	 */
	public boolean CreateVar(String name, float inival)
	{
		if(table.containsKey(name))
			return false;
			
		table.put(name, new Float(inival));
		return true;
	}
	
	/**
	 * cria vari�vel name e inicializa com zero;
	 * se name j� existir, retorna false, sen�o true
	 */
	public boolean CreateVar(String name){return CreateVar(name, 0);}
	
	/**
	 * exclui vari�vel name
	 */
	public void DeleteVar(String name){table.remove(name);}
	
	/**
	 * atribui � vari�vel name valor value;
	 * se name n�o existir retorna false, sen�o true
	 */
	public boolean SetVar(String name, float value)
	{
		if(!table.containsKey(name))
			return false;
		
		table.put(name, new Float(value));
		return true;
	}
	
	/**
	 * recupera valor da vari�vel name; se name n�o existir retorna NAN
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
	
	/**
	 * atribui tabela contendo (nome da fila, refer�ncia)
	 * de todas as filas do modelo, fazendo com que seus nomes
	 * se tornem nomes globais; o nome de uma fila retorn seu comprimento
	 */
	public void AssignQueuesTable(HashMap qtable){queues = qtable;}
	
}