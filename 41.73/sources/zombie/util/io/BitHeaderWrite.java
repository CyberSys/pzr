package zombie.util.io;


public interface BitHeaderWrite {

	int getStartPosition();

	void create();

	void write();

	void addFlags(int int1);

	void addFlags(long long1);

	boolean hasFlags(int int1);

	boolean hasFlags(long long1);

	boolean equals(int int1);

	boolean equals(long long1);

	int getLen();

	void release();
}
