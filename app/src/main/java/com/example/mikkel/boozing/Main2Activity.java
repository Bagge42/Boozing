package com.example.mikkel.boozing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "Ved stadig ikke hvad den her gør";
    public final static String KEY_MESSAGE = "Hallo";
    public final static String FRIENDS_PHONE = "asdasdasf";
    private String ownerKey="";

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(); //mDatabase.getReference("Child");
    DatabaseReference mMembersRef = mRootRef.child("Members");
    ArrayList<String> friends = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        ownerKey = extras.getString(MapsActivity.keyMessage);
        friends = (ArrayList<String>) extras.getSerializable("myList");

        final ListView listview = (ListView) findViewById(R.id.listview);

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, friends);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                friends.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                                mMembersRef.child(ownerKey).child("Friends").child(item).removeValue();
                            }
                        });
            }

        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        Bundle extras = new Bundle();
        EditText editText = (EditText) findViewById(R.id.editText2);
        String message = editText.getText().toString();
        EditText friendsPhone = (EditText) findViewById(R.id.editText4);
        extras.putString(FRIENDS_PHONE, friendsPhone.getText().toString());
        extras.putString(KEY_MESSAGE, ownerKey);
        extras.putString(EXTRA_MESSAGE, message);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
