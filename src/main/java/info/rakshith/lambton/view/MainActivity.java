package info.rakshith.lambton.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import info.androidhive.sqlite.R;
import info.rakshith.lambton.database.DatabaseHelper;
import info.rakshith.lambton.database.model.Contact;
import info.rakshith.lambton.utils.MyDividerItemDecoration;
import info.rakshith.lambton.utils.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {
    private NotesAdapter mAdapter;
    private List<Contact> contactsList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;
    EditText etSearch;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);
        etSearch=(EditText)findViewById(R.id.etSearch);

        db = new DatabaseHelper(this);

        contactsList.addAll(db.getAllContacts());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        mAdapter = new NotesAdapter(this, contactsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,             int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(s.toString());
                System.out.println(count);
                if(count>0){
                   contactsList.clear();
                   contactsList.addAll(db.getSearchedContacts(s.toString()));
                   mAdapter.notifyDataSetChanged();
                }else{
                    contactsList.clear();
                    contactsList.addAll(db.getAllContacts());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(Contact contact) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertContact(contact);

        // get the newly inserted note from db
        Contact n = db.getContact(id);

        if (n != null) {
            // adding new note to array list at 0 position
            contactsList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(Contact contact, int position) {
        Contact n = contactsList.get(position);
        // updating note text
      //  n.setNote(note);

        // updating note in db
        n.setAddress(contact.getAddress());
        n.setPhoneNumber(contact.getPhoneNumber());
        n.setEmail(contact.getEmail());
        n.setFirstName(contact.getFirstName());
        n.setLastName(contact.getLastName());
        db.updateContact(n);

        // refreshing the list
        contactsList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteContact(contactsList.get(position));

        // removing the note from the list
        contactsList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, contactsList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }


    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Contact contact, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        System.out.println("shouldUpdate====>"+shouldUpdate);

         final EditText firstName = view.findViewById(R.id.note4);

         final EditText lastName = view.findViewById(R.id.note1);

         final EditText email = view.findViewById(R.id.note6);

         final EditText phoneNumber = view.findViewById(R.id.note7);

         final EditText address = view.findViewById(R.id.note8);

         TextView dialogTitle = view.findViewById(R.id.dialog_title);
         dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate) {
            firstName.setText(contact.getFirstName());
            lastName.setText(contact.getLastName());
            email.setText(contact.getEmail());
            phoneNumber.setText(contact.getPhoneNumber());
            address.setText(contact.getAddress());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                System.out.println("firstName.getText().toString()==>"+firstName.getText().toString());
                if (TextUtils.isEmpty(firstName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Contact!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate) {

                    Contact contact1 = new Contact();

                    contact1.setFirstName(firstName.getText().toString());
                    contact1.setLastName(lastName.getText().toString());
                    contact1.setEmail(email.getText().toString());
                    contact1.setPhoneNumber(phoneNumber.getText().toString());
                    contact1.setAddress(address.getText().toString());


                    // update note by it's id
                    updateNote(contact1, position);
                } else {
                    System.out.println("inside  saving...................");
                    Contact contact1 = new Contact();
                    System.out.println("firstName.getText().toString()===>"+firstName.getText().toString());
                    contact1.setFirstName(firstName.getText().toString());
                    contact1.setLastName(lastName.getText().toString());
                    contact1.setEmail(email.getText().toString());
                    contact1.setPhoneNumber(phoneNumber.getText().toString());
                    contact1.setAddress(address.getText().toString());
                    // create new note
                    createNote(contact1);
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getContactsCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}
