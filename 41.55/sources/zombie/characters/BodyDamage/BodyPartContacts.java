package zombie.characters.BodyDamage;

import java.util.ArrayList;
import zombie.debug.DebugLog;


public final class BodyPartContacts {
	private static final BodyPartContacts.ContactNode root;
	private static final BodyPartContacts.ContactNode[] nodes;

	public static BodyPartType[] getAllContacts(BodyPartType bodyPartType) {
		for (int int1 = 0; int1 < nodes.length; ++int1) {
			BodyPartContacts.ContactNode contactNode = nodes[int1];
			if (contactNode.bodyPart == bodyPartType) {
				return contactNode.bodyPartAllContacts;
			}
		}

		return null;
	}

	public static BodyPartType[] getChildren(BodyPartType bodyPartType) {
		for (int int1 = 0; int1 < nodes.length; ++int1) {
			BodyPartContacts.ContactNode contactNode = nodes[int1];
			if (contactNode.bodyPart == bodyPartType) {
				return contactNode.bodyPartChildren;
			}
		}

		return null;
	}

	public static BodyPartType getParent(BodyPartType bodyPartType) {
		for (int int1 = 0; int1 < nodes.length; ++int1) {
			BodyPartContacts.ContactNode contactNode = nodes[int1];
			if (contactNode.bodyPart == bodyPartType) {
				if (contactNode.depth == 0) {
					DebugLog.log("Warning, root node parent is always null.");
				}

				return contactNode.bodyPartParent;
			}
		}

		return null;
	}

	public static int getNodeDepth(BodyPartType bodyPartType) {
		for (int int1 = 0; int1 < nodes.length; ++int1) {
			BodyPartContacts.ContactNode contactNode = nodes[int1];
			if (contactNode.bodyPart == bodyPartType) {
				if (!contactNode.initialised) {
					DebugLog.log("Warning: attempting to get depth for non initialised node \'" + contactNode.bodyPart.toString() + "\'.");
				}

				return contactNode.depth;
			}
		}

		return -1;
	}

	private static BodyPartContacts.ContactNode getNodeForBodyPart(BodyPartType bodyPartType) {
		for (int int1 = 0; int1 < nodes.length; ++int1) {
			if (nodes[int1].bodyPart == bodyPartType) {
				return nodes[int1];
			}
		}

		return null;
	}

	private static void initNodes(BodyPartContacts.ContactNode contactNode, int int1, BodyPartContacts.ContactNode contactNode2) {
		contactNode.parent = contactNode2;
		contactNode.depth = int1;
		ArrayList arrayList = new ArrayList();
		if (contactNode.parent != null) {
			arrayList.add(contactNode.parent);
		}

		if (contactNode.children != null) {
			BodyPartContacts.ContactNode[] contactNodeArray = contactNode.children;
			int int2 = contactNodeArray.length;
			for (int int3 = 0; int3 < int2; ++int3) {
				BodyPartContacts.ContactNode contactNode3 = contactNodeArray[int3];
				arrayList.add(contactNode3);
				initNodes(contactNode3, int1 + 1, contactNode);
			}
		}

		contactNode.allContacts = new BodyPartContacts.ContactNode[arrayList.size()];
		arrayList.toArray(contactNode.allContacts);
		contactNode.initialised = true;
	}

	private static void postInit() {
		BodyPartContacts.ContactNode[] contactNodeArray = nodes;
		int int1 = contactNodeArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			BodyPartContacts.ContactNode contactNode = contactNodeArray[int2];
			if (contactNode.parent != null) {
				contactNode.bodyPartParent = contactNode.parent.bodyPart;
			}

			int int3;
			if (contactNode.children != null && contactNode.children.length > 0) {
				contactNode.bodyPartChildren = new BodyPartType[contactNode.children.length];
				for (int3 = 0; int3 < contactNode.children.length; ++int3) {
					contactNode.bodyPartChildren[int3] = contactNode.children[int3].bodyPart;
				}
			} else {
				contactNode.bodyPartChildren = new BodyPartType[0];
			}

			if (contactNode.allContacts != null && contactNode.allContacts.length > 0) {
				contactNode.bodyPartAllContacts = new BodyPartType[contactNode.allContacts.length];
				for (int3 = 0; int3 < contactNode.allContacts.length; ++int3) {
					contactNode.bodyPartAllContacts[int3] = contactNode.allContacts[int3].bodyPart;
				}
			} else {
				contactNode.bodyPartAllContacts = new BodyPartType[0];
			}

			if (!contactNode.initialised) {
				DebugLog.log("Warning: node for \'" + contactNode.bodyPart.toString() + "\' is not initialised!");
			}
		}
	}

	static  {
	int var0 = BodyPartType.ToIndex(BodyPartType.MAX);
		nodes = new BodyPartContacts.ContactNode[var0];
	for (int var1 = 0; var1 < var0; ++var1) {
		nodes[var1] = new BodyPartContacts.ContactNode(BodyPartType.FromIndex(var1));
	}

		root = getNodeForBodyPart(BodyPartType.Torso_Upper);
		root.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Neck), getNodeForBodyPart(BodyPartType.Torso_Lower), getNodeForBodyPart(BodyPartType.UpperArm_L), getNodeForBodyPart(BodyPartType.UpperArm_R)};
	BodyPartContacts.ContactNode var2 = getNodeForBodyPart(BodyPartType.Neck);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Head)};
		var2 = getNodeForBodyPart(BodyPartType.UpperArm_L);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.ForeArm_L)};
		var2 = getNodeForBodyPart(BodyPartType.ForeArm_L);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Hand_L)};
		var2 = getNodeForBodyPart(BodyPartType.UpperArm_R);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.ForeArm_R)};
		var2 = getNodeForBodyPart(BodyPartType.ForeArm_R);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Hand_R)};
		var2 = getNodeForBodyPart(BodyPartType.Torso_Lower);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Groin)};
		var2 = getNodeForBodyPart(BodyPartType.Groin);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.UpperLeg_L), getNodeForBodyPart(BodyPartType.UpperLeg_R)};
		var2 = getNodeForBodyPart(BodyPartType.UpperLeg_L);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.LowerLeg_L)};
		var2 = getNodeForBodyPart(BodyPartType.LowerLeg_L);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Foot_L)};
		var2 = getNodeForBodyPart(BodyPartType.UpperLeg_R);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.LowerLeg_R)};
		var2 = getNodeForBodyPart(BodyPartType.LowerLeg_R);
		var2.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Foot_R)};
		initNodes(root, 0, (BodyPartContacts.ContactNode)null);
		postInit();
	}

	private static class ContactNode {
		BodyPartType bodyPart;
		int depth = -1;
		BodyPartContacts.ContactNode parent;
		BodyPartContacts.ContactNode[] children;
		BodyPartContacts.ContactNode[] allContacts;
		BodyPartType bodyPartParent;
		BodyPartType[] bodyPartChildren;
		BodyPartType[] bodyPartAllContacts;
		boolean initialised = false;

		public ContactNode(BodyPartType bodyPartType) {
			this.bodyPart = bodyPartType;
		}
	}
}
