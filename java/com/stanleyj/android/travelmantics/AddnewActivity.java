package com.stanleyj.android.travelmantics;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class AddnewActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    Uri imgUri;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseDatabase firebaseDatabase2;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference;
    ImageView imageView1;
    String imgUri1, tag;
    EditText pName, pDesc, price;
    Button upload;
    String pid;
    boolean check;
    private ProgressDialog pd;

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnew);
        pd = new ProgressDialog(this);
        views();
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        //        getting Extras from intent
        pid = getIntent().getStringExtra("prodid");
        check = getIntent().getBooleanExtra("check", false);

        if (check) {
            firebaseDatabase2 = FirebaseDatabase.getInstance();
            databaseReference2 = firebaseDatabase.getReference("Places").child(pid);
            pd.setMessage(" Loading Data Please wait...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String discrp = dataSnapshot.child("description").getValue(String.class);
                    int prize = dataSnapshot.child("price").getValue(int.class);
                    String img1 = dataSnapshot.child("image").getValue(String.class);
                    try {
                        pName.setText(title);
                        pDesc.setText(discrp);
                        price.setText("" + prize);
                        imageView1.setImageBitmap(decodeFromFirebaseBase64(img1));
                        pd.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Places");

    }

    private void openImage() {
        try {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        } catch (Exception ignored) {

        }
    }

    public String encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            imgUri = data.getData();

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            long length = imageInByte.length;
            long spec = 500000;
            if (length > spec) {
                AlertDialog alertDialog = new AlertDialog.Builder(AddnewActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                alertDialog.setMessage("Error Setting image\n" +
                        "Image size is > 500kb");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return;
            }
            imageView1.setImageBitmap(bitmap);

        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(AddnewActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Error Updating\n" + e.getMessage());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void views() {
        pName = (EditText) findViewById(R.id.Add_title);
        pDesc = (EditText) findViewById(R.id.Add_description);
        price = (EditText) findViewById(R.id.Add_price);
        upload = (Button) findViewById(R.id.upload);
        imageView1 = (ImageView) findViewById(R.id.img2);
    }

    private void load() {
        if (TextUtils.isEmpty(pName.getText().toString())) {
            //product name is empty
            Toast.makeText(this, "Please Enter Product Name", Toast.LENGTH_SHORT).show();
            return;

        }

        if (TextUtils.isEmpty(pDesc.getText().toString())) {
            // product description is empty
            Toast.makeText(this, "Please Enter Description", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(price.getText().toString())) {
            // product description is empty
            Toast.makeText(this, "Please Enter Description", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hrs = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        Calendar calendar = new GregorianCalendar(year, month, day, hrs, min, sec);

        tag = "D" + Long.toString(calendar.getTimeInMillis());
        databaseReference = firebaseDatabase.getReference("Places");
//        if (startday == day && startyear == year && startmonth == month) {
        try {

            pd.setMessage("Uploading Data...");
            pd.show();
            pd.setCanceledOnTouchOutside(false);
            Drawable mDrawable1 = imageView1.getDrawable();
            Bitmap mBitmap1 = ((BitmapDrawable) mDrawable1).getBitmap();
            imgUri1 = encodeBitmapAndSaveToFirebase(mBitmap1);
            UploadModel AM = new UploadModel(
                    pName.getText().toString(),
                    imgUri1, pDesc.getText().toString(), tag, Integer.parseInt(price.getText().toString())
            );

            databaseReference.child(tag).setValue(AM).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.dismiss();
                    AlertDialog alertDialog = new AlertDialog.Builder(AddnewActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                    alertDialog.setMessage("Uploading Successful");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    AlertDialog alertDialog = new AlertDialog.Builder(AddnewActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                    alertDialog.setMessage("Error Uploading" + e.getMessage() + "\n" + e.getCause());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });

        } catch (Exception e) {
            pd.dismiss();
            AlertDialog alertDialog = new AlertDialog.Builder(AddnewActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
            alertDialog.setMessage("Error Uploading" + e.getMessage() + "\n" + e.getCause());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }
    private void updates() {
        if (TextUtils.isEmpty(pName.getText().toString())) {
            //product name is empty
            Toast.makeText(this, "Please Enter Product Name", Toast.LENGTH_SHORT).show();
            return;

        }

        if (TextUtils.isEmpty(pDesc.getText().toString())) {
            // product description is empty
            Toast.makeText(this, "Please Enter Description", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(price.getText().toString())) {
            // product description is empty
            Toast.makeText(this, "Please Enter Description", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            pd.setMessage("Updating Data...");
            pd.show();
            pd.setCanceledOnTouchOutside(false);
            Drawable mDrawable1 = imageView1.getDrawable();
            Bitmap mBitmap1 = ((BitmapDrawable) mDrawable1).getBitmap();
            imgUri1 = encodeBitmapAndSaveToFirebase(mBitmap1);
            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("title", pName.getText().toString());
            updates.put("description", pDesc.getText().toString());
            updates.put("image", imgUri1);
            databaseReference2.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                    alertDialog.setTitle("Update " + pid);
                    alertDialog.setMessage("The product  has been rejected");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(AddnewActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                    alertDialog.setMessage("Error Updating" + e.getMessage() + "\n" + e.getCause());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });
        } catch (Exception e) {
            pd.dismiss();
            AlertDialog alertDialog = new AlertDialog.Builder(AddnewActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
            alertDialog.setMessage("Error Uploading" + e.getMessage() + "\n" + e.getCause());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save) {
            if (check) {
                updates();
            } else {
                load();
            }

        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent z = new Intent(getApplicationContext(), MainActivity.class);
            z.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(z);
        } else if (id == R.id.del) {
            if (check) {
                databaseReference2.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                        alertDialog.setTitle(pid + " Deleted");
                        alertDialog.setMessage("Item has been deleted");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                    }
                });
            } else {
                Toast.makeText(AddnewActivity.this, "No item to delete", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

}
