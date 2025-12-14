// Arquivo TTreeEntry.java
// 19.Mar.1999	Wladimir

package simula;

/**
 */
class TTreeEntry
{
	public TTreeEntry left, right, middle, parent;
	public float time;
	public ActiveState a;
	public TTreeEntry(){ left = right = middle = parent = null;
		time = (float)0.0; a = null;}
	public TTreeEntry(ActiveState a, double duetime)
		{ left = right = middle = parent = null;
		  time = (float)duetime; this.a = a;}
}
