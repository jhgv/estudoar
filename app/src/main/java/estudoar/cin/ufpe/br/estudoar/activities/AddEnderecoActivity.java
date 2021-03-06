package estudoar.cin.ufpe.br.estudoar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import estudoar.cin.ufpe.br.estudoar.R;


public class AddEnderecoActivity extends Activity implements OnMapReadyCallback {

    protected GoogleMap mMap;
    double longitude, latitude;
    Location myLocation;

    private Button btnAddLocal;
    private Button btnAddByAddress;
    private EditText enderecoQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_endereco);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.gMap);
        mapFragment.getMapAsync(this);

        enderecoQuery = (EditText) findViewById(R.id.enderecoQuery);

        btnAddLocal = (Button) findViewById(R.id.btnGetLocal);
        btnAddLocal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                //ParseGeoPoint gp = new ParseGeoPoint(latitude, longitude);
                ArrayList<Double> coordenadas = new ArrayList<Double>();
                coordenadas.add(latitude);
                coordenadas.add(longitude);
                i.putExtra("coordenadas", coordenadas);
                setResult(Activity.RESULT_OK, i);
                finish();

            }
        });

        btnAddByAddress = (Button) findViewById(R.id.getEndereco);
        btnAddByAddress.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String enderecoTxt = enderecoQuery.getText().toString();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + enderecoTxt);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap map) {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        longitude = myLocation.getLongitude();
        latitude = myLocation.getLatitude();
        LatLng location = new LatLng(latitude, longitude);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
        map.addMarker(new MarkerOptions()
                .title("Voce!")
                .snippet("Sua atual localizacao.")
                .position(location));
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
