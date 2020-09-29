package com.example.annual_report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class viewpdf extends AppCompatActivity {

    ListView mypdflistview;
    DatabaseReference databaseReference;
    List<arsluploadpdf> uploadpdfs;
    private ProgressBar mProgressCircle;
    private ArrayList<String> arrayList = new ArrayList<>();

    private ArrayAdapter<String> adapter;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpdf);

        mypdflistview = findViewById(R.id.mylistview);
        mProgressCircle = findViewById(R.id.progress_circle);
       // module=((Module)getApplicationContext());

        uploadpdfs = new ArrayList<>();
        //adapter = new ArrayAdapter(viewpdf.this, mUploads);

        adapter = new ArrayAdapter(viewpdf.this, android.R.layout.simple_list_item_1, uploadpdfs);
        mypdflistview.setAdapter(adapter);

        viewAllFIlews();

        mypdflistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                arsluploadpdf arsluploadpdf = uploadpdfs.get(i);

                Intent intent = new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(arsluploadpdf.getUrl()));
                startActivity(intent);
            }
        });

        mypdflistview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                {
                    databaseReference.child("Students").child(String.valueOf((mypdflistview))).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //databaseReference = FirebaseDatabase.getInstance().getReference("Students");
                            databaseReference.child(String.valueOf(mypdflistview)).removeValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Toast.makeText(viewpdf.this,"Student is deleted",Toast.LENGTH_LONG).show();
                    //Intent intphto =new Intent(getApplicationContext(),viewpdf.class);
                    //startActivity(intphto);
                }
                return false;
            }
        });
    }

//        mypdflistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                String name = adapter.getItem(i);
//                adapter.remove(name);
//                adapter.notifyDataSetChanged();
//
//                return false;
//            }
//        });


//        mypdflistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                final int which_item = i;
//
//                new AlertDialog.Builder(viewpdf.this)
//                      //  .setIcon(android.R.drawable.ic_delete)
//                        .setTitle("Are you sure?")
//                        .setMessage("Do you want to delete this item")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                adapter.remove(which_item);
//                                //adapter.notifyDataSetChanged();
//                            }
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
//
//                return true;
//            }
//        });


    private void viewAllFIlews() {

        databaseReference = FirebaseDatabase.getInstance().getReference("uploads/pdfs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    arsluploadpdf arsluploadpdf = postSnapshot.getValue(com.example.annual_report.arsluploadpdf.class);
                    uploadpdfs.add(arsluploadpdf);

                }

                String[] uploads = new String[uploadpdfs.size()];

                for (int i = 0; i < uploads.length; i++) {
                    uploads[i] = uploadpdfs.get(i).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, uploads) {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        View view = super.getView(position, convertView, parent);

                        TextView mytext = (TextView) view.findViewById(android.R.id.text1);
                        mytext.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                mypdflistview.setAdapter(adapter);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}