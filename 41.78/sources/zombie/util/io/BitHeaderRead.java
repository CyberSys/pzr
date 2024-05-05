package zombie.util.io;


public interface BitHeaderRead {

	int getStartPosition();

	void read();

	boolean hasFlags(int int1);

	boolean hasFlags(long long1);

	boolean equals(int int1);

	boolean equals(long long1);

	int getLen();

	void release();
}
