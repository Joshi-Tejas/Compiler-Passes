import java.io.*;
import java.util.*;

public class pass1
{
	public static void main(String[] args) throws Exception
	{
		String str;
		String currToken = new String();
		String mname = new String();
		File f = new File("Input.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		FileOutputStream fout = new FileOutputStream(new File("dump-table-output.txt"));

		List<String> inputData = new ArrayList<String>();
		List<MacroDefTable> MDT = new ArrayList<MacroDefTable>();
		List<MacroNameTable> MNT = new ArrayList<MacroNameTable>();
		List<ArgListArray> ALA = new ArrayList<ArgListArray>();
		int MDTcount=0,MNTcount=0,ALAcount=0;
		boolean nameflag = false,endflag = true;		

		while((str=br.readLine())!=null)
		{
			inputData.add(str);
			System.out.println(str);
		}
		for(String currLine : inputData)
		{
			StringTokenizer st = new StringTokenizer(currLine," ,");

			if(currLine.equals("MACRO"))
			{
				nameflag = true;
				endflag = false;
				continue;
			}
			while(st.hasMoreTokens())
			{
				currToken = st.nextToken();
				if(nameflag == true)
				{
					mname = currToken;	
					MNT.add(new MacroNameTable(++MNTcount,mname,MDTcount+1));
					nameflag = false;
				}
				if(currToken.startsWith("&") && ArgListArray.getRecord(ALA,currToken,mname)==null)
				{
					ALA.add(new ArgListArray(++ALAcount,currToken,"-",mname));
				}
				if(currToken.equals("MEND"))
				{
					MDT.add(new MacroDefTable(++MDTcount,currLine));
					endflag = true;
				}	
			}
			if(endflag == false)
			{
				if(!currLine.startsWith(mname))
				{
					StringTokenizer st1 = new StringTokenizer(currLine," ,");
					while(st1.hasMoreTokens())
					{
						String tempToken = st1.nextToken();
						String ind;
						if(tempToken.startsWith("&"))
						{
							ArgListArray ALAtemp = ArgListArray.getRecord(ALA,tempToken,mname);
							ind = String.valueOf(ALAtemp.index);
							currLine = currLine.replace(tempToken,ind);
						}
					}
				}
				MDT.add(new MacroDefTable(++MDTcount,currLine));
			}
		}
		
		System.out.println("\n\tMNT\n-------------------------------\nINDEX\tNAME\tMDT INDEX\n-------------------------------");
		String str1 = "MNT\nINDEX\tNAME\tMDT INDEX";
		fout.write(str1.getBytes(),0,str1.length());
		for(MacroNameTable a:MNT)
		{
			System.out.println(a.index+"\t"+a.macroName+"\t"+a.MDTindex);
			str1 = "\n"+a.index+"\t"+a.macroName+"\t"+a.MDTindex;
			fout.write(str1.getBytes(),0,str1.length());
		}

		System.out.println("\n\tALA\n-------------------------------\nINDEX\tFORMAL\tACTUAL\tMACRO\n-------------------------------");
		str1 = "\nALA\nINDEX\tFORMAL\tACTUAL\tMACRO";
		fout.write(str1.getBytes(),0,str1.length());
		for(ArgListArray a :ALA)
		{
			System.out.println(a.index+"\t"+a.formal+"\t"+a.actual+"\t"+a.macro);
			str1 = "\n"+a.index+"\t"+a.formal+"\t"+a.actual+"\t"+a.macro;
			fout.write(str1.getBytes(),0,str1.length());
		}
		System.out.println("\n\tMDT\n-------------------------------\nINDEX\tINSTRUCTION\n-------------------------------");
		str1 = "\nMDT\nINDEX\tINSTRUCTION";
		fout.write(str1.getBytes(),0,str1.length());
		for(MacroDefTable a:MDT)
		{
			System.out.println(a.index+"\t"+a.instruct);
			str1 = "\n"+a.index+"\t"+a.instruct;
			fout.write(str1.getBytes(),0,str1.length());
		}

		br.close();
	}

	public static class MacroDefTable
	{
		public int index;
		public String instruct;

		public MacroDefTable()
		{
			index = 0;
			instruct = null;
		}

		public MacroDefTable(int index,String instruct)
		{
			this.index = index;
			this.instruct = instruct;
		}

		public static MacroDefTable getRecord(List<MacroDefTable> MDT,String token)
		{
			for(MacroDefTable rec : MDT)
			{
				if(rec.instruct.equals(token))
				{
					return rec;
				}
			}
			return null;
		}
	}

	public static class MacroNameTable
	{
		public int index;
		public String macroName;
		public int MDTindex;

		public MacroNameTable(int index,String macroName,int MDTindex)
		{
			this.index = index;
			this.macroName = macroName;
			this.MDTindex = MDTindex;
		}

		public static MacroNameTable getRecord(List<MacroNameTable> MNT,String token)
		{
			for(MacroNameTable rec : MNT)
			{
				if(rec.macroName.equals(token))
				{
					return rec;
				}
			}
			return null;
		}
	}

	public static class ArgListArray
	{
		public int index;
		public String formal;
		public String actual;
		public String macro;

		public ArgListArray(int index,String formal,String actual,String macro)
		{
			this.index = index;
			this.formal = formal;
			this.actual = actual;
			this.macro = macro;
		}

		public static ArgListArray getRecord(List<ArgListArray> ALA,String token,String mname)
		{
			for(ArgListArray rec : ALA)
			{
				if(rec.formal.equals(token) && rec.macro.equals(mname))
				{
					return rec;
				}
			}
			return null;
		}
	}
}
