package com.stanleyj.android.travelmantics;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.FirebaseException;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {
    RecyclerView mrecyclerView;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mdatabaseReference;
    LinearLayoutManager linearLayoutManager;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        pd = new ProgressDialog(this);
        linearLayoutManager = new LinearLayoutManager(this);
        mrecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mrecyclerView.setHasFixedSize(true);

//        set layout as LinearLayout
        mrecyclerView.setLayoutManager(linearLayoutManager);

//        send query to firebase
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference("Places");
    }

    @Override
    protected void onStart() {
        pd.setMessage(" Loading Data Please wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        super.onStart();
        FirebaseRecyclerAdapter<DownloadModel, ViewHolder> firebaseRecyclerAdapter3 =
                new FirebaseRecyclerAdapter<DownloadModel, ViewHolder>(
                        DownloadModel.class,
                        R.layout.row,
                        ViewHolder.class,
                        mdatabaseReference
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, DownloadModel model, int position) {
                        if (model.getTitle().equals(null)){
                            Toast.makeText(DealActivity.this, "No Item found", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            return;
                        }
                        try {
                            viewHolder.setDetail(DealActivity.this, model.getTitle(), model.getDescription(),
                                    model.getImage(),model.itemID, model.getPrice());
                            pd.dismiss();
                        } catch (FirebaseException e) {

                            AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                            alertDialog.setTitle("Error Alert");
                            alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                            alertDialog.setMessage("The following Error occurred\n" + e.getMessage());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            pd.dismiss();
                                        }
                                    });
                        }
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                TextView proid = (TextView) view.findViewById(R.id.item);
                                //getting data from views
                                Intent intent = new Intent(view.getContext(), AddnewActivity.class);
                                intent.putExtra("prodid", proid.getText().toString());
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                            }
                        });
                        return viewHolder;
                    }
                };
//        set adapter to recylerview
        mrecyclerView.setAdapter(firebaseRecyclerAdapter3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.deal_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Add_new) {
            startActivity(new Intent(DealActivity.this,AddnewActivity.class));
        }else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent z = new Intent(getApplicationContext(), MainActivity.class);
            z.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(z);
        }
        return super.onOptionsItemSelected(item);
    }

}
