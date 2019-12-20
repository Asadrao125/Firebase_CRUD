package com.example.firebasecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText edtArtistName;
    Spinner spinnerGenres;
    Button btnAddArtist;
    Toast toast;
    Button btnViewArtist;

    DatabaseReference databaseArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");

        edtArtistName = findViewById(R.id.edtArtistName);
        spinnerGenres = findViewById(R.id.spinnerGenres);
        btnAddArtist = findViewById(R.id.btnAddArtist);
        btnViewArtist = findViewById(R.id.btnViewArtist);

        /*View Data From Firebase Starts Here*/
        btnViewArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Main2Activity.class));
            }
        });
        /*View Data From Firebase Ends Here*/

        /*Adding Data To Firebase Starts Here*/
        btnAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });
        /*Adding Data To Firebase Ends Here*/
    }
    //Adding Data To Firebase
    private void addArtist() {
        String name = edtArtistName.getText().toString().trim();
        String genre = spinnerGenres.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name)) {
            String id = databaseArtists.push().getKey();
            Artist artist = new Artist(id, name, genre);

            databaseArtists.child(id).setValue(artist);
            toast = Toast.makeText(this, "Artist Added!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();

            edtArtistName.setText("");
        } else {
            edtArtistName.setError("You Must Enter Something!");
            edtArtistName.requestFocus();
        }
    }
    //Adding Data Ends Here
}