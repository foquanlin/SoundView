package com.lalagrass.soundview;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static volatile boolean isRecording = false;
    private static final int fftSize = 8192;
    private static final int frequency = 44100;
    private static final int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawingView = (DrawingView) this.findViewById(R.id.drawingView);
    }


    @Override
    public void onDestroy() {
        isRecording = false;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        isRecording = false;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        isRecording = true;
        new RecordAudio().execute();
    }

    private class RecordAudio extends AsyncTask<Void, double[], Void> {
        private double avg = 0;
        private int count = 0;

        @Override
        protected Void doInBackground(Void... params) {
            AudioRecord audioRecord = null;
            try {

                FFT fft = new FFT(fftSize);
                int bufferSize = AudioRecord.getMinBufferSize(frequency,
                        channelConfiguration, audioEncoding);
                bufferSize = fftSize;
                audioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC, frequency,
                        channelConfiguration, audioEncoding, bufferSize);
                Log.i("a", "init startRecording");
                short[] buffer = new short[bufferSize];
                int offset = 0;
                audioRecord.startRecording();
                Log.i("a", "startRecording");
                while (isRecording) {
                    while (offset < bufferSize && isRecording) {
                        int bufferReadResult = audioRecord.read(buffer, offset,
                                bufferSize - offset);
                        offset += bufferReadResult;
                    }
                    if (isRecording) {
                        offset = 0;
                        double[] ret = fft.fft3(buffer);
                        publishProgress(ret);
                    }
                }
                audioRecord.stop();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (audioRecord != null)
                    audioRecord.release();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(double[]... progress) {
            double[] ret = progress[0];
            drawingView.UpdateSpectrum(ret);
        }

        protected void onPostExecute(Void result) {

        }
    }
}
