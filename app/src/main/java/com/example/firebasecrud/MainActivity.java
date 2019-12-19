package com.example.firebasecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
    List<Artist> artistList;
    ListView listViewArtists;
    Toast toast;

    DatabaseReference databaseArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");

        edtArtistName = findViewById(R.id.edtArtistName);
        spinnerGenres = findViewById(R.id.spinnerGenres);
        btnAddArtist = findViewById(R.id.btnAddArtist);

        /*Retrieving Data Part*/
        listViewArtists = findViewById(R.id.listViewArtists);
        artistList = new ArrayList<>();
        /*Retrieving Data Part*/

        btnAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });
        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Artist artist = artistList.get(position);
                showUpdateDialog(artist.getArtistID(), artist.getArtistName());

                return false;
            }
        });
    }

    /**************************************************************************************************/
    //Adding Data To Firebase
    private void addArtist() {
        String name = edtArtistName.getText().toString().trim();
        String genre = spinnerGenres.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name)) {
            String id = databaseArtists.push().getKey();
            Artist artist = new Artist(id, name, genre);

            databaseArtists.child(id).setValue(artist);
            Toast.makeText(this, "Artist Added!", Toast.LENGTH_SHORT).show();
        } else {
            edtArtistName.setError("You Must Enter Something!");
            edtArtistName.requestFocus();
        }
    }
    //Adding Data Ends Here

    /**************************************************************************************************/
    //Retrieving Firebase Data Here
    @Override
    protected void onStart() {
        super.onStart();
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                artistList.clear();
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    artistList.add(artist);
                }
                ArtistList adapter = new ArtistList(MainActivity.this, artistList);
                listViewArtists.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //Data Retrieving Ends Here

    /**********************************************************************************************/
    //Data Update Here
    private void showUpdateDialog(final String artistID, String artistName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = dialogView.findViewById(R.id.btnUpdate);
        final Spinner spinnerGenres = dialogView.findViewById(R.id.spinnerGenres);

        dialogBuilder.setTitle("Updating Artist " + artistName);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenres.getSelectedItem().toString();

                if (TextUtils.isEmpty(name))
                {
                    editTextName.setError("Name Required!");
                    return;
                }
                else
                {
                    updateArtist(artistID, name, genre);
                    alertDialog.dismiss();
                }
            }
        });

    }
    private boolean updateArtist(String id, String name, String genre)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists").child(id);
        Artist artist = new Artist(id,name,genre);
        databaseReference.setValue(artist);
        toast = Toast.makeText(this, "Artist Updated As " +name, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        return true;
    }
}
