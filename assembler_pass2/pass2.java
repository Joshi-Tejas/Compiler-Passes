import java.io.*;
import java.util.*;

class pass2
{
	static class LiteralTable
	{
		int Index = 0;
		String Literal = "";
		String Address = "";

		LiteralTable(int Index,String Literal,String Address)
		{
			this.Index = Index;
			this.Literal = Literal;
			this.Address = Address;
		}
	}

	static class SymbolTable
	{
		int Index = 0;
		String Symbol = "";
		String Address = "";

		SymbolTable(int Index,String Symbol,String Address)
		{
			this.Index = Index;
			this.Symbol = Symbol;
			this.Address = Address;
		}
	}

	static SymbolTable searchSymbol(List<SymbolTable> table,String ind)
	{
		int ind1 = Integer.parseInt(ind);
		for(SymbolTable tab : table)
		{
			if(tab.Index == ind1)
				return tab;
		}
		return null;
	}

	static LiteralTable searchLiteral(List<LiteralTable> table,String ind)
	{
		int ind1 = Integer.parseInt(ind);
		for(LiteralTable tab : table)
		{
			if(tab.Index == ind1)
				return tab;
		}
		return null;
	}

	static String[] tokenizeString(String line,String arg)
	{
		StringTokenizer st = new StringTokenizer(line,arg);
		String arr[] = new String[st.countTokens()];
		for(int i=0;i<arr.length;i++)
			arr[i] = st.nextToken();
		return arr;
	}

	public static void main(String[] args) throws Exception
	{
		LiteralTable littab;
		SymbolTable symtab;
		String str = "";
		List<LiteralTable> LIT = new ArrayList<LiteralTable>();
		List<SymbolTable> SYT = new ArrayList<SymbolTable>();
		int LITcount = 0, SYTcount = 0, pooltab = 0;
		List<String> inputData = new ArrayList<String>();
		String OutputString = "";

		File f1 = new File("literal-table.txt");
		BufferedReader br = new BufferedReader(new FileReader(f1));
		while((str=br.readLine())!=null)
		{
			StringTokenizer st = new StringTokenizer(str," \t");
			while(st.hasMoreTokens())
			{
				littab = new LiteralTable(Integer.parseInt(st.nextToken()),st.nextToken(),st.nextToken());
				LIT.add(littab);
			}
		}
		f1 = new File("symbol-table.txt");
		br = new BufferedReader(new FileReader(f1));
		while((str=br.readLine())!=null)
		{
			StringTokenizer st = new StringTokenizer(str," \t");
			while(st.hasMoreTokens())
			{
				symtab = new SymbolTable(Integer.parseInt(st.nextToken()),st.nextToken(),st.nextToken());
				SYT.add(symtab);
			}
		}

		f1 = new File("dump-output.txt");
		br = new BufferedReader(new FileReader(f1));
		while((str=br.readLine())!=null)
		{
			inputData.add(str);
			System.out.println(str);
		}

		FileOutputStream fout = new FileOutputStream(new File("final-output.txt"));
		String currToken = "";
		for(String currLine : inputData)
		{
			int flag = 0;
			OutputString = "";
			currLine=currLine.replaceAll("(\\()","");
			currLine=currLine.replaceAll("(\\))","");
			String st[] = tokenizeString(currLine," \t");
			if(st.length==1)
				continue;

			st = tokenizeString(currLine," \t,");
			for(int i=0;i<st.length;i++)
			{
				if(i==0)
				{
					OutputString += st[i];
					continue;
				}
				if(st[i].equalsIgnoreCase("IS"))
				{
					OutputString += "\t" + st[i+1];
					i++;
				}
				if(st[i].equalsIgnoreCase("RG"))
				{
					OutputString += "\t" + st[i+1];
					i++;
				}
				if(st[i].equalsIgnoreCase("DL") && st[i+1].equals("02"))
				{
					OutputString += "\t00";
					flag = 1;
					i++;
				}
				if(st[i].equalsIgnoreCase("C") && flag == 1)
				{
					OutputString += "\t" + st[i+1];
					i++;
				}
				if(st[i].equalsIgnoreCase("S"))
				{
					if((symtab=searchSymbol(SYT,st[i+1]))!=null)
					{
						OutputString += "\t" + symtab.Address;
						i++;
					}
					else
					{
						System.out.println("SymbolNotFoundException");
						System.exit(0);
					}
				}
				if(st[i].equalsIgnoreCase("L"))
				{
					if((littab=searchLiteral(LIT,st[i+1]))!=null)
					{
						OutputString += "\t" + littab.Address;
						i++;
					}
					else
					{
						System.out.println("LiteralNotFoundException");
						System.exit(0);
					}
				}
			}
			OutputString += "\n";
			fout.write(OutputString.getBytes(),0,OutputString.length());
		}
	}
}