package com.example.serge.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final String FILENAME = "te.out";
    private static final int REQUEST_CODE_ADD = 0;
    private static final int REQUEST_CODE_SHOW = 1;
    private EditText search_edit_text;
    private PatientAdapter adapter;
    private ArrayList<Patient> arrayPatients;
    private ArrayList<Patient> copyarrayPatients;
    static Context context;
    private Intent showing;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        arrayPatients = new ArrayList<>();
        copyarrayPatients = new ArrayList<>();
        createInterface();
        loadPatients();
        setImages();
        initializeArray();
        searchListening();
    }

    private void setImages()
    {
        for (Patient patient : arrayPatients)
            if (patient.getBytes() == null) patient.setBytes(getBytesFromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)));
    }

    private void initializeArray() {
        copyarrayPatients.addAll(arrayPatients);
        addPatients(listView);
        adapter.notifyDataSetChanged();
    }


    private void searchListening() {
        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(search_edit_text.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK) {
            Patient patient = (Patient) data.getSerializableExtra("Patient");
            Date date = (Date) data.getSerializableExtra("Registration");
            long ID = data.getLongExtra("ID", 0);
            patient.setRegistration(date.getTime());
            patient.setSERIALIZE_ID(ID);
            patient.setBytes(getBytesFromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)));
            arrayPatients.add(patient);
            if(adapter.copy != null)
                adapter.copy.add(patient);
            copyarrayPatients.add(patient);
            adapter.notifyDataSetChanged();
            savePatients();
        }
        if (requestCode == REQUEST_CODE_SHOW && resultCode == RESULT_OK)
        {
            Patient patient = (Patient) data.getSerializableExtra("patient");

            if (patient.getSERIALIZE_ID() == 0) {
                arrayPatients.remove(patient.getIndex());
                copyarrayPatients.remove(patient.getIndex());
            }
            else{
                for (Patient was_patient : arrayPatients)
                {
                    if(was_patient.getSERIALIZE_ID() == patient.getSERIALIZE_ID())
                    {
                        was_patient.setBytes(patient.getBytes());
                        was_patient.setFirstname(patient.getFirstname());
                        was_patient.setLastname(patient.getLastname());
                        was_patient.setPatronymic(patient.getPatronymic());
                        was_patient.setBirth(patient.getBirth());
                        was_patient.setNumber1(patient.getNumber1());
                        was_patient.setNumber2(patient.getNumber2());
                        was_patient.setNumber3(patient.getNumber3());
                        was_patient.setAddress(patient.getAddress());
                        was_patient.setNextVisits(patient.getNextVisits());
                        was_patient.setWasVisits(patient.getWasVisits());
                        was_patient.setDescriptions(patient.getDescriptions());
                        was_patient.setLast_visit(patient.getLastVisit());
                    }
                }
            }

            adapter.copy = new ArrayList<>();
            for (int i = 0; i < arrayPatients.size(); i++)
                adapter.copy.add(i,arrayPatients.get(i));
            adapter.notifyDataSetChanged();
            savePatients();
        }
        else if (requestCode == REQUEST_CODE_SHOW && resultCode == RESULT_CANCELED)
        {
            if(data == null) return;
            Patient patient = (Patient) data.getSerializableExtra("patient");

            for (Patient was_patient : arrayPatients)
            {
                if (was_patient.getSERIALIZE_ID() == patient.getSERIALIZE_ID())
                {
                    was_patient.setBytes(patient.getBytes());
                    was_patient.setFirstname(patient.getFirstname());
                    was_patient.setLastname(patient.getLastname());
                    was_patient.setPatronymic(patient.getPatronymic());
                    was_patient.setBirth(patient.getBirth());
                    was_patient.setNumber1(patient.getNumber1());
                    was_patient.setNumber2(patient.getNumber2());
                    was_patient.setNumber3(patient.getNumber3());
                    was_patient.setAddress(patient.getAddress());
                    was_patient.setNextVisits(patient.getNextVisits());
                    was_patient.setWasVisits(patient.getWasVisits());
                    was_patient.setDescriptions(patient.getDescriptions());
                    was_patient.setLast_visit(patient.getLastVisit());
                }
            }
            adapter.copy = new ArrayList<>();
            for (int i = 0; i < arrayPatients.size(); i++)
                adapter.copy.add(i,arrayPatients.get(i));
            adapter.notifyDataSetChanged();
            savePatients();
        }
    }

    public Bitmap getBitmapFromBytes(byte[] bitmapdata)
    {
        return BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    private void createInterface() {
        search_edit_text = (EditText) findViewById(R.id.search_text);
        listView = (ListView) findViewById(R.id.patients_list);
    }

    private void addPatients(ListView listView) {
        adapter = new PatientAdapter(this);
        listView.setAdapter(adapter);
    }

    private void savePatients() {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(arrayPatients);
            os.close();
            fos.close();
        } catch (IOException w) {
            w.printStackTrace();
        }
    }

    private void loadPatients() {
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            while (is.available() != -1)
                arrayPatients = (ArrayList<Patient>) is.readObject();
            is.close();
            fis.close();
        } catch (IOException | ClassNotFoundException w) {
            w.printStackTrace();
        }
    }

    private class PatientAdapter extends ArrayAdapter<Patient> {
        private ArrayList<Patient> copy;
        private ArrayList<Patient> changeCopy;

        PatientAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2, arrayPatients);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            final Patient patient = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item, null);
            }
            assert patient != null;
            ((TextView) convertView.findViewById(R.id.full_name))
                    .setText(patient.getFullName());
            if (patient.getWasVisits().size() > 0) {
                String st = DateUtils.formatDateTime(context,patient.getLastVisit(),DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                ((TextView) convertView.findViewById(R.id.date_last_visit))
                        .setText(st);
            }
            else ((TextView) convertView.findViewById(R.id.date_last_visit))
                    .setText(getResources().getString(R.string.not_visited));

            String st = DateUtils.formatDateTime(context,patient.getRegistration(),DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
            ((TextView) convertView.findViewById(R.id.registration_date))
                    .setText("Дата регистрации : " + st);

            ((ImageView) convertView.findViewById(R.id.image_id))
                    .setImageBitmap(getBitmapFromBytes(patient.getBytes()));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search_edit_text.setText("");
                    arrayPatients.removeAll(copyarrayPatients);
                    arrayPatients.addAll(copyarrayPatients);
                    showing = new Intent(getContext(), PatientInfoActivity.class);
                    patient.setIndex(position);
                    showing.putExtra("patient", patient);
                    startActivityForResult(showing, REQUEST_CODE_SHOW);
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

        void filter(String charText) {
            if (copy == null) {
                copy = new ArrayList<>();
                copy.addAll(arrayPatients);
            }
            if (changeCopy == null) {
                changeCopy = new ArrayList<>();
            }
            charText = charText.toLowerCase(Locale.getDefault());
            arrayPatients.removeAll(copy);
            notifyDataSetChanged();
            if (charText.length() != 0) {
                for (int i = 0; i < copy.size(); i++) {
                    if (copy.get(i).getFullName().toLowerCase(Locale.getDefault()).contains(charText))
                        changeCopy.add(copy.get(i));
                }
                arrayPatients.addAll(changeCopy);
            } else {
                arrayPatients.addAll(copy);
            }
            changeCopy = null;
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.adding:
                Intent adding = new Intent(MainActivity.this, PatientAddingActivity.class);
                search_edit_text.setText("");
                arrayPatients.removeAll(copyarrayPatients);
                arrayPatients.addAll(copyarrayPatients);
                startActivityForResult(adding, REQUEST_CODE_ADD);
                adapter.notifyDataSetChanged();
                break;
            case R.id.sort_by_alphabet:
                sortByAlphabet();
                break;
            case R.id.sort_by_registration_date:
                sortByRegistrationDate();
                break;
            case R.id.sort_by_visited_date:
                sortByVisitedDate();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortByVisitedDate() {
        Comparator<Patient> comparator = new VisitedDateComparator();
        Collections.sort(arrayPatients, comparator);
        adapter.notifyDataSetChanged();
    }

    private void sortByRegistrationDate() {
        Comparator<Patient> comparator = new RegistrationDateComparator();
        Collections.sort(arrayPatients, comparator);
        adapter.notifyDataSetChanged();
    }

    private void sortByAlphabet() {
        Comparator<Patient> comparator = new AlphabetComparator();
        Collections.sort(arrayPatients, comparator);
        adapter.notifyDataSetChanged();
    }
}