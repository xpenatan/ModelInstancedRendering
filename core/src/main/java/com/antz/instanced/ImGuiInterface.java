package com.antz.instanced;

import com.badlogic.gdx.InputMultiplexer;

public interface ImGuiInterface {

    default void initialize(Runnable run) {
        run.run();
    }

    default void setup(InputMultiplexer inputMultiplexer) {

    }

    default void begin() {

    }

    default void end() {

    }

    default void render(Object data) {
    }
}
