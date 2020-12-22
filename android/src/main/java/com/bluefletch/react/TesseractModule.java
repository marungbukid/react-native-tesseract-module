package com.bluefletch.react;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TesseractModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static String DATA_PATH = Environment.getExternalStorageDirectory().toString();
    private static final String TESS_FILES_DIRECTORY = "tessdata";
    private static final String TESS_FILES_EXTENSION = ".traineddata";
    private static final String LANG = "eng";
    private static String TESS_FILES_PATH;
    private TessBaseAPI tesseract;

    public TesseractModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        if (!this.DATA_PATH.contains(reactContext.getPackageName())) {
            this.DATA_PATH += File.separator + reactContext.getPackageName();
        }
        this.TESS_FILES_PATH = this.DATA_PATH + File.separator + this.TESS_FILES_DIRECTORY;
    }

    @ReactMethod
    public void recognize(String uri, final Promise promise) {
        Log.d(getName(), "recognize");

        try {
            if (shouldCopyTrainedFile(LANG)) {
                prepareTrainedFilesDirectory();
                copyTrainedFile(LANG);
            }

            final Bitmap bitmap = getBitmap(uri);

            if (bitmap != null) {
                new Thread() {
                    @Override
                    public void run() {
                        tesseract = createTesseractAPI(LANG);
                        tesseract.setImage(bitmap);
                        tesseract.getHOCRText(0);

                        String recognizedText = tesseract.getUTF8Text();

                        tesseract.end();
                        promise.resolve(recognizedText);
                    }

                }.start();
            } else {
                throw new IOException("Could not decode a file path into a bitmap.");
            }
        } catch (IOException e) {
            Log.e(getName(), "Could not access trained files. " + e.toString(), e);
            promise.reject("Could not access trained files", e.toString());
        } catch (Exception e) {
            Log.e(getName(), "Could not recognize text. " + e.toString(), e);
            promise.reject("Could not recognize text", e.toString());
        }
    }

    @NonNull
    @Override
    public String getName() {
        return "TesseractModule";
    }

    private TessBaseAPI createTesseractAPI(String lang) {
        TessBaseAPI tessBaseAPI = new TessBaseAPI(createProgressNotifier());
        tessBaseAPI.init(DATA_PATH + File.separator, lang);
        return tessBaseAPI;
    }


    private TessBaseAPI.ProgressNotifier createProgressNotifier() {
        return new TessBaseAPI.ProgressNotifier() {
            @Override
            public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {
                Log.d(getName(), "progress " + progressValues.getPercent());
                // onProgress(progressValues.getPercent());
            }
        };
    }

    private Bitmap getBitmap(String imageSource) throws Exception {
        String path = imageSource.startsWith("file://") ? imageSource.replace("file://", "") : imageSource;

        if (path.startsWith("http://") || path.startsWith("https://")) {
            // TODO: support remote files
            throw new Exception("Cannot select remote files");
        }

        return BitmapFactory.decodeFile(path, new BitmapFactory.Options());
    }

    private boolean shouldCopyTrainedFile(String lang) {
        Log.d(getName(), "should copy " + lang + " trained files?");
        String filePath = TESS_FILES_PATH + File.separator + lang + TESS_FILES_EXTENSION;
        File file = new File(filePath);
        return !file.exists();
    }

    private void prepareTrainedFilesDirectory() throws IOException {
        Log.d(getName(), "prepare trained files directory");
        File dir = new File(TESS_FILES_PATH);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(getName(), "Could not create directory, please make sure the app has write permission");
                throw new IOException("Could not create directory");
            }
        }
    }

    private void copyTrainedFile(String lang) throws IOException {
        Log.d(getName(), "copy tesseract data file (" + lang + ")");
        String assetPath = TESS_FILES_DIRECTORY + File.separator + lang + TESS_FILES_EXTENSION;
        String newAssetPath = DATA_PATH + File.separator + assetPath;
        copyAsset(assetPath, newAssetPath);
    }

    private void copyAsset(String from, String to) throws IOException {
        Log.d(getName(), "copy asset " + from + " to " + to);

        InputStream in = reactContext.getAssets().open(from);
        OutputStream out = new FileOutputStream(to);
        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
