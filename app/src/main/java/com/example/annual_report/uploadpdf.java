package com.example.annual_report;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class uploadpdf extends AppCompatActivity {

    EditText editpdfname;
    Button btnupload;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String userEmailNode;

//    private CardView addPdf;
//    private EditText pdftitle;
//    private Button uploadpdfbtn;
//    private TextView pdftextview;
//    private String pdfname,title;
//    public String name;
//    public String url;
//
//    private final int REQ = 1;
//    private Uri pdfData;
//    private DatabaseReference databaseReference;
//    private StorageReference storageReference;
//    String downloadUrl="";
//    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpdf);

        editpdfname = (EditText)findViewById(R.id.pdfname);
        btnupload = (Button) findViewById(R.id.btnupload);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("students");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        int at_index = firebaseUser.getEmail().indexOf('@');
        userEmailNode = firebaseUser.getEmail().substring(0,at_index);

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPDFFiles();
            }
        });



//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        storageReference = FirebaseStorage.getInstance().getReference();
//
//        pd = new ProgressDialog(this);
//
//        addPdf = findViewById(R.id.addpdf);
//        pdftitle = findViewById(R.id.pdftitle);
//        uploadpdfbtn = findViewById(R.id.uploadPdfBtn);
//        pdftextview = findViewById(R.id.pdftextview);
//
//        addPdf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openGallery();
//            }
//        });
//
//        uploadpdfbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                title = pdftitle.getText().toString();
//                if(title.isEmpty()){
//                    pdftitle.setError("Empty");
//                    pdftitle.requestFocus();
//                }
//                else if(pdfData == null){
//                    Toast.makeText(uploadpdf.this, "Please upload pdf", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    uploadPdf();
//                }
//            }
//        });

    }

    private void selectPDFFiles() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data !=null && data.getData()!= null){
            uploadPDFFile(data.getData());
        }
    }

    private void uploadPDFFile(Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference reference = storageReference.child("uploads/"+System.currentTimeMillis()+".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete());
                        Uri url = uri.getResult();

                        arsluploadpdf arsluploadpdf = new arsluploadpdf(editpdfname.getText().toString(),url.toString());
                        databaseReference.child(userEmailNode).child("pdf").push().setValue(arsluploadpdf);
                        Toast.makeText(uploadpdf.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                double progress = (100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();

                progressDialog.setMessage("Uploaded: "+(int)progress+"%");

            }
        });

    }

    //    private void uploadPdf() {
//        pd.setTitle("Please wait...");
//        pd.setMessage("Uploading pdf");
//        pd.show();
//        StorageReference reference = storageReference.child("pdf/"+ pdfname + "-"+System.currentTimeMillis()+".pdf");
//        reference.putFile(pdfData)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                        while (!uriTask.isComplete());
//                        Uri uri = uriTask.getResult();
//                        uploadData(String.valueOf(uri));
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                pd.dismiss();
//                Toast.makeText(uploadpdf.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void uploadData(String valueOf) {
//        String uniqueKey = databaseReference.child("pdf").push().getKey();
//
//        HashMap data = new HashMap();
//        data.put("pdfTitle",title);
//        data.put("pdfUri",downloadUrl);
//
//        databaseReference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                pd.dismiss();
//                Toast.makeText(uploadpdf.this, "Pdf uploaded successfully", Toast.LENGTH_SHORT).show();
//                pdftitle.setText("");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                pd.dismiss();
//                Toast.makeText(uploadpdf.this, "Failed to upload pdf", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void openGallery() {
//        Intent intent = new Intent();
//        intent.setType("pdf/docs/ppt");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Pdf File"), REQ);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == REQ && resultCode == RESULT_OK){
//            pdfData = data.getData();
//
//            if(pdfData.toString().startsWith("content://")){
//                Cursor cursor = null;
//                try {
//                    cursor = uploadpdf.this.getContentResolver().query(pdfData,null,null,null,null);
//                    if(cursor != null && cursor.moveToFirst()){
//                        pdfname = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            else if(pdfData.toString().startsWith("file://")){
//                pdfname =  new File(pdfData.toString()).getName();
//            }
//            pdftextview.setText(pdfname);
//        }
//    }
//
//    public String getName() {
//        return name;
//    }
//    public String getUrl() {
//        return url;
//    }
//
    public void btn_action(View view) {
        startActivity(new Intent(getApplicationContext(),viewpdf.class));
    }
}