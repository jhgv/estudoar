package estudoar.cin.ufpe.br.estudoar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;


public class SearchResultsActivity extends Activity implements SearchView.OnQueryTextListener {
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        onSearchRequested();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("MyApp", "Submetendo a query: " + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("MyApp", "Escrevendo nova query (parcial : " + newText + ")");
        return false;
    }
}
