// Arquivo IntPriorityQ.java
// 9.Abr.1999	Wladimir

package simula;

import java.util.NoSuchElementException;
import java.util.Vector;

class IntPriorityQ
{
	private Vector q;					// implementa fila como vetor

	/**
	 */
	public IntPriorityQ()
	{
		q = new Vector(5, 5);	
	}
	
	/**
	 */
	public void Enqueue(IntQEntry e)
	{
		int min, max, cur;	// max pode ser negativo (qdo for ins
		IntQEntry e2;
		min = 0;
		max = q.size() - 1;
		cur = 0;
		while(min <= max)
		{
			cur = (min + max) / 2;
			e2 = (IntQEntry)q.elementAt(cur);
			if(e.duetime < e2.duetime)
				max = cur - 1;
			else if(e.duetime >= e2.duetime)				// aridade
				min = ++cur;								// qu
		}
		q.insertElementAt(e, cur);
	}
	
	/**

	 */
	public void PutBack(IntQEntry e)
	{	
		q.insertElementAt(e, 0);
	}
	
	/**
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
	 */
	public boolean IsEmpty(){return q.isEmpty();}
}