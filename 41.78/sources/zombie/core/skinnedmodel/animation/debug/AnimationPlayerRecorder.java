package zombie.core.skinnedmodel.animation.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.action.ActionState;
import zombie.core.logger.LoggerManager;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSource;
import zombie.core.skinnedmodel.advancedanimation.LiveAnimNode;
import zombie.debug.DebugLog;
import zombie.iso.Vector2;
import zombie.iso.Vector3;


public final class AnimationPlayerRecorder {
	private boolean m_isRecording = false;
	private final AnimationTrackRecordingFrame m_animationTrackFrame;
	private final AnimationNodeRecordingFrame m_animationNodeFrame;
	private final AnimationVariableRecordingFrame m_animationVariableFrame;
	private final IsoGameCharacter m_character;
	private static String s_startupTimeStamp = null;
	private static final SimpleDateFormat s_fileNameSdf = new SimpleDateFormat("yy-MM-dd_HH-mm");

	public AnimationPlayerRecorder(IsoGameCharacter gameCharacter) {
		this.m_character = gameCharacter;
		String string = this.m_character.getUID();
		String string2 = string + "_AnimRecorder";
		this.m_animationTrackFrame = new AnimationTrackRecordingFrame(string2 + "_Track");
		this.m_animationNodeFrame = new AnimationNodeRecordingFrame(string2 + "_Node");
		this.m_animationVariableFrame = new AnimationVariableRecordingFrame(string2 + "_Vars");
	}

	public void beginLine(int int1) {
		this.m_animationTrackFrame.reset();
		this.m_animationTrackFrame.setFrameNumber(int1);
		this.m_animationNodeFrame.reset();
		this.m_animationNodeFrame.setFrameNumber(int1);
		this.m_animationVariableFrame.reset();
		this.m_animationVariableFrame.setFrameNumber(int1);
	}

	public void endLine() {
		this.m_animationTrackFrame.writeLine();
		this.m_animationNodeFrame.writeLine();
		this.m_animationVariableFrame.writeLine();
	}

	public void discardRecording() {
		this.m_animationTrackFrame.closeAndDiscard();
		this.m_animationNodeFrame.closeAndDiscard();
		this.m_animationVariableFrame.closeAndDiscard();
	}

	public static PrintStream openFileStream(String string, boolean boolean1, Consumer consumer) {
		String string2 = getTimeStampedFilePath(string);
		try {
			consumer.accept(string2);
			File file = new File(string2);
			return new PrintStream(new FileOutputStream(file, boolean1));
		} catch (FileNotFoundException fileNotFoundException) {
			DebugLog.General.error("Exception thrown trying to create animation player recording file.");
			DebugLog.General.error(fileNotFoundException);
			fileNotFoundException.printStackTrace();
			return null;
		}
	}

	private static String getTimeStampedFilePath(String string) {
		String string2 = LoggerManager.getLogsDir();
		return string2 + File.separator + getTimeStampedFileName(string) + ".csv";
	}

	private static String getTimeStampedFileName(String string) {
		String string2 = getStartupTimeStamp();
		return string2 + "_" + string;
	}

	private static String getStartupTimeStamp() {
		if (s_startupTimeStamp == null) {
			s_startupTimeStamp = s_fileNameSdf.format(Calendar.getInstance().getTime());
		}

		return s_startupTimeStamp;
	}

	public void logAnimWeights(List list, int[] intArray, float[] floatArray, Vector2 vector2) {
		this.m_animationTrackFrame.logAnimWeights(list, intArray, floatArray, vector2);
	}

	public void logAnimNode(LiveAnimNode liveAnimNode) {
		if (liveAnimNode.isTransitioningIn()) {
			this.m_animationNodeFrame.logWeight("transition(" + liveAnimNode.getTransitionFrom() + "->" + liveAnimNode.getName() + ")", liveAnimNode.getTransitionLayerIdx(), liveAnimNode.getTransitionInWeight());
		}

		this.m_animationNodeFrame.logWeight(liveAnimNode.getName(), liveAnimNode.getLayerIdx(), liveAnimNode.getWeight());
	}

	public void logActionState(ActionState actionState, List list) {
		this.m_animationNodeFrame.logActionState(actionState, list);
	}

	public void logAIState(State state, List list) {
		this.m_animationNodeFrame.logAIState(state, list);
	}

	public void logAnimState(AnimState animState) {
		this.m_animationNodeFrame.logAnimState(animState);
	}

	public void logVariables(IAnimationVariableSource iAnimationVariableSource) {
		this.m_animationVariableFrame.logVariables(iAnimationVariableSource);
	}

	public void logCharacterPos() {
		IsoPlayer player = IsoPlayer.getInstance();
		IsoGameCharacter gameCharacter = this.getOwner();
		Vector3 vector3 = player.getPosition(new Vector3());
		Vector3 vector32 = gameCharacter.getPosition(new Vector3());
		Vector3 vector33 = vector3.sub(vector32, new Vector3());
		this.m_animationNodeFrame.logCharacterToPlayerDiff(vector33);
	}

	public IsoGameCharacter getOwner() {
		return this.m_character;
	}

	public boolean isRecording() {
		return this.m_isRecording;
	}

	public void setRecording(boolean boolean1) {
		if (this.m_isRecording != boolean1) {
			this.m_isRecording = boolean1;
			DebugLog.General.println("AnimationPlayerRecorder %s.", this.m_isRecording ? "recording" : "stopped");
		}
	}
}
