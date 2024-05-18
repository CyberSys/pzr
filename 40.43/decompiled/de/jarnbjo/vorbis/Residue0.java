package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;

class Residue0 extends Residue {
   protected Residue0(BitInputStream var1, SetupHeader var2) throws VorbisFormatException, IOException {
      super(var1, var2);
   }

   protected int getType() {
      return 0;
   }

   protected void decodeResidue(VorbisStream var1, BitInputStream var2, Mode var3, int var4, boolean[] var5, float[][] var6) throws VorbisFormatException, IOException {
      throw new UnsupportedOperationException();
   }
}
