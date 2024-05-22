package com.antz.instanced;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ModelInstancedRendering extends Game {

    private ImGuiInterface imGuiInterface;

    public ModelInstancedRendering() {
        this.imGuiInterface = new ImGuiInterface() {};
    }

    public ModelInstancedRendering(ImGuiInterface imGuiInterface) {
        this.imGuiInterface = imGuiInterface;
    }

    @Override
    public void create() {
        //setScreen(new ModelInstancedRenderingBasicScreen()); // Original
        imGuiInterface.initialize(new Runnable() {
            @Override
            public void run() {
                setScreen(new ModelInstancedRenderingPBRScreen(imGuiInterface)); // gdx-gltf + PBR Shaders
            }
        });
    }
}
