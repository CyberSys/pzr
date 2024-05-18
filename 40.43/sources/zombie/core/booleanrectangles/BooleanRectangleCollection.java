package zombie.core.booleanrectangles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.lwjgl.util.Rectangle;


public class BooleanRectangleCollection extends ArrayList {
	static boolean[][] donemap = new boolean[400][400];
	private static BooleanRectangleCollection.Point intersection = new BooleanRectangleCollection.Point();
	static int retWidth = 0;
	static int retHeight = 0;

	public void doIt(ArrayList arrayList, Rectangle rectangle) {
		ArrayList arrayList2 = new ArrayList();
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			Rectangle rectangle2 = (Rectangle)iterator.next();
			ArrayList arrayList3 = this.doIt(rectangle2, rectangle);
			arrayList2.addAll(arrayList3);
		}

		this.clear();
		this.addAll(arrayList2);
		this.optimize();
	}

	public void cutRectangle(Rectangle rectangle) {
		ArrayList arrayList = new ArrayList();
		arrayList.addAll(this);
		this.doIt(arrayList, rectangle);
	}

	public ArrayList doIt(Rectangle rectangle, Rectangle rectangle2) {
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		ArrayList arrayList3 = new ArrayList();
		ArrayList arrayList4 = new ArrayList();
		Rectangle rectangle3 = rectangle;
		Rectangle rectangle4 = rectangle2;
		ArrayList arrayList5 = new ArrayList();
		ArrayList arrayList6 = new ArrayList();
		arrayList5.add(new BooleanRectangleCollection.Line(new BooleanRectangleCollection.Point(rectangle.getX(), rectangle.getY()), new BooleanRectangleCollection.Point(rectangle.getX() + rectangle.getWidth(), rectangle.getY())));
		arrayList5.add(new BooleanRectangleCollection.Line(new BooleanRectangleCollection.Point(rectangle.getX() + rectangle.getWidth(), rectangle.getY()), new BooleanRectangleCollection.Point(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight())));
		arrayList5.add(new BooleanRectangleCollection.Line(new BooleanRectangleCollection.Point(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight()), new BooleanRectangleCollection.Point(rectangle.getX(), rectangle.getY() + rectangle.getHeight())));
		arrayList5.add(new BooleanRectangleCollection.Line(new BooleanRectangleCollection.Point(rectangle.getX(), rectangle.getY() + rectangle.getHeight()), new BooleanRectangleCollection.Point(rectangle.getX(), rectangle.getY())));
		arrayList6.add(new BooleanRectangleCollection.Line(new BooleanRectangleCollection.Point(rectangle2.getX(), rectangle2.getY()), new BooleanRectangleCollection.Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY())));
		arrayList6.add(new BooleanRectangleCollection.Line(new BooleanRectangleCollection.Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY()), new BooleanRectangleCollection.Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY() + rectangle2.getHeight())));
		arrayList6.add(new BooleanRectangleCollection.Line(new BooleanRectangleCollection.Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY() + rectangle2.getHeight()), new BooleanRectangleCollection.Point(rectangle2.getX(), rectangle2.getY() + rectangle2.getHeight())));
		arrayList6.add(new BooleanRectangleCollection.Line(new BooleanRectangleCollection.Point(rectangle2.getX(), rectangle2.getY() + rectangle2.getHeight()), new BooleanRectangleCollection.Point(rectangle2.getX(), rectangle2.getY())));
		int int1;
		int int2;
		for (int1 = 0; int1 < arrayList5.size(); ++int1) {
			for (int2 = 0; int2 < arrayList6.size(); ++int2) {
				if (this.IntesectsLine((BooleanRectangleCollection.Line)arrayList5.get(int1), (BooleanRectangleCollection.Line)arrayList6.get(int2)) != 0 && this.IsPointInRect(intersection.X, intersection.Y, rectangle3)) {
					arrayList2.add(new BooleanRectangleCollection.Point(intersection.X, intersection.Y));
				}
			}
		}

		if (this.IsPointInRect(rectangle2.getX(), rectangle2.getY(), rectangle3)) {
			arrayList2.add(new BooleanRectangleCollection.Point(rectangle2.getX(), rectangle2.getY()));
		}

		if (this.IsPointInRect(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY(), rectangle3)) {
			arrayList2.add(new BooleanRectangleCollection.Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY()));
		}

		if (this.IsPointInRect(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY() + rectangle2.getHeight(), rectangle3)) {
			arrayList2.add(new BooleanRectangleCollection.Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY() + rectangle2.getHeight()));
		}

		if (this.IsPointInRect(rectangle2.getX(), rectangle2.getY() + rectangle2.getHeight(), rectangle3)) {
			arrayList2.add(new BooleanRectangleCollection.Point(rectangle2.getX(), rectangle2.getY() + rectangle2.getHeight()));
		}

		arrayList2.add(new BooleanRectangleCollection.Point(rectangle3.getX(), rectangle3.getY()));
		arrayList2.add(new BooleanRectangleCollection.Point(rectangle3.getX() + rectangle3.getWidth(), rectangle3.getY()));
		arrayList2.add(new BooleanRectangleCollection.Point(rectangle3.getX() + rectangle3.getWidth(), rectangle3.getY() + rectangle3.getHeight()));
		arrayList2.add(new BooleanRectangleCollection.Point(rectangle3.getX(), rectangle3.getY() + rectangle3.getHeight()));
		Collections.sort(arrayList2, new Comparator(){
			
			public int compare(BooleanRectangleCollection.Point rectangle, BooleanRectangleCollection.Point rectangle2) {
				return rectangle.Y != rectangle2.Y ? rectangle.Y - rectangle2.Y : rectangle.X - rectangle2.X;
			}
		});
		int1 = ((BooleanRectangleCollection.Point)arrayList2.get(0)).X;
		int2 = ((BooleanRectangleCollection.Point)arrayList2.get(0)).Y;
		arrayList3.add(int1);
		arrayList4.add(int2);
		Iterator iterator = arrayList2.iterator();
		while (iterator.hasNext()) {
			BooleanRectangleCollection.Point point = (BooleanRectangleCollection.Point)iterator.next();
			if (point.X > int1) {
				int1 = point.X;
				arrayList3.add(int1);
			}

			if (point.Y > int2) {
				int2 = point.Y;
				arrayList4.add(int2);
			}
		}

		for (int int3 = 0; int3 < arrayList4.size() - 1; ++int3) {
			for (int int4 = 0; int4 < arrayList3.size() - 1; ++int4) {
				int int5 = (Integer)arrayList3.get(int4);
				int int6 = (Integer)arrayList4.get(int3);
				int int7 = (Integer)arrayList3.get(int4 + 1) - int5;
				int int8 = (Integer)arrayList4.get(int3 + 1) - int6;
				Rectangle rectangle5 = new Rectangle(int5, int6, int7, int8);
				if (!this.Intersects(rectangle5, rectangle4)) {
					arrayList.add(rectangle5);
				}
			}
		}

		return arrayList;
	}

	public void optimize() {
		ArrayList arrayList = new ArrayList();
		int int1 = 1000000;
		int int2 = 1000000;
		int int3 = -1000000;
		int int4 = -1000000;
		int int5;
		for (int5 = 0; int5 < this.size(); ++int5) {
			Rectangle rectangle = (Rectangle)this.get(int5);
			if (rectangle.getX() < int1) {
				int1 = rectangle.getX();
			}

			if (rectangle.getY() < int2) {
				int2 = rectangle.getY();
			}

			if (rectangle.getX() + rectangle.getWidth() > int3) {
				int3 = rectangle.getX() + rectangle.getWidth();
			}

			if (rectangle.getY() + rectangle.getHeight() > int4) {
				int4 = rectangle.getY() + rectangle.getHeight();
			}
		}

		int5 = int3 - int1;
		int int6 = int4 - int2;
		int int7;
		int int8;
		for (int7 = 0; int7 < int5; ++int7) {
			for (int8 = 0; int8 < int6; ++int8) {
				donemap[int7][int8] = true;
			}
		}

		int int9;
		int int10;
		int int11;
		int int12;
		for (int7 = 0; int7 < this.size(); ++int7) {
			Rectangle rectangle2 = (Rectangle)this.get(int7);
			int9 = rectangle2.getX() - int1;
			int10 = rectangle2.getY() - int2;
			for (int11 = 0; int11 < rectangle2.getWidth(); ++int11) {
				for (int12 = 0; int12 < rectangle2.getHeight(); ++int12) {
					donemap[int11 + int9][int12 + int10] = false;
				}
			}
		}

		for (int7 = 0; int7 < int5; ++int7) {
			for (int8 = 0; int8 < int6; ++int8) {
				if (!donemap[int7][int8]) {
					int9 = this.DoHeight(int7, int8, int6);
					int10 = this.DoWidth(int7, int8, int9, int5);
					for (int11 = 0; int11 < int10; ++int11) {
						for (int12 = 0; int12 < int9; ++int12) {
							donemap[int11 + int7][int12 + int8] = true;
						}
					}

					arrayList.add(new Rectangle(int7 + int1, int8 + int2, int10, int9));
				}
			}
		}

		this.clear();
		this.addAll(arrayList);
	}

	public boolean IsPointInRect(int int1, int int2, Rectangle rectangle) {
		return int1 >= rectangle.getX() && int1 <= rectangle.getX() + rectangle.getWidth() && int2 >= rectangle.getY() && int2 <= rectangle.getY() + rectangle.getHeight();
	}

	public int IntesectsLine(BooleanRectangleCollection.Line line, BooleanRectangleCollection.Line line2) {
		intersection.X = 0;
		intersection.Y = 0;
		int int1 = line.End.X - line.Start.X;
		int int2 = line.End.Y - line.Start.Y;
		int int3 = line2.End.X - line2.Start.X;
		int int4 = line2.End.Y - line2.Start.Y;
		if (int1 != int3 && int2 != int4) {
			int int5;
			int int6;
			int int7;
			int int8;
			if (int2 == 0) {
				int5 = Math.min(line.Start.X, line.End.X);
				int6 = Math.max(line.Start.X, line.End.X);
				int7 = Math.min(line2.Start.Y, line2.End.Y);
				int8 = Math.max(line2.Start.Y, line2.End.Y);
				intersection.X = line2.Start.X;
				intersection.Y = line.Start.Y;
				return 1;
			} else {
				int5 = Math.min(line2.Start.X, line2.End.X);
				int6 = Math.max(line2.Start.X, line2.End.X);
				int7 = Math.min(line.Start.Y, line.End.Y);
				int8 = Math.max(line.Start.Y, line.End.Y);
				intersection.X = line.Start.X;
				intersection.Y = line2.Start.Y;
				return -1;
			}
		} else {
			return 0;
		}
	}

	public boolean Intersects(Rectangle rectangle, Rectangle rectangle2) {
		int int1 = rectangle.getX() + rectangle.getWidth();
		int int2 = rectangle.getX();
		int int3 = rectangle.getY() + rectangle.getHeight();
		int int4 = rectangle.getY();
		int int5 = rectangle2.getX() + rectangle2.getWidth();
		int int6 = rectangle2.getX();
		int int7 = rectangle2.getY() + rectangle2.getHeight();
		int int8 = rectangle2.getY();
		return int1 > int6 && int3 > int8 && int2 < int5 && int4 < int7;
	}

	private int DoHeight(int int1, int int2, int int3) {
		int int4 = 0;
		for (int int5 = int2; int5 < int3; ++int5) {
			if (donemap[int1][int5]) {
				return int4;
			}

			++int4;
		}

		return int4;
	}

	private int DoWidth(int int1, int int2, int int3, int int4) {
		int int5 = 0;
		for (int int6 = int1; int6 < int4; ++int6) {
			for (int int7 = int2; int7 < int3; ++int7) {
				if (donemap[int6][int7]) {
					return int5;
				}
			}

			++int5;
		}

		return int5;
	}

	private void DoRect(int int1, int int2) {
	}

	public class Line {
		public BooleanRectangleCollection.Point Start = new BooleanRectangleCollection.Point();
		public BooleanRectangleCollection.Point End = new BooleanRectangleCollection.Point();

		public Line(BooleanRectangleCollection.Point point, BooleanRectangleCollection.Point point2) {
			this.Start.X = point.X;
			this.Start.Y = point.Y;
			this.End.X = point2.X;
			this.End.Y = point2.Y;
		}
	}

	public static class Point {
		public int X;
		public int Y;

		public Point() {
		}

		public Point(int int1, int int2) {
			this.X = int1;
			this.Y = int2;
		}
	}
}
