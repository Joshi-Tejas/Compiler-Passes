import java.io.*;
import java.util.*;

class pass1
{
	static class OpcodeTable
	{
		String InstrName = "";
		String type = "";
		String opcode = "";
		int InstrLength = 0;

		OpcodeTable(String InstrName,String type,String opcode,int InstrLength)
		{
			this.InstrName = InstrName;
			this.type = type;
			this.opcode = opcode;
			this.InstrLength = InstrLength;
		}
	}

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

	static OpcodeTable searchInstr(List<OpcodeTable> table,String token)
	{
		for(OpcodeTable tab : table)
		{
			if(tab.InstrName.equalsIgnoreCase(token))
				return tab;
		}
		return null;
	}

	static SymbolTable searchSymbol(List<SymbolTable> table,String symbol)
	{
		for(SymbolTable tab : table)
		{
			if(tab.Symbol.equalsIgnoreCase(symbol))
				return tab;
		}
		return null;
	}

	public static void main(String[] args) throws Exception
	{
		String str = "";
		int LocationCounter = 0;
		OpcodeTable optab;
		LiteralTable littab;
		SymbolTable symtab;
		List<OpcodeTable> MOT = new ArrayList<OpcodeTable>();
		List<OpcodeTable> POT = new ArrayList<OpcodeTable>();
		List<LiteralTable> LIT = new ArrayList<LiteralTable>();
		List<SymbolTable> SYT = new ArrayList<SymbolTable>();
		int LITcount = 0, SYTcount = 0, pooltab = 0;
		List<String> inputData = new ArrayList<String>();

		File f1 = new File("opcode-table.txt");
		BufferedReader br = new BufferedReader(new FileReader(f1));
		while((str=br.readLine())!=null)
		{
			StringTokenizer st = new StringTokenizer(str," \t");
			while(st.hasMoreTokens())
			{
				optab = new OpcodeTable(st.nextToken(),st.nextToken(),st.nextToken(),Integer.parseInt(st.nextToken()));
				MOT.add(optab);
			}
		}
		f1 = new File("pseudo-opcode.txt");
		br = new BufferedReader(new FileReader(f1));
		while((str=br.readLine())!=null)
		{
			StringTokenizer st = new StringTokenizer(str," \t");
			while(st.hasMoreTokens())
			{
				optab = new OpcodeTable(st.nextToken(),st.nextToken(),st.nextToken(),Integer.parseInt(st.nextToken()));
				POT.add(optab);
			}
		}

		f1 = new File("input.asm");
		String currToken = "";
		int symStart = 0;
		br = new BufferedReader(new FileReader(f1));
		FileOutputStream fout = new FileOutputStream(new File("dump-output.txt"));
		FileOutputStream SymbolOut = new FileOutputStream(new File("symbol-table.txt"));
		FileOutputStream LiteralOut = new FileOutputStream(new File("literal-table.txt"));
		FileOutputStream PoolOut = new FileOutputStream(new File("pool-table.txt"));
		while((str=br.readLine())!=null)
		{
			inputData.add(str);
			System.out.println(str);
		}

		for(String currLine : inputData)
		{
			int symFlag = 0;
			String OutputString;
			StringTokenizer st = new StringTokenizer(currLine,"\t ");
			if(st.countTokens()==0)
				continue;
			if(st.countTokens()==3)
				symFlag = 1;
			
			st = new StringTokenizer(currLine,"\t ,");
			while(st.hasMoreTokens())
			{
				OutputString = "";
				currToken = st.nextToken();
				if(currToken.equalsIgnoreCase("START"))
				{
					System.out.println("start");
					currToken = st.nextToken();
					OutputString += "\t(C," + currToken + ")";
					LocationCounter = Integer.parseInt(currToken);
					LocationCounter--;
					fout.write(OutputString.getBytes(),0,OutputString.length());
				}
				else if((optab=searchInstr(MOT,currToken))!=null)
				{
					System.out.println("mnemonic");
					OutputString += Integer.toString(LocationCounter) + "\t(" + optab.type + "," + optab.opcode + ")";
					if(optab.type.equalsIgnoreCase("DL"))
					{
						String numSize = st.nextToken();
						OutputString += "\t(C," + numSize + ")";
						LocationCounter += Integer.parseInt(numSize);
						LocationCounter--;
					}
					fout.write(OutputString.getBytes(),0,OutputString.length());
				}
				else if((optab=searchInstr(POT,currToken))!=null)
				{
					System.out.println("pseudo-opcode");
					OutputString += "\t(" + optab.type + "," + optab.opcode + ")";
					fout.write(OutputString.getBytes(),0,OutputString.length());
				}
				else if(currToken.startsWith("="))
				{
					System.out.println("literal");
					littab = new LiteralTable(LITcount,currToken,"");
					OutputString += "\t(L," + Integer.toString(littab.Index) + ")";
					LIT.add(littab);
					fout.write(OutputString.getBytes(),0,OutputString.length());
					LITcount++;	
				}
				else if(symFlag == 1)
				{
					System.out.println("left Symbol");
					if((symtab=searchSymbol(SYT,currToken))!=null)
					{
						int indexOfRecord = SYT.indexOf(symtab);
						SYT.set(indexOfRecord,new SymbolTable(symtab.Index,symtab.Symbol,Integer.toString(LocationCounter)));
						System.out.println(symtab.Symbol);
					}
					else
					{
						symtab = new SymbolTable(SYTcount,currToken,Integer.toString(LocationCounter));
						SYT.add(symtab);
						System.out.println(symtab.Symbol);
						SYTcount++;
					}
					symFlag = 0;
				}
				else if(currToken.equalsIgnoreCase("LTORG") || currToken.equalsIgnoreCase("END"))
				{
					for(LiteralTable lit : LIT)
					{
						if(lit.Address=="")
						{
							lit.Address = Integer.toString(LocationCounter);
							OutputString = Integer.toString(LocationCounter) + "\t(DL,02)";
							fout.write(OutputString.getBytes(),0,OutputString.length());
							StringTokenizer tmp = new StringTokenizer(lit.Literal,"=\"");
							OutputString = "\t(C," + tmp.nextToken() + ")";
							fout.write(OutputString.getBytes(),0,OutputString.length());
							LocationCounter++;
							pooltab++;
						}
					}
					LocationCounter--;
					str = Integer.toString(pooltab)+"\n";
					PoolOut.write(str.getBytes(),0,str.length());
				}
				else if((optab=searchInstr(MOT,currToken))==null && (optab=searchInstr(POT,currToken))==null && !currToken.startsWith("="))
				{
					System.out.println("right Symbol");
					symtab=searchSymbol(SYT,currToken);
					if(symtab!=null && symtab.Address!="")
						OutputString += "\t(C," + symtab.Address + ")";
					else if(symtab!=null && symtab.Address=="")
					{
						OutputString += "\t(S," + Integer.toString(symtab.Index) + ")";
					}
					else
					{
						symtab = new SymbolTable(SYTcount,currToken,"");
						SYT.add(symtab);
						OutputString += "\t(S," + Integer.toString(symtab.Index) + ")";
						SYTcount++;
					}
					fout.write(OutputString.getBytes(),0,OutputString.length());
				}
			}
			LocationCounter++;
			OutputString = "\n";
			fout.write(OutputString.getBytes(),0,OutputString.length());
		}

		for(LiteralTable lit : LIT)
		{
			str = Integer.toString(lit.Index)+"\t"+lit.Literal+"\t"+lit.Address+"\n";
			LiteralOut.write(str.getBytes(),0,str.length());
		}
		for(SymbolTable sym : SYT)
		{
			str = Integer.toString(sym.Index)+"\t"+sym.Symbol+"\t"+sym.Address+"\n";
			SymbolOut.write(str.getBytes(),0,str.length());
		}
	}
}