package se.krka.kahlua.vm;


public interface Platform {

	double pow(double double1, double double2);

	KahluaTable newTable();

	KahluaTable newEnvironment();

	void setupEnvironment(KahluaTable kahluaTable);
}
