package com.example.serge.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PatientInfoActivity extends AppCompatActivity
{
    private TextView firstName;
    private TextView lastName;
    private TextView patronymic;
    private TextView birth;
    private TextView number1;
    private TextView number2;
    private TextView number3;
    private TextView address;
    private TextView text_view1;
    private TextView text_view2;

    private EditText firstName_edit;
    private EditText lastName_edit;
    private EditText patronymic_edit;
    private EditText birth_edit;
    private EditText number1_edit;
    private EditText number2_edit;
    private EditText number3_edit;
    private EditText address_edit;
    private ListView listView1;
    private ListView listView2;

    private EditText userInput;

    private ImageView imageView;
    private Button save_button;
    private Button choose_image;


    private Intent mainIntent;

    private Patient patient;
    private ArrayList<Patient> arrayPatients;

    private ArrayList<Date> next_visits;
    private ArrayAdapter<Date> next_visits_adapter;

    private ArrayList<Date> was_visits;
    private ArrayAdapter<Date> was_visits_adapter;

    private ArrayList<String> descriptions;

    private static final int GALLERY_REQUEST = 0;

    private static final String FILENAME = "te.out";

    private Bitmap DEFAULT_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        createInterface();
        loadPatients();
        setIfSomethingHas();
        choose_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
            }
        });
        save_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                makePatient();
                mainIntent.putExtra("patient",patient);
                setResult(RESULT_OK,mainIntent);
                finish();
            }
        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                addDescription(position);
                mainIntent.putExtra("patient",patient);
                setResult(RESULT_CANCELED,mainIntent);
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                showDescription(position);
            }
        });
    }

    private void savePatients() {
        try {
            FileOutputStream fos = MainActivity.context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
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
            FileInputStream fis = MainActivity.context.openFileInput(FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            while (is.available() != -1)
                arrayPatients = (ArrayList<Patient>) is.readObject();
            is.close();
            fis.close();
        } catch (IOException | ClassNotFoundException w) {
            w.printStackTrace();
        }
    }

    private void showDescription(final int position)
    {
        LayoutInflater li = LayoutInflater.from(PatientInfoActivity.this);
        View promptsView = li.inflate(R.layout.prompt3, null);

        //Создаем AlertDialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PatientInfoActivity.this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(true);

        System.out.println("Date : " + patient.getWasVisits().get(position));

        ((TextView) promptsView.findViewById(R.id.description_date)).setText((new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)).format(patient.getWasVisits().get(position)));
        final TextView description = (TextView) promptsView.findViewById(R.id.was_description);


        String st = (new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)).format(patient.getWasVisits().get(position));
        System.out.println(st);
        description.setText(patient.getDescriptions().get(position));

        alertDialog.setPositiveButton("Удалить дату",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        patient.removeDescription(position);
                        patient.removeWasVisits(position);
                        if(patient.getWasVisits().size() > 0)
                            patient.setLast_visit(patient.getWasVisits().get(0).getTime());

                        descriptions.remove(position);
                        was_visits.remove(position);

                        mainIntent.putExtra("patient",patient);
                        setResult(RESULT_CANCELED,mainIntent);

                        was_visits_adapter.notifyDataSetChanged();

                        for (Patient was_patient : arrayPatients)
                        {
                            if (was_patient.getSERIALIZE_ID() == patient.getSERIALIZE_ID())
                            {
                                was_patient.setDescriptions(patient.getDescriptions());
                                was_patient.setWasVisits(patient.getWasVisits());
                                if(patient.getWasVisits().size() > 0) was_patient.setLast_visit(patient.getWasVisits().get(0).getTime());
                            }
                        }
                        savePatients();
                    }
                });

        alertDialog.setNegativeButton("Вернуться",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });

        AlertDialog nalertDialog = alertDialog.create();

        //и отображаем его:
        nalertDialog.show();
    }

    private void addDescription(final int position) {
        LayoutInflater li = LayoutInflater.from(PatientInfoActivity.this);
        View promptsView = li.inflate(R.layout.prompt2, null);

        //Создаем AlertDialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PatientInfoActivity.this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(true);

        ((TextView) promptsView.findViewById(R.id.description_text)).setText((new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)).format(patient.getNextVisits().get(position)));

        final EditText input = (EditText) promptsView.findViewById(R.id.description_edit_text);

        alertDialog.setPositiveButton("Сохранить",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String description = input.getText().toString();

                        patient.addWasVisitDate(patient.getNextVisits().get(position));
                        was_visits.add(0,patient.getNextVisits().get(position));
                        patient.setLast_visit(patient.getWasVisits().get(0).getTime());

                        patient.removeNextVisitDate(patient.getNextVisits().get(position));
                        next_visits.remove(position);

                        patient.addDescription(description);
                        descriptions.add(description);
                        next_visits_adapter.notifyDataSetChanged();
                        was_visits_adapter.notifyDataSetChanged();
                        mainIntent.putExtra("patient",patient);
                        setResult(RESULT_CANCELED,mainIntent);

                        for (Patient was_patient : arrayPatients)
                        {
                            if (was_patient.getSERIALIZE_ID() == patient.getSERIALIZE_ID())
                            {
                                was_patient.setDescriptions(patient.getDescriptions());
                                was_patient.setWasVisits(patient.getWasVisits());
                                was_patient.setNextVisits(patient.getNextVisits());
                                if(patient.getWasVisits().size() > 0) was_patient.setLast_visit(patient.getWasVisits().get(0).getTime());
                            }
                        }
                        savePatients();
                    }
                });

        alertDialog.setNegativeButton("Отмена",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog nalertDialog = alertDialog.create();

        //и отображаем его:
        nalertDialog.show();
    }

    private void makePatient()
    {
        if (!firstName_edit.getText().toString().equals("")) patient.setFirstname(firstName_edit.getText().toString());
        if (!lastName_edit.getText().toString().equals("")) patient.setLastname(lastName_edit.getText().toString());
        if (!patronymic_edit.getText().toString().equals("")) patient.setPatronymic(patronymic_edit.getText().toString());
        if (!birth_edit.getText().toString().equals("")) patient.setBirth(birth_edit.getText().toString());
        if (!number1_edit.getText().toString().equals("")) patient.setNumber1(number1_edit.getText().toString());
        if (!number2_edit.getText().toString().equals("")) patient.setNumber2(number2_edit.getText().toString());
        if (!number3_edit.getText().toString().equals("")) patient.setNumber3(number3_edit.getText().toString());
        if (!address_edit.getText().toString().equals("")) patient.setAddress(address_edit.getText().toString());
        if (was_visits != null) patient.setWasVisits(was_visits);
        if (next_visits != null) patient.setNextVisits(next_visits);
        if (descriptions != null) patient.setDescriptions(descriptions);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (DEFAULT_IMAGE != drawable.getBitmap()) patient.setBytes(getBytesFromBitmap(imageView.getDrawingCache()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;

        switch(requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bitmap);
                    imageView.buildDrawingCache();
                }
        }
    }

    private void setIfSomethingHas()
    {
        if(patient.hasFirstName()) firstName.setText(patient.getFirstname());
        if(patient.hasLastName()) lastName.setText(patient.getLastname());
        if(patient.hasPatronymic()) patronymic.setText(patient.getPatronymic());
        if(patient.hasBirth()) birth.setText(patient.getBirth());
        if(patient.hasNumber1()) number1.setText(patient.getNumber1());
        if(patient.hasNumber2()) number2.setText(patient.getNumber2());
        if(patient.hasNumber3()) number3.setText(patient.getNumber3());
        if(patient.hasAddress()) address.setText(patient.getAddress());
        if(patient.hasNextVisits()) next_visits.addAll(patient.getNextVisits());
        if(patient.hasWasVisits()) was_visits.addAll(patient.getWasVisits());
        if(patient.hasDescriptions()) descriptions.addAll(patient.getDescriptions());

        imageView.setImageBitmap(getBitmapFromBytes(patient.getBytes()));

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        DEFAULT_IMAGE = drawable.getBitmap();
    }

    public Bitmap getBitmapFromBytes(byte[] bitmapdata)
    {
        return BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void createInterface()
    {
        firstName = (TextView) findViewById(R.id.name_info);
        lastName = (TextView) findViewById(R.id.surname_info);
        patronymic = (TextView) findViewById(R.id.patronymic_info);
        birth = (TextView) findViewById(R.id.birth_info);
        number1 = (TextView) findViewById(R.id.number1_info);
        number2 = (TextView) findViewById(R.id.number2_info);
        number3 = (TextView) findViewById(R.id.number3_info);
        address = (TextView) findViewById(R.id.address_info);

        firstName_edit = (EditText) findViewById(R.id.name_info_edit);
        lastName_edit = (EditText) findViewById(R.id.surname_info_edit);
        patronymic_edit = (EditText) findViewById(R.id.patronymic_info_edit);
        birth_edit = (EditText) findViewById(R.id.birth_info_edit);
        number1_edit = (EditText) findViewById(R.id.number1_info_edit);
        number2_edit = (EditText) findViewById(R.id.number2_info_edit);
        number3_edit = (EditText) findViewById(R.id.number3_info_edit);
        address_edit = (EditText) findViewById(R.id.address_info_edit);
        text_view1 = (TextView) findViewById(R.id.next_visit_date_text);
        text_view2 = (TextView) findViewById(R.id.was_visit_date_text);

        imageView = (ImageView) findViewById(R.id.imageView_info);
        save_button = (Button) findViewById(R.id.save_button);
        choose_image = (Button) findViewById(R.id.choose_image);

        mainIntent = new Intent();
        next_visits = new ArrayList<>();
        was_visits = new ArrayList<>();
        descriptions = new ArrayList<>();
        listView1 = (ListView) findViewById(R.id.next_visits);
        listView2 = (ListView) findViewById(R.id.was_visits);
        next_visits_adapter = new NextVisitAdapter();
        was_visits_adapter = new WasVisitAdapter();
        listView1.setAdapter(next_visits_adapter);
        listView2.setAdapter(was_visits_adapter);


        patient = (Patient) getIntent().getSerializableExtra("patient");
        arrayPatients = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.redactor_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.redactor :

                if(!item.isChecked())
                {
                    item.setChecked(true);
                    editInterface();
                }
                else
                {
                    item.setChecked(false);
                    textInterface();
                    checkImage();
                }
                break;

            case R.id.add_date :
                addDate();
                break;

            case R.id.remove_patient :

                removePatient();
                break;
            default:break;
        }



        return super.onOptionsItemSelected(item);
    }

    private void removePatient()
    {
        System.out.println(patient.getSERIALIZE_ID());
        patient.setSERIALIZE_ID(0);
        System.out.println(patient.getSERIALIZE_ID());
        mainIntent.putExtra("patient",patient);
        setResult(RESULT_OK,mainIntent);
        finish();
    }

    private void addDate()
    {
        System.out.println("Add date");
        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(PatientInfoActivity.this);
        View promptsView = li.inflate(R.layout.prompt, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(PatientInfoActivity.this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        userInput = (EditText) promptsView.findViewById(R.id.add_date_edit_view);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                String text = userInput.getText().toString();
                                if (!text.equals("")) {
                                    DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                                    try {
                                        Date result =  df.parse(text);
                                        if (result.getDate() >= 1 && result.getDate() <= 31 && result.getMonth() >= 0 && result.getMonth() <= 11 && result.getYear() >= 117 && result.getYear() < 300)
                                        {
                                            patient.addNextVisitDate(result);
                                            next_visits.add(result);
                                            mainIntent.putExtra("patient",patient);
                                            setResult(RESULT_CANCELED,mainIntent);
                                            next_visits_adapter.notifyDataSetChanged();
                                            Toast.makeText(PatientInfoActivity.this,"Дата добавлена в список",Toast.LENGTH_SHORT).show();
                                            for (Patient was_patient : arrayPatients)
                                            {
                                                if (was_patient.getSERIALIZE_ID() == patient.getSERIALIZE_ID())
                                                    was_patient.setNextVisits(patient.getNextVisits());

                                            }
                                            savePatients();
                                        }
                                        else Toast.makeText(PatientInfoActivity.this,"Дата не соответствует нужным параметрам",Toast.LENGTH_SHORT).show();
                                    } catch (ParseException e) {
                                        Toast.makeText(PatientInfoActivity.this,"Введён не правильный формат даты",Toast.LENGTH_SHORT).show();
                                    }
                                } else Toast.makeText(PatientInfoActivity.this,"Сохранять нечего",Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();

        //и отображаем его:
        alertDialog.show();
    }



    private void checkImage()
    {
        if (imageView.getDrawingCache() != DEFAULT_IMAGE) imageView.setImageBitmap(DEFAULT_IMAGE);
    }

    private void editInterface()
    {
        firstName.setVisibility(View.INVISIBLE);
        lastName.setVisibility(View.INVISIBLE);
        patronymic.setVisibility(View.INVISIBLE);
        birth.setVisibility(View.INVISIBLE);
        number1.setVisibility(View.INVISIBLE);
        number2.setVisibility(View.INVISIBLE);
        number3.setVisibility(View.INVISIBLE);
        address.setVisibility(View.INVISIBLE);
        listView1.setVisibility(View.INVISIBLE);
        listView2.setVisibility(View.INVISIBLE);
        text_view1.setVisibility(View.INVISIBLE);
        text_view2.setVisibility(View.INVISIBLE);

        firstName_edit.setVisibility(View.VISIBLE);
        lastName_edit.setVisibility(View.VISIBLE);
        patronymic_edit.setVisibility(View.VISIBLE);
        birth_edit.setVisibility(View.VISIBLE);
        number1_edit.setVisibility(View.VISIBLE);
        number2_edit.setVisibility(View.VISIBLE);
        number3_edit.setVisibility(View.VISIBLE);
        address_edit.setVisibility(View.VISIBLE);

        save_button.setVisibility(View.VISIBLE);
        choose_image.setVisibility(View.VISIBLE);

        if (!firstName.getText().toString().equals("") && !firstName.getText().toString().equals("Имя")) firstName_edit.setText(firstName.getText().toString());
        if (!lastName.getText().toString().equals("") && !lastName.getText().toString().equals("Фамилия")) lastName_edit.setText(lastName.getText().toString());
        if (!patronymic.getText().toString().equals("") && !patronymic.getText().toString().equals("Отчество")) patronymic_edit.setText(patronymic.getText().toString());
        if (!birth.getText().toString().equals("") && !birth.getText().toString().equals("Дата рождения")) birth_edit.setText(birth.getText().toString());
        if (!number1.getText().toString().equals("") && !number1.getText().toString().equals("Номер телефона")) number1_edit.setText(number1.getText().toString());
        if (!number2.getText().toString().equals("") && !number2.getText().toString().equals("Номер телефона")) number2_edit.setText(number2.getText().toString());
        if (!number3.getText().toString().equals("") && !number3.getText().toString().equals("Номер телефона")) number3_edit.setText(number3.getText().toString());
        if (!address.getText().toString().equals("") && !address.getText().toString().equals("Адрес")) address_edit.setText(address.getText().toString());
    }

    private void textInterface()
    {
        firstName.setVisibility(View.VISIBLE);
        lastName.setVisibility(View.VISIBLE);
        patronymic.setVisibility(View.VISIBLE);
        birth.setVisibility(View.VISIBLE);
        number1.setVisibility(View.VISIBLE);
        number2.setVisibility(View.VISIBLE);
        number3.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        listView1.setVisibility(View.VISIBLE);
        listView2.setVisibility(View.VISIBLE);
        text_view1.setVisibility(View.VISIBLE);
        text_view2.setVisibility(View.VISIBLE);

        firstName_edit.setVisibility(View.INVISIBLE);
        lastName_edit.setVisibility(View.INVISIBLE);
        patronymic_edit.setVisibility(View.INVISIBLE);
        birth_edit.setVisibility(View.INVISIBLE);
        number1_edit.setVisibility(View.INVISIBLE);
        number2_edit.setVisibility(View.INVISIBLE);
        number3_edit.setVisibility(View.INVISIBLE);
        address_edit.setVisibility(View.INVISIBLE);

        save_button.setVisibility(View.INVISIBLE);
        choose_image.setVisibility(View.INVISIBLE);

        firstName_edit.setText("");
        lastName_edit.setText("");
        patronymic_edit.setText("");
        birth_edit.setText("");
        number1_edit.setText("");
        number2_edit.setText("");
        number3_edit.setText("");
        address_edit.setText("");
    }

    class NextVisitAdapter extends ArrayAdapter<Date>
    {
        NextVisitAdapter()
        {
            super(PatientInfoActivity.this,android.R.layout.simple_list_item_2,next_visits);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            final Date date = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item3, null);
            }

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            String converted_date = df.format(date);

            ((TextView) convertView.findViewById(R.id.next_visit_date_text)).setText(converted_date);

            return convertView;
        }

    }

    class WasVisitAdapter extends ArrayAdapter<Date>
    {
        WasVisitAdapter()
        {
            super(PatientInfoActivity.this,android.R.layout.simple_list_item_2,was_visits);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            final Date date = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item2, null);
            }

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            String converted_date = df.format(date);

            ((TextView) convertView.findViewById(R.id.description)).setText(converted_date);

            return convertView;
        }

    }
}