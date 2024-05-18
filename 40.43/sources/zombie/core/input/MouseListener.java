package zombie.core.input;


public interface MouseListener extends ControlledInputReciever {

	void mouseWheelMoved(int int1);

	void mouseClicked(int int1, int int2, int int3, int int4);

	void mousePressed(int int1, int int2, int int3);

	void mouseReleased(int int1, int int2, int int3);

	void mouseMoved(int int1, int int2, int int3, int int4);

	void mouseDragged(int int1, int int2, int int3, int int4);
}
