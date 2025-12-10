// Arquivo IntPriorityQ.java
// Implementa��o das Classes do Grupo de Modelagem da Biblioteca de Simula��o JAVA
// 9.Abr.1999	Wladimir

package simula;

import java.util.NoSuchElementException;
import java.util.Vector;

class IntPriorityQ
{
	private Vector q;					// implementa fila como vetor

	/**
	 * constr�i uma fila vazia com capacidade ilimitada. 
	 */
	public IntPriorityQ()
	{
		q = new Vector(5, 5);	
	}
	
	/**
	 * coloca no fim da fila de acordo com o instante de servi�o
	 */
	public void Enqueue(IntQEntry e)
	{
		int min, max, cur;	// max pode ser negativo (qdo for inserir no come�o)
		IntQEntry e2;
		// encontra posi��o de inser��o baseado no tempo de servi�o de IntQEntry e.
		// implementa busca bin�ria, j� que os elementos es�o ordenados por tempo.
		min = 0;
		max = q.size() - 1;
		cur = 0;
		while(min <= max)
		{
			cur = (min + max) / 2;
			e2 = (IntQEntry)q.elementAt(cur);
			if(e.duetime < e2.duetime)
				max = cur - 1;
			else if(e.duetime >= e2.duetime)				// ap�s os de mesma prioridade 
				min = ++cur;								// que j� est�o na fila
		}
		// cur cont�m a posi��o de inser��o
		q.insertElementAt(e, cur);
	}
	
	/**
	 * recoloca na cabe�a da fila;
	 * n�o � checado o tempo de servi�o, portanto s� deve ser usada se for para elemento que
	 * acabou de ser retirado atrav�s de Dequeue
	 */
	public void PutBack(IntQEntry e)
	{	
		q.insertElementAt(e, 0);
	}
	
	/**
	 * retira da cabe�a da fila.
	 */
	public IntQEntry Dequeue()
	{
		try
		{
			IntQEntry e = (IntQEntry)q.firstElement();
			q.removeElementAt(0);
			return e;
		}
		catch(NoSuchElementException x){return null;}
	}

	/**
	 * retira do fim da fila
	 */
	public IntQEntry FromTail()
	{
		try
		{
			IntQEntry e = (IntQEntry)q.lastElement();
			q.removeElementAt(q.size() - 1);
			return e;
		}
		catch(NoSuchElementException x){return null;}
	}
	
	/**
	 * retorna true se fila est� vazia
	 */
	public boolean IsEmpty(){return q.isEmpty();}
}