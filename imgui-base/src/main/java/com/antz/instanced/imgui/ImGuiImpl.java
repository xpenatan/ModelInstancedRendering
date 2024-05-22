package com.antz.instanced.imgui;

import com.antz.instanced.ImGuiInterface;
import com.antz.instanced.ModelInstancedRenderingPBRScreen;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
import imgui.ImDrawData;
import imgui.ImGui;
import imgui.ImGuiCond;
import imgui.ImGuiConfigFlags;
import imgui.ImGuiIO;
import imgui.ImGuiLoader;
import imgui.ImVec2;
import imgui.gdx.ImGuiGdxImpl;
import imgui.gdx.ImGuiGdxInputMultiplexer;
import imgui.idl.helper.IDLBool;
import imgui.idl.helper.IDLFloatArray;
import imgui.idl.helper.IDLInt;

public class ImGuiImpl implements ImGuiInterface {
    ImGuiGdxImpl impl;

    IDLFloatArray floatArray;
    int values_offset = 0;
    double refresh_time = 0.0;
    int maxScale = 60;

    IDLInt cameraFar;

    @Override
    public void initialize(Runnable run) {
        ImGuiLoader.init(run);
    }

    @Override
    public void setup(InputMultiplexer inputMultiplexer) {
        if(Gdx.app.getType() == Application.ApplicationType.WebGL) {
            // Not possible to have ini filename with webgl
            ImGui.CreateContext(false);
        }
        else {
            ImGui.CreateContext(true);
        }

        ImGuiIO io = ImGui.GetIO();
        io.ConfigFlags(ImGuiConfigFlags.ImGuiConfigFlags_DockingEnable);

        ImGuiGdxInputMultiplexer input = new ImGuiGdxInputMultiplexer();
        impl = new ImGuiGdxImpl();
        inputMultiplexer.addProcessor(input);

        floatArray = new IDLFloatArray(90);
        cameraFar = new IDLInt();
    }

    @Override
    public void begin() {
        impl.update();
    }

    @Override
    public void end() {
        ImGui.Render();
        ImDrawData drawData = ImGui.GetDrawData();
        impl.render(drawData);
    }

    @Override
    public void render(Object data) {
        if(data instanceof ModelInstancedRenderingPBRScreen) {
            ModelInstancedRenderingPBRScreen screen = (ModelInstancedRenderingPBRScreen)data;

//            bkPositionZ = screen.camera.position.z;
//            bkCULLING_FACTOR = ModelInstancedRenderingPBRScreen.CULLING_FACTOR;

            ImGui.SetNextWindowSize(ImVec2.TMP_1.set(400, 200), ImGuiCond.ImGuiCond_FirstUseEver);
            ImGui.Begin("Debug Window");
            {
                renderFPSPlot();

                if(ImGui.Button("Debug mode")) {
                    screen.camera.direction.set(Vector3.Z);
                    screen.camera.position.z = -300;
                }

                cameraFar.set((int)screen.camera.far);
                if(ImGui.InputInt("Camera far", cameraFar)) {
                    screen.camera.far = cameraFar.getValue();
                }

            }
            ImGui.End();
        }
    }

    private void renderFPSPlot() {
        if(refresh_time == 0.0) {
            refresh_time = ImGui.GetTime();
        }
        int size = floatArray.getSize();
        int framesPerSecond = Gdx.graphics.getFramesPerSecond();
        float updateRate = 5f;
        while (refresh_time < ImGui.GetTime()) // Create data at fixed 60 Hz rate for the demo
        {
            floatArray.setValue(values_offset, framesPerSecond);
            values_offset = (values_offset + 1) % size;
            refresh_time += 1.0f / updateRate;
        }
        if(framesPerSecond > maxScale) {
            maxScale = framesPerSecond + 20;
        }

        String overlay = "FPS " + framesPerSecond;
        ImGui.PlotLines("##Lines", floatArray, size, values_offset, overlay, 0.0f, maxScale, ImVec2.TMP_1.set(-1, 60.0f));
    }
}
