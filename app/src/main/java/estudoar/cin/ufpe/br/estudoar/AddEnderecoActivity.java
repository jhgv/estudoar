package estudoar.cin.ufpe.br.estudoar;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;


public class AddEnderecoActivity extends Activity implements OnMapReadyCallback {

    protected GoogleMap mMap;
    double longitude, latitude;
    Location myLocation;

    Button btnAddLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_endereco);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.gMap);
        mapFragment.getMapAsync(this);

        btnAddLocal = (Button) findViewById(R.id.btnGetLocal);
        btnAddLocal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ParseGeoPoint gp = new ParseGeoPoint(latitude, longitude);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_endereco, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        double longitude = myLocation.getLongitude();
        double latitude = myLocation.getLatitude();
        LatLng location = new LatLng(latitude, longitude);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
        map.addMarker(new MarkerOptions()
                .title("Você!")
                .snippet("Sua atual localização.")
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
