package zombie.core;

import java.io.PrintStream;


public class ProxyPrintStream extends PrintStream {
	private PrintStream fileStream = null;
	private PrintStream systemStream = null;

	public ProxyPrintStream(PrintStream printStream, PrintStream printStream2) {
		super(printStream);
		this.systemStream = printStream;
		this.fileStream = printStream2;
	}

	public void print(String string) {
		this.systemStream.print(string);
		this.fileStream.print(string);
		this.fileStream.flush();
	}

	public void println(String string) {
		this.systemStream.println(string);
		this.fileStream.println(string);
		this.fileStream.flush();
	}

	public void println(Object object) {
		this.systemStream.println(object);
		this.fileStream.println(object);
		this.fileStream.flush();
	}

	public void write(byte[] byteArray, int int1, int int2) {
		this.systemStream.write(byteArray, int1, int2);
		this.fileStream.write(byteArray, int1, int2);
		this.fileStream.flush();
	}
}
