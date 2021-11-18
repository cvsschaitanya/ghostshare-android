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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    private EditText keySlot;
    private Button pickButton;
    Uri file;
    StorageReference stRef;
    DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keySlot = findViewById(R.id.key_slot);
        pickButton = findViewById(R.id.scan_button);

        pickButton.setOnClickListener(v -> {
            MainActivity.this.pickFile();
        });

    }

    private void pickFile(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            file = data.getData();
            sendFile();
        }
    }

    private void sendFile() {
        String key = keySlot.getText().toString();
        stRef = FirebaseStorage.getInstance().getReference().child(key+"/image.png");
        dbRef = FirebaseDatabase.getInstance().getReference().child("keys/" + key);

        stRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        dbRef.setValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("TAG", "onFailure: :(");
                    }
                });
    }
}