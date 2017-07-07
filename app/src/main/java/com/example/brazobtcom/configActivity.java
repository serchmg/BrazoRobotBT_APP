package com.example.brazobtcom;

import android.content.Intent;
import android.content.pm.InstrumentationInfo;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class configActivity extends AppCompatActivity implements View.OnClickListener{

    SeekBar seekBarPulgar, seekBarIndice, seekBarMedio, seekBarAnular, seekBarMenique;
    TextView textViewValorPulgar, textViewValorIndice, textViewValorMedio;
    TextView textViewValorAnular, textViewValorMenique;
    EditText etNombre;
    Spinner spinner;
    JSONObject movimiento;
    JSONObject jsonObj;
    JSONArray jsonArray;
    int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        seekBarPulgar = (SeekBar) findViewById(R.id.seekBarPulgar);
        seekBarIndice = (SeekBar) findViewById(R.id.seekBarIndice);
        seekBarMedio = (SeekBar) findViewById(R.id.seekBarMedio);
        seekBarAnular = (SeekBar) findViewById(R.id.seekBarAnular);
        seekBarMenique = (SeekBar) findViewById(R.id.seekBarMenique);

        textViewValorPulgar = (TextView) findViewById(R.id.tv_valor_pulgar);
        textViewValorIndice = (TextView) findViewById(R.id.tv_valor_indice);
        textViewValorMedio = (TextView) findViewById(R.id.tv_valor_medio);
        textViewValorAnular = (TextView) findViewById(R.id.tv_valor_anular);
        textViewValorMenique = (TextView) findViewById(R.id.tv_valor_menique);

        etNombre = (EditText) findViewById(R.id.et_nombre);
        spinner = (Spinner) findViewById(R.id.spinner);

        findViewById(R.id.buttonGuardar).setOnClickListener(this);
        findViewById(R.id.buttonNuevo).setOnClickListener(this);
        findViewById(R.id.buttonEliminar).setOnClickListener(this);

        copiaArchivoJSON();
        String json = loadJSON();

        try {
            jsonObj = new JSONObject(json);
            jsonArray = jsonObj.getJSONArray("movimientos");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] movSpinner = new String[jsonArray.length()];
        String nombre = null;
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                movimiento = jsonArray.getJSONObject(i);
                nombre = movimiento.getString("nombre");
                movSpinner[i] = nombre;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, movSpinner));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentItem = position;
                Toast.makeText(parent.getContext(), (String) parent.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
                for (int i=0; i<jsonArray.length(); i++) {
                    try {
                        movimiento = jsonArray.getJSONObject(i);
                        if (movimiento.getString("nombre") == (String) parent.getItemAtPosition(position)) {
                            imprimeMovimiento(i);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        seekBarPulgar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                textViewValorPulgar.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewValorPulgar.setText(String.valueOf(value));
            }
        });
        seekBarIndice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                textViewValorIndice.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewValorIndice.setText(String.valueOf(value));
            }
        });
        seekBarMedio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                textViewValorMedio.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewValorMedio.setText(String.valueOf(value));
            }
        });
        seekBarAnular.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                textViewValorAnular.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewValorAnular.setText(String.valueOf(value));
            }
        });
        seekBarMenique.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                textViewValorMenique.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewValorMenique.setText(String.valueOf(value));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonGuardar:
                actualizaMovimiento();
                overwriteFile();
                break;
            case R.id.buttonNuevo:
                guardarNuevo();
                overwriteFile();
                break;
            case R.id.buttonEliminar:
                eliminaMovimiento();
                overwriteFile();
                break;
            default:
                break;
        }
    }

    public String loadJSON() {
        String json = null;
        try{
            InputStream in;
            File inFile = new File(getExternalFilesDir(null), "archivoJson.json");
            in = new FileInputStream(inFile);
            //InputStream in = getAssets().open("archivoJson.json"); //Para obtener el archivo desde los assets
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            json = new String(buffer, "UTF-8");
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void imprimeMovimiento (int i){
        String nombre = null;
        int []valor = new int[]{0, 0, 0, 0, 0};
        try {
            movimiento = jsonArray.getJSONObject(i);
            nombre = movimiento.getString("nombre");
            valor[0] = movimiento.getInt("pulgar");
            valor[1] = movimiento.getInt("indice");
            valor[2] = movimiento.getInt("medio");
            valor[3] = movimiento.getInt("anular");
            valor[4] = movimiento.getInt("menique");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        etNombre.setText(nombre);
        textViewValorPulgar.setText(String.valueOf(valor[0]));
        textViewValorIndice.setText(String.valueOf(valor[1]));
        textViewValorMedio.setText(String.valueOf(valor[2]));
        textViewValorAnular.setText(String.valueOf(valor[3]));
        textViewValorMenique.setText(String.valueOf(valor[4]));
        seekBarPulgar.setProgress(valor[0]);
        seekBarIndice.setProgress(valor[1]);
        seekBarMedio.setProgress(valor[2]);
        seekBarAnular.setProgress(valor[3]);
        seekBarMenique.setProgress(valor[4]);
    }

    public void guardarNuevo () {
        String nombre = etNombre.getText().toString();
        int pulgar = seekBarPulgar.getProgress();
        int indice = seekBarIndice.getProgress();
        int medio = seekBarMedio.getProgress();
        int anular = seekBarAnular.getProgress();
        int menique = seekBarMenique.getProgress();
        JSONObject nuevo = new JSONObject();
        boolean repetido = false;

        //Creacion de objeto JSON
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                movimiento = jsonArray.getJSONObject(i);
                if (movimiento.getString("nombre").equals(nombre)) {
                    Toast.makeText(this, "No se guardo el movimiento, cambie el nombre", Toast.LENGTH_SHORT).show();
                    repetido = true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!repetido) {
            try {
                nuevo.put("nombre", nombre);
                nuevo.put("pulgar", pulgar);
                nuevo.put("indice", indice);
                nuevo.put("medio", medio);
                nuevo.put("anular", anular);
                nuevo.put("menique", menique);
                //Toast.makeText(this, ""+nuevo, Toast.LENGTH_SHORT).show();
                jsonArray.put(nuevo);
                //Toast.makeText(this, "Length: " + jsonArray.length(), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            actualizaSpinner();
            actualizaJSONObj();
        }
    }

    public void imprimeJSON () {
        //Toast.makeText(this, jsonArray.toString(),Toast.LENGTH_LONG).show();
        Toast.makeText(this, jsonObj.toString(), Toast.LENGTH_LONG).show();
    }

    public void actualizaMovimiento () {
        int id = currentItem;
        int pulgar = seekBarPulgar.getProgress();
        int indice = seekBarIndice.getProgress();
        int medio = seekBarMedio.getProgress();
        int anular = seekBarAnular.getProgress();
        int menique = seekBarMenique.getProgress();
        try {
            movimiento = jsonArray.getJSONObject(id);
            String nombre = movimiento.getString("nombre");
            movimiento.remove("pulgar");
            movimiento.remove("indice");
            movimiento.remove("medio");
            movimiento.remove("anular");
            movimiento.remove("menique");
            movimiento.put("pulgar",pulgar);
            movimiento.put("indice", indice);
            movimiento.put("medio", medio);
            movimiento.put("anular", anular);
            movimiento.put("menique", menique);
            etNombre.setText(nombre);
            Toast.makeText(this, "Guardado como: " + movimiento.getString("nombre"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        actualizaJSONObj();
}

    public void eliminaMovimiento () {
        int id = currentItem;
        String nombre;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            jsonArray.remove(id);
        }
        actualizaSpinner();
        actualizaJSONObj();
    }

    public void actualizaSpinner () {
        //Actualizacion de Spinner
        String[] movSpinner = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String nombre;
                movimiento = jsonArray.getJSONObject(i);
                nombre = movimiento.getString("nombre");
                movSpinner[i] = nombre;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, movSpinner));
    }

    public void actualizaJSONObj () {
        try {
            jsonObj.put("movimientos", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void copiaArchivoJSON () {
        AssetManager assetManager = getAssets();
        String[] files = null;
        File path = getExternalFilesDir(null);

        try {
            files = assetManager.list("");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "No se pudo obtener la lista de archivos",Toast.LENGTH_LONG).show();
        }

        if(files != null && path.listFiles().length == 0) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void copyFile (InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void overwriteFile () {
        File file = new File(getExternalFilesDir(null), "archivoJson.json");
        try {
            FileOutputStream fileStream = new FileOutputStream(file, false);
            byte[] myBytes = jsonObj.toString().getBytes();
            fileStream.write(myBytes);
            fileStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
