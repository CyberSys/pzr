package org.lwjglx.util.glu.tessellation;


class Dict {
	DictNode head;
	Object frame;
	Dict.DictLeq leq;

	private Dict() {
	}

	static Dict dictNewDict(Object object, Dict.DictLeq dictLeq) {
		Dict dict = new Dict();
		dict.head = new DictNode();
		dict.head.key = null;
		dict.head.next = dict.head;
		dict.head.prev = dict.head;
		dict.frame = object;
		dict.leq = dictLeq;
		return dict;
	}

	static void dictDeleteDict(Dict dict) {
		dict.head = null;
		dict.frame = null;
		dict.leq = null;
	}

	static DictNode dictInsert(Dict dict, Object object) {
		return dictInsertBefore(dict, dict.head, object);
	}

	static DictNode dictInsertBefore(Dict dict, DictNode dictNode, Object object) {
		do {
			dictNode = dictNode.prev;
		} while (dictNode.key != null && !dict.leq.leq(dict.frame, dictNode.key, object));

		DictNode dictNode2 = new DictNode();
		dictNode2.key = object;
		dictNode2.next = dictNode.next;
		dictNode.next.prev = dictNode2;
		dictNode2.prev = dictNode;
		dictNode.next = dictNode2;
		return dictNode2;
	}

	static Object dictKey(DictNode dictNode) {
		return dictNode.key;
	}

	static DictNode dictSucc(DictNode dictNode) {
		return dictNode.next;
	}

	static DictNode dictPred(DictNode dictNode) {
		return dictNode.prev;
	}

	static DictNode dictMin(Dict dict) {
		return dict.head.next;
	}

	static DictNode dictMax(Dict dict) {
		return dict.head.prev;
	}

	static void dictDelete(Dict dict, DictNode dictNode) {
		dictNode.next.prev = dictNode.prev;
		dictNode.prev.next = dictNode.next;
	}

	static DictNode dictSearch(Dict dict, Object object) {
		DictNode dictNode = dict.head;
		do {
			dictNode = dictNode.next;
		} while (dictNode.key != null && !dict.leq.leq(dict.frame, object, dictNode.key));

		return dictNode;
	}

	public interface DictLeq {

		boolean leq(Object object, Object object2, Object object3);
	}
}
