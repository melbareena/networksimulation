package dataStructure;

public class Triple<A,B,C>
{
	A a;
	B b;
	C c;
	
	public Triple(A a1, B b1, C c1)
	{
		a = a1;
		b = b1;
		c = c1;
	}
	
	public A getA()
	{
		return a;
	}
	public B getB()
	{
		return b;
	}
	public C getC()
	{
		return c;
	}
	
}
