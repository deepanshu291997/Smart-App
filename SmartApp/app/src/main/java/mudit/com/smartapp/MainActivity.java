package mudit.com.smartapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Button btUpload;
    Button btCapture;
    ImageView mMainImage;
    TextView mImageDetails;

    ImageButton imgButton,imgRealTimeOcr;
    TextToSpeech t1;
    String detection="TEXT DETECTION";
    String[] detectionType={"TEXT DETECTION","LABEL DETECTION"};
    Spinner spinner;
    public static final String FILE_NAME = "temp.jpg";
    private static final String TAG = "Ma";

    public static final int GALLERY_INTENT = 2;
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btUpload = (Button) findViewById(R.id.btUpload);
        btCapture=(Button)findViewById(R.id.btTakePicture);
        mMainImage= (ImageView) findViewById(R.id.imgView);
        mImageDetails= (TextView) findViewById(R.id.tvResult);

        imgButton= (ImageButton) findViewById(R.id.imgButton);
        imgRealTimeOcr= (ImageButton) findViewById(R.id.imgRealTimeOcr);
        spinner=(Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,detectionType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter );
        t1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!t1.isSpeaking()) {
                    t1.speak(mImageDetails.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
                else
                {
                    t1.stop();
                }
            }
        });

        imgRealTimeOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detection=="TEXT DETECTION"){
                Intent i=new Intent(MainActivity.this,RealTimeOcr.class);
                startActivity(i);
                }
                else
                {
                    Intent intent=new Intent(MainActivity.this,ObjectDetection.class);
                    startActivity(intent);
                }
            }
        });

        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, GALLERY_INTENT);
             //-->   startGalleryChooser();
            }
        });
        btCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,GALLERY_INTENT);
                //-->startCamera();
            }
        });
    }

    public void startGalleryChooser() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);

    }

    public void startCamera() {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);

    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(detection=="TEXT DETECTION"){
//        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
//            uploadImage(data.getData());
//        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
//            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
//            uploadImage(photoUri);
//        }}
//    }



    public void uploadImage(Uri uri) {
        Log.d(TAG, "onActivityResult: "+2);

        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Log.d(TAG, "onActivityResult: "+3);

                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                Log.d(TAG, "onActivityResult: "+4);

                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
              //  Log.d(TAG, "Image picking failed because " + e.getMessage());
               // Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
           Log.d(TAG, "Image picker gave us a null image.");
           // Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                Log.d(TAG, "onActivityResult: "+5);

                try {
                    Log.d(TAG, "onActivityResult: "+6);

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = new AndroidJsonFactory();



                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(
                        new VisionRequestInitializer("AIzaSyDxxO6WF75REY9dWy7vzW_yYUVpB5SPKs8"));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                Log.d(TAG, "onActivityResult: "+6);

                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                Log.d(TAG, "onPostExecute: "+"abcddd"+result);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                mImageDetails.setText(result);
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }

        return message;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: "+detectionType[position]);
        detection=detectionType[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
//-----------------------------------------------------------------------------------------------------------//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(detection.equals("LABEL DETECTION")){
            Log.d(TAG, "onActivityResult: "+9);

            if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null) {
                Log.d(TAG, "onActivityResult: "+1);
                uploadImage(data.getData());
            } else if (resultCode == RESULT_OK) {
                Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
                uploadImage(photoUri);
            }}

        else {
            if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
                if (data == null) {
                    //Display an error
                    return;
                }
                Vision.Builder visionBuilder = new Vision.Builder(
                        new NetHttpTransport(),
                        new AndroidJsonFactory(),
                        null);

                visionBuilder.setVisionRequestInitializer(
                        new VisionRequestInitializer("AIzaSyDxxO6WF75REY9dWy7vzW_yYUVpB5SPKs8"));
                final Vision vision = visionBuilder.build();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Convert photo to byte array
                        InputStream inputStream = null;
                        try {
                            //  inputStream = getContentResolver().openInputStream(data.getData());

                            //byte[] photoData = IOUtils.toByteArray(inputStream);
                            //inputStream.close();


                            final Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()), 1200);
                            //  Add the image
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMainImage.setImageBitmap(bitmap);

                                }
                            });

                            Image base64EncodedImage = new Image();
                            // Convert the bitmap to a JPEG
                            // Just in case it's a format that Android understands but Cloud Vision
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();

                            // Base64 encode the JPEG
                            base64EncodedImage.encodeContent(imageBytes);

                            //  Picasso.with(MainActivity.this).load().fit().centerInside().into(imgPicture);
                            // Image inputImage = new Image();
                            // inputImage.encodeContent(photoData);
                            Feature desiredFeature = new Feature();
                            desiredFeature.setType("TEXT_DETECTION");
                            AnnotateImageRequest request = new AnnotateImageRequest();
                            request.setImage(base64EncodedImage);
                            request.setFeatures(Arrays.asList(desiredFeature));
                            BatchAnnotateImagesRequest batchRequest =
                                    new BatchAnnotateImagesRequest();

                            batchRequest.setRequests(Arrays.asList(request));
                            Log.d("MainActivity", "run: " + batchRequest);
                            BatchAnnotateImagesResponse batchResponse =
                                    vision.images().annotate(batchRequest).execute();


                            final TextAnnotation text = batchResponse.getResponses()
                                    .get(0).getFullTextAnnotation();
                            Log.d("MainActivity", "run: " + text.getText());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mImageDetails.setText(text.getText());
                                    mImageDetails.setMovementMethod(new ScrollingMovementMethod());
                                    Toast.makeText(getApplicationContext(),
                                            text.getText(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        // More code here
                    }
                });
            }

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            //Do something
            Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i,GALLERY_INTENT);
        }
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            //Do something
            if(detection=="TEXT DETECTION"){
                Intent i=new Intent(MainActivity.this,RealTimeOcr.class);
                startActivity(i);
            }
            else
            {
                Intent intent=new Intent(MainActivity.this,ObjectDetection.class);
                startActivity(intent);
            }
        }
        if((keyCode == KeyEvent.KEYCODE_BACK))
        {
            int i=spinner.getSelectedItemPosition();
            Log.d(TAG, "onBackPressed: "+i);
            if(i==0) {
                Log.d(TAG, "onBackPressed: ");
                spinner.setSelection(1);
            }
            else {
                Log.d(TAG, "onBackPressed: ");
                spinner.setSelection(0);
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        int i=spinner.getSelectedItemPosition();
        Log.d(TAG, "onBackPressed: "+i);
        if(i==0) {
            Log.d(TAG, "onBackPressed: ");
            spinner.setSelection(1);
        }
        else {
            Log.d(TAG, "onBackPressed: ");
            spinner.setSelection(0);
        }
    }
}