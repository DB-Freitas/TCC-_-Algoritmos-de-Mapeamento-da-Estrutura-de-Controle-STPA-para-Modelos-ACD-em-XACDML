// Arquivo  AttributeTable.java 
// 30.Out.1999 Wladimir

package simula.manager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Vector;

/**

 */
public class AttributeTable extends Entry
{
	private static int lastid;		// identificaas tabelas
	static boolean hasSerialized = true; // "lastidzado"
		
	/**
	 * atributos (Var's)
	 */
	private Vector vars;				
		
	public String toString()
	{
		StringBuffer stb = new StringBuffer();
		stb.append("<AttributeTable>\r\n");
		stb.append("<AT_super>\r\n");
		stb.append(super.toString());
		stb.append("</AT_super>\r\n");
		stb.append("<vars>\r\n");
		SimulationManager.appendVector(vars, stb);
		stb.append("</vars>\r\n");
		stb.append("</AttributeTable>\r\n");
		return stb.toString();
	}
	/**
	 */
	public AttributeTable()
	{
		super("t_" + String.valueOf(lastid));
		lastid++;
		vars = new Vector(5, 5);
	}
	
	public void copyAttributes(Entry v_e)
	{
		super.copyAttributes(v_e);
		AttributeTable attTable = (AttributeTable)v_e;
		vars = attTable.vars;
	}
	
	public final Iterator getVarsIterator(){	return vars.iterator();	}
	public final void addVar(Object v_o){	vars.add(v_o);	}
	public final Object getVar(int v_i){	return vars.get(v_i);	}
	public void removeVar(int v_i){	vars.remove(v_i);	}
	
	boolean Generate(SimulationManager m){return true;}
	
	/**
	 */
	public float[] GetValues()
	{
		//if(vars.isEmpty())
		//	return null;
		float[] vals = new float[vars.size()];
		Iterator it = vars.iterator();
		int i = 0;
		while(it.hasNext())
		{
			vals[i++] = ((Var)it.next()).value;
		}
		
		return vals;
	}
	
	/*** armazenados no vetor vars. Retorna null se vars estiver vazio.
	 */
	public String[] GetIds()
	{
		//if(vars.isEmpty())
		//	return null;
		String[] vals = new String[vars.size()];
		Iterator it = vars.iterator();
		int i = 0;
		while(it.hasNext())
		{
			vals[i++] = ((Var)it.next()).id;
		}
		
		return vals;
	}
	
	private void writeObject(ObjectOutputStream stream)
     throws IOException
	{
		stream.defaultWriteObject();
		
		if(hasSerialized)
			return;
			
		stream.writeInt(lastid);
		hasSerialized = true;
	}
 	private void readObject(ObjectInputStream stream)
     throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		
		if(hasSerialized)
			return;
			
		lastid = stream.readInt();
		hasSerialized = true;
	}
}