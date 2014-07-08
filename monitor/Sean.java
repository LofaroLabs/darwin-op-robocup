import java.net.*;
import java.io.*;
import java.util.*;


/**
	When you select a ROBOT,
	we display:
		Ball location according to that robot
		Battery power for that robot
		His status
		His declared [1, 2, 3]
		His id
*/



class Node
	{
// a Node has a NAME
	public String name;  // this might be a number
// and it has a VALUE, one of:
	public String valueS = null;
	public double valueD = 0;
	public ArrayList<Node> nodes = null;
// A node has an END position just beyond it
	public int end = -1;
	
	public Node get(String name)
		{
		for(int i = 0; i < nodes.size(); i++)
			{
			if (nodes.get(i).name.equals(name))
				return nodes.get(i);
			}
		return null;
		}
	
	String spaces(int tab)
		{
		String s = "";
		for(int i = 0; i < tab; i++)
			s += " ";
		return s;
		}
	
	public String toString() { return out(0); }
	public String out(int tab)
		{
		String s = spaces(tab) + name + " = ";
		if (nodes != null)
			{
			s = s + "{\n";
			for(int i = 0; i < nodes.size(); i++)
				s = s + nodes.get(i).out(tab + 4);
			s = s + spaces(tab) + "}\n";
			}
		else if (valueS != null)
			{
			s += "\"" + valueS + "\"\n";
			}
		else
			{
			s += valueD + "\n";
			}
		return s;
		}
	}

public class Sean
	{
	public static final int PORT = 11111;
	public static final int MAX_PACKET_LENGTH = 64 * 1024;
	
	public static void main(String[] args) throws Exception
		{
		DatagramSocket sock = new DatagramSocket(PORT);  // automatically bound to the wildcard address
		DatagramPacket pack = new DatagramPacket(new byte[MAX_PACKET_LENGTH], MAX_PACKET_LENGTH);
		while(true)
			{
			sock.receive(pack);
			System.err.println("Got " + pack.getLength() + " from " + pack.getSocketAddress());
			System.err.println(new String(pack.getData()));
			Node node = getNode(new String(pack.getData()), 0);
			System.err.println(node);
			}
		}
	
	// Finds the position of the given character in the string starting at pos
	public static int find(String str, int pos, char c)
		{
		for( ; pos < str.length() ; pos++)
			{
			if (str.charAt(pos) == c) return pos;
			}
		if (pos == str.length())
			throw new RuntimeException("Bad Packet?");
		return pos;
		}
	
	// reads a string from the given string a pos
	public static String getString(String str, int pos)
		{
		// munch "
		pos++;
		int pos2 = find(str, pos, '"');
		return str.substring(pos, pos2);
		}
	
	// returns the new position after reading a string
	public static int jumpString(String str, int pos)
		{
		// munch "
		pos++;
		pos = find(str, pos, '"');
		// munch "
		pos++;
		// munch ,
		pos++;
		return pos;
		}
	
	// reads a number from the given string a pos
	public static double getNumber(String str, int pos)
		{
		int pos2 = find(str, pos, ',');
		return Double.parseDouble(str.substring(pos, pos2));
		}
	
	// returns the new position after reading a string
	public static int jumpNumber(String str, int pos)
		{
		pos = find(str, pos, ',');
		// munch ,
		pos++;
		return pos;
		}

	// reads a name from the given string a pos
	public static String getName(String str, int pos)
		{
		// munch [
		pos++;
		// munch "
		pos++;
		int pos2 = find(str, pos, '"');
		return str.substring(pos, pos2);
		}
	
	// returns the new position after reading a string
	public static int jumpName(String str, int pos)
		{
		// munch [
		pos++;
		// munch "
		pos++;
		pos = find(str, pos, '"');
		// munch "
		pos++;
		// munch ]
		pos++;
		// munch =
		pos++;
		return pos;
		}

	public static Node getNode(String str, int pos)
		{
		int nth = 1;
		
		Node node = new Node();
		node.nodes = new ArrayList<Node>();
		
		// munch {
		pos++;
		
		while(str.charAt(pos) != '}')
			{
			Node node2;
			if (str.charAt(pos) == '[')  // a name
				{
				String label = getName(str, pos);
				pos = jumpName(str, pos);
				if (str.charAt(pos) == '{')
					{
					node2 = getNode(str, pos);
					node2.name = label;
					pos = node2.end;
					}
				else if (str.charAt(pos) == '"')
					{
					String val = getString(str,pos);
					node2 = new Node();
					node2.valueS = val;
					node2.name = label;
					pos = jumpString(str,pos);
					node2.end = pos;
					}
				else 
					{
					double val = getNumber(str,pos);
					node2 = new Node();
					node2.valueD = val;
					node2.name = label;
					pos = jumpNumber(str,pos);
					node2.end = pos;
					}
				}
			else if (str.charAt(pos) == '"') // a string
				{
				String val = getString(str,pos);
				node2 = new Node();
				node2.valueS = val;
				node2.name = "" + nth;
				pos = jumpString(str,pos);
				node2.end = pos;
				nth++;
				}
			else						// a number
				{
				double val = getNumber(str,pos);
				node2 = new Node();
				node2.valueD = val;
				node2.name = "" + nth;
				pos = jumpNumber(str,pos);
				node2.end = pos;
				nth++;
				}
			
			node.nodes.add(node2);
			}

		// munch }
		pos++;
		// munch ,
		pos++;

		node.end = pos;
		return node;
		}
	}