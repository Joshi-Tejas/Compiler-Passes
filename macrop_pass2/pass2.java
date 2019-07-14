import java.io.*;
import java.util.*;

public class pass2 implements Serializable
{
	public static void expdef(String getMname,List<MacroDefTable> MDT,String[] ActualList,List<ArgListArray> ALA,int index,FileOutputStream fout) throws IOException
	{
		int j;
		for(int i=index-1;i<MDT.size();i++)
		{
			j = 1;
			MacroDefTable md = MDT.get(i);
			if(md.instruct.startsWith(getMname))
				continue;
			if(md.instruct.equalsIgnoreCase("MEND"))
				break;
			String instr = md.instruct;
			for(ArgListArray al : ALA)
			{
				if(al.macro.equalsIgnoreCase(getMname) && instr.contains(String.valueOf(j)))
				{
					instr = md.instruct.replace(String.valueOf(j),al.actual);
				}
				j++;
			}
			System.out.println(instr);
			fout.write(instr.getBytes(),0,instr.length());
			fout.write("\n".getBytes());
		}
	}

	public static int searchMacro(List<MacroNameTable> MNT,String MacName)
	{
		for(int i=0;i<MNT.size();i++)
		{
			MacroNameTable m1 = MNT.get(i);
			if(MacName.equalsIgnoreCase(m1.macroName))
				return m1.MDTindex;
		}
		return 0;
	}

	public static void main(String[] args) throws Exception
	{
		String str;
		String currToken = new String();
		String mname = new String();
		File f = new File("dump-table-output.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		FileOutputStream fout = new FileOutputStream(new File("dump-exp-output.txt"));

		List<String> inputTables = new ArrayList<String>();
		List<MacroDefTable> MDT = new ArrayList<MacroDefTable>();
		List<MacroNameTable> MNT = new ArrayList<MacroNameTable>();
		List<ArgListArray> ALA = new ArrayList<ArgListArray>();
		int MDTcount=0,MNTcount=0,ALAcount=0;
		boolean MNTflag = false,MDTflag = false, ALAflag = false;
		boolean MacroDefFlag = false;
		int getMDTind;

		while((str=br.readLine())!=null)
		{
			if(str.equalsIgnoreCase("MNT"))
			{
				MNTflag = true;
				MDTflag = false;
				ALAflag = false;
				str=br.readLine();
				continue;
			}
			if(str.equalsIgnoreCase("ALA"))
			{
				MNTflag = false;
				MDTflag = false;
				ALAflag = true;
				str=br.readLine();
				continue;
			}
			if(str.equalsIgnoreCase("MDT"))
			{
				MNTflag = false;
				MDTflag = true;
				ALAflag = false;
				str=br.readLine();
				continue;
			}
			StringTokenizer st = new StringTokenizer(str,"\t");
			if(MNTflag == true)
			{
				MNT.add(new MacroNameTable(Integer.parseInt(st.nextToken()),st.nextToken(),Integer.parseInt(st.nextToken())));
			}
			if(ALAflag == true)
			{
				ALA.add(new ArgListArray(Integer.parseInt(st.nextToken()),st.nextToken(),st.nextToken(),st.nextToken()));
			}
			if(MDTflag == true)
			{
				MDT.add(new MacroDefTable(Integer.parseInt(st.nextToken()),st.nextToken()));
			}
		}

		f = new File("Input.txt");
		br = new BufferedReader(new FileReader(f));
		String getMname;
		int j = 1;

		while((str=br.readLine())!=null)
		{
			if(str.equalsIgnoreCase("MACRO"))
			{
				MacroDefFlag = true;
			}
			if(MacroDefFlag == true)
			{
				if(str.equalsIgnoreCase("MEND"))
					MacroDefFlag = false;
				continue;
			}
			StringTokenizer st = new StringTokenizer(str," ");
			getMname = st.nextToken();
			if((getMDTind=searchMacro(MNT,getMname))!=0)
			{
				String[] ActualList = st.nextToken().split(",");
				for(String a : ActualList)
				{
					ArgListArray temp = ArgListArray.getRecord(ALA,j,getMname);
					int indexOfRecord = ALA.indexOf(temp);
					ALA.set(indexOfRecord,new ArgListArray(temp.index,temp.formal,a,temp.macro));
					j++;
				}
				expdef(getMname,MDT,ActualList,ALA,getMDTind,fout);
			}
			else
			{
				System.out.println(str);
				fout.write(str.getBytes(),0,str.length());
				fout.write("\n".getBytes());
			}
		}

		System.out.println("\n\tMNT\n-------------------------------\nINDEX\tNAME\tMDT INDEX\n-------------------------------");
		for(MacroNameTable a:MNT)
		{
			System.out.println(a.index+"\t"+a.macroName+"\t"+a.MDTindex);
		}

		System.out.println("\n\tALA\n-------------------------------\nINDEX\tFORMAL\tACTUAL\tMACRO\n-------------------------------");
		for(ArgListArray a :ALA)
		{
			System.out.println(a.index+"\t"+a.formal+"\t"+a.actual+"\t"+a.macro);
		}
		System.out.println("\n\tMDT\n-------------------------------\nINDEX\tINSTRUCTION\n-------------------------------");
		for(MacroDefTable a:MDT)
		{
			System.out.println(a.index+"\t"+a.instruct);
		}

		br.close();
	}

	public static class OutputWriter implements Serializable
	{
		public List<MacroDefTable> MDT;
		public List<MacroNameTable> MNT;
		public List<ArgListArray> ALA;
		public OutputWriter(List<MacroDefTable> MDT,List<MacroNameTable> MNT,List<ArgListArray> ALA) 
		{
			this.ALA=ALA;
			this.MNT=MNT;
			this.MDT=MDT;
		}
		public void writeOutput() throws IOException
		{
			ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream("output.txt"));
			oout.writeObject(this);
			oout.close();
		}
	}

	public static class MacroDefTable implements Serializable
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

	public static class MacroNameTable implements Serializable
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

	public static class ArgListArray implements Serializable
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

		public static ArgListArray getRecord(List<ArgListArray> ALA,int ind,String mname)
		{
			for(ArgListArray rec : ALA)
			{
				if(rec.index == ind && rec.macro.equals(mname))
				{
					return rec;
				}
			}
			return null;
		}
	}
}
