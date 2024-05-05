package zombie.util.list;

import java.util.Objects;


public interface FloatConsumer {

	void accept(float float1);

	default FloatConsumer andThen(FloatConsumer floatConsumer) {
		Objects.requireNonNull(floatConsumer);
		return (var2)->{
			this.accept(var2);
			floatConsumer.accept(var2);
		};
	}
}
