package in.vvest.compiler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.InterruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

public class Compiler {

	private Lexer lex;
	private Parser parser;

	public Compiler() {
		Map<String, Token> tokenID = new HashMap<String, Token>();
		tokenID.put("+", new Operator.Add());
		tokenID.put("-", new Operator.Subtract());
		tokenID.put("*", new Operator.Multiply());
		tokenID.put("/", new Operator.Divide());
		tokenID.put("<", new Operator.Compare(true, false));
		tokenID.put(">", new Operator.Compare(false, false));;
		tokenID.put("<=", new Operator.Compare(true, true));
		tokenID.put(">=", new Operator.Compare(false, true));
		tokenID.put("=", new Operator.Equal(false));
		tokenID.put("=/=", new Operator.Equal(true));
		tokenID.put(" and ", new Operator.AND());
		tokenID.put(" or ", new Operator.OR());
		tokenID.put(" xor ", new Operator.XOR());
		tokenID.put("->", new Operator.Store());
		tokenID.put("Disp(", new Function("Disp", 1));
		tokenID.put("not(", new Function("Not", 1));
		tokenID.put("If(", new ControlStructure.If());
		tokenID.put("While(", new ControlStructure.While());
		tokenID.put("Repeat(", new ControlStructure.Repeat());
		tokenID.put("For(", new ControlStructure.For());
		tokenID.put("End", new ControlStructure.End());
		tokenID.put(":", new Colon());
		tokenID.put(",", new Comma());
		tokenID.put("(", new Parenthesis(true));
		tokenID.put(")", new Parenthesis(false));
		tokenID.put("Pause", new Command("Pause"));
		tokenID.put("ClrHome", new Command("ClrHome"));
		lex = new Lexer(tokenID);
		parser = new Parser();
	}

	public void compile(File f) throws FileNotFoundException {
		compile(new Scanner(f));
	}

	public void compile(Scanner s) {
		String src = "";
		while (s.hasNextLine()) {
			src += ":" + s.nextLine();
		}
		s.close();
		compile(src);
	}

	public void compile(String src) {
		System.out.println(src.replace(":", "\n"));
		List<Token> tokens = lex.tokenize(src);
		Token prgm = parser.parse(tokens);
		prgm.print();
		try {
			Thread.sleep(500); // Makes sure Systen.err and System.out streams don't overlap. Unneccessary, but nice
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<String> code = new LinkedList<String>();
		code.add("#include \"ti83plus.inc\""); // Boiler plate at beginning
		code.add("#define progStart $9D95");
		code.add("#define divScratch $8000");
		code.add(".org progStart-2");
		code.add(".db $BB,$6D");
		code.add("bcall(_ClrLCDFull)");
		code.add("bcall(_HomeUp)");
		prgm.compile(code); // Actual code
		code.add("ret"); // Boiler plate at end and library inclusion
		code.add("");
		for (String statement : code) {
			System.out.println((statement.endsWith(":") || statement.startsWith(".") || statement.startsWith("#")  || statement.startsWith(";") ? "" : "\t") + statement);
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader("res/lib.z80"));
			String line;
			while ((line = reader.readLine()) != null) {
				code.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			compileWeb(code);
		} catch (IOException e) {
			System.err.println("Unable to connect to ClrHome Assembler.");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			System.err.println("Assembler response not able to be parsed");
		}
	}

	private void compileWeb(List<String> code) throws IOException, ParserConfigurationException {
		String assemblerURL = "http://clrhome.org/asm/";
		URL url = new URL(assemblerURL);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setRequestMethod("POST");
		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		PrintWriter print = new PrintWriter("out/asm.txt", "UTF-8");
		StringBuilder prgm = new StringBuilder();
		for (String statement : code) {
			String line = (statement.endsWith(":") || statement.startsWith(".") || statement.startsWith("#") ? "" : "\t") + statement;
			prgm.append(URLEncoder.encode(line + "\n", "UTF-8"));
			print.println(line);	
		}
		print.close();
		
		http.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(http.getOutputStream());
		wr.writeBytes(""
				+ "action=p&"
				+ "start=foo_z80&"
				+ "foo_z80="
				+ prgm);
		wr.flush();
		wr.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
		String response = "";
		String input;
		while ((input = in.readLine()) != null)
			response += input;
		in.close();
		System.out.println();
		System.out.println("URL: " + http.getURL());
		System.out.println("Response Code: " + http.getResponseCode());
		System.out.println("Content-Type: " + http.getContentType());
		
		try {
			Thread.sleep(500); // Unnecessary. Makes sure output Streams don't mix in console
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Pattern errorPat = Pattern.compile("<p class=\"en err\">(.[^<]*)<\\/p>");
		Matcher errorMat = errorPat.matcher(response);
		boolean foundErrors = false;
		while (errorMat.find()) {
			System.err.println(errorMat.group(1));
			foundErrors = true;
		}
		System.out.println();
		if (!foundErrors) {
			Pattern outputPat = Pattern.compile("<a href=\"(.*)\">download<\\/a>");
			Matcher outputMat = outputPat.matcher(response);
			if (outputMat.find()) {
				try {
					downloadPrgm(assemblerURL + outputMat.group(1));		
				} catch (IOException e) {
					System.err.println("Unable to download and write compiler program");
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void downloadPrgm(String download) throws IOException {
		System.out.println("Downloading from " + download);
		URL url = new URL(download);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("out/vvestin.8xp");
		} catch (FileNotFoundException e) {
			System.out.println("Creating out/vvestin.8xp");
			File f = new File("out/vvestin.8xp");
			f.createNewFile();
			fos = new FileOutputStream("/out/vvestin.8xp");
		}
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}


	public static void main(String[] args) throws FileNotFoundException {
		Compiler c = new Compiler();
		c.compile(new File("res/Theta0.txt"));
	}

}
