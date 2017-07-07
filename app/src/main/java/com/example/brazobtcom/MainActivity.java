package com.example.brazobtcom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBlueToothAdapter;
    BluetoothDevice mmDevice;
    BluetoothSocket mmSocket;
    byte mensaje[];

    OutputStream mmOutputStream;
    InputStream mmInputStream;

    String json = null;
    JSONObject movimiento;
    JSONObject jsonObj;
    JSONArray jsonArray;

    String[] movListStr;
    List<String> movList;
    ArrayAdapter<String> arrayAdapter;

    TextView textViewBtStatus;
    TextView textViewBtDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Declaración de elementos
        ListView listViewMov = (ListView) findViewById(R.id.listViewMovimientos);
        textViewBtStatus = (TextView) findViewById(R.id.tv_bt_status);
        textViewBtDevice = (TextView) findViewById(R.id.tv_bt_device);

        //Listener botones
        findViewById(R.id.buttonActualizar).setOnClickListener(this);
        findViewById(R.id.buttonEditar).setOnClickListener(this);
        findViewById(R.id.buttonActMov).setOnClickListener(this);

        //Adaptador de lista con ListView
        movListStr = new String[]{};
        movList = new ArrayList<String>(Arrays.asList(movListStr));
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,movList);
        listViewMov.setAdapter(arrayAdapter);

        if (mBlueToothAdapter == null) {
            Toast.makeText(this, "Bluetooth no soportado", Toast.LENGTH_LONG).show();
        } else {
            if (!mBlueToothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            if (mBlueToothAdapter.isEnabled()) {
                textViewBtStatus.setText("Activado, no conectado");
            }
        }

        listViewMov.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    movimiento = jsonArray.getJSONObject(position);
                    crearMensaje();
                    if (mmSocket != null) {
                        if (mmSocket.isConnected()) {
                            sendData();
                        } else {
                            Toast.makeText(getApplicationContext(), "Mensaje no enviado. Revisa la conexión Bluetooth", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No se ha iniciado la conexión bluetooth", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            textViewBtStatus.setText("Activado, no conectado");
        } else if (resultCode == RESULT_CANCELED) {
            textViewBtStatus.setText("Desactivado");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonEditar:
                Intent intent = new Intent(this, configActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonActMov:
                json = loadJSON();
                actualizaLista();
                break;
            case R.id.buttonActualizar:
                findBT();
                openBT();
                break;
            default:
                break;
        }
    }

    public void sendData () {
        String mensajeToast = null;
        try {
            mensajeToast = movimiento.getString("nombre");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mmOutputStream.write(mensaje);
            Toast.makeText(this, "Movimiento enviado: " + mensajeToast, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void actualizaLista () {
        try {
            jsonObj = new JSONObject(json);
            jsonArray = jsonObj.getJSONArray("movimientos");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        movList.clear();

        String nombre = null;
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                movimiento = jsonArray.getJSONObject(i);
                nombre = movimiento.getString("nombre");
                movList.add(nombre);
                arrayAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "fallo actualizacion", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openBT() {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            Toast.makeText(this, "Conexión bluetooth abierta",Toast.LENGTH_SHORT).show();
            textViewBtStatus.setText("Activado, conectado");
            textViewBtDevice.setText("HC-05");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findBT() {
        Set<BluetoothDevice> pairedDevices = mBlueToothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("HC-05")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        Toast.makeText(this, "HC-05 emparejado", Toast.LENGTH_SHORT).show();
    }

    public void crearMensaje () {
        try {
            int pulgar = movimiento.getInt("pulgar");
            int indice = movimiento.getInt("indice");
            int medio = movimiento.getInt("medio");
            int anular = movimiento.getInt("anular");
            int menique = movimiento.getInt("menique");

            pulgar = 4*(1000 + (pulgar*10));
            indice = 4*(1000 + (indice*10));
            medio = 4*(1000 + (medio*10));
            anular = 4*(1000 + (anular*10));
            menique = 4*(1000 + (menique*10));

            byte bajoPulgar = (byte) (pulgar & 0x007F);
            byte altoPulgar = (byte) (pulgar >> 7 & 0x007F);
            byte bajoIndice = (byte) (indice & 0x007F);
            byte altoIndice = (byte) (indice >> 7 & 0x007F);
            byte bajoMedio = (byte) (medio & 0x007F);
            byte altoMedio = (byte) (medio >> 7 & 0x007F);
            byte bajoAnular = (byte) (anular & 0x007F);
            byte altoAnular = (byte) (anular >> 7 & 0x007F);
            byte bajoMenique = (byte) (menique & 0x007F);
            byte altoMenique = (byte) (menique >> 7 & 0x007F);

            //Toast.makeText(this, String.format("0x%02X",bajo)+ " " +String.format("0x%02X",alto), Toast.LENGTH_LONG).show();
            mensaje = new byte[]{(byte) 0xaa, 12, 0x1f, 5, 0, bajoPulgar, altoPulgar, bajoIndice, altoIndice,
                    bajoMedio, altoMedio, bajoAnular, altoAnular, bajoMenique, altoMenique};

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
