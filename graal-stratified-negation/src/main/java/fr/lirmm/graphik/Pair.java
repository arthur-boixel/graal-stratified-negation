package fr.lirmm.graphik;

public class Pair<L,R> {
	
	private L first;
	private R last;
	
	public Pair(L first , R last)
	{
		this.first = first;
		this.last = last;
	}
	
	public void setFirst(L f)
	{
		first = f;
	}
	
	public void setLast(R l)
	{
		last = l;
	}
	
	public L getFirst()
	{
		return first;
	}
	
	public R getLast()
	{
		return last;
	}
	
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append("<");
		s.append(first);
		s.append(",");
		s.append(last);
		s.append(">");
		
		return s.toString();
	}
}
