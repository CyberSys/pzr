package zombie.text.templating;

import zombie.characters.IsoGameCharacter;


public class ReplaceProviderCharacter extends ReplaceProvider {

	public ReplaceProviderCharacter(IsoGameCharacter gameCharacter) {
		this.addReplacer("firstname", new IReplace(){
			
			public String getString() {
				return gameCharacter != null && gameCharacter.getDescriptor() != null && gameCharacter.getDescriptor().getForename() != null ? gameCharacter.getDescriptor().getForename() : "Bob";
			}
		});
		this.addReplacer("lastname", new IReplace(){
			
			public String getString() {
				return gameCharacter != null && gameCharacter.getDescriptor() != null && gameCharacter.getDescriptor().getSurname() != null ? gameCharacter.getDescriptor().getSurname() : "Smith";
			}
		});
	}
}
