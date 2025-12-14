// Arquivo IntQEntry.java
// 9.Abr.1999	Wladimir

package simula;

class IntQEntry
{
	/**
	 */
	public Entity ve[];				
	/**
	 */
	public float duetime;		

	public IntQEntry(int nentities, float duetime)
	{
		ve = new Entity[nentities];
		this.duetime = duetime;
	}
}