package zombie.characters;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.math.PZMath;


public final class Stats {
	public float Anger = 0.0F;
	public float boredom = 0.0F;
	public float endurance = 1.0F;
	public boolean enduranceRecharging = false;
	public float endurancelast = 1.0F;
	public float endurancedanger = 0.25F;
	public float endurancewarn = 0.5F;
	public float fatigue = 0.0F;
	public float fitness = 1.0F;
	public float hunger = 0.0F;
	public float idleboredom = 0.0F;
	public float morale = 0.5F;
	public float stress = 0.0F;
	public float Fear = 0.0F;
	public float Panic = 0.0F;
	public float Sanity = 1.0F;
	public float Sickness = 0.0F;
	public float Boredom = 0.0F;
	public float Pain = 0.0F;
	public float Drunkenness = 0.0F;
	public int NumVisibleZombies = 0;
	public int LastNumVisibleZombies = 0;
	public boolean Tripping = false;
	public float TrippingRotAngle = 0.0F;
	public float thirst = 0.0F;
	public int NumChasingZombies = 0;
	public int LastVeryCloseZombies = 0;
	public static int NumCloseZombies = 0;
	public int LastNumChasingZombies = 0;
	public float stressFromCigarettes = 0.0F;
	public float ChasingZombiesDanger;
	public int MusicZombiesVisible = 0;
	public int MusicZombiesTargeting = 0;

	public int getNumVisibleZombies() {
		return this.NumVisibleZombies;
	}

	public int getNumChasingZombies() {
		return this.LastNumChasingZombies;
	}

	public void load(DataInputStream dataInputStream) throws IOException {
		this.Anger = dataInputStream.readFloat();
		this.boredom = dataInputStream.readFloat();
		this.endurance = dataInputStream.readFloat();
		this.fatigue = dataInputStream.readFloat();
		this.fitness = dataInputStream.readFloat();
		this.hunger = dataInputStream.readFloat();
		this.morale = dataInputStream.readFloat();
		this.stress = dataInputStream.readFloat();
		this.Fear = dataInputStream.readFloat();
		this.Panic = dataInputStream.readFloat();
		this.Sanity = dataInputStream.readFloat();
		this.Sickness = dataInputStream.readFloat();
		this.Boredom = dataInputStream.readFloat();
		this.Pain = dataInputStream.readFloat();
		this.Drunkenness = dataInputStream.readFloat();
		this.thirst = dataInputStream.readFloat();
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.Anger = byteBuffer.getFloat();
		this.boredom = byteBuffer.getFloat();
		this.endurance = byteBuffer.getFloat();
		this.fatigue = byteBuffer.getFloat();
		this.fitness = byteBuffer.getFloat();
		this.hunger = byteBuffer.getFloat();
		this.morale = byteBuffer.getFloat();
		this.stress = byteBuffer.getFloat();
		this.Fear = byteBuffer.getFloat();
		this.Panic = byteBuffer.getFloat();
		this.Sanity = byteBuffer.getFloat();
		this.Sickness = byteBuffer.getFloat();
		this.Boredom = byteBuffer.getFloat();
		this.Pain = byteBuffer.getFloat();
		this.Drunkenness = byteBuffer.getFloat();
		this.thirst = byteBuffer.getFloat();
		if (int1 >= 97) {
			this.stressFromCigarettes = byteBuffer.getFloat();
		}
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeFloat(this.Anger);
		dataOutputStream.writeFloat(this.boredom);
		dataOutputStream.writeFloat(this.endurance);
		dataOutputStream.writeFloat(this.fatigue);
		dataOutputStream.writeFloat(this.fitness);
		dataOutputStream.writeFloat(this.hunger);
		dataOutputStream.writeFloat(this.morale);
		dataOutputStream.writeFloat(this.stress);
		dataOutputStream.writeFloat(this.Fear);
		dataOutputStream.writeFloat(this.Panic);
		dataOutputStream.writeFloat(this.Sanity);
		dataOutputStream.writeFloat(this.Sickness);
		dataOutputStream.writeFloat(this.Boredom);
		dataOutputStream.writeFloat(this.Pain);
		dataOutputStream.writeFloat(this.Drunkenness);
		dataOutputStream.writeFloat(this.thirst);
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putFloat(this.Anger);
		byteBuffer.putFloat(this.boredom);
		byteBuffer.putFloat(this.endurance);
		byteBuffer.putFloat(this.fatigue);
		byteBuffer.putFloat(this.fitness);
		byteBuffer.putFloat(this.hunger);
		byteBuffer.putFloat(this.morale);
		byteBuffer.putFloat(this.stress);
		byteBuffer.putFloat(this.Fear);
		byteBuffer.putFloat(this.Panic);
		byteBuffer.putFloat(this.Sanity);
		byteBuffer.putFloat(this.Sickness);
		byteBuffer.putFloat(this.Boredom);
		byteBuffer.putFloat(this.Pain);
		byteBuffer.putFloat(this.Drunkenness);
		byteBuffer.putFloat(this.thirst);
		byteBuffer.putFloat(this.stressFromCigarettes);
	}

	public float getAnger() {
		return this.Anger;
	}

	public void setAnger(float float1) {
		this.Anger = float1;
	}

	public float getBoredom() {
		return this.boredom;
	}

	public void setBoredom(float float1) {
		this.boredom = float1;
	}

	public float getEndurance() {
		return this.endurance;
	}

	public void setEndurance(float float1) {
		this.endurance = float1;
	}

	public float getEndurancelast() {
		return this.endurancelast;
	}

	public void setEndurancelast(float float1) {
		this.endurancelast = float1;
	}

	public float getEndurancedanger() {
		return this.endurancedanger;
	}

	public void setEndurancedanger(float float1) {
		this.endurancedanger = float1;
	}

	public float getEndurancewarn() {
		return this.endurancewarn;
	}

	public void setEndurancewarn(float float1) {
		this.endurancewarn = float1;
	}

	public boolean getEnduranceRecharging() {
		return this.enduranceRecharging;
	}

	public float getFatigue() {
		return this.fatigue;
	}

	public void setFatigue(float float1) {
		this.fatigue = float1;
	}

	public float getFitness() {
		return this.fitness;
	}

	public void setFitness(float float1) {
		this.fitness = float1;
	}

	public float getHunger() {
		return this.hunger;
	}

	public void setHunger(float float1) {
		this.hunger = float1;
	}

	public float getIdleboredom() {
		return this.idleboredom;
	}

	public void setIdleboredom(float float1) {
		this.idleboredom = float1;
	}

	public float getMorale() {
		return this.morale;
	}

	public void setMorale(float float1) {
		this.morale = float1;
	}

	public float getStress() {
		return this.stress + this.getStressFromCigarettes();
	}

	public void setStress(float float1) {
		this.stress = float1;
	}

	public float getStressFromCigarettes() {
		return this.stressFromCigarettes;
	}

	public void setStressFromCigarettes(float float1) {
		this.stressFromCigarettes = PZMath.clamp(float1, 0.0F, this.getMaxStressFromCigarettes());
	}

	public float getMaxStressFromCigarettes() {
		return 0.51F;
	}

	public float getFear() {
		return this.Fear;
	}

	public void setFear(float float1) {
		this.Fear = float1;
	}

	public float getPanic() {
		return this.Panic;
	}

	public void setPanic(float float1) {
		this.Panic = float1;
	}

	public float getSanity() {
		return this.Sanity;
	}

	public void setSanity(float float1) {
		this.Sanity = float1;
	}

	public float getSickness() {
		return this.Sickness;
	}

	public void setSickness(float float1) {
		this.Sickness = float1;
	}

	public float getPain() {
		return this.Pain;
	}

	public void setPain(float float1) {
		this.Pain = float1;
	}

	public float getDrunkenness() {
		return this.Drunkenness;
	}

	public void setDrunkenness(float float1) {
		this.Drunkenness = float1;
	}

	public int getVisibleZombies() {
		return this.NumVisibleZombies;
	}

	public void setNumVisibleZombies(int int1) {
		this.NumVisibleZombies = int1;
	}

	public boolean isTripping() {
		return this.Tripping;
	}

	public void setTripping(boolean boolean1) {
		this.Tripping = boolean1;
	}

	public float getTrippingRotAngle() {
		return this.TrippingRotAngle;
	}

	public void setTrippingRotAngle(float float1) {
		this.TrippingRotAngle = float1;
	}

	public float getThirst() {
		return this.thirst;
	}

	public void setThirst(float float1) {
		this.thirst = float1;
	}

	public void resetStats() {
		this.Anger = 0.0F;
		this.boredom = 0.0F;
		this.fatigue = 0.0F;
		this.hunger = 0.0F;
		this.idleboredom = 0.0F;
		this.morale = 0.5F;
		this.stress = 0.0F;
		this.Fear = 0.0F;
		this.Panic = 0.0F;
		this.Sanity = 1.0F;
		this.Sickness = 0.0F;
		this.Boredom = 0.0F;
		this.Pain = 0.0F;
		this.Drunkenness = 0.0F;
		this.thirst = 0.0F;
	}
}
