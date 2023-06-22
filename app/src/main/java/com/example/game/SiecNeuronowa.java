package com.example.game;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SiecNeuronowa {

    private Interpreter interpreter;

    public SiecNeuronowa(Context context) {
        try {
            interpreter = new Interpreter(loadModelFile(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float predict(float num_correct, float avg_time, float current_difficulty) {
        float[][] input = {{num_correct, avg_time, current_difficulty}};
        float[][] output = new float[1][1];
        interpreter.run(input, output);
        // zaokrąglanie do najbliższej liczby całkowitej z przedziału [1, 10]
        float predicted_level = Math.min(Math.max(Math.round(output[0][0]), 1), 10);
        return predicted_level;
    }

    private MappedByteBuffer loadModelFile(Context context) throws Exception {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
