package com.example.ghostshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SendActivity extends AppCompatActivity {

    private EditText keySlot;
    private Button pickButton, scanButton;
    private TextView filenameBox;
    Uri file;
    StorageReference stRef;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        filenameBox = findViewById(R.id.filename);
        keySlot = findViewById(R.id.key_slot);
        scanButton = findViewById(R.id.scan_button);
        pickButton = findViewById(R.id.send_button);

        file = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
        filenameBox.setText(file.getLastPathSegment());

        scanButton.setOnClickListener(v->{
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            intentIntegrator.setPrompt("Scan a barcode or QR Code");
//            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.initiateScan();
        });

        pickButton.setOnClickListener(v -> {
            SendActivity.this.sendFile();
        });


    }

    private void sendFile() {
        String key = keySlot.getText().toString();
        stRef = FirebaseStorage.getInstance().getReference().child(key+"/"+file.getLastPathSegment());
        dbRef = FirebaseDatabase.getInstance().getReference().child("keys/" + key);

        stRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        dbRef.setValue(new DatabaseHelper(file.getLastPathSegment(), "uploaded"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Log.d("TAG", "onFailure: :(");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                keySlot.setText(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}